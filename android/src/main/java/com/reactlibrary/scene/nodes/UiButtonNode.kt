package com.reactlibrary.scene.nodes

import android.content.Context
import android.graphics.drawable.GradientDrawable
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

    override fun setup(props: ReadableMap, update: Boolean) {
        super.setup(props, update)
        val btnView = view as Button?
        if (btnView != null) {
            btnView.setTitle(props)
            btnView.setTextSize(props, update)
            btnView.setRoundness(props, update)
        }
    }

    private fun Button.setTitle(props: ReadableMap) {
        val title = props.getStringSafely("title")
        if (title != null) {
            text = title
        }
    }

    private fun Button.setTextSize(props: ReadableMap, update: Boolean) {
        val textSize = props.getDoubleSafely("textSize")
        if (textSize != null) {
            val size = metersToPx(textSize, context).toFloat()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        } else if (!update) {
            val size = (this@UiButtonNode.height / 3).toFloat()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    // Sets the corners roundness (0 - sharp, 1 - fully rounded)
    private fun Button.setRoundness(props: ReadableMap, update: Boolean) {
        val roundness = props.getDoubleSafely("roundness")
        val background = background.current as GradientDrawable
        if (roundness != null) {
            background.mutate() // must be called to modify shared drawables loaded from resources
            background.cornerRadius = (roundness * 90).toFloat()
        } else if (!update) {
            background.mutate()
            background.cornerRadius = 90f // fully rounded by default
        }
    }

}