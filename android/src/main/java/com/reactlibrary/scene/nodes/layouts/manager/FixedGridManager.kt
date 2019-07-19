package com.reactlibrary.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.Alignment
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.layouts.UiGridLayout
import com.reactlibrary.utils.logMessage

/**
 * Grid manager that lay outs the children when grid size is specified (non 0)
 * Each column has fixed size equal to (grid_width - padding) / columns
 */
class FixedGridManager(private val grid: UiGridLayout,
                       private val columns: Int,
                       private val padding: Double) : LayoutManager {

    private var childIdx = 0

    override fun addNode(node: Node) {
        grid.addChild(node)
        val cellWidth = grid.width / columns
        val cellHeight = cellWidth // TODO
        val paddingSum = (columns - 1) * padding
        val startX = -grid.width / 2 - paddingSum / 2
        val startY = 0
        val col = childIdx % columns
        val row = childIdx / columns

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

        logMessage("addChildToLayout idx=$childIdx, " +
                "x=$x, " +
                "y=$y, " +
                "width=${grid.width}," +
                " columns=$columns," +
                " colWidth=$cellWidth")

        childIdx++
    }

}