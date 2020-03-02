/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.layouts.LayoutUtils
import com.magicleap.magicscript.scene.nodes.layouts.params.GridLayoutParams
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.sumByFloat
import kotlin.math.max

class GridLayoutManager : SizedLayoutManager<GridLayoutParams>() {

    // <column index, max child width in that column> pairs
    private val maxChildWidthInColumnMap = mutableMapOf<Int, Float>()

    // <row index, max child height in that row> pairs
    private val maxChildHeightInRowMap = mutableMapOf<Int, Float>()

    // column index, column width
    private val columnsWidthMap = mutableMapOf<Int, Float>()

    // row index, row height
    private val rowsHeightMap = mutableMapOf<Int, Float>()

    override fun layoutChildren(
        layoutParams: GridLayoutParams,
        children: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>
    ) {
        super.layoutChildren(layoutParams, children, childrenBounds)

        for (i in children.indices) {
            val node = childrenList[i]
            val nodeBounds = childrenBounds.getValue(node)
            val nodeInfo = LayoutUtils.createNodeInfo(i, node, nodeBounds)
            layoutNode(nodeInfo, layoutParams)
        }
    }

    override fun onPreLayout(
        children: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>,
        layoutParams: LayoutParams
    ) {
        super.onPreLayout(children, childrenBounds, layoutParams)

        maxChildWidthInColumnMap.clear()
        maxChildHeightInRowMap.clear()
        columnsWidthMap.clear()
        rowsHeightMap.clear()

        for (i in children.indices) {
            layoutParams as GridLayoutParams
            val col = LayoutUtils.getColumnIndex(i, layoutParams.columns, layoutParams.rows)
            val row = LayoutUtils.getRowIndex(i, layoutParams.columns, layoutParams.rows)
            val node = children[i]
            val bounds = childrenBounds[node] ?: AABB()

            val width = bounds.size().x
            if (width > maxChildWidthInColumnMap[col] ?: 0.0F) {
                maxChildWidthInColumnMap[col] = width
            }

            val padding = layoutParams.itemsPadding[node] ?: Padding()

            val childWidthWithPadding = width + padding.left + padding.right
            columnsWidthMap[col] = max(columnsWidthMap[col] ?: 0f, childWidthWithPadding)

            val height = bounds.size().y
            if (height > maxChildHeightInRowMap[row] ?: 0.0F) {
                maxChildHeightInRowMap[row] = height
            }

            val childHeightWithPadding = height + padding.top + padding.bottom
            rowsHeightMap[row] = max(rowsHeightMap[row] ?: 0f, childHeightWithPadding)
        }

        val layoutSize = layoutParams.size

        if (layoutSize.x != WRAP_CONTENT_DIMENSION) {
            val columnsSumWidth = columnsWidthMap.values.sumByFloat { it }
            val columnsScale = layoutSize.x / columnsSumWidth
            maxChildWidthInColumnMap.forEach {
                maxChildWidthInColumnMap[it.key] = columnsScale * it.value
            }

            columnsWidthMap.forEach {
                columnsWidthMap[it.key] = columnsScale * it.value
            }
        }

        if (layoutSize.y != WRAP_CONTENT_DIMENSION) {
            val rowsSumHeight = rowsHeightMap.values.sumByFloat { it }
            val rowsScale = layoutSize.y / rowsSumHeight
            maxChildHeightInRowMap.forEach {
                maxChildHeightInRowMap[it.key] = rowsScale * it.value
            }

            rowsHeightMap.forEach {
                rowsHeightMap[it.key] = rowsScale * it.value
            }
        }
    }

    private fun layoutNode(nodeInfo: NodeInfo, layoutParams: GridLayoutParams) {
        val index = nodeInfo.index
        val itemPadding = layoutParams.itemsPadding[nodeInfo.node] ?: Padding()

        val col = LayoutUtils.getColumnIndex(index, layoutParams.columns, layoutParams.rows)
        val row = LayoutUtils.getRowIndex(index, layoutParams.columns, layoutParams.rows)

        val columnWidth = columnsWidthMap[col] ?: 0f
        val rowHeight = rowsHeightMap[row] ?: 0f

        // calculating x position for a child
        val columnX = getColumnX(col)
        val itemAlignment = layoutParams.itemsAlignment[nodeInfo.node] ?: Alignment()

        val x = when (itemAlignment.horizontal) {
            Alignment.Horizontal.LEFT -> {
                columnX + nodeInfo.width / 2 + nodeInfo.pivotOffsetX + itemPadding.left
            }

            Alignment.Horizontal.CENTER -> {
                val paddingDiff = itemPadding.left - itemPadding.right
                columnX + columnWidth / 2 + nodeInfo.pivotOffsetX + paddingDiff
            }

            Alignment.Horizontal.RIGHT -> {
                columnX + columnWidth - nodeInfo.width / 2 + nodeInfo.pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val rowY = getRowY(row)

        val y = when (itemAlignment.vertical) {
            Alignment.Vertical.TOP -> {
                rowY - nodeInfo.height / 2 + nodeInfo.pivotOffsetY - itemPadding.top
            }

            Alignment.Vertical.CENTER -> {
                val paddingDiff = itemPadding.top - itemPadding.bottom
                rowY - rowHeight / 2 + nodeInfo.pivotOffsetY - paddingDiff
            }

            Alignment.Vertical.BOTTOM -> {
                rowY - rowHeight + nodeInfo.height / 2 + nodeInfo.pivotOffsetY + itemPadding.bottom
            }
        }

        val node = nodeInfo.node
        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    override fun getLayoutBounds(layoutParams: GridLayoutParams): AABB {
        val width = if (layoutParams.size.x == WRAP_CONTENT_DIMENSION)
            columnsWidthMap.values.sumByFloat { it }
        else {
            layoutParams.size.x
        }

        val height = if (layoutParams.size.y == WRAP_CONTENT_DIMENSION) {
            rowsHeightMap.values.sumByFloat { it }
        } else {
            layoutParams.size.y
        }

        val minZ = LayoutUtils.getMinZ(childrenList, childrenBounds)
        val maxZ = LayoutUtils.getMaxZ(childrenList, childrenBounds)

        return AABB(min = Vector3(0f, -height, minZ), max = Vector3(width, 0f, maxZ))
    }

    override fun getMaxChildWidth(childIdx: Int, layoutParams: GridLayoutParams): Float {
        return if (layoutParams.size.x == WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            val col = LayoutUtils.getColumnIndex(childIdx, layoutParams.columns, layoutParams.rows)
            maxChildWidthInColumnMap[col] ?: 0f
        }
    }

    override fun getMaxChildHeight(childIdx: Int, layoutParams: GridLayoutParams): Float {
        return if (layoutParams.size.y == WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            val row = LayoutUtils.getRowIndex(childIdx, layoutParams.columns, layoutParams.rows)
            maxChildHeightInRowMap[row] ?: 0f
        }
    }

    // returns the position (x) of a column at the given index (includes padding)
    private fun getColumnX(columnIdx: Int): Float {
        var x = 0.0F // start
        for (i in 0 until columnIdx) {
            x += columnsWidthMap[i] ?: 0.0F
        }
        return x
    }

    // returns the position (y) of a row at the given index (includes padding)
    private fun getRowY(rowIdx: Int): Float {
        var y = 0.0F // start
        for (i in 0 until rowIdx) {
            y -= rowsHeightMap[i] ?: 0.0F
        }
        return y
    }

}