package com.magicleap.magicscript.scene.nodes.layouts.params

import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

open class LayoutParams(
    val size: Vector2,
    val itemsPadding: Map<Int, Padding>,
    val itemsAlignment: Map<Int, Alignment>
)

