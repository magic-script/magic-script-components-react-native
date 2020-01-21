package com.magicleap.magicscript.scene.nodes.layouts.params

import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.ItemPaddingMap
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

class PageViewLayoutParams(
    val visiblePage: Int,
    size: Vector2,
    defaultItemPadding: Padding,
    itemHorizontalAlignment: Alignment.HorizontalAlignment,
    itemVerticalAlignment: Alignment.VerticalAlignment,
    itemsPadding: ItemPaddingMap? = null
) : LayoutParams(
    size,
    defaultItemPadding,
    itemHorizontalAlignment,
    itemVerticalAlignment,
    itemsPadding
)