package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Vector2

data class LayoutInfo<T : LayoutParams>(
    val childrenBounds: Map<Int, Bounding>,
    val contentSize: Vector2,
    val layoutSizeLimit: Vector2,
    val layoutParams: T
)