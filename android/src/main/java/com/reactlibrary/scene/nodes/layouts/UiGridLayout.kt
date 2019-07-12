package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.base.UiLayout

class UiGridLayout(props: ReadableMap) : UiLayout(props) {

    companion object {
        // properties
        const val PROP_ITEM_PADDING = "itemPadding"
    }

    private var columns = 2
    private var itemPadding = 0.1 // in meters
    private var width = properties.getDouble(PROP_WIDTH, 1.0)


    override fun loadRenderable(): Boolean {
        // it does not contain its own renderable
        return false
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setItemPadding(props)
    }

    override fun addChildToLayout(child: Node) {

    }

    private fun setItemPadding(props: Bundle) {
        if(props.containsKey(PROP_ITEM_PADDING)) {
            this.itemPadding = props.getDouble(PROP_ITEM_PADDING)
        }
    }

}