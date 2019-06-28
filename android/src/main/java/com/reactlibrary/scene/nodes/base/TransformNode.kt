package com.reactlibrary.scene.nodes.base

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.utils.getArraySafely
import com.reactlibrary.utils.logMessage
import com.reactlibrary.utils.toVector3

// Base node
abstract class TransformNode(props: ReadableMap) : Node() {

    companion object {
        // properties
        private const val PROP_LOCAL_POSITION = "localPosition"
        private const val PROP_LOCAL_SCALE = "localScale"
        private const val PROP_LOCAL_ROTATION = "localRotation"
    }

    protected var props: ReadableMap = props
        private set

    /**
     * Return true if already tried to attach the view (otherwise false)
     */
    var isRenderableAttached = false
        private set

    /**
     * Builds the node by calling [applyProperties]
     */
    open fun build() {
        applyProperties(props, false)
    }

    /**
     * Updates node's properties.
     * It should be called after [build]
     */
    fun update(props: ReadableMap) {
        this.props = props
        applyProperties(props, true)
    }

    /**
     * Applies properties on the node.localRotation
     * @param update if true it's called on [update],
     * else it's called when initialized ([build])
     */
    protected open fun applyProperties(props: ReadableMap, update: Boolean) {
        logMessage("applyProperties")
        setPosition(props, update)
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

    private fun setPosition(props: ReadableMap, update: Boolean) {
        val localPosition = props.getArraySafely(PROP_LOCAL_POSITION)?.toVector3()
        if (localPosition != null) {
            this.localPosition = localPosition
        } else if (!update) { // build with default position
            this.localPosition = Vector3.zero()
        }
    }

    private fun setLocalScale(props: ReadableMap) {
        val localScale = props.getArraySafely(PROP_LOCAL_SCALE)?.toVector3()
        if (localScale != null) {
            logMessage("setting scale")
            this.localScale = localScale
        }
    }

    private fun setLocalRotation(props: ReadableMap) {
        val quaternionArray = props.getArraySafely(PROP_LOCAL_ROTATION)
        if (quaternionArray != null && quaternionArray.size() == 4) {
            logMessage("setting rotation")
            val x = quaternionArray.getDouble(0).toFloat()
            val y = quaternionArray.getDouble(1).toFloat()
            val z = quaternionArray.getDouble(2).toFloat()
            val w = quaternionArray.getDouble(3).toFloat()

            this.localRotation = Quaternion(x, y, z, w)  // Quaternion.axisAngle
        }
    }

}