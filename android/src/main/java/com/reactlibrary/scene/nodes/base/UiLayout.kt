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

import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

// Base class for layouts (grid, linear, rect)
abstract class UiLayout(initProps: ReadableMap)
    : TransformNode(initProps, hasRenderable = false, useContentNodeAlignment = true) {

    companion object {
        private const val MEASURE_INTERVAL = 100L // in milliseconds
    }

    var layoutManager: LayoutManager? = null

    // we should re-draw the grid after adding / removing a child
    private var shouldRedraw = false

    // child index, bounding
    private val childrenBounds = mutableMapOf<Int, Bounding>()

    private var handler = Handler(Looper.getMainLooper())

    init {
        layoutLoop()
    }

    fun requestLayout() {
        shouldRedraw = true
    }

    override fun addContent(child: Node) {
        contentNode.addChild(child)
        shouldRedraw = true
    }

    /**
     * Loop that requests re-drawing the grid if needed.
     * It measures the children, because the nodes' view size is not known
     * from the beginning, also a client may change the view size at any time: we need to
     * re-draw the layout in such case.
     */
    private fun layoutLoop() {
        handler.postDelayed({
            measureChildren()
            if (shouldRedraw) {
                layoutManager?.layoutChildren(contentNode.children, childrenBounds)
                // applyAlignment()
                shouldRedraw = false
                logMessage("grid redraw")
            }
            layoutLoop()
        }, MEASURE_INTERVAL)
    }

    /**
     * Measures the bounds of children nodes; if any bound has changed
     * it sets the [shouldRedraw] flag to true.
     */
    private fun measureChildren() {
        for (i in 0 until contentNode.children.size) {
            val node = contentNode.children[i]
            val oldBounds = childrenBounds[i] ?: Bounding()
            childrenBounds[i] = if (node is TransformNode) {
                node.getBounding()
            } else {
                Utils.calculateBoundsOfNode(node)
            }

            if (!Bounding.equalInexact(childrenBounds[i]!!, oldBounds)) {
                shouldRedraw = true
            }
        }
    }

    override fun setClipBounds(clipBounds: RectF) {
        for (i in 0 until contentNode.children.size) {
            val child = contentNode.children[i]
            val contentClip = RectF(
                    clipBounds.left - contentNode.localPosition.x,
                    clipBounds.top - contentNode.localPosition.y,
                    clipBounds.right - contentNode.localPosition.x,
                    clipBounds.bottom - contentNode.localPosition.y)

            if (child is TransformNode) {
                val childClip = RectF(
                        contentClip.left - child.localPosition.x,
                        contentClip.top - child.localPosition.y,
                        contentClip.right - child.localPosition.x,
                        contentClip.bottom - child.localPosition.y)
                child.setClipBounds(childClip)
            }
        }
    }

}
