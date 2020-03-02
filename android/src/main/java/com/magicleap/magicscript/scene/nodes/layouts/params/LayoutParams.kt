package com.magicleap.magicscript.scene.nodes.layouts.params

import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

open class LayoutParams(
    val size: Vector2,
    val itemsPadding: Map<TransformNode, Padding>,
    val itemsAlignment: Map<TransformNode, Alignment>
)

