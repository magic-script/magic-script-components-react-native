package com.reactlibrary.scene.nodes.base

import android.os.Bundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.reactlibrary.utils.logMessage
import com.reactlibrary.utils.toQuaternion
import com.reactlibrary.utils.toVector3

/**
 * Base node.
 * It's characterised by [properties] bundle based on [props].
 * Some properties may be added or changed on [update] function.
 * @param props the initial properties of the node
 */
abstract class TransformNode(props: ReadableMap) : Node() {

    companion object {
        // props
        private const val PROP_LOCAL_POSITION = "localPosition"
        private const val PROP_LOCAL_SCALE = "localScale"
        private const val PROP_LOCAL_ROTATION = "localRotation"
    }

    // converting to Bundle to avoid "already consumed" bugs
    protected val properties = Arguments.toBundle(props) ?: Bundle()

    init {
        // Set default properties if not present
        if (!properties.containsKey(PROP_LOCAL_POSITION)) {
            val position: ArrayList<Double> = arrayListOf(0.0, 0.0, 0.0)
            properties.putSerializable(PROP_LOCAL_POSITION, position)
        }
        logMessage("initial properties = ${this.properties}")
    }

    /**
     * Return true if already tried to attach the renderable (view or model),
     * otherwise false
     */
    var isRenderableAttached = false
        private set

    /**
     * Builds the node by calling [applyProperties] with all initial properties
     */
    open fun build() {
        applyProperties(properties)
    }

    /**
     * Updates properties of the node
     * It should be called after [build]
     * @param props properties to change or new properties to apply
     */
    fun update(props: ReadableMap) {
        val propsToUpdate = Arguments.toBundle(props) ?: Bundle()
        this.properties.putAll(propsToUpdate) // save new props

        logMessage("updating properties: $propsToUpdate")
        applyProperties(propsToUpdate)
    }

    /**
     * Applies props on the node.localRotation
     * @param update if true it's called on [update],
     * else it's called when initialized ([build])
     */
    protected open fun applyProperties(props: Bundle) {
        setPosition(props)
        setLocalScale(props)
        setLocalRotation(props)
    }

    /**
     * Should attach renderable to the node (view or model)
     */
    fun attachRenderable() {
        isRenderableAttached = loadRenderable()
    }

    /** Should assign renderable to the node
     *  @return true if renderable has been assigned to the node, false otherwise
     */
    protected abstract fun loadRenderable(): Boolean

    private fun setPosition(props: Bundle) {
        val localPosition = props.getSerializable(PROP_LOCAL_POSITION).toVector3()
        if (localPosition != null) {
            this.localPosition = localPosition
        }
    }

    private fun setLocalScale(props: Bundle) {
        val localScale = props.getSerializable(PROP_LOCAL_SCALE).toVector3()
        if (localScale != null) {
            this.localScale = localScale
        }
    }

    private fun setLocalRotation(props: Bundle) {
        val quaternion = props.getSerializable(PROP_LOCAL_ROTATION).toQuaternion()
        if (quaternion != null) {
            this.localRotation = quaternion
        }
    }

}