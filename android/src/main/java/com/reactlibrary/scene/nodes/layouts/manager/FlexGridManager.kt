package com.reactlibrary.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.Alignment
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.layouts.UiGridLayout
import com.reactlibrary.utils.Bounding
import com.reactlibrary.utils.logMessage

/**
 * Grid manager for a grid layout with flexible columns' width.
 * Columns will grow to fit the content
 */
class FlexGridManager(private val grid: UiGridLayout) : LayoutManager {

    // <column index, column width> pairs
    private val columnsWidthMap = mutableMapOf<Int, Float>()

    // <row index, row height> pairs
    private val rowsHeightMap = mutableMapOf<Int, Float>()

    override fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>) {
        columnsWidthMap.clear()
        rowsHeightMap.clear()

        for (i in 0 until children.size) {
            val col = i % grid.columns
            val row = i / grid.columns
            val bounds = childrenBounds[i]!!
            val width = calculateColumnWidth(bounds)

            if (width > columnsWidthMap[col] ?: 0.0F) {
                columnsWidthMap[col] = width
            }

            val height = calculateRowHeight(bounds)
            if (height > rowsHeightMap[row] ?: 0.0F) {
                rowsHeightMap[row] = height
            }
        }

        for (i in 0 until children.size) {
            layoutNode(i, children[i], childrenBounds[i]!!)
        }
    }

    // sets the proper position for the child node
    private fun layoutNode(index: Int, node: Node, nodeBounds: Bounding) {
        val col = index % grid.columns
        val row = index / grid.columns

        val columnWidth = columnsWidthMap[col] ?: 0.0F
        val nodeWidth = nodeBounds.right - nodeBounds.left

        val rowHeight = rowsHeightMap[row] ?: 0.0F
        val nodeHeight = nodeBounds.top - nodeBounds.bottom

        // calculating x position for a child
        val boundsCenterX = nodeBounds.left + nodeWidth / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val boundsCenterY = nodeBounds.top - nodeHeight / 2
        val pivotOffsetY = node.localPosition.y - boundsCenterY  // aligning according to center

        val x = when (grid.itemHorizontalAlignment) {
            Alignment.Horizontal.LEFT -> {
                getColumnX(col) + nodeWidth / 2 + pivotOffsetX + grid.itemPadding.left
            }

            Alignment.Horizontal.CENTER -> {
                val paddingDiff = grid.itemPadding.left - grid.itemPadding.right
                getColumnX(col) + columnWidth / 2 + pivotOffsetX + paddingDiff
            }

            Alignment.Horizontal.RIGHT -> {
                getColumnX(col) + columnWidth - nodeWidth / 2 + pivotOffsetX - grid.itemPadding.right
            }
        }

        // calculating y position for a child
        val y = when (grid.itemVerticalAlignment) {
            Alignment.Vertical.TOP -> {
                getRowY(row) - nodeHeight / 2 + pivotOffsetY - grid.itemPadding.top
            }

            Alignment.Vertical.CENTER -> {
                val paddingDiff = grid.itemPadding.top - grid.itemPadding.bottom
                getRowY(row) - rowHeight / 2 + pivotOffsetY - paddingDiff
            }

            Alignment.Vertical.BOTTOM -> {
                getRowY(row) - rowHeight + nodeHeight / 2 + pivotOffsetY + grid.itemPadding.bottom
            }
        }

        logMessage("child[$index]," +
                "localPosBefore=${node.localPosition}")

        node.localPosition = Vector3(x, y, node.localPosition.z)

        logMessage("child[$index]," +
                "localPos=${node.localPosition}" +
                "width=$nodeWidth, " +
                "height=$nodeHeight, " +
                "columnWidth=$columnWidth, " +
                "rowHeight=$rowHeight, " +
                "bounds=$nodeBounds," +
                "boundsCenterX=$boundsCenterX, " +
                "pivotOffsetX=$pivotOffsetX" +
                "boundsCenterY=$boundsCenterY, " +
                "pivotOffsetY=$pivotOffsetY"
        )

    }

    // returns the position (x) of a column at the given index (includes padding)
    private fun getColumnX(columnIdx: Int): Float {
        var x = 0.0F // start
        for (i in 0 until columnIdx) {
            x += (columnsWidthMap[i] ?: 0.0F) // + padding
        }
        return x
    }

    // returns the position (y) of a row at the given index (includes padding)
    private fun getRowY(rowIdx: Int): Float {
        var y = 0.0F // start
        for (i in 0 until rowIdx) {
            y -= (rowsHeightMap[i] ?: 0.0F) // + padding
        }
        return y
    }

    private fun calculateColumnWidth(itemBounds: Bounding): Float {
        return itemBounds.right - itemBounds.left + grid.itemPadding.left + grid.itemPadding.right
    }

    private fun calculateRowHeight(itemBounds: Bounding): Float {
        return itemBounds.top - itemBounds.bottom + grid.itemPadding.top + grid.itemPadding.bottom
    }

}