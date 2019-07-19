package com.reactlibrary.scene.nodes.base

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap

// Base class for layouts (grid, linear, rect)
abstract class UiLayout(props: ReadableMap) : TransformNode(props) {

    companion object {
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"

        private const val DEFAULT_WIDTH = 0.0
        private const val DEFAULT_HEIGHT = 0.0
    }

    // Zero or less means the dimensions fit to the content
    var width: Double = properties.getDouble(PROP_WIDTH, DEFAULT_WIDTH) // in meters
        private set

    var height: Double = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT) // in meters
        private set

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