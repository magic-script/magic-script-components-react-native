package com.magicleap.magicscript.scene.nodes.layouts.params

import com.magicleap.magicscript.scene.nodes.model.ItemPaddingMap
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

class LinearLayoutParams(
    val orientation: String,
    size: Vector2,
    defaultItemPadding: Padding,
    itemHorizontalAlignment: Alignment.HorizontalAlignment,
    itemVerticalAlignment: Alignment.VerticalAlignment,
    itemsPadding: ItemPaddingMap?
) : LayoutParams(
    size,
    defaultItemPadding,
    itemHorizontalAlignment,
    itemVerticalAlignment,
    itemsPadding
)



