package com.reactlibrary.nodes

import android.os.Bundle
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransformNodeTest {

    @Test
    @UiThreadTest
    fun propertiesBundleShouldContainInitialProperties() {
        val propsMap = JavaOnlyMap()
        val localPosition = generateDoubleArray(3)
        propsMap.putArray(TransformNode.PROP_LOCAL_POSITION, localPosition)
        val localScale = generateDoubleArray(3)
        propsMap.putArray(TransformNode.PROP_LOCAL_SCALE, localScale)

        val node = object : TransformNode(propsMap, false) {
            // expose properties for test purposes
            fun getProps(): Bundle {
                return properties
            }

            override fun getBounding(): Bounding {
                return Utils.calculateBoundsOfNode(this)
            }
        }

        assertTrue(node.getProps().containsKey(TransformNode.PROP_LOCAL_POSITION))
        assertTrue(node.getProps().containsKey(TransformNode.PROP_LOCAL_SCALE))
    }

    private fun generateDoubleArray(elements: Int): JavaOnlyArray {
        val array = JavaOnlyArray()
        for (i in 0 until elements) {
            array.pushDouble(i.toDouble())
        }
        return array
    }
}