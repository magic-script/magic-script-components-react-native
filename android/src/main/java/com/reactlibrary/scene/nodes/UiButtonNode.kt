package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.CustomButton
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils

class UiButtonNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_WIDTH = "width"
        private const val PROP_HEIGHT = "height"
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_TEXT_COLOR = "textColor"
        private const val PROP_ROUNDNESS = "roundness"

        private const val DEFAULT_TEXT_SIZE = 0.0167
    }

    private var playingAnim = false

    init {
        // set default values of properties

        if (!properties.containsKey(PROP_ROUNDNESS)) {
            properties.putDouble(PROP_ROUNDNESS, 1.0)
        }

        if (!properties.containsKey(PROP_TEXT_SIZE)) {
            // calculate default text size based on button height
            if (properties.containsKey(PROP_HEIGHT)) {
                val textSize = properties.getDouble(PROP_HEIGHT) / 3
                properties.putDouble(PROP_TEXT_SIZE, textSize)
            } else {
                properties.putDouble(PROP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
            }
        }
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.button, null)
    }

    override fun onViewClick() {
        super.onViewClick()
        animate()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setText(props)
        setTextSize(props)
        setTextColor(props)
        setRoundness(props)
    }

    override fun setViewSize() {
        // default dimension
        var widthPx = ViewGroup.LayoutParams.WRAP_CONTENT
        var heightPx = ViewGroup.LayoutParams.WRAP_CONTENT

        if (properties.containsKey(PROP_WIDTH)) {
            val widthInMeters = properties.getDouble(PROP_WIDTH).toFloat()
            widthPx = Utils.metersToPx(widthInMeters, context)
        }

        if (properties.containsKey(PROP_HEIGHT)) {
            val heightInMeters = properties.getDouble(PROP_HEIGHT).toFloat()
            heightPx = Utils.metersToPx(heightInMeters, context)
        }
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }


    private fun animate() {
        if (playingAnim) {
            return
        }
        playingAnim = true
        val originalPos = worldPosition
        worldPosition = Vector3(originalPos.x, originalPos.y, originalPos.z - 0.05f)
        Handler().postDelayed({
            worldPosition = originalPos
            playingAnim = false
        }, 150)
    }

    private fun setText(props: Bundle) {
        val text = props.getString(PROP_TEXT)
        if (text != null) {
            (view as CustomButton).setText(text)
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val textSize = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToPx(textSize, view.context) * Utils.FONT_SCALE_FACTOR
            (view as CustomButton).setTextSize(size)
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_TEXT_COLOR)
        if (color != null) {
            (view as CustomButton).setTextColor(color)
        }
    }

    // Sets the corners roundness (0 - sharp, 1 - fully rounded)
    private fun setRoundness(props: Bundle) {
        if (props.containsKey(PROP_ROUNDNESS)) {
            val roundness = props.getDouble(PROP_ROUNDNESS).toFloat()
            (view as CustomButton).setRoundnessFactor(roundness)
        }
    }

}