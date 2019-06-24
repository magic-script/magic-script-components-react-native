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
open class UiNode(props: ReadableMap, private val context: Context) : TransformNode(props) {

    var clickListener: (() -> Unit)? = null

    protected fun attachView(view: View, props: ReadableMap) {
        val width = props.getDoubleSafely("width")
        val height = props.getDoubleSafely("height")

        if (width != null && height != null) {
            // convert meters to px (1m is DP_TO_METER_RATIO by default)
            val widthPx = metersToPx(width, context)
            val heightPx = metersToPx(height, context)
            view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
        }

        // TODO replace delay with callback when AR fragment has been loaded
        // Wait until AR engine was loaded
        // @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
        Handler().postDelayed({
            ViewRenderable
                    .builder()
                    .setView(context, view)
                    .build()
                    .thenAccept {
                        this.renderable = it
                        view.setOnClickListener { clickListener?.invoke() }
                    }
        }, 1000)
    }


}