package com.reactlibrary.scene.nodes.layouts.manager

import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding

interface RectLayoutManager: LayoutManager {

    var itemPadding: Padding

    var contentHorizontalAlignment: Alignment.HorizontalAlignment

    var contentVerticalAlignment: Alignment.VerticalAlignment
}