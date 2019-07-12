package com.reactlibrary.scene.nodes.base

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node

// Base class for layouts (grid, linear, rect)
abstract class UiLayout(props: ReadableMap) : TransformNode(props) {

    companion object {
        /**
         * Setting a size of zero or less in either the X or Y dimension
         * indicates the layout should grow to fit content in that
         * dimension.
         */
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
    }

    /**
     * Add child via this method to be laid out correctly
     */
    abstract fun addChildToLayout(child: Node)
}