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
import kotlinx.android.synthetic.main.image.view.*

class UiImageNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_WIDTH = "width"
        private const val PROP_HEIGHT = "height"
        private const val PROP_FILE_PATH = "filePath"
        private const val PROP_COLOR = "color"
        private const val PROP_FRAME = "useFrame"
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setImagePath(props)
        setColor(props)
        setUseFrame(props)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.image, null)
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
                    .into(view.image_view)

            val color = PropertiesReader.readColor(props, PROP_COLOR)
            if (color != null) {
                (view.image_view as ImageView).setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    private fun setColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_COLOR)
        if (color != null) {
            if (properties.containsKey(PROP_FILE_PATH)) {
                // blend color with image
                (view.image_view as ImageView).setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            } else { // use color instead of image
                (view.image_view as ImageView).setBackgroundColor(color)
            }
        }
    }

    private fun setUseFrame(props: Bundle) {
        if (props.containsKey(PROP_FRAME)) {
            val useFrame = props.getBoolean(PROP_FRAME)
            if (useFrame) {
                view.setPadding(1, 1, 1, 1)
                view.setBackgroundResource(R.drawable.image_border)
            } else {
                view.setPadding(0, 0, 0, 0)
                view.setBackgroundResource(0)
            }
        }
    }

}