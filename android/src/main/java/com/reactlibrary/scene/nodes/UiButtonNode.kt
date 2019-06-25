package com.reactlibrary.scene.nodes

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.UiNode
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.getStringSafely
import com.reactlibrary.utils.metersToPx

class UiButtonNode(context: Context) : UiNode(context) {

    override fun provideView(props: ReadableMap, context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.button, null)
    }

    override fun update(props: ReadableMap, useDefaults: Boolean) {
        super.update(props, useDefaults)
        val btnView = view as Button?
        if (btnView != null) {
            btnView.setTitle(props)
            btnView.setTextSize(props, useDefaults)
            btnView.setRoundness(props, useDefaults)
        }
    }

    private fun Button.setTitle(props: ReadableMap) {
        val title = props.getStringSafely("title")
        if (title != null) {
            text = title
        }
    }

    private fun Button.setTextSize(props: ReadableMap, useDefaults: Boolean) {
        val textSize = props.getDoubleSafely("textSize")
        if (textSize != null) {
            val size = metersToPx(textSize, context).toFloat()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        } else if (useDefaults) {
            val size = (this@UiButtonNode.height / 3).toFloat()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun Button.setRoundness(props: ReadableMap, useDefaults: Boolean) {
        val roundness = props.getDoubleSafely("roundness")
        if (roundness != null) {
            // PaintDrawable
        }
    }

}