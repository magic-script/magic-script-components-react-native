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
class FlexGridManager(private val grid: UiGridLayout,
                      private val columns: Int,
                      private val rows: Int,
                      private val padding: Double
) : LayoutManager {

    // <index, column width> pairs
    private val columnsWidthMap = mutableMapOf<Int, Double>()

    override fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>) {
        columnsWidthMap.clear()
        for (i in 0 until children.size) {
            val col = i % columns
            val bounds = childrenBounds[i]!!
            val nodeWidth = bounds.right - bounds.left
            if (nodeWidth > columnsWidthMap[col] ?: 0.0) {
                columnsWidthMap[col] = nodeWidth.toDouble()
            }
        }
        for (i in 0 until children.size) {
            layoutNode(i, children[i], childrenBounds[i]!!)
        }
    }

    // sets the proper position for the child node
    private fun layoutNode(index: Int, node: Node, nodeBounds: Bounding) {
        val col = index % columns
        val row = index / columns
        // TODO pre-fill column width array
        val columnWidth = columnsWidthMap[col] ?: 0.0 // without padding
        val nodeWidth = nodeBounds.right - nodeBounds.left

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

        logMessage("child[$index], width=$nodeWidth, columnWidth=$columnWidth, bounds=$nodeBounds, boundsCenter=$boundsCenter, pivotOffset=$pivotOffset")

        // calculating y position for a child
        val startY = 0
        val nodeHeight = 0.07 // abs(nodeBounds.bottom - nodeBounds.top).toDouble()
        var y = startY - row * nodeHeight

        if (row > 0) {
            y -= row * padding
        }

        node.localPosition = Vector3(x.toFloat(), y.toFloat(), node.localPosition.z)
        logMessage("columnsWidthMap=$columnsWidthMap")
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