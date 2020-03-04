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

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.clip.TextureClipper
import com.magicleap.magicscript.ar.clip.UiNodeClipper
import com.magicleap.magicscript.ar.clip.UiNodeColliderClipper
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.layouts.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.equalInexact
import com.magicleap.magicscript.utils.isCloseTo
import com.nhaarman.mockitokotlin2.mock
import kotlin.test.assertTrue

private const val EPSILON = 1e-5f

open class NodeBuilder(protected val useContentNodeAlignment: Boolean = true) {
    protected var props = JavaOnlyMap()

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

    fun withAlignment(alignment: String): NodeBuilder {
        props.putString(TransformNode.PROP_ALIGNMENT, alignment)
        return this
    }

    fun withAnchorUUID(anchorUUID: String): NodeBuilder {
        props.putString(TransformNode.PROP_ANCHOR_UUID, anchorUUID)
        return this
    }

    open fun build(): TransformNode {
        val node = object : TransformNode(props, false, useContentNodeAlignment) {}
        node.build()
        return node
    }
}

class UiNodeBuilder(
    private val context: Context,
    useContentNodeAlignment: Boolean = false,
    private val viewRenderableLoader: ViewRenderableLoader? = null,
    private val nodeClipper: Clipper? = null
) : NodeBuilder(useContentNodeAlignment) {
    private var width = UiNode.WRAP_CONTENT_DIMENSION
    private var height = UiNode.WRAP_CONTENT_DIMENSION

    fun withSize(width: Float, height: Float): UiNodeBuilder {
        this.width = width
        this.height = height
        return this
    }

    override fun build(): UiNode {
        val renderableLoader = viewRenderableLoader ?: mock()
        val clipper = nodeClipper ?: UiNodeClipper(TextureClipper(), UiNodeColliderClipper())
        val node = object :
            UiNode(props, context, renderableLoader, clipper, useContentNodeAlignment) {

            override fun provideView(context: Context): View {
                return View(context)
            }

            override fun provideDesiredSize(): Vector2 {
                return Vector2(width, height)
            }
        }
        node.build()
        return node
    }
}

fun JavaOnlyMap.toBundle(): Bundle {
    return Arguments.toBundle(this) ?: Bundle()
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

infix fun Float.shouldEqualInexact(other: Float) {
    val equal = isCloseTo(other, EPSILON)
    assertTrue(equal, "expected: $other, but was: $this")
}

infix fun Vector2.shouldEqualInexact(other: Vector2) {
    val equal = x.isCloseTo(other.x, EPSILON) && y.isCloseTo(other.y, EPSILON)
    assertTrue(equal, "expected: $other, but was: $this")
}

infix fun Vector3.shouldEqualInexact(other: Vector3) {
    val equal = equalInexact(other, EPSILON)
    assertTrue(equal, "expected: $other, but was: $this")
}

infix fun Bounding.shouldEqualInexact(other: Bounding) =
    assertTrue(equalInexact(other), "expected: $other, but was: $this")

infix fun AABB.shouldEqualInexact(other: AABB) =
    assertTrue(equalInexact(other), "expected: $other, but was: $this")

fun TransformNode.forceUpdate(deltaSeconds: Float) {
    TransformNode.Test(this).forceUpdate(deltaSeconds)
}

fun UiNode.performClick() {
    UiNode.Test(this).performClick()
}

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
    childrenBounds: MutableMap<TransformNode, AABB>,
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
    childrenBounds: MutableMap<TransformNode, AABB>
): Boolean {
    var changed = false
    for (node in childrenList) {
        val oldBounds = childrenBounds[node] ?: AABB()
        val newBounds = node.getBounding()
        childrenBounds[node] = newBounds

        if (!newBounds.equalInexact(oldBounds)) {
            changed = true
        }
    }
    return changed
}