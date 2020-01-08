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

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.manager.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
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
        val position = reactArrayOf(x, y, z)
        props.putArray(TransformNode.PROP_LOCAL_POSITION, position)
        return this
    }

    fun withScale(x: Double, y: Double, z: Double): NodeBuilder {
        val scale = reactArrayOf(x, y, z)
        props.putArray(TransformNode.PROP_LOCAL_SCALE, scale)
        return this
    }

    fun withRotation(x: Double, y: Double, z: Double, w: Double): NodeBuilder {
        val rotation = reactArrayOf(x, y, z, w)
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
        }
        node.build()
        return node
    }
}

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
    update(reactMapOf(*keysAndValues))
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

/**
 * Calls [LayoutManager.layoutChildren] on a [layoutManager] until
 * bounds of all children are stable. It usually takes some iterations, depending on children
 * number, until all children are positioned correctly in a layout and their
 * bounds do not change anymore.
 *
 * We have to use this method in tests instead of [LayoutManager.layoutChildren],
 * because we don't use the standard layout loop in tests.
 *
 * @param maxIterations maximum number of layout iterations
 * @throws RuntimeException when [maxIterations] number is not big enough
 * in order to bounds were stable
 */
fun <T : LayoutParams> LayoutManager<T>.layoutUntilStableBounds(
    childrenList: List<TransformNode>,
    childrenBounds: MutableMap<Int, Bounding>,
    layoutParams: T,
    maxIterations: Int
) {
    var iterations = 0
    do {
        iterations++
        if (iterations > maxIterations) {
            throw RuntimeException("maxIterations is not enough in order to bounds were stable")
        }
        val boundsChanged = measureChildren(childrenList, childrenBounds)
        layoutChildren(layoutParams, childrenList, childrenBounds)
    } while (boundsChanged)
}

/**
 * Measures all children bounds and return true if bounding of any child
 * has changed or false otherwise
 *
 * @param childrenList children list
 * @param childrenBounds current children bounds map (pass an empty map)
 */
fun measureChildren(
    childrenList: List<TransformNode>,
    childrenBounds: MutableMap<Int, Bounding>
): Boolean {
    var changed = false
    for (i in childrenList.indices) {
        val node = childrenList[i]
        val oldBounds = childrenBounds[i] ?: Bounding()
        childrenBounds[i] = node.getBounding()

        if (!Bounding.equalInexact(childrenBounds[i]!!, oldBounds)) {
            changed = true
        }
    }
    return changed
}