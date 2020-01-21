package com.magicleap.magicscript.scene.nodes.layouts.params

import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.ItemAlignmentMap
import com.magicleap.magicscript.scene.nodes.props.ItemPaddingMap
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

open class LayoutParams(
    val size: Vector2,
    val defaultItemPadding: Padding,
    val itemHorizontalAlignment: Alignment.HorizontalAlignment,
    val itemVerticalAlignment: Alignment.VerticalAlignment,
    val itemsPadding: ItemPaddingMap? = null,
    val itemsAlignment: ItemAlignmentMap? = null
)

