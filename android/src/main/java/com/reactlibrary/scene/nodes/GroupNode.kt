package com.reactlibrary.scene.nodes

import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.Bounding
import com.reactlibrary.utils.Utils

/**
 * Container for other Nodes
 */
class GroupNode(props: ReadableMap) : TransformNode(props) {

    override fun loadRenderable(): Boolean {
        // it does not contain its own renderable
        return false
    }

    override fun getBounding(): Bounding {
        return Utils.calculateSumBounds(children)
    }
}

