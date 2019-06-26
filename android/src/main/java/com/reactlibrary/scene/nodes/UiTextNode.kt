package com.reactlibrary.scene.nodes

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.UiNode
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.getStringSafely
import com.reactlibrary.utils.metersToPx

class UiTextNode(context: Context) : UiNode(context) {

    override fun provideView(props: ReadableMap, context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text, null)
    }

    override fun setup(props: ReadableMap, update: Boolean) {
        super.setup(props, update)
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