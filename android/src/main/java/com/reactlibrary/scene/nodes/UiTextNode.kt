package com.reactlibrary.scene.nodes

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.getBooleanSafely
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.getStringSafely

class UiTextNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_ALL_CAPS = "allCaps"
        private const val PROP_CHARACTER_SPACING = "charSpacing"
    }

    private val textView by lazy { view as TextView }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text, null)
    }

    override fun applyProperties(props: ReadableMap, update: Boolean) {
        super.applyProperties(props, update)
        setText(props) // currently text value is available only on update
        setTextSize(props)
        setAllCaps(props)
        setCharacterSpacing(props)
    }

    private fun setText(props: ReadableMap) {
        val text = props.getStringSafely(PROP_TEXT)
        if (text != null) {
            textView.text = text
        }
    }

    private fun setTextSize(props: ReadableMap) {
        props.getDoubleSafely(PROP_TEXT_SIZE)?.let { textSize ->
            val size = Utils.metersToPx(textSize, textView.context).toFloat()
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun setAllCaps(props: ReadableMap) {
        props.getBooleanSafely(PROP_ALL_CAPS)?.let { allCaps ->
            textView.isAllCaps = allCaps
        }
    }

    private fun setCharacterSpacing(props: ReadableMap) {
        props.getDoubleSafely(PROP_CHARACTER_SPACING)?.let { spacing ->
            textView.letterSpacing = spacing.toFloat()
        }
    }

}