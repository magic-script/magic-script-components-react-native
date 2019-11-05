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

package com.reactlibrary.scene.nodes.base

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

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
        val node = createNode(JavaOnlyMap())
        node.build()

        assertEquals(Vector3.zero(), node.localPosition)
    }

    @Test
    fun `should contain initial properties`() {
        val property = "key"
        val propsMap = JavaOnlyMap()
        propsMap.putString(property, "value")

        val node = createNode(propsMap)

        assertNotNull(node.getProperty(property))
    }

    @Test
    fun `should add new property when passed in update`() {
        val initialProperty = "initialProp"
        val initialProps = JavaOnlyMap()
        initialProps.putString(initialProperty, "value")
        val node = createNode(initialProps)

        val newProps = JavaOnlyMap()
        val propertyToAdd = "propToAdd"
        newProps.putString(propertyToAdd, "value")
        node.update(newProps)

        assertNotNull(node.getProperty(initialProperty))
        assertNotNull(node.getProperty(propertyToAdd))
    }

    @Test
    fun `should save new property value when updated`() {
        val propKey = "key"
        val initialValue = "value"
        val initialProps = JavaOnlyMap()
        initialProps.putString(propKey, initialValue)
        val node = createNode(initialProps)

        val updatedValue = "updated value"
        val propsToChange = JavaOnlyMap()
        propsToChange.putString(propKey, updatedValue)
        node.update(propsToChange)

        val result = node.getProperty(propKey)
        assertEquals(updatedValue, result)
    }

    @Test
    fun `should apply all properties on build`() {
        val positionArray = arrayOf(4.0, 2.0, -5.0)
        val scaleArray = arrayOf(2.0, 2.0, 2.0)
        val properties = JavaOnlyMap.of(
                TransformNode.PROP_LOCAL_POSITION, JavaOnlyArray.of(*positionArray),
                TransformNode.PROP_LOCAL_SCALE, JavaOnlyArray.of(*scaleArray)
        )
        val node = createNode(properties)

        node.build()

        assertEquals(positionArray.toVector3(), node.localPosition)
        assertEquals(scaleArray.toVector3(), node.localScale)
    }

    @Test
    fun `should apply new properties on update`() {
        val node = createNode(JavaOnlyMap())
        node.build()
        val alignment = "bottom-right"
        val propsToUpdate = JavaOnlyMap.of(TransformNode.PROP_ALIGNMENT, alignment)

        node.update(propsToUpdate)

        assertEquals(Alignment.VerticalAlignment.BOTTOM, node.verticalAlignment)
        assertEquals(Alignment.HorizontalAlignment.RIGHT, node.horizontalAlignment)
    }

    @Test
    fun `get bounding should return content bounding with position offset`() {
        val positionProp = JavaOnlyArray.of(2.0, 5.0, 0.0)
        val props = JavaOnlyMap.of(TransformNode.PROP_LOCAL_POSITION, positionProp)
        val bounds = Bounding(-2F, -2F, 2F, 2F)
        val node = createNodeWithContentBounding(props, bounds)
        node.build()
        val expected = Bounding(0F, 3F, 4F, 7F)

        val result = node.getBounding()

        assertTrue(Bounding.equalInexact(expected, result))
    }

    @Test
    fun `should return correct bounding when rotated 90 degrees around Z`() {
        val position = JavaOnlyArray.of(0.0, 0.0, 0.0)
        val rotation = JavaOnlyArray.of(0.0, 0.0, 0.7071068, 0.7071068)
        val props = JavaOnlyMap.of(
                TransformNode.PROP_LOCAL_POSITION, position,
                TransformNode.PROP_LOCAL_ROTATION, rotation
        )
        val bounds = Bounding(0F, 0F, 2F, 1F)
        val node = createNodeWithContentBounding(props, bounds)
        node.build()
        val expected = Bounding(-1F, 0F, 0F, 2F)

        val result = node.getBounding()

        assertTrue(Bounding.equalInexact(expected, result))
    }

    @Test
    fun `should return zero-width bounding when rotated 90 degrees around Y`() {
        val position = JavaOnlyArray.of(0.0, 0.0, 0.0)
        val rotation = JavaOnlyArray.of(0, 0.7071068, 0, 0.7071068)
        val props = JavaOnlyMap.of(
                TransformNode.PROP_LOCAL_POSITION, position,
                TransformNode.PROP_LOCAL_ROTATION, rotation
        )
        val bounds = Bounding(-1F, -1F, 1F, 1F)
        val node = createNodeWithContentBounding(props, bounds)
        node.build()
        val expected = Bounding(0F, -1F, 0F, 1F)

        val result = node.getBounding()

        assertTrue(Bounding.equalInexact(expected, result))
    }

    @Test
    fun `should return zero-height bounding when rotated 90 degrees around X`() {
        val position = JavaOnlyArray.of(0.0, 0.0, 0.0)
        val rotation = JavaOnlyArray.of(0.7071068, 0, 0, 0.7071068)
        val props = JavaOnlyMap.of(
                TransformNode.PROP_LOCAL_POSITION, position,
                TransformNode.PROP_LOCAL_ROTATION, rotation
        )
        val bounds = Bounding(-1F, -1F, 1F, 1F)
        val node = createNodeWithContentBounding(props, bounds)
        node.build()
        val expected = Bounding(-1F, 0F, 1F, 0F)

        val result = node.getBounding()

        assertTrue(Bounding.equalInexact(expected, result))
    }

    @Test
    fun `should align content node when using content node alignment`() {
        val alignment = "top-right"
        val props = JavaOnlyMap.of(TransformNode.PROP_ALIGNMENT, alignment)
        val bounds = Bounding(-2F, -5F, 2F, 5F)
        val node = createNodeWithContentBounding(props, bounds)

        node.build()

        assertEquals(-2.0F, node.contentNode.localPosition.x, eps)
        assertEquals(-5.0F, node.contentNode.localPosition.y, eps)
        assertEquals(Vector3.zero(), node.localPosition)
    }

    @Test
    fun `should return proper content position`() {
        val localPosition = JavaOnlyArray.of(5.0, 3.0, 0.0)
        val props = JavaOnlyMap.of(
                TransformNode.PROP_LOCAL_POSITION, localPosition,
                TransformNode.PROP_ALIGNMENT, "bottom-right"
        )
        val bounds = Bounding(-2F, -1F, 2F, 1F)
        val node = createNodeWithContentBounding(props, bounds)
        node.build()

        val contentPosition = node.getContentPosition()

        assertEquals(3f, contentPosition.x, eps)
        assertEquals(4f, contentPosition.y, eps)
    }

    @Test
    fun `children should be added to content node`() {
        val node = createNode(JavaOnlyMap())
        val child1 = Node()
        val child2 = Node()

        node.addContent(child1)
        node.addContent(child2)

        assertTrue(node.contentNode.children.contains(child1))
        assertTrue(node.contentNode.children.contains(child2))
    }

    @Test
    fun `renderableRequested should be true after attaching renderable`() {
        val node = createNode(JavaOnlyMap())
        node.attachRenderable()
        assertTrue(node.renderableRequested)
    }

    private fun createNodeWithContentBounding(properties: ReadableMap, bounding: Bounding): TransformNode {
        return object : TransformNode(properties, false, true) {
            override fun getContentBounding(): Bounding {
                return bounding
            }
        }
    }

    private fun createNode(properties: ReadableMap): TransformNode {
        return object : TransformNode(properties, false, true) {}
    }

    private fun Array<Double>.toVector3(): Vector3 {
        return Vector3(this[0].toFloat(), this[1].toFloat(), this[2].toFloat())
    }

}