package com.reactlibrary.scene.nodes.base

import android.content.Context
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

    // width in meters (optional)
    var width: Double? = null
        private set

    // height in meters (optional)
    var height: Double? = null
        private set

    protected lateinit var view: View

    var viewAttached = false
        private set

    override fun build(props: ReadableMap) {
        view = provideView(props, context)
        view.setOnClickListener { clickListener?.invoke() }
        // build calls setup, so we need to initialize the view before
        readSize(props)
        super.build(props)
    }

    override fun setup(props: ReadableMap, update: Boolean) {
        super.setup(props, update)
        // currently we don't update the size
        readSize(props)
    }

    protected abstract fun provideView(props: ReadableMap, context: Context): View

    /**
     * Attaches renderable (View) to the Node
     * Must be called after AR native code has been loaded
     * @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
     */
    fun attachView() {
        val widthTmp = width
        val heightTmp = height
        if (widthTmp != null && heightTmp != null) {
            val widthPx = metersToPx(widthTmp, context)
            val heightPx = metersToPx(heightTmp, context)

            val params = view.layoutParams
            if (params != null) {
                params.width = widthPx
                params.height = heightPx
                view.layoutParams = params
            } else {
                view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
            }
        }

        ViewRenderable
                .builder()
                .setView(context, view)
                .setHorizontalAlignment(ViewRenderable.HorizontalAlignment.CENTER)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                .build()
                .thenAccept {
                    this.renderable = it
                }

        viewAttached = true
    }

    private fun readSize(props: ReadableMap) {
        val width = props.getDoubleSafely("width")
        val height = props.getDoubleSafely("height")

        if (width != null && height != null) {
            this.width = width
            this.height = height
        }
    }


}