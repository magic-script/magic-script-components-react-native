package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.scene.nodes.layouts.manager.FlexGridManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils

class UiGridLayout(props: ReadableMap) : UiLayout(props) {

    companion object {
        // properties
        const val PROP_COLUMNS = "columns"
        const val PROP_ROWS = "rows"
        const val PROP_ITEM_PADDING = "itemPadding"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_ITEM_ALIGNMENT = "itemAlignment"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"

        // default values
        const val COLUMNS_DEFAULT = 1
        const val ROWS_DEFAULT = 0 // 0 means unspecified (will grow with content)
    }

    init {
        layoutManager = FlexGridManager(this)
    }

    var columns: Int = properties.getDouble(PROP_COLUMNS, COLUMNS_DEFAULT.toDouble()).toInt()
        private set(value) {
            if (value == 0 && rows == 0) {
                field = 1 // can't be 0 along with rows
            } else {
                field = value
            }

        }

    var rows: Int = properties.getDouble(PROP_ROWS, ROWS_DEFAULT.toDouble()).toInt()
        private set(value) {
            if (value == 0 && columns == 0) {
                field = 1 // can't be 0 along with columns
            } else {
                field = value
            }
        }

    // default padding for each item [top, right, bottom, left]
    var itemPadding = Padding(0F, 0F, 0F, 0F)
        private set

    var itemHorizontalAlignment = ViewRenderable.HorizontalAlignment.CENTER
        private set

    var itemVerticalAlignment = ViewRenderable.VerticalAlignment.CENTER
        private set

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setColumns(props)
        setRows(props)
        setItemPadding(props)
        setItemAlignment(props)
    }

    override fun getBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(children)
        return Bounding(
                childBounds.left + localPosition.x - itemPadding.left,
                childBounds.bottom + localPosition.y - itemPadding.bottom,
                childBounds.right + localPosition.x + itemPadding.right,
                childBounds.top + localPosition.y + itemPadding.top
        )
    }

    private fun setColumns(props: Bundle) {
        if (props.containsKey(PROP_COLUMNS)) {
            columns = props.getDouble(PROP_COLUMNS).toInt()
            requestLayout()
        }
    }

    private fun setRows(props: Bundle) {
        if (props.containsKey(PROP_ROWS)) {
            rows = props.getDouble(PROP_ROWS).toInt()
            requestLayout()
        }
    }

    private fun setItemPadding(props: Bundle) {
        var padding = PropertiesReader.readPadding(props, PROP_ITEM_PADDING)
        if (padding == null) {
            padding = PropertiesReader.readPadding(props, PROP_DEFAULT_ITEM_PADDING)
        }
        if (padding != null) {
            itemPadding = padding
            requestLayout()
        }
    }

    private fun setItemAlignment(props: Bundle) {
        var alignment = props.getString(PROP_ITEM_ALIGNMENT)
        if (alignment == null) {
            alignment = props.getString(PROP_DEFAULT_ITEM_ALIGNMENT)
        }

        if (alignment != null) {
            val alignmentArray = alignment.split("-")
            if (alignmentArray.size == 2) {
                val verticalAlign = alignmentArray[0]
                val horizontalAlign = alignmentArray[1]
                itemVerticalAlignment = ViewRenderable.VerticalAlignment.valueOf(verticalAlign.toUpperCase())
                itemHorizontalAlignment = ViewRenderable.HorizontalAlignment.valueOf(horizontalAlign.toUpperCase())
            }
        }
    }

}