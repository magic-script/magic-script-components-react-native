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

package com.magicleap.magicscript

import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.nhaarman.mockitokotlin2.argThat
import org.mockito.ArgumentMatcher
import kotlin.test.assertTrue

class NodeBuilder {
    private var props = JavaOnlyMap()
    private var contentBounding = Bounding()

    fun withProps(initProps: JavaOnlyMap): NodeBuilder {
        props = initProps
        return this
    }

    fun withPosition(x: Double, y: Double, z: Double): NodeBuilder {
        val position = JavaOnlyArray.of(x, y, z)
        props.putArray(TransformNode.PROP_LOCAL_POSITION, position)
        return this
    }

    fun withScale(x: Double, y: Double, z: Double): NodeBuilder {
        val scale = JavaOnlyArray.of(x, y, z)
        props.putArray(TransformNode.PROP_LOCAL_SCALE, scale)
        return this
    }

    fun withRotation(x: Double, y: Double, z: Double, w: Double): NodeBuilder {
        val rotation = JavaOnlyArray.of(x, y, z, w)
        props.putArray(TransformNode.PROP_LOCAL_ROTATION, rotation)
        return this
    }

    fun withContentBounds(bounds: Bounding): NodeBuilder {
        contentBounding = bounds
        return this
    }

    fun withAlignment(alignment: String): NodeBuilder {
        props.putString(TransformNode.PROP_ALIGNMENT, alignment)
        return this
    }

    fun build(): TransformNode {
        val node = object : TransformNode(props, false, true) {
            override fun getContentBounding(): Bounding {
                return contentBounding
            }

            override fun onVisibilityChanged(visibility: Boolean) {}
        }
        node.build()
        return node
    }
}

fun createProperty(vararg keysAndValues: Any): Bundle =
    Arguments.toBundle(JavaOnlyMap.of(*keysAndValues)) ?: Bundle()

fun View.createActionDownEvent(): MotionEvent {
    val coordinates = IntArray(2)
    getLocationOnScreen(coordinates)
    return MotionEvent.obtain(
        SystemClock.uptimeMillis(),
        SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN,
        coordinates[0].toFloat(),
        coordinates[1].toFloat(),
        0
    )
}

fun <T : TransformNode> T.update(vararg keysAndValues: Any) {
    update(JavaOnlyMap.of(*keysAndValues))
}

// region Custom matchers

infix fun Bounding.shouldEqualInexact(other: Bounding) =
    assertTrue(Bounding.equalInexact(this, other), "expected: $other, but was: $this")

fun matchesInexact(bounds: Bounding) = argThat(
    ArgumentMatcher<Bounding> { argument ->
        Bounding.equalInexact(argument, bounds)
    }
)

// endregion

