package com.reactlibrary.scene.nodes.base

import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

// Base class for layouts (grid, linear, rect)
abstract class UiLayout(props: ReadableMap)
    : TransformNode(props, false) {

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

    override fun addChildNode(child: Node) {
        addChild(child)
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
                layoutManager?.layoutChildren(children, childrenBounds)
                shouldRedraw = false
                logMessage("grid redraw")
            }
            layoutLoop()
        }, 100)
    }

    /**
     * Measures the bounds of children nodes; if any bound has changed
     * it sets the [shouldRedraw] flag to true.
     */
    private fun measureChildren() {
        for (i in 0 until children.size) {
            val node = children[i]
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

}