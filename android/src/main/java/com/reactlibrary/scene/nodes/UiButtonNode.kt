package com.reactlibrary.scene.nodes

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.getStringSafely
import com.reactlibrary.utils.metersToPx

class UiButtonNode(context: Context) : UiNode(context) {

    companion object {
        // properties
        private const val PROP_TITLE = "title"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_ROUNDNESS = "roundness"
    }

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
        val title = props.getStringSafely(PROP_TITLE)
        if (title != null) {
            text = title
        }
    }

    private fun Button.setTextSize(props: ReadableMap, update: Boolean) {
        val textSize = props.getDoubleSafely(PROP_TEXT_SIZE)
        if (textSize != null) {
            val size = metersToPx(textSize, context).toFloat()
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        } else if (!update) {  // set default value
            this@UiButtonNode.height?.let {
                val size = (it / 3).toFloat()
                setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            }
        }
    }

    // Sets the corners roundness (0 - sharp, 1 - fully rounded)
    private fun Button.setRoundness(props: ReadableMap, update: Boolean) {
        val roundness = props.getDoubleSafely(PROP_ROUNDNESS)
        val background = background.current as GradientDrawable
        if (roundness != null) {
            // must be called to modify shared drawables loaded from resources
            background.mutate()
            background.cornerRadius = (roundness * 90).toFloat()
        } else if (!update) { // set default value
            background.mutate()
            background.cornerRadius = 90f // fully rounded by default
        }
    }

}