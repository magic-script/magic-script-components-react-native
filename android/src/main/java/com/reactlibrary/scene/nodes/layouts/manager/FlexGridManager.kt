package com.reactlibrary.scene.nodes.layouts.manager

import android.os.Handler
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.Alignment
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.layouts.UiGridLayout
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage
import kotlin.math.abs

/**
 * Grid manager for a grid layout with flexible columns' width
 * (when width of the grid is not specified)
 */
class FlexGridManager(private val grid: UiGridLayout,
                      private val columns: Int,
                      private val padding: Double) : LayoutManager {

    private val columnsWidthMap = mutableMapOf<Int, Double>() // index, column width

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

        var columnWidth = columnsWidthMap[col] ?: 0.0
        val nodeBounds = Utils.calculateBoundsOfNode(node)
        logMessage("bounds=$nodeBounds")
        val nodeWidth = nodeBounds.right - nodeBounds.left
        if (nodeWidth > columnWidth) {
            columnWidth = nodeWidth.toDouble()
            columnsWidthMap[col] = columnWidth
        }

        val nodeHeight = abs(nodeBounds.bottom - nodeBounds.top).toDouble()

        // TODO center entire grid every time new child is added ?
        val startY = 0

        var x = getCellX(col)
        var y = startY - row * nodeHeight

        if (row > 0) {
            y -= row * padding
        }

        // applying only horizontal alignment

        val nodePos = node.localPosition.x
        val boundsCenter = (nodeBounds.right - nodeBounds.left) / 2
        logMessage("nodePos=$nodePos bounds center=$boundsCenter")

        // TODO check node's own alignment (if node position x != (right - left / 2), add proper shift)
        if (grid.itemHorizontalAlignment == Alignment.Horizontal.CENTER) {
            x += (columnWidth - nodeWidth) / 2 //shift node to the center
        }

        if (grid.itemHorizontalAlignment == Alignment.Horizontal.RIGHT) {
            x += columnWidth - nodeWidth
        }

        node.localPosition = Vector3(x.toFloat(), y.toFloat(), node.localPosition.z)

        logMessage("addChildToLayout idx=$index, " +
                "x=$x, " +
                "y=$y, " +
                "width=${grid.width}," +
                " columns=$columns," +
                " colWidth=$columnWidth")
    }

    // return the starting position of cell at given index
    private fun getCellX(columnIdx: Int): Double {
        var x = 0.0
        for (i in 0 until columnIdx) {
            x += columnsWidthMap[i] ?: 0.0 + padding
        }
        return x
    }

}