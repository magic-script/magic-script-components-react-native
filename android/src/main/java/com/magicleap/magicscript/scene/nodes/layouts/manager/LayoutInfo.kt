package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.Vector2

data class LayoutInfo<T : LayoutParams>(
    val childrenBounds: Map<Int, AABB>,
    val contentSize: Vector2,
    val sizeLimit: Vector2,
    val params: T
)