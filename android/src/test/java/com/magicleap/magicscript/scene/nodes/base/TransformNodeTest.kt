/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes.base

import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.shouldEqualInexact
import org.amshove.kluent.shouldEqual
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class TransformNodeTest {

    // epsilon
    private val eps = 1e-5f

    @Test
    fun `should be located at zero position by default`() {
        val node = NodeBuilder().build()

        assertEquals(Vector3.zero(), node.localPosition)
    }

    @Test
    fun `anchor UUID should be empty by default`() {
        val node = NodeBuilder().build()

        node.anchorUuid shouldEqual ""
    }

    @Test
    fun `should contain initial properties`() {
        val property = "key"
        val propsMap = JavaOnlyMap()
        propsMap.putString(property, "value")

        val node = NodeBuilder().withProps(propsMap).build()

        assertNotNull(node.getProperty(property))
    }

    @Test
    fun `should add new property when passed in update`() {
        val initialProperty = "initialProp"
        val initialProps = JavaOnlyMap()
        initialProps.putString(initialProperty, "value")
        val node = NodeBuilder().withProps(initialProps).build()

        val newProps = JavaOnlyMap()
        val propertyToAdd = "propToAdd"
        newProps.putString(propertyToAdd, "value")
        node.update(newProps)

        assertNotNull(node.getProperty(initialProperty))
        assertNotNull(node.getProperty(propertyToAdd))
    }

    @Test
    fun `should set anchor UUID on build when property present`() {
        val uuid = UUID.randomUUID().toString()

        val node = NodeBuilder().withAnchorUUID(uuid).build()

        node.anchorUuid shouldEqual uuid
    }

    @Test
    fun `should save new property value when updated`() {
        val propKey = "key"
        val initialValue = "value"
        val initialProps = JavaOnlyMap()
        initialProps.putString(propKey, initialValue)
        val node = NodeBuilder().withProps(initialProps).build()

        val updatedValue = "updated value"
        val propsToChange = JavaOnlyMap()
        propsToChange.putString(propKey, updatedValue)
        node.update(propsToChange)

        val result = node.getProperty(propKey)
        assertEquals(updatedValue, result)
    }

    @Test
    fun `should apply all properties on build`() {
        val node = NodeBuilder()
            .withPosition(4.0, 2.0, -5.0)
            .withScale(2.0, 2.0, 2.0)
            .build()

        assertEquals(Vector3(4f, 2f, -5f), node.localPosition)
        assertEquals(Vector3(2f, 2f, 2f), node.localScale)
    }

    @Test
    fun `should apply new properties on update`() {
        val node = NodeBuilder().build()
        val alignment = "bottom-right"
        val propsToUpdate = reactMapOf(TransformNode.PROP_ALIGNMENT, alignment)

        node.update(propsToUpdate)

        assertEquals(Alignment.VerticalAlignment.BOTTOM, node.verticalAlignment)
        assertEquals(Alignment.HorizontalAlignment.RIGHT, node.horizontalAlignment)
    }

    @Test
    fun `get bounding should return content bounding with position offset`() {
        val bounds = Bounding(-2F, -2F, 2F, 2F)
        val node = NodeBuilder()
            .withPosition(2.0, 5.0, 0.0)
            .withContentBounds(bounds)
            .build()
        val expected = Bounding(0F, 3F, 4F, 7F)

        val result = node.getBounding()

        result shouldEqualInexact expected
    }

    @Test
    fun `should return correct bounding when rotated 90 degrees around Z`() {
        val bounds = Bounding(0F, 0F, 2F, 1F)
        val node = NodeBuilder()
            .withPosition(0.0, 0.0, 0.0)
            .withRotation(0.0, 0.0, 0.7071068, 0.7071068)
            .withContentBounds(bounds)
            .build()
        val expected = Bounding(-1F, 0F, 0F, 2F)

        val result = node.getBounding()

        result shouldEqualInexact expected
    }

    @Test
    fun `should return zero-width bounding when rotated 90 degrees around Y`() {
        val bounds = Bounding(-1F, -1F, 1F, 1F)
        val node = NodeBuilder()
            .withPosition(0.0, 0.0, 0.0)
            .withRotation(0.0, 0.7071068, 0.0, 0.7071068)
            .withContentBounds(bounds)
            .build()
        val expected = Bounding(0F, -1F, 0F, 1F)

        val result = node.getBounding()

        result shouldEqualInexact expected
    }

    @Test
    fun `should return zero-height bounding when rotated 90 degrees around X`() {
        val bounds = Bounding(-1F, -1F, 1F, 1F)
        val node = NodeBuilder()
            .withPosition(0.0, 0.0, 0.0)
            .withRotation(0.7071068, 0.0, 0.0, 0.7071068)
            .withContentBounds(bounds)
            .build()
        val expected = Bounding(-1F, 0F, 1F, 0F)

        val result = node.getBounding()

        result shouldEqualInexact expected
    }

    @Test
    fun `should align content node when using content node alignment`() {
        val bounds = Bounding(-2F, -5F, 2F, 5F)

        val node = NodeBuilder()
            .withAlignment("top-right")
            .withContentBounds(bounds)
            .build()

        assertEquals(-2.0F, node.contentNode.localPosition.x, eps)
        assertEquals(-5.0F, node.contentNode.localPosition.y, eps)
        assertEquals(Vector3.zero(), node.localPosition)
    }

    @Test
    fun `should return proper content position`() {
        val bounds = Bounding(-2F, -1F, 2F, 1F)
        val node = NodeBuilder()
            .withPosition(5.0, 3.0, 0.0)
            .withAlignment("bottom-right")
            .withContentBounds(bounds)
            .build()

        val contentPosition = node.getContentPosition()

        assertEquals(3f, contentPosition.x, eps)
        assertEquals(4f, contentPosition.y, eps)
    }

    @Test
    fun `children should be added to content node`() {
        val node = NodeBuilder().build()
        val child1 = NodeBuilder().build()
        val child2 = NodeBuilder().build()

        node.addContent(child1)
        node.addContent(child2)

        assertTrue(node.contentNode.children.contains(child1))
        assertTrue(node.contentNode.children.contains(child2))
    }

    @Test
    fun `renderableRequested should be true after attaching renderable`() {
        val node = NodeBuilder().build()
        node.attachRenderable()
        assertTrue(node.renderableRequested)
    }
}