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

package com.magicleap.magicscript.scene.nodes.base

import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.*
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
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
    fun `should be located at zero position by default`() {
        val node = NodeBuilder().build()

        assertEquals(Vector3.zero(), node.localPosition)
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
            .withRotation(0.0, 0.42, 0.0, 0.9)
            .build()

        assertEquals(Vector3(4f, 2f, -5f), node.localPosition)
        assertEquals(Vector3(2f, 2f, 2f), node.localScale)
        assertEquals(Quaternion(0.0f, 0.42f, 0.0f, 0.9f), node.localRotation)
    }

    @Test
    fun `should apply new properties on update`() {
        val node = NodeBuilder().build()
        val alignment = "bottom-right"
        val propsToUpdate = reactMapOf(TransformNode.PROP_ALIGNMENT, alignment)

        node.update(propsToUpdate)

        assertEquals(Alignment.Vertical.BOTTOM, node.verticalAlignment)
        assertEquals(Alignment.Horizontal.RIGHT, node.horizontalAlignment)
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
    fun `should return proper React parent when child added to content node`() {
        val parent = NodeBuilder().build()
        val child = NodeBuilder().build()

        parent.addContent(child)

        child.reactParent shouldEqual parent
    }

    @Test
    fun `should apply clip bounds on the node and children nodes`() {
        val node = NodeBuilder().build()
        val child1 = NodeBuilder().build()
        val child2 = NodeBuilder().build()
        node.addContent(child1)
        node.addContent(child2)
        val clipBounds = AABB(min = Vector3.zero(), max = Vector3.one())

        node.clipBounds = clipBounds

        node.clipBounds shouldEqual clipBounds
        child1.clipBounds shouldEqual clipBounds
        child2.clipBounds shouldEqual clipBounds
    }

    @Test
    fun `should apply clipping on newly added child when clip bounds are set`() {
        val node = NodeBuilder().build()
        val clipBounds = AABB(min = Vector3.zero(), max = Vector3.one())
        node.clipBounds = clipBounds

        val child = NodeBuilder().build()
        node.addContent(child)

        child.clipBounds shouldEqual clipBounds
    }

    @Test
    fun `should apply shifted clipping on child when local position changed`() {
        val node = NodeBuilder().build()
        val clipBounds = AABB(min = Vector3(0f, 0f, 0f), max = Vector3(1f, 1f, 1f))
        val expectedChildBounds = AABB(min = Vector3(3f, 0f, 0f), max = Vector3(4f, 1f, 1f))
        val child = NodeBuilder().build()
        node.addContent(child)
        node.clipBounds = clipBounds

        node.localPosition = Vector3(-3f, 0f, 0f)

        child.clipBounds shouldEqual expectedChildBounds
    }

    @Test
    fun `should hide the node when visible property is false`() {
        val node = NodeBuilder()
            .withProps(reactMapOf(TransformNode.PROP_VISIBLE, false))
            .build()

        node.isVisible shouldBe false
    }

    @Test
    fun `should show the node when visible property updated to true`() {
        val node = NodeBuilder()
            .withProps(reactMapOf(TransformNode.PROP_VISIBLE, false))
            .build()

        node.update(reactMapOf(TransformNode.PROP_VISIBLE, true))

        node.isVisible shouldBe true
    }

    @Test
    fun `should call the transform changed listener when assigned new position`() {
        var listenerCalled = false
        val node = NodeBuilder()
            .withPosition(2.0, 1.5, -1.0)
            .build()
        node.addOnLocalTransformChangedListener(object : TransformAwareNode.LocalTransformListener {
            override fun onTransformed() {
                listenerCalled = true
            }
        })

        node.update(reactMapOf().localPosition(Vector3(3f, 4f, 5f)))

        listenerCalled shouldBe true
    }


    @Test
    fun `should not call the transform changed listener when assigned same position`() {
        var listenerCalled = false
        val node = NodeBuilder()
            .withPosition(2.0, 1.5, -1.0)
            .build()
        node.addOnLocalTransformChangedListener(object : TransformAwareNode.LocalTransformListener {
            override fun onTransformed() {
                listenerCalled = true
            }
        })

        node.update(reactMapOf().localPosition(Vector3(2f, 1.5f, -1f)))

        listenerCalled shouldBe false
    }

    @Test
    fun `should call the transform changed listener when assigned new scale`() {
        var listenerCalled = false
        val node = NodeBuilder()
            .withScale(2.05, 1.33, 1.0)
            .build()
        node.addOnLocalTransformChangedListener(object : TransformAwareNode.LocalTransformListener {
            override fun onTransformed() {
                listenerCalled = true
            }
        })

        node.update(reactMapOf().localScale(3.0, 3.0, 1.0))

        listenerCalled shouldBe true
    }

    @Test
    fun `should not call the transform changed listener when assigned same scale`() {
        var listenerCalled = false
        val node = NodeBuilder()
            .withScale(2.05, 1.33, 1.0)
            .build()
        node.addOnLocalTransformChangedListener(object : TransformAwareNode.LocalTransformListener {
            override fun onTransformed() {
                listenerCalled = true
            }
        })

        node.update(reactMapOf().localScale(2.05, 1.33, 1.0))

        listenerCalled shouldBe false
    }

    @Test
    fun `should call the transform changed listener when assigned new rotation`() {
        var listenerCalled = false
        val node = NodeBuilder()
            .withRotation(0.0, 0.9999383, 0.0, 0.0111104)
            .build()
        node.addOnLocalTransformChangedListener(object : TransformAwareNode.LocalTransformListener {
            override fun onTransformed() {
                listenerCalled = true
            }
        })

        node.update(reactMapOf().localRotation(0.04, 0.0, 0.0, 0.9))

        listenerCalled shouldBe true
    }

    @Test
    fun `should not call the transform changed listener when assigned same rotation`() {
        var listenerCalled = false
        val node = NodeBuilder()
            .withRotation(0.0, 0.9999383, 0.0, 0.0111104)
            .build()
        node.addOnLocalTransformChangedListener(object : TransformAwareNode.LocalTransformListener {
            override fun onTransformed() {
                listenerCalled = true
            }
        })

        node.update(reactMapOf().localRotation(0.0, 0.9999383, 0.0, 0.0111104))

        listenerCalled shouldBe false
    }


}