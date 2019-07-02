package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils

class UiTextNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_ALL_CAPS = "allCaps"
        private const val PROP_CHARACTER_SPACING = "charSpacing"
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text, null)
    }

    override fun applyProperties(properties: Bundle, update: Boolean) {
        super.applyProperties(properties, update)
        setText(properties) // currently text value is available only on update
        setTextSize(properties)
        setAllCaps(properties)
        setCharacterSpacing(properties)
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            (view as TextView).text = text
        }
    }

    private fun setTextSize(properties: Bundle) {
        if (properties.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = properties.getDouble(PROP_TEXT_SIZE)
            val size = Utils.metersToPx(sizeMeters, view.context).toFloat()
            (view as TextView).setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }

    }

    private fun setAllCaps(properties: Bundle) {
        if (properties.containsKey(PROP_ALL_CAPS)) {
            (view as TextView).isAllCaps = properties.getBoolean(PROP_ALL_CAPS)
        }
    }

    private fun setCharacterSpacing(properties: Bundle) {
        if (properties.containsKey(PROP_CHARACTER_SPACING)) {
            val spacing = properties.getDouble(PROP_CHARACTER_SPACING)
            (view as TextView).letterSpacing = spacing.toFloat()
        }
    }

}