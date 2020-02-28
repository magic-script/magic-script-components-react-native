package com.magicleap.magicscript.scene.nodes.layouts

import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.NodeInfo
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2

object LayoutUtils {

    fun createChildrenPaddingMap(
        childCount: Int,
        defaultPadding: Padding,
        childPaddings: Map<Int, Padding>? = null
    ) = mutableMapOf<Int, Padding>()
        .apply {
            for (i in 0 until childCount) {
                this[i] = childPaddings?.get(i) ?: defaultPadding
            }
        }

    fun createChildrenAlignmentMap(
        childCount: Int,
        defaultAlignment: Alignment,
        childAlignments: Map<Int, Alignment>? = null
    ) = mutableMapOf<Int, Alignment>()
        .apply {
            for (i in 0 until childCount) {
                this[i] = childAlignments?.get(i) ?: defaultAlignment
            }
        }

    fun createChildrenPaddingMap(
        columns: Int,
        rows: Int,
        childCount: Int,
        defaultPadding: Padding,
        childPaddings: Map<Pair<Int, Int>, Padding>? = null
    ) = mutableMapOf<Int, Padding>()
        .apply {
            for (i in 0 until childCount) {
                val column = getColumnIndex(i, columns, rows)
                val row = getRowIndex(i, columns, rows)
                this[i] = childPaddings?.get(Pair(column, row)) ?: defaultPadding
            }
        }

    fun createChildrenAlignmentMap(
        columns: Int,
        rows: Int,
        childCount: Int,
        defaultAlignment: Alignment,
        childAlignments: Map<Pair<Int, Int>, Alignment>? = null
    ) = mutableMapOf<Int, Alignment>()
        .apply {
            for (i in 0 until childCount) {
                val column = getColumnIndex(i, columns, rows)
                val row = getRowIndex(i, columns, rows)
                this[i] = childAlignments?.get(Pair(column, row)) ?: defaultAlignment
            }
        }

    fun getColumnIndex(childIdx: Int, columns: Int, rows: Int): Int {
        return if (rows != 0) {
            childIdx / rows
        } else {
            childIdx % columns
        }
    }

    fun getRowIndex(childIdx: Int, columns: Int, rows: Int): Int {
        return if (rows != 0) {
            childIdx % rows
        } else {
            childIdx / columns
        }
    }

    fun getVerticalPaddingSumUntil(itemsPadding: Map<Int, Padding>, end: Int): Float {
        var sumPadding = 0f
        for (i in 0 until end) {
            val top = itemsPadding[i]?.top ?: 0f
            val bottom = itemsPadding[i]?.bottom ?: 0f
            sumPadding += top + bottom
        }
        return sumPadding
    }

    fun getHorizontalPaddingSumUntil(itemsPadding: Map<Int, Padding>, end: Int): Float {
        var sumPadding = 0f
        for (i in 0 until end) {
            val left = itemsPadding[i]?.left ?: 0f
            val right = itemsPadding[i]?.right ?: 0f
            sumPadding += left + right
        }
        return sumPadding
    }

    fun createNodeInfo(index: Int, node: TransformNode, nodeBounds: AABB): NodeInfo {
        val nodeWidth = nodeBounds.size().x
        val nodeHeight = nodeBounds.size().y
        val boundsCenterX = nodeBounds.min.x + nodeWidth / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val boundsCenterY = nodeBounds.max.y - nodeHeight / 2
        val pivotOffsetY = node.localPosition.y - boundsCenterY // aligning according to center

        return NodeInfo(node, index, nodeWidth, nodeHeight, pivotOffsetX, pivotOffsetY)
    }

    fun calculateLayoutSizeLimit(contentSize: Vector2, layoutSize: Vector2): Vector2 {
        val sizeLimitX = if (layoutSize.x == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            contentSize.x
        } else {
            layoutSize.x
        }

        val sizeLimitY = if (layoutSize.y == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            contentSize.y
        } else {
            layoutSize.y
        }

        return Vector2(sizeLimitX, sizeLimitY)
    }

}