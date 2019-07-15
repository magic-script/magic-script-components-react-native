package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.utils.logMessage

class UiGridLayout(props: ReadableMap) : UiLayout(props) {

    companion object {
        // properties
        const val PROP_COLUMNS = "columns"
        const val PROP_ROWS = "rows"
        const val PROP_ITEM_PADDING = "itemPadding"
    }

    private var columns: Int = 2
    private var rows: Int? = null
    private var itemPadding = 0.1 // in meters

    private var childCounter = 0

    override fun loadRenderable(): Boolean {
        // it does not contain its own renderable
        return false
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setColumns(props)
        setRows(props)
        setItemPadding(props)
    }

    override fun addChildToLayout(child: Node) {
        logMessage("add child to grid, grid size = [$width, $height]")
        addChild(child)
        //TODO child.localPosition =
        childCounter++
    }

    private fun setColumns(props: Bundle) {
        if (props.containsKey(PROP_COLUMNS)) {
            this.columns = props.getInt(PROP_COLUMNS)
        }
    }

    private fun setRows(props: Bundle) {
        logMessage("setting rows")
        if (props.containsKey(PROP_ROWS)) {
            this.rows = props.getInt(PROP_ROWS)
        }
    }

    private fun setItemPadding(props: Bundle) {
        if (props.containsKey(PROP_ITEM_PADDING)) {
            this.itemPadding = props.getDouble(PROP_ITEM_PADDING)
        }
    }

}