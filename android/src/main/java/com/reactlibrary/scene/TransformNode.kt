package com.reactlibrary.scene

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.utils.getArraySafely
import com.reactlibrary.utils.toVector3

// Base node
open class TransformNode(props: ReadableMap) : Node() {

    init {
        setProperties(props)
    }

    open fun update(props: ReadableMap) {
        setProperties(props)
        this.localPosition
    }

    private fun setProperties(props: ReadableMap) {
        val localPosition = props.getArraySafely("localPosition")?.toVector3()
        if (localPosition != null) {
            this.localPosition = localPosition
        } else {
            this.localPosition = Vector3.zero()
        }

    }

}