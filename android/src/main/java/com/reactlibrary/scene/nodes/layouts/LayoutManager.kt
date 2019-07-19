package com.reactlibrary.scene.nodes.layouts

import com.google.ar.sceneform.Node

/**
 * Grid manager that places the children nodes in the correct positions
 */
interface LayoutManager {
    fun addNode(node: Node)
}