package com.reactlibrary.scene.nodes.layouts

import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.props.Bounding

/**
 * Grid manager that places the children nodes in the correct positions
 */
interface LayoutManager {
    fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>)
}