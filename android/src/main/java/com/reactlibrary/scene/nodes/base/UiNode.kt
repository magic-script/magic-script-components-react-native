package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.utils.getBooleanSafely
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.metersToPx

/**
 * Base node that represents UI controls
 */
abstract class UiNode(private val context: Context) : TransformNode() {

    companion object {
        // properties
        private const val PROP_WIDTH = "width"
        private const val PROP_HEIGHT = "height"
        private const val PROP_ENABLED = "enabled"
    }

    var clickListener: (() -> Unit)? = null

    /**
     * Width in meters (optional)
     */
    var width: Double? = null
        private set

    /**
     * Height in meters (optional)
     */
    var height: Double? = null
        private set

    /**
     * Return true if already tried to attach the view (otherwise false)
     */
    var isViewAttached = false
        private set

    /**
     * A view attached to the node
     */
    protected lateinit var view: View

    override fun build(props: ReadableMap) {
        view = provideView(props, context)
        view.setOnClickListener { clickListener?.invoke() }
        // build calls setup, so we need to initialize the view before
        readSize(props)
        super.build(props)
    }

    override fun setup(props: ReadableMap, update: Boolean) {
        super.setup(props, update)
        // currently we don't resize the view on update
        readSize(props)
        setEnabled(props)
    }

    protected abstract fun provideView(props: ReadableMap, context: Context): View

    /**
     * Attaches view to the Node (must be called after AR Core native code has been loaded)
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

        // TODO handle error exceptionally { }
        ViewRenderable
                .builder()
                .setView(context, view)
                .setHorizontalAlignment(ViewRenderable.HorizontalAlignment.CENTER)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                .build()
                .thenAccept {
                    this.renderable = it
                }

        isViewAttached = true
    }

    private fun readSize(props: ReadableMap) {
        val width = props.getDoubleSafely(PROP_WIDTH)
        val height = props.getDoubleSafely(PROP_HEIGHT)

        if (width != null && height != null) {
            this.width = width
            this.height = height
        }
    }

    private fun setEnabled(props: ReadableMap) {
        val enabled = props.getBooleanSafely(PROP_ENABLED)
        if (enabled != null) {
            view.isEnabled = enabled
        }
    }


}