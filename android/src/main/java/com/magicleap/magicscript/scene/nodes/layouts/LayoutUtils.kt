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
        children: List<TransformNode>,
        defaultPadding: Padding,
        childPaddings: Map<Int, Padding>? = null
    ) = mutableMapOf<TransformNode, Padding>()
        .apply {
            for (i in children.indices) {
                this[children[i]] = childPaddings?.get(i) ?: defaultPadding
            }
        }

    fun createChildrenAlignmentMap(
        children: List<TransformNode>,
        defaultAlignment: Alignment,
        childAlignments: Map<Int, Alignment>? = null
    ) = mutableMapOf<TransformNode, Alignment>()
        .apply {
            for (i in children.indices) {
                this[children[i]] = childAlignments?.get(i) ?: defaultAlignment
            }
        }

    fun createChildrenPaddingMap(
        columns: Int,
        rows: Int,
        children: List<TransformNode>,
        defaultPadding: Padding,
        childPaddings: Map<Pair<Int, Int>, Padding>? = null
    ) = mutableMapOf<TransformNode, Padding>()
        .apply {
            for (i in children.indices) {
                val column = getColumnIndex(i, columns, rows)
                val row = getRowIndex(i, columns, rows)
                this[children[i]] = childPaddings?.get(Pair(column, row)) ?: defaultPadding
            }
        }

    fun createChildrenAlignmentMap(
        columns: Int,
        rows: Int,
        children: List<TransformNode>,
        defaultAlignment: Alignment,
        childAlignments: Map<Pair<Int, Int>, Alignment>? = null
    ) = mutableMapOf<TransformNode, Alignment>()
        .apply {
            for (i in children.indices) {
                val column = getColumnIndex(i, columns, rows)
                val row = getRowIndex(i, columns, rows)
                this[children[i]] = childAlignments?.get(Pair(column, row)) ?: defaultAlignment
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

    fun getVerticalPaddingSumOf(
        children: List<TransformNode>,
        paddingMap: Map<TransformNode, Padding>
    ): Float {
        var sumPadding = 0f
        for (child in children) {
            val top = paddingMap[child]?.top ?: 0f
            val bottom = paddingMap[child]?.bottom ?: 0f
            sumPadding += top + bottom
        }
        return sumPadding
    }

    fun getHorizontalPaddingSumOf(
        children: List<TransformNode>,
        paddingMap: Map<TransformNode, Padding>
    ): Float {
        var sumPadding = 0f
        for (child in children) {
            val left = paddingMap[child]?.left ?: 0f
            val right = paddingMap[child]?.right ?: 0f
            sumPadding += left + right
        }
        return sumPadding
    }

    fun getVerticalBoundsSumOf(children: List<TransformNode>, bounds: Map<TransformNode, AABB>):
            Float {
        var sumBounds = 0f
        for (child in children) {
            sumBounds += bounds[child]?.size()?.y ?: 0f
        }
        return sumBounds
    }

    fun getHorizontalBoundsSumOf(
        children: List<TransformNode>,
        bounds: Map<TransformNode, AABB>
    ): Float {
        var sumBounds = 0f
        for (child in children) {
            sumBounds += bounds[child]?.size()?.x ?: 0f
        }
        return sumBounds
    }

    fun getMinZ(
        childrenList: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>
    ): Float {
        val filteredBounds = childrenBounds.filter { it.key in childrenList }
        return filteredBounds.values.minBy { it.min.z }?.min?.z ?: 0f
    }

    fun getMaxZ(
        childrenList: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>
    ): Float {
        val filteredBounds = childrenBounds.filter { it.key in childrenList }
        return filteredBounds.values.maxBy { it.max.z }?.max?.z ?: 0f
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