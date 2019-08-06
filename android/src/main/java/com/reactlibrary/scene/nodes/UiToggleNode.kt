package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils
import kotlinx.android.synthetic.main.toggle.view.*

class UiToggleNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_HEIGHT = "height"
        private const val PROP_CHECKED = "on"
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_TEXT_COLOR = "textColor"
    }

    var toggleChangedListener: ((on: Boolean) -> Unit)? = null

    private var isOn = false

    override fun provideView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.toggle, null)
        view.iv_toggle.setOnClickListener {
            isOn = !isOn
            refreshImage()
            toggleChangedListener?.invoke(isOn)
        }
        return view
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        setIsChecked(props)
        setText(props)
        setTextSize(props)
        setTextColor(props)
    }

    override fun setViewSize() {
        val switchHeight = if (properties.containsKey(PROP_HEIGHT)) {
            val height = properties.getDouble(PROP_HEIGHT).toFloat()
            Utils.metersToPx(height, context)
        } else {
            ViewGroup.LayoutParams.WRAP_CONTENT
        }

        view.iv_toggle.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                switchHeight
        )

        view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun refreshImage() {
        if (isOn) {
            view.iv_toggle.setImageResource(R.drawable.toggle_on)
        } else {
            view.iv_toggle.setImageResource(R.drawable.toggle_off)
        }
    }

    private fun setIsChecked(props: Bundle) {
        if (props.containsKey(PROP_CHECKED)) {
            isOn = props.getBoolean(PROP_CHECKED)
            refreshImage()
        }
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            view.tv_toggle.text = text
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToPx(sizeMeters, view.context).toFloat()
            view.tv_toggle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_TEXT_COLOR)
        if (color != null) {
            view.tv_toggle.setTextColor(color)
        }
    }

}