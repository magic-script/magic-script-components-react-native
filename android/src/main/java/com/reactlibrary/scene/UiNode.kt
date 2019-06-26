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
abstract class UiNode(private val context: Context) : TransformNode() {

    var clickListener: (() -> Unit)? = null

    // width in meters
    var width = 0.0
        private set

    // height in meters
    var height = 0.0
        private set

    protected lateinit var view: View

    override fun build(props: ReadableMap) {
        view = provideView(props, context)
        attachView(props)
        // this calls setup, so we need to create the view before
        super.build(props)
    }

    override fun setup(props: ReadableMap, update: Boolean) {
        super.setup(props, update)
        setViewSize(props)
    }

    protected abstract fun provideView(props: ReadableMap, context: Context): View

    private fun attachView(props: ReadableMap) {
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
            view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
        }
    }


}