package com.reactlibrary.scene.nodes

import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.TransformNode

class GroupNode(props: ReadableMap) : TransformNode(props) {
    override fun loadRenderable(): Boolean {
        // it does not contain the view
        return false
    }
}

