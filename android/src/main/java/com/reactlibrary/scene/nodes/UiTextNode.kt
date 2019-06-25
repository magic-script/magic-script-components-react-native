package com.reactlibrary.scene.nodes

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.UiNode
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.getStringSafely
import com.reactlibrary.utils.metersToPx

class UiTextNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.text, null)
        attachView(view, props)
        update(props, true)
    }

    override fun update(props: ReadableMap, useDefaults: Boolean) {
        super.update(props, useDefaults)
        val textView = view as TextView?
        textView?.setText(props)
        textView?.setTextSize(props)
    }

    private fun TextView.setText(props: ReadableMap) {
        val text = props.getStringSafely("text")
        if (text != null) {
            this.text = text
        }
    }

    private fun TextView.setTextSize(props: ReadableMap) {
        props.getDoubleSafely("textSize")?.let { textSize ->
            val size = metersToPx(textSize, context).toFloat()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

}