package com.reactlibrary.scene.nodes.base

import android.os.Bundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.utils.logMessage
import com.reactlibrary.utils.toQuaternion
import com.reactlibrary.utils.toVector3

// Base node
abstract class TransformNode(properties: ReadableMap) : Node() {

    companion object {
        // properties
        private const val PROP_LOCAL_POSITION = "localPosition"
        private const val PROP_LOCAL_SCALE = "localScale"
        private const val PROP_LOCAL_ROTATION = "localRotation"
    }

    // converting to Bundle to avoid "already consumed" bugs
    private val props = Arguments.toBundle(properties) ?: Bundle()

    init {
        logMessage("init props = $props")
    }

    /**
     * Return true if already tried to attach the renderable (view or model),
     * otherwise false
     */
    var isRenderableAttached = false
        private set

    /**
     * Builds the node by calling [applyProperties]
     */
    open fun build() {
        applyProperties(props, false)
        logMessage("build props = $props")
    }

    /**
     * Updates some node's properties.
     * It should be called after [build]
     */
    fun update(properties: ReadableMap) {
        val propsToUpdate = Arguments.toBundle(properties) ?: Bundle()
        this.props.putAll(propsToUpdate) // save new properties

        applyProperties(propsToUpdate, true)
        logMessage("update: new properties: $propsToUpdate")
        logMessage("update: all properties: $props")
    }

    /**
     * Applies properties on the node.localRotation
     * @param update if true it's called on [update],
     * else it's called when initialized ([build])
     */
    protected open fun applyProperties(properties: Bundle, update: Boolean) {
        logMessage("applyProperties")

        setPosition(properties, update)
        setLocalScale(properties)
        setLocalRotation(properties)
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

    private fun setPosition(properties: Bundle, update: Boolean) {
        val localPosition = properties.getSerializable(PROP_LOCAL_POSITION).toVector3()
        if (localPosition != null) {
            this.localPosition = localPosition
        } else if (!update) { // build with default position
            this.localPosition = Vector3.zero()
            logMessage("position is null, bundle= $properties")
        }
    }

    private fun setLocalScale(properties: Bundle) {
        val localScale = properties.getSerializable(PROP_LOCAL_SCALE).toVector3()
        if (localScale != null) {
            logMessage("setting scale")
            this.localScale = localScale
        }
    }

    private fun setLocalRotation(properties: Bundle) {
        val quaternion = properties.getSerializable(PROP_LOCAL_ROTATION).toQuaternion()
        if (quaternion != null) {
            this.localRotation = quaternion
        }
    }

}