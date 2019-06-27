package com.reactlibrary.scene.nodes.base

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.utils.getArraySafely
import com.reactlibrary.utils.logDebug
import com.reactlibrary.utils.toVector3

// Base node
open class TransformNode : Node() {

    companion object {
        protected const val PROP_LOCAL_POSITION = "localPosition"
        protected const val PROP_LOCAL_SCALE = "localScale"
        protected const val PROP_LOCAL_ROTATION = "localRotation"
    }

    /**
     * Builds the node by calling [setup]
     */
    open fun build(props: ReadableMap) {
        setup(props, false)
    }

    /**
     * Updates node's properties.
     * It should be called after [build]
     */
    fun update(props: ReadableMap) {
        setup(props, true)
    }

    /**
     * Applies properties on the node.localRotation
     * @param update if true it's called on [update],
     * else it's called when initialized ([build])
     */
    protected open fun setup(props: ReadableMap, update: Boolean) {
        logDebug("setup")
        setPosition(props, update)
        setLocalScale(props)
        setLocalRotation(props)
    }

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
            logDebug("setting scale")
            this.localScale = localScale
        }
    }

    private fun setLocalRotation(props: ReadableMap) {
        val quaternionArray = props.getArraySafely(PROP_LOCAL_ROTATION)
        if (quaternionArray != null && quaternionArray.size() == 4) {
            logDebug("setting rotation")
            val x = quaternionArray.getDouble(0).toFloat()
            val y = quaternionArray.getDouble(1).toFloat()
            val z = quaternionArray.getDouble(2).toFloat()
            val angle = quaternionArray.getDouble(3).toFloat()
            this.localRotation = Quaternion(x, y, z, angle)
        }
    }

}