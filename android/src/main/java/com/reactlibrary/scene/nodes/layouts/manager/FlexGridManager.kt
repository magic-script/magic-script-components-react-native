package com.reactlibrary.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.layouts.UiGridLayout
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.logMessage

/**
 * Grid layout's manager with flexible columns and rows size:
 * column and row will grow to fit the bounding (+ padding) of a child.
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
            val col = getColumnIndex(i)
            val row = getRowIndex(i)
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
        val col = getColumnIndex(index)
        val row = getRowIndex(index)

        val columnWidth = columnsWidthMap[col] ?: 0.0F
        val nodeWidth = nodeBounds.right - nodeBounds.left

        val rowHeight = rowsHeightMap[row] ?: 0.0F
        val nodeHeight = nodeBounds.top - nodeBounds.bottom

        val boundsCenterX = nodeBounds.left + nodeWidth / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val boundsCenterY = nodeBounds.top - nodeHeight / 2
        val pivotOffsetY = node.localPosition.y - boundsCenterY  // aligning according to center

        // calculating x position for a child
        val x = when (grid.itemHorizontalAlignment) {
            ViewRenderable.HorizontalAlignment.LEFT -> {
                getColumnX(col) + nodeWidth / 2 + pivotOffsetX + grid.itemPadding.left
            }

            ViewRenderable.HorizontalAlignment.CENTER -> {
                val paddingDiff = grid.itemPadding.left - grid.itemPadding.right
                getColumnX(col) + columnWidth / 2 + pivotOffsetX + paddingDiff
            }

            ViewRenderable.HorizontalAlignment.RIGHT -> {
                getColumnX(col) + columnWidth - nodeWidth / 2 + pivotOffsetX - grid.itemPadding.right
            }
        }

        // calculating y position for a child
        val y = when (grid.itemVerticalAlignment) {
            ViewRenderable.VerticalAlignment.TOP -> {
                getRowY(row) - nodeHeight / 2 + pivotOffsetY - grid.itemPadding.top
            }

            ViewRenderable.VerticalAlignment.CENTER -> {
                val paddingDiff = grid.itemPadding.top - grid.itemPadding.bottom
                getRowY(row) - rowHeight / 2 + pivotOffsetY - paddingDiff
            }

            ViewRenderable.VerticalAlignment.BOTTOM -> {
                getRowY(row) - rowHeight + nodeHeight / 2 + pivotOffsetY + grid.itemPadding.bottom
            }
        }

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

    private fun getColumnIndex(childIdx: Int): Int {
        return if (grid.rows != 0) {
            childIdx / grid.rows
        } else {
            childIdx % grid.columns
        }
    }

    private fun getRowIndex(childIdx: Int): Int {
        return if (grid.rows != 0) {
            childIdx % grid.rows
        } else {
            childIdx / grid.columns
        }
    }

}