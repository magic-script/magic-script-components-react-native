package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.scene.nodes.base.TransformNode

// Used by layout managers
data class NodeInfo(
    val node: TransformNode,
    val index: Int,
    val width: Float,
    val height: Float,
    val pivotOffsetX: Float,
    val pivotOffsetY: Float
)