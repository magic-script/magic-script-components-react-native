package com.reactlibrary.scene.nodes.base

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.utils.getArraySafely
import com.reactlibrary.utils.toVector3

// Base node
open class TransformNode : Node() {

    /**
     * Builds the view by calling [setup]
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
     * Applies properties on the node.
     * @param update if true it's called on [update], else it's called when initialized ([build])
     */
    protected open fun setup(props: ReadableMap, update: Boolean) {
        val localPosition = props.getArraySafely("localPosition")?.toVector3()
        if (localPosition != null) {
            this.localPosition = localPosition
        } else if (!update) {
            this.localPosition = Vector3.zero()
        }
    }

}