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
import com.reactlibrary.scene.nodes.base.TransformNode
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

    @Test
    fun shouldContainInitialProperties() {
        val property = "key"
        val propsMap = JavaOnlyMap()
        propsMap.putString(property, "value")

        val node = object : TransformNode(propsMap, false, true) {}

        assertNotNull(node.getProperty(property))
    }

    @Test
    fun shouldAddNewPropertyWhenPassedInUpdate() {
        val initialProperty = "initialProp"
        val initialProps = JavaOnlyMap()
        initialProps.putString(initialProperty, "value")
        val node = object : TransformNode(initialProps, false, true) {}

        val newProps = JavaOnlyMap()
        val propertyToAdd = "propToAdd"
        newProps.putString(propertyToAdd, "value")
        node.update(newProps)

        assertNotNull(node.getProperty(initialProperty))
        assertNotNull(node.getProperty(propertyToAdd))
    }

    @Test
    fun shouldSaveNewPropertyValueWhenUpdated() {
        val propKey = "key"
        val initialValue = "value"
        val initialProps = JavaOnlyMap()
        initialProps.putString(propKey, initialValue)
        val node = object : TransformNode(initialProps, false, true) {}

        val updatedValue = "updated value"
        val propsToChange = JavaOnlyMap()
        propsToChange.putString(propKey, updatedValue)
        node.update(propsToChange)

        val result = node.getProperty(propKey)
        assertEquals(updatedValue, result)
    }

    @Test
    fun shouldApplyAllPropertiesOnBuild() {
        val positionArray = arrayOf(4.0, 2.0, -5.0)
        val scaleArray = arrayOf(2.0, 2.0, 2.0)
        val properties = JavaOnlyMap.of(
                TransformNode.PROP_LOCAL_POSITION, JavaOnlyArray.of(*positionArray),
                TransformNode.PROP_LOCAL_SCALE, JavaOnlyArray.of(*scaleArray)
        )
        val node = object : TransformNode(properties, false, true) {}

        node.build()

        assertEquals(positionArray.toVector3(), node.localPosition)
        assertEquals(scaleArray.toVector3(), node.localScale)
    }

    @Test
    fun shouldApplyNewPropertiesOnUpdate() {
        val node = object : TransformNode(JavaOnlyMap(), false, true) {}
        node.build()
        val alignment = "bottom-right"
        val propsToUpdate = JavaOnlyMap.of(TransformNode.PROP_ALIGNMENT, alignment)

        node.update(propsToUpdate)

        assertEquals(Alignment.VerticalAlignment.BOTTOM, node.verticalAlignment)
        assertEquals(Alignment.HorizontalAlignment.RIGHT, node.horizontalAlignment)
    }

    @Test
    fun getBoundingShouldReturnContentBoundingWithPositionOffset() {
        val positionProp = JavaOnlyArray.of(2.0, 5.0, 0.0)
        val props = JavaOnlyMap.of(TransformNode.PROP_LOCAL_POSITION, positionProp)
        val bounds = Bounding(-2F, -2F, 2F, 2F)
        val node = createNodeWithBounding(props, bounds, useContentNodeAlignment = true)
        node.build()
        val expected = Bounding(
                node.localPosition.x + bounds.left,
                node.localPosition.y + bounds.bottom,
                node.localPosition.x + bounds.right,
                node.localPosition.y + bounds.top
        )

        val result = node.getBounding()

        assertEquals(expected, result)
    }

    @Test
    fun shouldAlignContentNodeWhenUsingContentNodeAlignment() {
        val alignment = "top-right"
        val props = JavaOnlyMap.of(TransformNode.PROP_ALIGNMENT, alignment)
        val bounds = Bounding(-2F, -5F, 2F, 5F)
        val node = createNodeWithBounding(props, bounds, useContentNodeAlignment = true)

        node.build()

        val epsilon = 0.0001F
        assertEquals(-2.0F, node.contentNode.localPosition.x, epsilon)
        assertEquals(-5.0F, node.contentNode.localPosition.y, epsilon)
        assertEquals(Vector3.zero(), node.localPosition)
    }

    @Test
    fun shouldChildrenBeAddedToContentNode() {
        val node = object : TransformNode(JavaOnlyMap(), false, true) {}
        val child1 = Node()
        val child2 = Node()

        node.addContent(child1)
        node.addContent(child2)

        assertTrue(node.contentNode.children.contains(child1))
        assertTrue(node.contentNode.children.contains(child2))
    }

    @Test
    fun shouldRenderableRequestedBeTrueAfterAttachingRenderable() {
        val node = object : TransformNode(JavaOnlyMap(), false, true) {}
        node.attachRenderable()
        assertTrue(node.renderableRequested)
    }

    private fun Array<Double>.toVector3(): Vector3 {
        return Vector3(this[0].toFloat(), this[1].toFloat(), this[2].toFloat())
    }

    private fun createNodeWithBounding(
            properties: ReadableMap,
            bounding: Bounding,
            useContentNodeAlignment: Boolean): TransformNode {

        return object : TransformNode(properties, false, useContentNodeAlignment) {
            override fun getContentBounding(): Bounding {
                return bounding
            }
        }
    }

}