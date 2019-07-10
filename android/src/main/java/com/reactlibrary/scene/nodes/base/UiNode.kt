package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

/**
 * Base node that represents UI controls
 */
abstract class UiNode(props: ReadableMap, private val context: Context) : TransformNode(props) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_ENABLED = "enabled"
    }

    var clickListener: (() -> Unit)? = null

    /**
     * A view attached to the node
     */
    protected lateinit var view: View
    private var replacingView = false

    override fun build() {
        initView()
        super.build()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (replacingView) {
            replacingView = false
        } else {
            setSize(props)
        }

        setEnabled(props)
    }

    /**
     * Attaches view to the Node (must be called after AR Core native code has been loaded)
     * @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
     */
    override fun loadRenderable(): Boolean {
        attachView()
        return true
    }

    protected abstract fun provideView(context: Context): View

    protected open fun onClick() {}

    private fun initView() {
        this.view = provideView(context)
        this.view.setOnClickListener {
            onClick()
            clickListener?.invoke()
        }
        // build calls applyProperties, so we need to initialize the view before
    }

    private fun attachView() {
        // default dimensions
        var widthPx = ViewGroup.LayoutParams.WRAP_CONTENT
        var heightPx = ViewGroup.LayoutParams.WRAP_CONTENT

        if (properties.containsKey(PROP_WIDTH)) {
            val widthInMeters = properties.getDouble(PROP_WIDTH)
            widthPx = Utils.metersToPx(widthInMeters, context)
        }

        if (properties.containsKey(PROP_HEIGHT)) {
            val widthInMeters = properties.getDouble(PROP_HEIGHT)
            heightPx = Utils.metersToPx(widthInMeters, context)
        }

        val params = view.layoutParams
        if (params != null) {
            params.width = widthPx
            params.height = heightPx
            view.layoutParams = params
        } else {
            view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
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
                    logMessage("loaded ViewRenderable")
                }
                .exceptionally { throwable ->
                    logMessage("error loading ViewRenderable: $throwable")
                    null
                }
    }

    private fun setSize(props: Bundle) {
        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            // cannot update renderable before [isRenderableAttached],
            // because Sceneform may be uninitialized yet
            if (isRenderableAttached) {
                replacingView = true
                // in order to resize the view have to be rebuild
                build()
                attachView()
            }
        }
    }

    private fun setEnabled(props: Bundle) {
        if (props.containsKey(PROP_ENABLED)) {
            view.isEnabled = props.getBoolean(PROP_ENABLED)
        }
    }

}