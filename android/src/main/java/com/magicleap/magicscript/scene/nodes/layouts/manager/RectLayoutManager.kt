package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.scene.nodes.layouts.LayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding

interface RectLayoutManager: LayoutManager {

    var itemPadding: Padding

    var contentHorizontalAlignment: Alignment.HorizontalAlignment

    var contentVerticalAlignment: Alignment.VerticalAlignment
}