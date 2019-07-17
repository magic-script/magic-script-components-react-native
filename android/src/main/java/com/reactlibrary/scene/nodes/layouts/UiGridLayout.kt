package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import android.os.Handler
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.utils.Bounding
import com.reactlibrary.utils.calculateBounds
import com.reactlibrary.utils.logMessage

class UiGridLayout(props: ReadableMap) : UiLayout(props) {

    companion object {
        // properties
        const val PROP_COLUMNS = "columns"
        const val PROP_ROWS = "rows"
        const val PROP_ITEM_PADDING = "itemPadding"
        const val PROP_ITEM_ALIGNMENT = "itemAlignment"
    }

    private var columns: Int = 2
    private var rows: Int? = null
    private var padding = 0.0 // in meters
    private var childIdx = 0

    private var itemHorizontalAlignment = HorizontalAlignment.CENTER
    private var itemVerticalAlignment = VerticalAlignment.CENTER

    private enum class HorizontalAlignment {
        LEFT, CENTER, RIGHT
    }

    private enum class VerticalAlignment {
        TOP, CENTER, BOTTOM
    }

    override fun loadRenderable(): Boolean {
        // it does not contain its own renderable
        Handler().postDelayed({
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

    override fun getBounding(): Bounding? {
        return children.calculateBounds()
    }

    override fun addChildNode(child: Node) {
        addChild(child)
        val columnWidth = width / columns
        val columnHeight = columnWidth // TODO
        val paddingSum = (columns - 1) * padding
        val startX = -width / 2 - paddingSum / 2
        val startY = 0
        val col = childIdx % columns
        val row = childIdx / columns

        var x = startX + col * columnWidth
        var y = startY - row * columnHeight

        if (col > 0) {
            x += col * padding
        }
        if (row > 0) {
            y -= row * padding
        }

        // TODO in order to apply alignment the item's width and height must be known
        if (itemHorizontalAlignment == HorizontalAlignment.CENTER) {
            x += columnWidth / 2
        }

        if (itemVerticalAlignment == VerticalAlignment.CENTER) {
            y -= columnHeight / 2
        }

        child.localPosition = Vector3(x.toFloat(), y.toFloat(), child.localPosition.z)
        logMessage("addChildToLayout idx=$childIdx, x=$x, y=$y, width=$width, columns=$columns, colWidth=$columnWidth")

        // TODO wait until renderable of a child is attached and get the
        // child size using collisionShape
        val collisionShape = child.renderable?.collisionShape as? Box
        if (collisionShape != null) {
            logMessage("child size=${collisionShape.size}")
        }

        childIdx++
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
                itemHorizontalAlignment = HorizontalAlignment.valueOf(horizontalAlign.toUpperCase())
                itemVerticalAlignment = VerticalAlignment.valueOf(verticalAlign.toUpperCase())
            }
        }
    }

}