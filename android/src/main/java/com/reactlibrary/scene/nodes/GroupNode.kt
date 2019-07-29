package com.reactlibrary.scene.nodes

import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Bounding
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
        val childBounds = Utils.calculateSumBounds(children)
        return Bounding(
                childBounds.left + localPosition.x,
                childBounds.bottom + localPosition.y,
                childBounds.right + localPosition.x,
                childBounds.top + localPosition.y
        )
    }
}

