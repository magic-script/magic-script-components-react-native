package com.magicleap.magicscript.scene.nodes.layouts.params

import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

class PageViewLayoutParams(
    val visiblePage: Int,
    size: Vector2,
    itemsPadding: Map<TransformNode, Padding>,
    itemsAlignment: Map<TransformNode, Alignment>
) : LayoutParams(
    size = size,
    itemsPadding = itemsPadding,
    itemsAlignment = itemsAlignment
)