package com.reactlibrary.nodes

import android.os.Bundle
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.TransformNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 * [JavaOnlyMap] was not available in older versions of React
 */
@RunWith(AndroidJUnit4::class)
class TransformNodeTest {

    @Test
    @UiThreadTest
    fun shouldContainInitialProperties() {
        val property = "key"
        val propsMap = JavaOnlyMap()
        propsMap.putString(property, "value")

        val node = TestNode(propsMap)

        assertTrue(node.getProps().containsKey(property))
    }

    @Test
    @UiThreadTest
    fun shouldAddNewPropertyWhenPassedInUpdate() {
        val initialProperty = "initialProp"
        val initialProps = JavaOnlyMap()
        initialProps.putString(initialProperty, "value")
        val node = TestNode(initialProps)

        val newProps = JavaOnlyMap()
        val propertyToAdd = "propToAdd"
        newProps.putString(propertyToAdd, "value")
        node.update(newProps)

        assertTrue(node.getProps().containsKey(initialProperty))
        assertTrue(node.getProps().containsKey(propertyToAdd))
    }

    @Test
    @UiThreadTest
    fun shouldSaveNewPropertyValueWhenUpdated() {
        val propKey = "key"
        val initialValue = "value"
        val initialProps = JavaOnlyMap()
        initialProps.putString(propKey, initialValue)
        val node = TestNode(initialProps)

        val updatedValue = "updated value"
        val propsToChange = JavaOnlyMap()
        propsToChange.putString(propKey, updatedValue)
        node.update(propsToChange)

        val result = node.getProps().getString(propKey)
        assertEquals(updatedValue, result)
    }

    companion object {
        class TestNode(props: ReadableMap) : TransformNode(props, false) {
            // exposing properties for test purposes
            fun getProps(): Bundle {
                return properties
            }
        }
    }

}