package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

/**
 * Base node that represents UI controls
 */
abstract class UiNode(props: ReadableMap, protected val context: Context) : TransformNode(props) {

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

    override fun getBounding(): Bounding {
        return Utils.calculateBoundsOfNode(this)
    }

    protected abstract fun provideView(context: Context): View

    /**
     * Should return desired horizontal alignment of the renderable
     */
    protected open fun getHorizontalAlignment(): ViewRenderable.HorizontalAlignment {
        return ViewRenderable.HorizontalAlignment.CENTER
    }

    /**
     * Should return desired vertical alignment of the renderable
     */
    protected open fun getVerticalAlignment(): ViewRenderable.VerticalAlignment {
        return ViewRenderable.VerticalAlignment.CENTER
    }

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
            val widthInMeters = properties.getDouble(PROP_WIDTH).toFloat()
            widthPx = Utils.metersToPx(widthInMeters, context)
        }

        if (properties.containsKey(PROP_HEIGHT)) {
            val widthInMeters = properties.getDouble(PROP_HEIGHT).toFloat()
            heightPx = Utils.metersToPx(widthInMeters, context)
        }

        val params = view.layoutParams
        if (params != null) { // it's an update (view has been already attached)
            params.width = widthPx
            params.height = heightPx
            view.layoutParams = params
        } else {
            // the size should be set before attaching view to the node
            view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
        }
        
        // TODO handle error exceptionally { }
        ViewRenderable
                .builder()
                .setView(context, view)
                .setHorizontalAlignment(getHorizontalAlignment())
                .setVerticalAlignment(getVerticalAlignment())
                .build()
                .thenAccept {
                    this.renderable = it
                    //renderable?.material?.setBoolean("doubleSided", false) does not work
                    logMessage("loaded ViewRenderable")
                }
                .exceptionally { throwable ->
                    logMessage("error loading ViewRenderable: $throwable")
                    null
                }
    }

    protected open fun setSize(props: Bundle) {
        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            // cannot update renderable before [renderableRequested],
            // because Sceneform may be uninitialized yet
            if (renderableRequested) {
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