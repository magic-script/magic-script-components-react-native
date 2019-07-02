package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.utils.Utils
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
        width = props.getDoubleSafely(PROP_WIDTH)
        height = props.getDoubleSafely(PROP_HEIGHT)
    }

    override fun build() {
        initView()
        super.build()
    }

    protected abstract fun provideView(context: Context): View

    override fun applyProperties(properties: Bundle, update: Boolean) {
        super.applyProperties(properties, update)

        setSize(properties, update)
        setEnabled(properties)
    }

    /**
     * Attaches view to the Node (must be called after AR Core native code has been loaded)
     * @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
     */
    override fun loadRenderable(): Boolean {
        attachViewRenderable()
        return true
    }

    private fun initView() {
        logMessage("assigning view")
        this.view = provideView(context)
        this.view.setOnClickListener { clickListener?.invoke() }
        // build calls applyProperties, so we need to initialize the view before
    }

    private fun attachViewRenderable() {
        // default dimensions
        var widthPx = ViewGroup.LayoutParams.WRAP_CONTENT
        var heightPx = ViewGroup.LayoutParams.WRAP_CONTENT

        width?.let {
            widthPx = Utils.metersToPx(it, context)
        }

        height?.let {
            heightPx = Utils.metersToPx(it, context)
        }

        val params = view.layoutParams
        if (params != null) {
            params.width = widthPx
            params.height = heightPx
            view.layoutParams = params
        } else {
            view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
        }

        logMessage("attachViewRenderable widthPx= $widthPx, heightPx= $heightPx")

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
                    logMessage("loaded ViewRenderable")
                }
                .exceptionally { throwable ->
                    logMessage("error loading view renderable: $throwable")
                    null
                }
    }

    private fun setSize(properties: Bundle, update: Boolean) {
        var sizeRead = false

        if (properties.containsKey(PROP_WIDTH)) {
            this.width = properties.getDouble(PROP_WIDTH)
            sizeRead = true
        }

        if (properties.containsKey(PROP_HEIGHT)) {
            this.height = properties.getDouble(PROP_HEIGHT)
            sizeRead = true
        }

        // cannot update renderable before [isRenderableAttached],
        // because Sceneform may be uninitialized yet
        if (sizeRead && update && isRenderableAttached) {
            build()
            attachViewRenderable()
            logMessage("set size building UI renderable")
        }
    }

    private fun setEnabled(properties: Bundle) {
        if (properties.containsKey(PROP_ENABLED)) {
            view.isEnabled = properties.getBoolean(PROP_ENABLED)
        }
    }

}