package com.reactlibrary.scene.nodes

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.getBooleanSafely
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.getStringSafely
import com.reactlibrary.utils.metersToPx

class UiTextNode(context: Context) : UiNode(context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_ALL_CAPS = "allCaps"
        private const val PROP_CHARACTER_SPACING = "charSpacing"
    }

    override fun provideView(props: ReadableMap, context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text, null)
    }

    override fun setup(props: ReadableMap, update: Boolean) {
        super.setup(props, update)
        val textView = view as TextView?
        if (textView != null) {
            textView.setText(props) // text (value) is available on update
            textView.setTextSize(props)
            textView.setAllCaps(props)
            textView.setCharacterSpacing(props)
        }
    }

    private fun TextView.setText(props: ReadableMap) {
        val text = props.getStringSafely(PROP_TEXT)
        if (text != null) {
            this.text = text
        }
    }

    private fun TextView.setTextSize(props: ReadableMap) {
        props.getDoubleSafely(PROP_TEXT_SIZE)?.let { textSize ->
            val size = metersToPx(textSize, context).toFloat()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun TextView.setAllCaps(props: ReadableMap) {
        props.getBooleanSafely(PROP_ALL_CAPS)?.let { allCaps ->
            isAllCaps = allCaps
        }
    }

    private fun TextView.setCharacterSpacing(props: ReadableMap) {
        props.getDoubleSafely(PROP_CHARACTER_SPACING)?.let { spacing ->
            letterSpacing = spacing.toFloat()
        }
    }

}