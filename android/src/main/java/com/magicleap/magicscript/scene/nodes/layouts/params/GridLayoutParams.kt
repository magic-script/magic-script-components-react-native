package com.magicleap.magicscript.scene.nodes.layouts.params

import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

class GridLayoutParams(
    val columns: Int,
    val rows: Int,
    size: Vector2,
    itemPadding: Padding,
    itemHorizontalAlignment: Alignment.HorizontalAlignment,
    itemVerticalAlignment: Alignment.VerticalAlignment
) : LayoutParams(size, itemPadding, itemHorizontalAlignment, itemVerticalAlignment)



