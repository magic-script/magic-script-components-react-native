package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.getBooleanSafely
import com.reactlibrary.utils.getDoubleSafely
import com.reactlibrary.utils.logMessage

/**
 * Base node that represents UI controls
 */
abstract class UiNode(props: ReadableMap, private val context: Context) : TransformNode(props) {

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
     * A view attached to the node
     */
    protected lateinit var view: View

    init {
        readSize(props)
    }

    override fun build() {
        view = provideView(context)
        view.setOnClickListener { clickListener?.invoke() }
        // build calls applyProperties, so we need to initialize the view before
        super.build()
    }

    protected abstract fun provideView(context: Context): View

    override fun applyProperties(props: ReadableMap, update: Boolean) {
        super.applyProperties(props, update)
        // TODO resize the view when dimensions changed on update
        readSize(props)
        setEnabled(props)
    }

    /**
     * Attaches view to the Node (must be called after AR Core native code has been loaded)
     * @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
     */
    override fun loadRenderable(): Boolean {
        val widthTmp = width
        val heightTmp = height
        if (widthTmp != null && heightTmp != null) {
            val widthPx = Utils.metersToPx(widthTmp, context)
            val heightPx = Utils.metersToPx(heightTmp, context)

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
                    //renderable?.material?.setBoolean("doubleSided", false)
                }
                .exceptionally { throwable ->
                    logMessage("error loading view renderable: $throwable")
                    null
                }

        return true
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