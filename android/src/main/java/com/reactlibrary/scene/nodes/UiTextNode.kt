package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils

class UiTextNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_BOUNDS_SIZE = "boundsSize"
        private const val PROP_WRAP = "wrap"
        private const val PROP_TEXT_ALIGNMENT = "textAlignment"
        private const val PROP_TEXT_COLOR = "textColor"
        private const val PROP_ALL_CAPS = "allCaps"
        private const val PROP_CHARACTER_SPACING = "charSpacing"

        private const val DEFAULT_TEXT_SIZE = 0.025 // in meters
        private const val DEFAULT_ALIGNMENT = "center-left" // view alignment (pivot)
    }

    init {
        // set default values of properties
        if (!properties.containsKey(PROP_TEXT_SIZE)) {
            properties.putDouble(PROP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
        }

        if (!properties.containsKey(PROP_ALIGNMENT)) {
            properties.putString(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        }
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text, null)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_BOUNDS_SIZE)) {
            setNeedsRebuild()
        }

        setText(props)
        setTextSize(props)
        setTextAlignment(props)
        setTextColor(props)
        setAllCaps(props)
        setCharacterSpacing(props)
        setWrap(props)
    }

    override fun setViewSize() {
        // default dimension
        var widthPx = ViewGroup.LayoutParams.WRAP_CONTENT
        var heightPx = ViewGroup.LayoutParams.WRAP_CONTENT

        if (properties.containsKey(PROP_BOUNDS_SIZE)) {
            val boundsData = properties.get(PROP_BOUNDS_SIZE) as Bundle
            val bounds = boundsData.getSerializable(PROP_BOUNDS_SIZE) as ArrayList<Double>
            widthPx = Utils.metersToPx(bounds[0].toFloat(), context)
            heightPx = Utils.metersToPx(bounds[1].toFloat(), context)
        }

        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            (view as TextView).text = text
            setNeedsRebuild()
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToPx(sizeMeters, view.context) * Utils.FONT_SCALE_FACTOR
            (view as TextView).setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            setNeedsRebuild()
        }
    }

    private fun setTextAlignment(props: Bundle) {
        when (props.getString(PROP_TEXT_ALIGNMENT)) {
            "left" -> {
                (view as TextView).gravity = Gravity.LEFT
            }
            "center" -> {
                (view as TextView).gravity = Gravity.CENTER_HORIZONTAL
            }
            "right" -> {
                (view as TextView).gravity = Gravity.RIGHT
            }
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_TEXT_COLOR)
        if (color != null) {
            (view as TextView).setTextColor(color)
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
            setNeedsRebuild()
        }
    }

    private fun setWrap(props: Bundle) {
        if (props.containsKey(PROP_BOUNDS_SIZE)) {
            val boundsData = props.get(PROP_BOUNDS_SIZE) as Bundle
            val wrap = boundsData.getBoolean(PROP_WRAP)
            (view as TextView).setSingleLine(!wrap)
        }
    }

}