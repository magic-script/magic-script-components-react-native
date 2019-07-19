package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import android.os.Handler
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.Alignment
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.scene.nodes.layouts.manager.FixedGridManager
import com.reactlibrary.scene.nodes.layouts.manager.FlexGridManager
import com.reactlibrary.utils.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

class UiGridLayout(props: ReadableMap) : UiLayout(props) {

    companion object {
        // properties
        const val PROP_COLUMNS = "columns"
        const val PROP_ROWS = "rows"
        const val PROP_ITEM_PADDING = "itemPadding"
        const val PROP_ITEM_ALIGNMENT = "itemAlignment"

        private const val COLUMNS_DEFAULT = 2
        private const val ROWS_DEFAULT = 0 // 0 means unspecified (will grow with content)
    }

    var itemHorizontalAlignment = Alignment.Horizontal.CENTER
        private set

    var itemVerticalAlignment = Alignment.Vertical.CENTER
        private set

    private var columns: Int = properties.getDouble(PROP_COLUMNS, COLUMNS_DEFAULT.toDouble()).toInt()

    private var rows: Int = properties.getDouble(PROP_ROWS, ROWS_DEFAULT.toDouble()).toInt()

    private var padding = properties.getDouble(PROP_ITEM_PADDING, 0.0) // in meters

    private var layoutManager: LayoutManager

    init {
        layoutManager = if (width == 0.0) {
            FlexGridManager(this, columns, padding)
        } else {
            FixedGridManager(this, columns, padding)
        }
    }

    override fun loadRenderable(): Boolean {

        // TODO remove handler (for tests only)
        Handler().postDelayed({
            children.forEachIndexed { index, node ->
                val childBounds = if (node is TransformNode) node.getBounding() else Bounding()
                logMessage("child[$index] bounds= $childBounds")
            }

            val bounds = getBounding()
            logMessage("grid bounds= $bounds")
        }, 3000)

        return false
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setColumns(props)
        setRows(props)
        setItemPadding(props)
        setItemAlignment(props)
    }

    override fun getBounding(): Bounding {
        return Utils.calculateBounds(children)
    }

    override fun addChildNode(child: Node) {
        layoutManager.addNode(child)
    }

    private fun setColumns(props: Bundle) {
        if (props.containsKey(PROP_COLUMNS)) {
            columns = props.getDouble(PROP_COLUMNS).toInt()
            if (columns <= 0) {
                columns = 1
            }
            logMessage("setting columns: $columns")
        }
    }

    private fun setRows(props: Bundle) {
        if (props.containsKey(PROP_ROWS)) {
            this.rows = props.getDouble(PROP_ROWS).toInt()
            logMessage("setting rows: $rows")
        }
    }

    private fun setItemPadding(props: Bundle) {
        if (props.containsKey(PROP_ITEM_PADDING)) {
            this.padding = props.getDouble(PROP_ITEM_PADDING)
        }
    }

    private fun setItemAlignment(props: Bundle) {
        if (props.containsKey(PROP_ITEM_ALIGNMENT)) {
            // TODO check the alignment array format
            val alignment = props.getSerializable(PROP_ITEM_ALIGNMENT) as ArrayList<String>
            if (alignment.size == 2) {
                val horizontalAlign = alignment[0]
                val verticalAlign = alignment[1]
                itemHorizontalAlignment = Alignment.Horizontal.valueOf(horizontalAlign.toUpperCase())
                itemVerticalAlignment = Alignment.Vertical.valueOf(verticalAlign.toUpperCase())
            }
        }
    }

}