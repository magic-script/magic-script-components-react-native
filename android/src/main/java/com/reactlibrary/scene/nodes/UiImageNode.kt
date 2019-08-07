package com.reactlibrary.scene.nodes

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils

class UiImageNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_WIDTH = "width"
        private const val PROP_HEIGHT = "height"
        private const val PROP_FILE_PATH = "filePath"
        private const val PROP_COLOR = "color"
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setImagePath(props)
        setColor(props)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.image, null) as ImageView
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

    private fun setImagePath(props: Bundle) {
        val path = props.getString(PROP_FILE_PATH)
        if (path != null) {
            val androidPath = Utils.getImagePath(path, context)
            Glide.with(context)
                    .load(androidPath)
                    .into(view as ImageView)

            val color = PropertiesReader.readColor(props, PROP_COLOR)
            if (color != null) {
                (view as ImageView).setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    private fun setColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_COLOR)
        if (color != null) {
            if (properties.containsKey(PROP_FILE_PATH)) {
                // blend color with image
                (view as ImageView).setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            } else { // use color instead of image
                (view as ImageView).setBackgroundColor(color)
            }
        }
    }

}