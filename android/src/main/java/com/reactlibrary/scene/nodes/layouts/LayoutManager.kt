package com.reactlibrary.scene.nodes.layouts

import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.props.Bounding

/**
 * Layout manager is responsible for placing the children nodes in
 * the correct positions inside [UiLayout]
 */
interface LayoutManager {
    fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>)
}