package com.reactlibrary.scene.nodes

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils

class UiButtonNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TITLE = "title"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_ROUNDNESS = "roundness"
    }

    init {

        // Set default properties

        if (!properties.containsKey(PROP_ROUNDNESS)) {
            properties.putDouble(PROP_ROUNDNESS, 1.0)
        }

        if (!properties.containsKey(PROP_TEXT_SIZE)) {
            // calculate default text size based on button height
            if (properties.containsKey(PROP_HEIGHT)) {
                val textSize = properties.getDouble(PROP_HEIGHT) / 3
                properties.putDouble(PROP_TEXT_SIZE, textSize)
            }
        }
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.button, null)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setTitle(props)
        setTextSize(props)
        setRoundness(props)
    }

    private fun setTitle(props: Bundle) {
        val title = props.getString(PROP_TITLE)
        if (title != null) {
            (view as Button).text = title
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val textSize = props.getDouble(PROP_TEXT_SIZE)
            val size = Utils.metersToPx(textSize, view.context).toFloat()
            (view as Button).setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    // Sets the corners roundness (0 - sharp, 1 - fully rounded)
    private fun setRoundness(props: Bundle) {
        val background = view.background.current as GradientDrawable
        if (props.containsKey(PROP_ROUNDNESS)) {
            val roundness = props.getDouble(PROP_ROUNDNESS)
            // must be called to modify shared drawables loaded from resources
            background.mutate()
            background.cornerRadius = (roundness * 90).toFloat()
        }
    }

}