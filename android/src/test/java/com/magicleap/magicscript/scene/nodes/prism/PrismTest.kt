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
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.*
import com.magicleap.magicscript.ar.AnchorCreator
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.magicleap.magicscript.scene.ReactScene
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatcher
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class PrismTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val cubeBuilder: CubeRenderableBuilder = mock()
    private val anchorCreator: AnchorCreator = mock()
    private val arResourcesProvider: ArResourcesProvider = mock()

    @Before
    fun setUp() {
        whenever(arResourcesProvider.getArScene()).thenReturn(mock())
        whenever(arResourcesProvider.getTransformationSystem()).thenReturn(getTransformationSystem())

        // we have to prevent renderable loading in tests, because ARCore is not initialized
        whenever(arResourcesProvider.isArLoaded()).thenReturn(false)
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
    fun `should create anchor when position changed`() {
        val prism = buildPrism(reactMapOf())
        val position = JavaOnlyArray.of(2.0, 1.0, 0.0)
        val anchorRotation = prism.localRotation
        val expectedPose = Pose(
            floatArrayOf(2f, 1f, 0f),
            floatArrayOf(anchorRotation.x, anchorRotation.y, anchorRotation.z, anchorRotation.w)
        )

        prism.update(reactMapOf(Prism.PROP_POSITION, position))

        verify(anchorCreator).createAnchor(argThat(PoseMatcher(expectedPose)), any())
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
    }

    private fun buildPrism(props: JavaOnlyMap): Prism {
        val appInfoProvider = TestAppInfoProvider()
        return Prism(
            props,
            cubeBuilder,
            anchorCreator,
            arResourcesProvider,
            context,
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
            return pose.rotationQuaternion.contentEquals(argument.rotationQuaternion)
                    && pose.translation.contentEquals(argument.translation)
        }
    }

}