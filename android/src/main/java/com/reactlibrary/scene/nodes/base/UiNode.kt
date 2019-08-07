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

    override fun build() {
        initView()
        super.build()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setAlignment(props)
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

    protected open fun onClick() {}

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
            onClick()
            clickListener?.invoke()
        }
        // build calls applyProperties, so we need to initialize the view before
    }

    private fun attachView() {
        setViewSize()

        ViewRenderable
                .builder()
                .setView(context, view)
                .setHorizontalAlignment(horizontalAlignment)
                .setVerticalAlignment(verticalAlignment)
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