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

        var cellWidth = columnsWidthMap[col] ?: 0.0
        val nodeBounds = Utils.calculateBoundsOfNode(node)
        logMessage("bounds=$nodeBounds")
        val nodeWidth = nodeBounds.right - nodeBounds.left
        if (nodeWidth > cellWidth) {
            cellWidth = nodeWidth.toDouble()
            columnsWidthMap[col] = cellWidth
        }

        val cellHeight = abs(nodeBounds.bottom - nodeBounds.top).toDouble()

        val paddingSum = (columns - 1) * padding
        val startX = -grid.width / 2 - paddingSum / 2
        val startY = 0


        var x = startX + col * cellWidth
        var y = startY - row * cellHeight

        if (col > 0) {
            x += col * padding
        }
        if (row > 0) {
            y -= row * padding
        }

        // TODO in order to apply alignment the item's width and height must be known
        if (grid.itemHorizontalAlignment == Alignment.Horizontal.CENTER) {
            x += cellWidth / 2
        }

        if (grid.itemVerticalAlignment == Alignment.Vertical.CENTER) {
            y -= cellHeight / 2
        }

        node.localPosition = Vector3(x.toFloat(), y.toFloat(), node.localPosition.z)

        logMessage("addChildToLayout idx=$index, " +
                "x=$x, " +
                "y=$y, " +
                "width=${grid.width}," +
                " columns=$columns," +
                " colWidth=$cellWidth")
    }

}