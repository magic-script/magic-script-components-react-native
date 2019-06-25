package com.reactlibrary.scene

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.metersToPx

/**
 * Base node that represents UI controls
 */
open class UiNode(private val context: Context) : TransformNode() {

    var clickListener: (() -> Unit)? = null

    // width in meters
    var width = 0.0
        private set

    // height in meters
    var height = 0.0
        private set

    // view that represents the Node
    protected var view: View? = null

    override fun update(props: ReadableMap, useDefaults: Boolean) {
        super.update(props, useDefaults)
        setViewSize(props)
    }

    protected fun attachView(view: View, props: ReadableMap) {
        this.view = view
        setViewSize(props)
        // TODO replace delay with callback when AR fragment has been loaded
        // Wait until AR engine was loaded
        // @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
        Handler().postDelayed({
            ViewRenderable
                    .builder()
                    .setView(context, view)
                    .setHorizontalAlignment(ViewRenderable.HorizontalAlignment.CENTER)
                    .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                    .build()
                    .thenAccept {
                        this.renderable = it
                        view.setOnClickListener { clickListener?.invoke() }
                    }
        }, 1000)
    }

    private fun setViewSize(props: ReadableMap) {
        val width = props.getDoubleSafely("width")
        val height = props.getDoubleSafely("height")

        if (width != null && height != null) {
            this.width = width
            this.height = height
            // convert meters to px (1m is DP_TO_METER_RATIO by default)
            val widthPx = metersToPx(width, context)
            val heightPx = metersToPx(height, context)
            view?.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
        }
    }


}