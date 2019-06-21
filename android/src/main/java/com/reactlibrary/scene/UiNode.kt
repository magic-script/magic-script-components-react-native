package com.reactlibrary.scene

import com.google.ar.sceneform.Node

/**
 * A node that represents UI controls
 */
class UiNode : Node() {
    var isVisible = true
    var clickListener: (() -> Unit)? = null
}