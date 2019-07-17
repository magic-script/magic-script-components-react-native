package com.reactlibrary.scene.nodes.base

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap

// Base class for layouts (grid, linear, rect)
abstract class UiLayout(props: ReadableMap) : TransformNode(props) {

    companion object {
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
    }

    // Zero or less means the dimensions fits to the content
    protected var width: Double = 0.0 // in meters
    protected var height: Double = 0.0 // in meters

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setSize(props)
    }

    private fun setSize(props: Bundle) {
        if (props.containsKey(PROP_WIDTH)) {
            this.width = props.getDouble(PROP_WIDTH)
        }

        if (props.containsKey(PROP_HEIGHT)) {
            this.height = props.getDouble(PROP_HEIGHT)
        }
    }

}