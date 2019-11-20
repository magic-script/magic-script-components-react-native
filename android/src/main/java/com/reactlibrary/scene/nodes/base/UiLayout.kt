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

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.logMessage
import java.lang.Float.min

// Base class for layouts (grid, linear, rect)
abstract class UiLayout(initProps: ReadableMap, protected val layoutManager: LayoutManager)
    : TransformNode(initProps, hasRenderable = false, useContentNodeAlignment = true), Layoutable {

    companion object {
        const val WRAP_CONTENT_DIMENSION = 0F

        private const val MEASURE_INTERVAL = 50L // in milliseconds
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
    }

    // "backed" children list, it may differ from [contentNode.children] because we
    // actually attach children with delay (when position for them is calculated)
    private val mChildrenList = mutableListOf<TransformNode>()
    val childrenList: List<TransformNode> = mChildrenList // expose immutable list

    var onAddedToLayoutListener: ((node: Node) -> Unit)? = null
    var onRemovedFromLayoutListener: ((node: Node) -> Unit)? = null

    protected var width: Float = WRAP_CONTENT_DIMENSION
    protected var height: Float = WRAP_CONTENT_DIMENSION

    protected var maxChildWidth: Float = Float.MAX_VALUE
    protected var maxChildHeight: Float = Float.MAX_VALUE

    // we should re-draw the grid after adding / removing a child
    var redrawRequested = false
        private set


    // <child index, bounding>
    private val childrenBounds = mutableMapOf<Int, Bounding>()

    private var handler = Handler(Looper.getMainLooper())
    private var loopStarted = false

    override fun build() {
        super.build()
        if (!loopStarted) {
            loopStarted = true
            layoutLoop()
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setLayoutSize(props)
    }

    protected open fun setLayoutSize(props: Bundle) {
        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            if (props.containsKey(PROP_WIDTH)) {
                width = props.getDouble(PROP_WIDTH).toFloat()
            }
            if (props.containsKey(PROP_HEIGHT)) {
                height = props.getDouble(PROP_HEIGHT).toFloat()
            }
            requestLayout()
        }
    }

    /**
     * For layouts child is actually added with delay,
     * after position for it is calculated.
     */
    override fun addContent(child: Node) {
        if (child is TransformNode) {
            mChildrenList.add(child)
            onAddedToLayoutListener?.invoke(child)
            redrawRequested = true
        } else {
            logMessage("Non transform nodes are not supported in layouts", true)
        }
    }

    override fun removeContent(child: Node) {
        mChildrenList.remove(child)
        if (contentNode.children.contains(child)) {
            contentNode.removeChild(child)
            onRemovedFromLayoutListener?.invoke(child)
        }
        childrenBounds.clear() // indexes changed
        redrawRequested = true
    }

    override fun setClipBounds(clipBounds: Bounding) {
        val localBounds = clipBounds.translate(-getContentPosition())
        contentNode.children
                .filterIsInstance<TransformNode>()
                .forEach { it.setClipBounds(localBounds) }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    protected fun requestLayout() {
        redrawRequested = true
    }

    /**
     * Loop that requests re-drawing the layout if needed.
     * It measures the children, because a client may change the layout's (or child) size
     * at any time: we need to re-draw the layout in such case.
     */
    private fun layoutLoop() {
        layoutManager.parentWidth = width
        layoutManager.parentHeight = height
        measureChildren()
        rescaleChildren(mChildrenList)
        if (redrawRequested) {
            layoutManager.layoutChildren(mChildrenList, childrenBounds)
            // applyAlignment()
            redrawRequested = false

            // Attach the child after position is calculated
            mChildrenList.forEach { child ->
                if (!contentNode.children.contains(child)) {
                    contentNode.addChild(child)
                }
            }
        }

        handler.postDelayed({
            layoutLoop()
        }, MEASURE_INTERVAL)
    }

    /**
     * Measures the bounds of children nodes; if any bound has changed
     * it sets the [redrawRequested] flag to true.
     */
    private fun measureChildren() {
        for (i in 0 until mChildrenList.size) {
            val node = mChildrenList[i]
            val oldBounds = childrenBounds[i] ?: Bounding()
            childrenBounds[i] = node.getBounding()

            if (!Bounding.equalInexact(childrenBounds[i]!!, oldBounds)) {
                redrawRequested = true
            }
        }
    }

    private fun rescaleChildren(children: List<TransformNode>) {
        for (i in children.indices) {
            val child = children[i]
            val childSize = (childrenBounds[i] ?: Bounding()).size()
            if (child.localScale.x > 0 && child.localScale.y > 0) {
                val childWidth = childSize.x / child.localScale.x
                val childHeight = childSize.y / child.localScale.y
                if (childWidth > 0 && childHeight > 0) {
                    val userSpecifiedScale = readUserSpecifiedScale(child)
                    val scaleX = min(maxChildWidth / childWidth, userSpecifiedScale.x)
                    val scaleY = min(maxChildHeight / childHeight, userSpecifiedScale.y)
                    val scaleXY = min(scaleX, scaleY) // scale saving width / height ratio
                    child.localScale = Vector3(scaleXY, scaleXY, child.localScale.z)
                }
            }
        }
    }

    private fun readUserSpecifiedScale(node: TransformNode): Vector3 {
        val scale = node.getProperty(PROP_LOCAL_SCALE) as? ArrayList<Double>
                ?: return Vector3.one()
        return Vector3(scale[0].toFloat(), scale[1].toFloat(), scale[2].toFloat())
    }

}