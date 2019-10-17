package com.reactlibrary.scene.nodes.layouts.manager

import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding

interface RectLayoutManager: LayoutManager {

    var parentBounds: Bounding?

    var itemPadding: Padding

    var itemHorizontalAlignment: Alignment.HorizontalAlignment

    var itemVerticalAlignment: Alignment.VerticalAlignment
}