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

class UiTextEditNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_CHARACTER_SPACING = "charSpacing"
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text_edit, null)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setText(props)
        setTextSize(props)
        setCharacterSpacing(props)
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            view.findViewById<TextView>(R.id.text_edit).text = text
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE)
            val size = Utils.metersToPx(sizeMeters, view.context).toFloat()
            val hintTv = view.findViewById<TextView>(R.id.text_edit_hint)
            val editTv = view.findViewById<TextView>(R.id.text_edit)
            editTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            hintTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun setCharacterSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTER_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTER_SPACING)
            view.findViewById<TextView>(R.id.text_edit).letterSpacing = spacing.toFloat()
        }
    }

}