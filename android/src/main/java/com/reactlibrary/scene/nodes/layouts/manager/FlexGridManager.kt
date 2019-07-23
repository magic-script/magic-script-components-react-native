package com.reactlibrary.scene.nodes.layouts.manager

import android.os.Handler
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.Alignment
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.layouts.UiGridLayout
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

/**
 * Grid manager for a grid layout with flexible columns' width.
 * Columns will grow to fit the content
 */
class FlexGridManager(private val grid: UiGridLayout,
                      private val columns: Int,
                      private val padding: Double) : LayoutManager {

    // <index, column width> pairs
    private val columnsWidthMap = mutableMapOf<Int, Double>()

    init {
        // TODO remove and re-layout children every some period
        // TODO  (implement it inside GridLayout)
        Handler().postDelayed({
            for (i in 0 until grid.children.size) {
                layoutNode(i, grid.children[i])
            }
        }, 3000)
    }

    override fun addNode(node: Node) {
        grid.addChild(node)
        val index = grid.children.size - 1
        layoutNode(index, node)
    }

    private fun layoutNode(index: Int, node: Node) {
        val col = index % columns
        val row = index / columns

        var columnWidth = columnsWidthMap[col] ?: 0.0 // without padding
        val nodeBounds = Utils.calculateBoundsOfNode(node)
        logMessage("bounds=$nodeBounds")
        val nodeWidth = nodeBounds.right - nodeBounds.left
        if (nodeWidth > columnWidth) {
            columnWidth = nodeWidth.toDouble()
            columnsWidthMap[col] = columnWidth
        }

        // TODO center entire grid every time new child is added

        // calculating x position for a child
        val boundsCenter = nodeBounds.left + nodeWidth / 2
        val pivotOffset = node.localPosition.x - boundsCenter // aligning according to center pivot

        val x = when (grid.itemHorizontalAlignment) {
            Alignment.Horizontal.LEFT -> {
                getColumnX(col) + nodeWidth / 2 + pivotOffset
            }

            Alignment.Horizontal.CENTER -> {
                getColumnX(col) + columnWidth / 2 + pivotOffset
            }

            Alignment.Horizontal.RIGHT -> {
                getColumnX(col) + columnWidth - nodeWidth / 2 + pivotOffset
            }
        }

        logMessage("child[$index], width=$nodeWidth, columnWidth=$columnWidth boundsCenter=$boundsCenter, pivotOffset=$pivotOffset, align=${grid.itemHorizontalAlignment}")

        // calculating y position for a child
        val startY = 0
        val nodeHeight = 0.07 // abs(nodeBounds.bottom - nodeBounds.top).toDouble()
        var y = startY - row * nodeHeight

        if (row > 0) {
            y -= row * padding
        }

        node.localPosition = Vector3(x.toFloat(), y.toFloat(), node.localPosition.z)

        logMessage("addChildToLayout idx=$index")
    }

    // returns the starting position of a column at the given index (includes padding)
    private fun getColumnX(columnIdx: Int): Double {
        var x = 0.0
        for (i in 0 until columnIdx) {
            x += columnsWidthMap[i] ?: 0.0 + padding
        }
        return x
    }

}