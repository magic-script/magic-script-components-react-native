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

import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.plus

// Base class for layouts (grid, linear, rect)
abstract class UiLayout(initProps: ReadableMap, protected val layoutManager: LayoutManager)
    : TransformNode(initProps, hasRenderable = false, useContentNodeAlignment = true) {

    companion object {
        private const val MEASURE_INTERVAL = 100L // in milliseconds
    }

    // we should re-draw the grid after adding / removing a child
    var redrawRequested = false
        private set

    // child index, bounding
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

    override fun addContent(child: Node) {
        contentNode.addChild(child)
        redrawRequested = true
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
        if (redrawRequested) {
            layoutManager.layoutChildren(contentNode.children, childrenBounds)
            // applyAlignment()
            redrawRequested = false
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
        for (i in 0 until contentNode.children.size) {
            val node = contentNode.children[i]
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

    override fun contentTranslation(): Vector3 {
        return Vector3()
    }

    override fun setClipBounds(clipBounds: Bounding) {
        for (i in 0 until contentNode.children.size) {

            val child = contentNode.children[i]
            if (child is TransformNode) {

                val offset = contentNode.localPosition + child.localPosition
                val childClip = Bounding(
                        clipBounds.left - offset.x,
                        clipBounds.top - offset.y,
                        clipBounds.right - offset.x,
                        clipBounds.bottom - offset.y)
                child.setClipBounds(childClip)
            }
        }
    }
}
