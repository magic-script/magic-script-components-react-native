package com.magicleap.magicscript.scene.nodes.layouts.params

import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

class GridLayoutParams(
    val columns: Int,
    val rows: Int,
    size: Vector2,
    itemsPadding: Map<Int, Padding>,
    itemsAlignment: Map<Int, Alignment>
) : LayoutParams(
    size = size,
    itemsPadding = itemsPadding,
    itemsAlignment = itemsAlignment
)



