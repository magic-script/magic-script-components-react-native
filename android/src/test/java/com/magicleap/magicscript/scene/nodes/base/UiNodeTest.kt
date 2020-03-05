/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.base

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.UiNodeBuilder
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.clip.TextureClipper
import com.magicleap.magicscript.ar.clip.UiNodeClipper
import com.magicleap.magicscript.ar.clip.UiNodeColliderClipper
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.shouldEqualInexact
import com.magicleap.magicscript.update
import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiNodeTest {

    private val EPSILON = 1e-5f

    private lateinit var context: Context
    private lateinit var viewRenderableLoader: ViewRenderableLoader
    private lateinit var nodeClipper: Clipper
    private lateinit var node: UiNode

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        viewRenderableLoader = mock()
        nodeClipper = spy(UiNodeClipper(TextureClipper(), UiNodeColliderClipper()))
        node = UiNodeBuilder(
            context = context,
            useContentNodeAlignment = true,
            viewRenderableLoader = viewRenderableLoader,
            nodeClipper = nodeClipper
        )
            .withSize(1f, 1f)
            .build()
    }

    @Test
    fun `should be enabled by default`() {
        val enabledProp = node.getProperty(UiNode.PROP_ENABLED)

        assertEquals(true, enabledProp)
    }

    @Test
    fun `should load renderable when attach requested`() {
        node.attachRenderable()

        verify(viewRenderableLoader).loadRenderable(any(), any())
    }

    @Test
    fun `should notify when property updated`() {
        var called = false
        node.onUpdatedListener = { called = true }

        node.update(TransformNode.PROP_ALIGNMENT, "top-right")

        called shouldEqual true
    }

    @Test
    fun `should notify when destroyed`() {
        var called = false
        node.onDeletedListener = { called = true }

        node.onDestroy()

        called shouldEqual true
    }

    @Test
    fun `should notify after disabling`() {
        var called = false
        node.onDisabledListener = { called = true }

        node.update(UiNode.PROP_ENABLED, false)

        called shouldEqual true
    }

    @Test
    fun `should notify after enabling`() {
        var called = false
        node.onEnabledListener = { called = true }
        node.update(UiNode.PROP_ENABLED, false)

        node.update(UiNode.PROP_ENABLED, true)

        called shouldEqual true
    }

    @Test
    fun `get bounding should return content bounding with position offset`() {
        val node = UiNodeBuilder(context)
            .withSize(4f, 4f)
            .withAlignment("center-center")
            .withPosition(2.0, 5.0, 0.0)
            .build()

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(0f, 3f, 0f)
        bounding.max shouldEqualInexact Vector3(4f, 7f, 0f)
    }

    @Test
    fun `should return correct bounding when rotated 90 degrees around Z`() {
        val node = UiNodeBuilder(context)
            .withSize(2f, 1f)
            .withAlignment("bottom-left")
            .withRotation(0.0, 0.0, 0.7071068, 0.7071068)
            .build()

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(-1f, 0f, 0f)
        bounding.max shouldEqualInexact Vector3(0f, 2f, 0f)
    }

    @Test
    fun `should return zero-width bounding when rotated 90 degrees around Y`() {
        val node = UiNodeBuilder(context)
            .withSize(2f, 2f)
            .withAlignment("center-center")
            .withPosition(0.0, 0.0, 0.0)
            .withRotation(0.0, 0.7071068, 0.0, 0.7071068)
            .build()

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(0f, -1f, -1f)
        bounding.max shouldEqualInexact Vector3(0f, 1f, 1f)
    }

    @Test
    fun `should return zero-height bounding when rotated 90 degrees around X`() {
        val node = UiNodeBuilder(context)
            .withSize(2f, 2f)
            .withAlignment("center-center")
            .withPosition(0.0, 0.0, 0.0)
            .withRotation(0.7071068, 0.0, 0.0, 0.7071068)
            .build()

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(-1f, 0f, -1f)
        bounding.max shouldEqualInexact Vector3(1f, 0f, 1f)
    }

    @Test
    fun `should align content node when using content node alignment`() {
        val node = UiNodeBuilder(context, useContentNodeAlignment = true)
            .withSize(4f, 10f)
            .withAlignment("top-right")
            .build()

        node.contentNode.localPosition shouldEqualInexact Vector3(-2f, -5f, 0f)
        node.localPosition shouldEqualInexact Vector3.zero()
    }

    @Test
    fun `should align content node when using content node alignment (case 2)`() {
        val node = UiNodeBuilder(context, useContentNodeAlignment = true)
            .withSize(4f, 2f)
            .withAlignment("bottom-center")
            .build()

        node.contentNode.localPosition shouldEqualInexact Vector3(0f, 1f, 0f)
    }

    @Test
    fun `should align content node when using content node alignment (case 3)`() {
        val node = UiNodeBuilder(context, useContentNodeAlignment = true)
            .withSize(5f, 5f)
            .withAlignment("top-left")
            .build()

        node.contentNode.localPosition shouldEqualInexact Vector3(2.5f, -2.5f, 0f)
    }

    @Test
    fun `should return proper content position when using content node alignment`() {
        val node = UiNodeBuilder(context, useContentNodeAlignment = true)
            .withSize(4f, 2f)
            .withPosition(5.0, 3.0, 0.0)
            .withAlignment("bottom-right")
            .build()

        val contentPosition = node.getContentPosition()

        assertEquals(3f, contentPosition.x, EPSILON)
        assertEquals(4f, contentPosition.y, EPSILON)
    }

    @Test
    fun `should apply clip bounds when assigned`() {
        val clipBounds = AABB(min = Vector3(0.5f, 0.5f, -1f), max = Vector3(1f, 1f, 1f))
        node.clipBounds = clipBounds

        verify(nodeClipper).applyClipBounds(eq(node), eq(clipBounds))
    }

    @Test
    fun `should apply clip bounds again when local position changed`() {
        val clipBounds = AABB(min = Vector3(0.5f, 0.5f, -1f), max = Vector3(1f, 1f, 1f))
        node.clipBounds = clipBounds

        node.localPosition = Vector3(2f, 1f, 8f)

        verify(nodeClipper, times(2)).applyClipBounds(eq(node), eq(clipBounds))
    }

    @Test
    fun `should apply clip bounds again when local scale changed`() {
        val clipBounds = AABB(min = Vector3(0.5f, 0.5f, -1f), max = Vector3(1f, 1f, 1f))
        node.clipBounds = clipBounds

        node.localScale = Vector3(2f, 3f, 1f)

        verify(nodeClipper, times(2)).applyClipBounds(eq(node), eq(clipBounds))
    }

    @Test
    fun `should apply clip bounds again when visibility changed to visible`() {
        val clipBounds = AABB(min = Vector3(0.5f, 0.5f, -1f), max = Vector3(1f, 1f, 1f))
        node.clipBounds = clipBounds
        node.hide()

        node.show()

        verify(nodeClipper, times(2)).applyClipBounds(eq(node), eq(clipBounds))
    }

    @Test
    fun `should clip collision shape after setting clip bounds`() {
        val node = UiNodeBuilder(context, nodeClipper = nodeClipper)
            .withSize(4f, 4f)
            .withAlignment("center-center")
            .build()

        node.clipBounds = AABB(min = Vector3(-1f, -1f, -1f), max = Vector3(1f, 1f, 1f))

        val collisionShape = node.contentNode.collisionShape
        collisionShape shouldBeInstanceOf Box::class
        (collisionShape as Box).size shouldEqualInexact Vector3(2f, 2f, 0f)
        collisionShape.center shouldEqualInexact Vector3.zero()
    }

    @Test
    fun `should not set collision shape after setting clip bounds if node is hidden`() {
        val node = UiNodeBuilder(context, nodeClipper = nodeClipper)
            .withSize(4f, 4f)
            .withAlignment("center-center")
            .build()
        node.hide()

        node.clipBounds = AABB(min = Vector3(-1f, -1f, -1f), max = Vector3(1f, 1f, 1f))

        node.contentNode.collisionShape shouldBe null
    }

}