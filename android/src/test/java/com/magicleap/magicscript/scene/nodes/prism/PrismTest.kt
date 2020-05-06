/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.prism

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.*
import com.magicleap.magicscript.ar.AnchorCreator
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.CustomArFragment
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.magicleap.magicscript.ar.renderable.ModelRenderableLoader
import com.magicleap.magicscript.scene.ReactScene
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.*
import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatcher
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.util.*

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class PrismTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val cubeBuilder: CubeRenderableBuilder = mock()
    private val modelLoader: ModelRenderableLoader = mock()
    private val anchorCreator: AnchorCreator = mock()
    private val arResourcesProvider: ArResourcesProvider = mock()

    @Before
    fun setUp() {
        whenever(arResourcesProvider.getArScene()).thenReturn(mock())
        whenever(arResourcesProvider.getTransformationSystem()).thenReturn(getTransformationSystem())

        // we have to prevent renderable loading in tests, because ARCore is not initialized
        whenever(arResourcesProvider.isArLoaded()).thenReturn(false)
        whenever(arResourcesProvider.getCameraInfo()).thenReturn(createCameraInfo())

        whenever(anchorCreator.createAnchor(any())).thenReturn(DataResult.Success(mock()))
    }

    @Test
    fun `should be possible to put Transform node inside prism`() {
        val prism = buildPrism(reactMapOf())
        val child = NodeBuilder().build()

        prism.addContent(child)

        prism.reactChildren.size shouldEqual 1
        prism.reactChildren.first() shouldBe child
    }

    @Test
    fun `should not be possible to put prism inside another prism`() {
        val prism = buildPrism(reactMapOf())
        val child = buildPrism(reactMapOf())

        prism.addContent(child)

        prism.reactChildren.shouldBeEmpty()
    }

    @Test
    fun `should be possible to replace content`() {
        val prism = buildPrism(reactMapOf())
        val child1 = NodeBuilder().build()
        val child2 = NodeBuilder().build()
        prism.addContent(child1)

        prism.removeContent(child1)
        prism.addContent(child2)

        prism.reactChildren.first() shouldBe child2
        prism.reactChildren.size shouldEqual 1
    }

    @Test
    fun `should clip child according to prism size when new child added`() {
        val size = JavaOnlyArray.of(0.4f, 0.6f, 0.2f)
        val expectedClip = AABB(min = Vector3(-0.2f, -0.3f, -0.1f), max = Vector3(0.2f, 0.3f, 0.1f))
        val prism = buildPrism(reactMapOf(Prism.PROP_SIZE, size))
        val child = spy(NodeBuilder().build())

        prism.addContent(child)

        verify(child).clipBounds = expectedClip
    }

    @Test
    fun `should rebuild cube when size changed`() {
        val size = JavaOnlyArray.of(1f, 1f, 1f)
        val prism = buildPrism(reactMapOf(Prism.PROP_SIZE, size))
        Mockito.reset(cubeBuilder)
        val sizeUpdated = JavaOnlyArray.of(2f, 2f, 2f)

        prism.update(reactMapOf(Prism.PROP_SIZE, sizeUpdated))

        verify(cubeBuilder).buildRenderable(any())
    }

    @Test
    fun `should anchor the prism at zero position and no rotation by default`() {
        buildPrism(reactMapOf())
        val expectedPose = Utils.createPose(
            position = Vector3.zero(),
            rotation = Quaternion.identity()
        )

        verify(anchorCreator).createAnchor(argThat(PoseMatcher(expectedPose)))
    }

    @Test
    fun `should not anchor to position if anchor UUID property also present`() {
        buildPrism(
            reactMapOf(
                Prism.PROP_POSITION, reactArrayOf(1.2f, 1.4f, -1f),
                Prism.PROP_ANCHOR_UUID, UUID.randomUUID().toString()
            )
        )

        // anchor assigned by XR client has a priority over "position" property
        verifyZeroInteractions(anchorCreator)
    }

    @Test
    fun `should anchor the node at updated position if mode is normal`() {
        val prism = buildPrism(reactMapOf(Prism.PROP_MODE, Prism.MODE_NORMAL))
        Mockito.reset(anchorCreator)
        val updatedPosition = reactArrayOf(0.5, 0.1, 2.0)
        val expectedPose = Utils.createPose(
            position = Vector3(0.5f, 0.1f, 2.0f),
            rotation = Quaternion.identity()
        )

        prism.update(reactMapOf(Prism.PROP_POSITION, updatedPosition))

        verify(anchorCreator).createAnchor(argThat(PoseMatcher(expectedPose)))
    }

    @Test
    fun `should position the node relative to camera if positionRelativeToCamera true`() {
        val cameraPosition = Vector3(2.5f, 0.4f, 1.2f)
        whenever(arResourcesProvider.getCameraInfo()).thenReturn(createCameraInfo(position = cameraPosition))
        val requestedPosition = reactArrayOf(1.0, 1.0, 1.0)
        val expectedPose = Utils.createPose(
            position = Vector3(3.5f, 1.4f, 2.2f),
            rotation = Quaternion.identity()
        )

        buildPrism(
            reactMapOf(
                Prism.PROP_POSITION, requestedPosition,
                Prism.PROP_POSITION_RELATIVE, true,
                Prism.PROP_MODE, Prism.MODE_NORMAL
            )
        )

        verify(anchorCreator).createAnchor(argThat(PoseMatcher(expectedPose)))
    }

    @Test
    fun `should anchor the node at desired rotation if mode is normal`() {
        val expectedPose = Utils.createPose(
            position = Vector3.zero(),
            rotation = Quaternion(0f, 0.42f, 0f, 0.9f)
        )

        buildPrism(
            reactMapOf(
                Prism.PROP_ROTATION, reactArrayOf(0.00, 0.42, 0.0, 0.9),
                Prism.PROP_MODE, Prism.MODE_NORMAL
            )
        )

        verify(anchorCreator).createAnchor(argThat(PoseMatcher(expectedPose)))
    }

    @Test
    fun `should update local position if position property updated in edit mode`() {
        val prism = buildPrism(reactMapOf(Prism.PROP_MODE, Prism.MODE_EDIT))
        Mockito.reset(anchorCreator)

        prism.update(
            reactMapOf(Prism.PROP_POSITION, reactArrayOf(-0.4, 0.2, -1.5))
        )

        prism.localPosition shouldEqualInexact Vector3(-0.4f, 0.2f, -1.5f)
        prism.localPosition shouldEqual prism.worldPosition
        verifyZeroInteractions(anchorCreator)
    }

    @Test
    fun `should set local rotation if rotation property applied in edit mode`() {
        val prism = buildPrism(
            reactMapOf(
                Prism.PROP_ROTATION, reactArrayOf(0.00, 0.42, 0.0, 0.9),
                Prism.PROP_MODE, Prism.MODE_EDIT
            )
        )

        prism.localRotation shouldEqual Quaternion(0.00f, 0.42f, 0.0f, 0.9f)
        verifyZeroInteractions(anchorCreator)
    }

    @Test
    fun `should recreate anchor after AR Core reload`() {
        val prism = buildPrism(
            reactMapOf(
                Prism.PROP_MODE, Prism.MODE_NORMAL,
                Prism.PROP_POSITION, reactArrayOf(0.5, 0.1, 2.0)
            )
        )

        prism.onArLoaded(firstTime = false)

        verify(anchorCreator, times(2)).createAnchor(any())
    }

    @Test
    fun `should not recreate anchor after AR Core reload if edit mode active`() {
        val prism = buildPrism(
            reactMapOf(
                Prism.PROP_MODE, Prism.MODE_EDIT,
                Prism.PROP_POSITION, reactArrayOf(0.2, 1.4, -0.6)
            )
        )

        prism.onArLoaded(firstTime = false)

        verify(anchorCreator, never()).createAnchor(any())
    }

    @Test
    fun `should not recreate anchor if AR Core loaded first time`() {
        val prism = buildPrism(
            reactMapOf(
                Prism.PROP_MODE, Prism.MODE_NORMAL,
                Prism.PROP_POSITION, reactArrayOf(0.2, 1.4, -0.6)
            )
        )

        prism.onArLoaded(firstTime = true)

        // should create anchor only one time at build
        verify(anchorCreator, times(1)).createAnchor(any())
    }

    @Test
    fun `should apply the scale property`() {
        val scale = reactArrayOf(1.5, 0.5, 1.0)
        val content = NodeBuilder().withScale(1.0, 1.0, 1.0).build()

        val prism = buildPrism(reactMapOf(Prism.PROP_SCALE, scale))
        prism.addContent(content)

        content.worldScale shouldEqualInexact Vector3(1.5f, 0.5f, 1f)
    }

    @Test
    fun `should globally rotate menu around Y axis to look at camera`() {
        val prism = buildPrism(
            reactMapOf(Prism.PROP_POSITION, reactArrayOf(0.0, 0.0, 0.0))
        )
        val menu = prism.children.filterIsInstance<PrismMenu>().firstOrNull()
        val cameraPosition = Vector3(0.6f, 0.4f, 1.4f)
        val cameraPose = Utils.createPose(cameraPosition, Quaternion.identity())
        val expectedMenuRotation = Quaternion(0f, 0.20106588f, 0f, 0.9795777f)

        prism.onCameraUpdated(cameraPose, TrackingState.TRACKING)

        menu shouldNotBe null
        menu!!.worldRotation shouldEqual expectedMenuRotation
    }

    @Test
    fun `should coerce min distance from camera if edit mode active`() {
        val prism = buildPrism(
            reactMapOf(
                Prism.PROP_MODE, Prism.MODE_EDIT,
                Prism.PROP_POSITION, reactArrayOf(0.0, 0.0, 0.0),
                Prism.PROP_SIZE, reactArrayOf(1.0, 1.0, 1.0)
            )
        )
        val cameraPosition = Vector3(0f, 0f, 0.2f)
        val cameraPose = Utils.createPose(cameraPosition, Quaternion.identity())
        // should be at a distance equal to the radius of a sphere that includes the prism
        val expectedPrismPosition = Vector3(0f, 0f, -0.6660254f) // camera z - prism sphere radius

        prism.onCameraUpdated(cameraPose, TrackingState.TRACKING)

        prism.worldPosition shouldEqualInexact expectedPrismPosition
    }

    @Test
    fun `should limit max distance from camera if edit mode active`() {
        val prism = buildPrism(
            reactMapOf(
                Prism.PROP_MODE, Prism.MODE_EDIT,
                Prism.PROP_POSITION, reactArrayOf(0.0, 0.0, -100f),
                Prism.PROP_SIZE, reactArrayOf(1.0, 1.0, 1.0)
            )
        )
        val cameraPosition = Vector3(0f, 0f, 0f)
        val cameraPose = Utils.createPose(cameraPosition, Quaternion.identity())
        // far clip plane minus radius of a sphere that includes the prism
        val limitedZDistance = CustomArFragment.FAR_CLIP_PLANE - 0.8660254f
        val expectedPrismPosition = Vector3(0f, 0f, -limitedZDistance)

        prism.onCameraUpdated(cameraPose, TrackingState.TRACKING)

        prism.worldPosition shouldEqualInexact expectedPrismPosition
    }

    @Test
    fun `should follow camera if edit mode active`() {
        val prism = buildPrism(
            reactMapOf(
                Prism.PROP_MODE, Prism.MODE_EDIT,
                Prism.PROP_POSITION, reactArrayOf(-0.4, 0.0, -0.2),
                Prism.PROP_SIZE, reactArrayOf(0.1, 0.1, 0.1)
            )
        )
        val cameraPosition = Vector3(0.5f, 0.25f, 0.8f)
        val cameraRotation = Quaternion.eulerAngles(Vector3(30f, 60f, 0f))
        val cameraPose = Utils.createPose(cameraPosition, cameraRotation)
        val contentNode = prism.children.filterIsInstance<PrismContentNode>().firstOrNull()

        prism.onCameraUpdated(cameraPose, TrackingState.TRACKING)

        prism.worldPosition shouldEqualInexact Vector3(0.16458982f, 0.47360682f, 0.60635084f)
        contentNode shouldNotBe null
        // content should rotate only around Y
        contentNode!!.worldRotation shouldEqual Quaternion.eulerAngles(Vector3(0f, 60f, 0f))
    }

    @Test
    fun `should not follow camera if edit mode inactive`() {
        val prism = buildPrism(
            reactMapOf(
                Prism.PROP_MODE, Prism.MODE_NORMAL,
                Prism.PROP_POSITION, reactArrayOf(1.0, 2.0, 0.0)
            )
        )
        val expectedPrismPose = Utils.createPose(Vector3(1f, 2f, 0f), Quaternion.identity())
        val cameraPosition = Vector3(0.4f, -0.2f, 0.1f)
        val cameraRotation = Quaternion.eulerAngles(Vector3(-20f, 45f, 0f))
        val cameraPose = Utils.createPose(cameraPosition, cameraRotation)
        val contentNode = prism.children.filterIsInstance<PrismContentNode>().firstOrNull()

        prism.onCameraUpdated(cameraPose, TrackingState.TRACKING)

        verify(anchorCreator, atLeastOnce()).createAnchor(argThat(PoseMatcher(expectedPrismPose)))
        verifyNoMoreInteractions(anchorCreator)
        contentNode shouldNotBe null
        contentNode!!.worldRotation shouldEqual Quaternion.identity()
    }

    @Test
    fun `should return scene as react parent when added to scene`() {
        val scene = ReactScene(reactMapOf(), arResourcesProvider)
        scene.build()
        val prism = buildPrism(reactMapOf())

        scene.addContent(prism)

        prism.reactParent shouldBe scene
    }

    // Because Prism's container is a TransformableNode, we have detach it explicitly.
    // See https://github.com/magic-script/magic-script-components-react-native/issues/494
    @Test
    fun `should detach transformable container on destroy`() {
        val prism = buildPrism(reactMapOf())

        prism.onDestroy()

        prism.children.filterIsInstance<TransformableNode>().shouldBeEmpty()
    }

    @Test
    fun `should unregister AR resources listeners on destroy`() {
        val prism = buildPrism(reactMapOf())

        prism.onDestroy()

        verify(arResourcesProvider).removeCameraUpdatedListener(eq(prism))
        verify(arResourcesProvider).removeTransformationSystemListener(eq(prism))
        verify(arResourcesProvider).removeArLoadedListener(eq(prism))
    }

    private fun buildPrism(props: JavaOnlyMap): Prism {
        val appInfoProvider = TestAppInfoProvider()
        return Prism(
            props,
            context,
            modelLoader,
            cubeBuilder,
            anchorCreator,
            arResourcesProvider,
            appInfoProvider
        ).apply {
            build()
        }
    }

    private fun getTransformationSystem(): TransformationSystem {
        val displayMetrics = context.resources.displayMetrics
        return TransformationSystem(displayMetrics, FootprintSelectionVisualizer())
    }

    private class PoseMatcher(private val pose: Pose) : ArgumentMatcher<Pose> {
        override fun matches(argument: Pose): Boolean {
            return pose.getTranslationVector() == argument.getTranslationVector()
                    && pose.getRotation().normalized() == argument.getRotation().normalized()

        }
    }

}