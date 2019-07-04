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

open class UiTextNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        protected const val PROP_TEXT = "text"
        protected const val PROP_TEXT_SIZE = "textSize"
        protected const val PROP_ALL_CAPS = "allCaps"
        protected const val PROP_CHARACTER_SPACING = "charSpacing"
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text, null)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setText(props)
        setTextSize(props)
        setAllCaps(props)
        setCharacterSpacing(props)
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            (view as TextView).text = text
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE)
            val size = Utils.metersToPx(sizeMeters, view.context).toFloat()
            (view as TextView).setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun setAllCaps(props: Bundle) {
        if (props.containsKey(PROP_ALL_CAPS)) {
            (view as TextView).isAllCaps = props.getBoolean(PROP_ALL_CAPS)
        }
    }

    private fun setCharacterSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTER_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTER_SPACING)
            (view as TextView).letterSpacing = spacing.toFloat()
        }
    }

}