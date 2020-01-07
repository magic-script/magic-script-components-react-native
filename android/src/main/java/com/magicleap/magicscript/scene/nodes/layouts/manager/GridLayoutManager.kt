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
import com.magicleap.magicscript.scene.nodes.layouts.params.GridLayoutParams
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.sumByFloat

class GridLayoutManager : SizedLayoutManager<GridLayoutParams>() {

    // <column index, max child width in that column> pairs
    private val maxChildWidthInColumnMap = mutableMapOf<Int, Float>()

    // <row index, max child height in that row> pairs
    private val maxChildHeightInRowMap = mutableMapOf<Int, Float>()

    override fun onPreLayout(
        children: List<TransformNode>,
        childrenBounds: Map<Int, Bounding>,
        layoutParams: LayoutParams
    ) {
        super.onPreLayout(children, childrenBounds, layoutParams)

        maxChildWidthInColumnMap.clear()
        maxChildHeightInRowMap.clear()
        for (i in children.indices) {
            layoutParams as GridLayoutParams
            val col = getColumnIndex(i, layoutParams)
            val row = getRowIndex(i, layoutParams)
            val bounds = childrenBounds[i]!!

            val width = bounds.size().x
            if (width > maxChildWidthInColumnMap[col] ?: 0.0F) {
                maxChildWidthInColumnMap[col] = width
            }

            val height = bounds.size().y
            if (height > maxChildHeightInRowMap[row] ?: 0.0F) {
                maxChildHeightInRowMap[row] = height
            }
        }

        val itemPadding = layoutParams.itemPadding
        val layoutSize = layoutParams.size
        if (layoutSize.x != WRAP_CONTENT_DIMENSION) {
            val paddingHorizontal = itemPadding.left + itemPadding.right
            val columnsSumWidth =
                maxChildWidthInColumnMap.values.sum() + maxChildWidthInColumnMap.size * paddingHorizontal
            val columnsScale = layoutSize.x / columnsSumWidth
            maxChildWidthInColumnMap.forEach {
                maxChildWidthInColumnMap[it.key] = columnsScale * it.value
            }
        }

        if (layoutSize.y != WRAP_CONTENT_DIMENSION) {
            val paddingVertical = itemPadding.top + itemPadding.bottom
            val rowsSumHeight =
                maxChildHeightInRowMap.values.sum() + maxChildHeightInRowMap.size * paddingVertical
            val rowsScale = layoutSize.y / rowsSumHeight
            maxChildHeightInRowMap.forEach {
                maxChildHeightInRowMap[it.key] = rowsScale * it.value
            }
        }
    }

    override fun <T : LayoutParams> layoutNode(
        nodeInfo: NodeInfo,
        layoutInfo: LayoutInfo<T>
    ) {
        val layoutParams = layoutInfo.params as GridLayoutParams
        val col = getColumnIndex(nodeInfo.index, layoutParams)
        val row = getRowIndex(nodeInfo.index, layoutParams)

        val itemPadding = layoutParams.itemPadding
        val columnWidth =
            maxChildWidthInColumnMap[col] ?: 0.0F + itemPadding.left + itemPadding.right

        val rowHeight = maxChildHeightInRowMap[row] ?: 0.0F + itemPadding.bottom + itemPadding.top

        // calculating x position for a child
        val columnX = getColumnX(col, itemPadding)

        val x = when (layoutParams.itemHorizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                columnX + nodeInfo.width / 2 + nodeInfo.pivotOffsetX + itemPadding.left
            }

            Alignment.HorizontalAlignment.CENTER -> {
                val paddingDiff = itemPadding.left - itemPadding.right
                columnX + columnWidth / 2 + nodeInfo.pivotOffsetX + paddingDiff
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                columnX + columnWidth - nodeInfo.width / 2 + nodeInfo.pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val rowY = getRowY(row, itemPadding)
        val y = when (layoutParams.itemVerticalAlignment) {
            Alignment.VerticalAlignment.TOP -> {
                rowY - nodeInfo.height / 2 + nodeInfo.pivotOffsetY - itemPadding.top
            }

            Alignment.VerticalAlignment.CENTER -> {
                val paddingDiff = itemPadding.top - itemPadding.bottom
                rowY - rowHeight / 2 + nodeInfo.pivotOffsetY - paddingDiff
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                rowY - rowHeight + nodeInfo.height / 2 + nodeInfo.pivotOffsetY + itemPadding.bottom
            }
        }

        val node = nodeInfo.node
        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    override fun getContentWidth(
        childrenBounds: Map<Int, Bounding>,
        layoutParams: GridLayoutParams
    ): Float {
        val itemPadding = layoutParams.itemPadding
        val paddingHorizontal = itemPadding.left + itemPadding.right
        val paddingSum = childrenBounds.size * paddingHorizontal
        return childrenBounds.values.sumByFloat { it.size().x } + paddingSum
    }

    override fun getContentHeight(
        childrenBounds: Map<Int, Bounding>,
        layoutParams: GridLayoutParams
    ): Float {
        val itemPadding = layoutParams.itemPadding
        val paddingVertical = itemPadding.top + itemPadding.bottom
        val paddingSum = childrenBounds.size * paddingVertical
        return childrenBounds.values.sumByFloat { it.size().y } + paddingSum
    }

    override fun calculateMaxChildWidth(
        childIdx: Int,
        childrenBounds: Map<Int, Bounding>,
        layoutParams: GridLayoutParams
    ): Float {
        return if (layoutParams.size.x == WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            maxChildWidthInColumnMap[getColumnIndex(childIdx, layoutParams)]!!
        }
    }

    override fun calculateMaxChildHeight(
        childIdx: Int,
        childrenBounds: Map<Int, Bounding>,
        layoutParams: GridLayoutParams
    ): Float {
        return if (layoutParams.size.y == WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            maxChildHeightInRowMap[getRowIndex(childIdx, layoutParams)]!!
        }
    }

    // returns the position (x) of a column at the given index (includes padding)
    private fun getColumnX(columnIdx: Int, itemPadding: Padding): Float {
        var x = 0.0F // start
        for (i in 0 until columnIdx) {
            x += (maxChildWidthInColumnMap[i] ?: 0.0F) + itemPadding.left + itemPadding.right
        }
        return x
    }

    // returns the position (y) of a row at the given index (includes padding)
    private fun getRowY(rowIdx: Int, itemPadding: Padding): Float {
        var y = 0.0F // start
        for (i in 0 until rowIdx) {
            y -= (maxChildHeightInRowMap[i] ?: 0.0F) + itemPadding.top + itemPadding.bottom
        }
        return y
    }

    private fun getColumnIndex(childIdx: Int, layoutParams: GridLayoutParams): Int {
        return if (layoutParams.rows != 0) {
            childIdx / layoutParams.rows
        } else {
            childIdx % layoutParams.columns
        }
    }

    private fun getRowIndex(childIdx: Int, layoutParams: GridLayoutParams): Int {
        return if (layoutParams.rows != 0) {
            childIdx % layoutParams.rows
        } else {
            childIdx / layoutParams.columns
        }
    }

}