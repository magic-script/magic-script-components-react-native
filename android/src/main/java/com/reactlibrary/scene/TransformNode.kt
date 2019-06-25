package com.reactlibrary.scene

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.utils.getArraySafely
import com.reactlibrary.utils.toVector3

// Base node
open class TransformNode : Node() {

    open fun update(props: ReadableMap, useDefaults: Boolean) {
        val localPosition = props.getArraySafely("localPosition")?.toVector3()
        if (localPosition != null) {
            this.localPosition = localPosition
        } else if (useDefaults) {
            this.localPosition = Vector3.zero()
        }
    }

}