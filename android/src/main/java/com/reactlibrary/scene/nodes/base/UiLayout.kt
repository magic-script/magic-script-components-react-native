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
import com.reactlibrary.utils.Utils

// Base class for layouts (grid, linear, rect)
abstract class UiLayout(initProps: ReadableMap, protected val layoutManager: LayoutManager)
    : TransformNode(initProps, hasRenderable = false, useContentNodeAlignment = true), Layoutable {

    protected var width: Float = 0f
    protected var height: Float = 0f

    protected var maxChildHeight: Float = 0f
    protected var maxChildWidth: Float = 0f

    companion object {
        private const val MEASURE_INTERVAL = 50L // in milliseconds
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
    }

    // we should re-draw the grid after adding / removing a child
    var redrawRequested = false
        private set

    // "backed" children
    private val childrenList = mutableListOf<Node>()

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

    protected fun isSizeSet(): Boolean {
        return (width > 0 && height > 0)
    }

    override fun addContent(child: Node) {
        //contentNode.addChild(child)
        childrenList.add(child)
        redrawRequested = true
    }

    override fun removeContent(child: Node) {
        childrenList.remove(child)
        if (contentNode.children.contains(child)) {
            contentNode.removeChild(child)
        }
        childrenBounds.clear() // indexes changed
        redrawRequested = true
    }

    override fun setClipBounds(clipBounds: Bounding, clipNativeView: Boolean) {
        val localBounds = clipBounds.translate(-getContentPosition())
        contentNode.children
                .filterIsInstance<TransformNode>()
                .forEach { it.setClipBounds(localBounds, clipNativeView) }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    protected fun requestLayout() {
        redrawRequested = true
    }

    /**
     * Loop that requests re-drawing the grid if needed.
     * It measures the children, because the nodes' view size is not known
     * from the beginning, also a client may change the view size at any time: we need to
     * re-draw the layout in such case.
     */
    private fun layoutLoop() {
        measureChildren()
        rescaleChildren(childrenList)
        if (redrawRequested) {
            layoutManager.layoutChildren(childrenList, childrenBounds)
            // applyAlignment()
            redrawRequested = false

            // Attach the child after position is calculated
            childrenList.forEach { child ->
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
        for (i in 0 until childrenList.size) {
            val node = childrenList[i]
            val oldBounds = childrenBounds[i] ?: Bounding()
            childrenBounds[i] = if (node is TransformNode) {
                node.getBounding()
            } else {
                Utils.calculateBoundsOfNode(node)
            }
            if (!Bounding.equalInexact(childrenBounds[i]!!, oldBounds)) {
                redrawRequested = true
            }
        }
    }

    private fun rescaleChildren(nodes: List<Node>) {
        for (i in 0 until nodes.size) {
            val node = nodes[i]
            val nodeBounds = childrenBounds[i] ?: Bounding()
            val nodeWidth = nodeBounds.right - nodeBounds.left
            val nodeHeight = nodeBounds.top - nodeBounds.bottom
            if (width > 0 || height > 0) {
                node.localScale = if (maxChildWidth < nodeWidth && maxChildHeight < nodeHeight) {
                    val scale = if (nodeWidth > nodeHeight) {
                        maxChildWidth / nodeWidth
                    } else {
                        maxChildHeight / nodeHeight
                    }
                    Vector3(scale, scale, node.localScale.z)
                } else if (maxChildWidth < nodeWidth) {
                    val scale = maxChildWidth / nodeWidth
                    Vector3(scale, scale, node.localScale.z)
                } else if (maxChildHeight < nodeHeight) {
                    val scale = maxChildHeight / nodeHeight
                    Vector3(scale, scale, node.localScale.z)
                } else {
                    node.localScale
                }

            }
        }
    }

}