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

package com.magicleap.magicscript.scene.nodes.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.magicleap.magicscript.scene.nodes.layouts.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.read

// Base class for layouts (grid, linear, rect)
abstract class UiBaseLayout<T : LayoutParams>(
    initProps: ReadableMap,
    protected val layoutManager: LayoutManager<T>
) : TransformNode(initProps, hasRenderable = false, useContentNodeAlignment = true), Layoutable {

    companion object {
        const val WRAP_CONTENT_DIMENSION = 0F

        private const val MEASURE_INTERVAL = 50L // in milliseconds
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_SKIP_INVISIBLE_ITEMS = "skipInvisibleItems"
    }

    // "backed" children list, it may differ from [contentNode.children] because we
    // actually attach children with delay (when position for them is calculated)
    private val mChildrenList = mutableListOf<TransformNode>()
    val childrenList: List<TransformNode> = mChildrenList // expose immutable list

    var onAddedToLayoutListener: ((node: Node) -> Unit)? = null
    var onRemovedFromLayoutListener: ((node: Node) -> Unit)? = null

    val width: Float
        get() = properties.getDouble(PROP_WIDTH, 0.0).toFloat()

    val height: Float
        get() = properties.getDouble(PROP_HEIGHT, 0.0).toFloat()

    private var skipInvisibleItems = false

    // we should re-draw the grid after adding / removing a child
    private var redrawRequested = false

    private val childrenBounds = mutableMapOf<TransformNode, AABB>()

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

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            redrawRequested = true
        }

        setSkipInvisibleItems(props)
    }

    // We should access children via [childrenList], because they may not have
    // been added yet to [contentNode]
    override fun onVisibilityChanged(visibility: Boolean) {
        childrenList
            .forEach { child ->
                if (visibility) {
                    child.show()
                } else {
                    child.hide()
                }
            }
    }

    /**
     * For layouts child is actually added with delay,
     * after position for it is calculated.
     */
    override fun addContent(child: TransformNode) {
        if (!isVisible) {
            child.hide()
        }
        child.addVisibilityListener(object : VisibilityChangedListener {
            override fun invoke(visible: Boolean) {
                if (skipInvisibleItems) {
                    requestLayout()
                }
            }
        })
        mChildrenList.add(child)
        onAddedToLayoutListener?.invoke(child)
        redrawRequested = true
    }

    override fun removeContent(child: TransformNode) {
        mChildrenList.remove(child)
        if (contentNode.children.contains(child)) {
            contentNode.removeChild(child)
            onRemovedFromLayoutListener?.invoke(child)
        }
        childrenBounds.remove(child)
        redrawRequested = true
    }

    abstract fun getLayoutParams(): T

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
        measureChildren()
        if (redrawRequested) {
            layout()
        }

        handler.postDelayed({
            layoutLoop()
        }, MEASURE_INTERVAL)
    }

    private fun layout() {
        layoutManager.layoutChildren(getLayoutParams(), getChildrenToLayout(), childrenBounds)
        redrawRequested = false

        // Attach the child after position is calculated
        mChildrenList
            .filter { it !in contentNode.children }
            .forEach { contentNode.addChild(it) }

        // need to clip materials, because content position has changed
        clipChildren()
    }

    /**
     * Measures the bounds of children nodes; if any bound has changed
     * it sets the [redrawRequested] flag to true.
     */
    private fun measureChildren() {
        val childrenToLayout = getChildrenToLayout()

        for (node in childrenToLayout) {
            val oldBounds = childrenBounds[node] ?: AABB()
            val newBounds = node.getBounding()
            childrenBounds[node] = newBounds

            if (!newBounds.equalInexact(oldBounds)) {
                redrawRequested = true
            }
        }
    }

    private fun getChildrenToLayout(): List<TransformNode> {
        return if (skipInvisibleItems) {
            childrenList.filter { it.isVisible }
        } else {
            childrenList
        }
    }

    private fun setSkipInvisibleItems(props: Bundle) {
        props.read<Boolean>(PROP_SKIP_INVISIBLE_ITEMS)?.let { skip ->
            this.skipInvisibleItems = skip
            requestLayout()
        }
    }

    class Test<T : LayoutParams>(val layoutNode: UiBaseLayout<T>) {
        fun forceLayout() {
            layoutNode.layout()
        }
    }

}