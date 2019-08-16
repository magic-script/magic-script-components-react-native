package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

/**
 * Base node that represents UI controls.
 * It contains a native Android view attached as [renderable]
 */
abstract class UiNode(props: ReadableMap, protected val context: Context) : TransformNode(props, true) {

    companion object {
        // properties
        const val PROP_ALIGNMENT = "alignment"
        const val PROP_ENABLED = "enabled"
    }

    var clickListener: (() -> Unit)? = null

    /**
     * A view attached to the node
     */
    protected lateinit var view: View

    private var horizontalAlignment = ViewRenderable.HorizontalAlignment.CENTER
    private var verticalAlignment = ViewRenderable.VerticalAlignment.CENTER

    private var shouldRebuild = false
    private var loadingView = false

    override fun build() {
        initView()
        super.build()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setAlignment(props)
        setEnabled(props)
    }

    override fun loadRenderable() {
        attachView()
    }

    override fun getBounding(): Bounding {
        return Utils.calculateBoundsOfNode(this)
    }

    override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)
        if (shouldRebuild && !loadingView) {
            build() // init a new view and apply all properties
            attachView()
            shouldRebuild = false
            logMessage("node rebuild, hash:{${this.hashCode()}}")
        }
    }

    /**
     * Should be called when the size of the node may have changed,
     * so we need to rebuild the native view (renderable)
     * (resizing the current view does not work - ARCore bug?)
     */
    fun setNeedsRebuild() {
        // no rebuilding if the renderable has not been requested yet
        // because ArCore may not be initialized yet
        if (renderableRequested) {
            shouldRebuild = true
        }
    }

    protected abstract fun provideView(context: Context): View

    protected open fun onViewClick() {}

    protected open fun setViewSize() {
        // default dimensions
        val widthPx = ViewGroup.LayoutParams.WRAP_CONTENT
        val heightPx = ViewGroup.LayoutParams.WRAP_CONTENT

        // the size should be set before attaching view to the node
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    private fun initView() {
        this.view = provideView(context)
        this.view.setOnClickListener {
            onViewClick()
            clickListener?.invoke()
        }
        // build calls applyProperties, so we need to initialize the view before
    }

    private fun attachView() {
        setViewSize()

        loadingView = true
        ViewRenderable
                .builder()
                .setView(context, view)
                .setHorizontalAlignment(horizontalAlignment)
                .setVerticalAlignment(verticalAlignment)
                .build()
                .thenAccept {
                    this.renderable = it
                    loadingView = false
                    //renderable?.material?.setBoolean("doubleSided", false) does not work
                    logMessage("loaded ViewRenderable")
                }
                .exceptionally { throwable ->
                    loadingView = false
                    logMessage("error loading ViewRenderable: $throwable")
                    null
                }
    }

    private fun setAlignment(props: Bundle) {
        val alignment = props.getString(PROP_ALIGNMENT)
        if (alignment != null) {
            val alignmentArray = alignment.split("-")
            if (alignmentArray.size == 2) {
                val verticalAlign = alignmentArray[0]
                val horizontalAlign = alignmentArray[1]
                verticalAlignment = ViewRenderable.VerticalAlignment.valueOf(verticalAlign.toUpperCase())
                horizontalAlignment = ViewRenderable.HorizontalAlignment.valueOf(horizontalAlign.toUpperCase())
            }
        }
    }

    private fun setEnabled(props: Bundle) {
        if (props.containsKey(PROP_ENABLED)) {
            view.isEnabled = props.getBoolean(PROP_ENABLED)
        }
    }

}