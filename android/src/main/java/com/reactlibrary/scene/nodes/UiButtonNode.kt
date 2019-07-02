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

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.button, null)
    }

    override fun applyProperties(properties: Bundle, update: Boolean) {
        super.applyProperties(properties, update)
        setTitle(properties)
        setTextSize(properties, update)
        setRoundness(properties, update)
    }

    private fun setTitle(properties: Bundle) {
        val title = properties.getString(PROP_TITLE)
        if (title != null) {
            (view as Button).text = title
        }
    }

    private fun setTextSize(properties: Bundle, update: Boolean) {
        val buttonView = view as Button
        if (properties.containsKey(PROP_TEXT_SIZE)) {
            val textSize = properties.getDouble(PROP_TEXT_SIZE)
            val size = Utils.metersToPx(textSize, buttonView.context).toFloat()
            buttonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        } else if (!update) {  // set default value
            this.height?.let {
                val size = (it / 3).toFloat()
                buttonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            }
        }
    }

    // Sets the corners roundness (0 - sharp, 1 - fully rounded)
    private fun setRoundness(properties: Bundle, update: Boolean) {
        val background = view.background.current as GradientDrawable
        if (properties.containsKey(PROP_ROUNDNESS)) {
            val roundness = properties.getDouble(PROP_ROUNDNESS)
            // must be called to modify shared drawables loaded from resources
            background.mutate()
            background.cornerRadius = (roundness * 90).toFloat()
        } else if (!update) { // set default value
            background.mutate()
            background.cornerRadius = 90f // fully rounded by default
        }
    }

}