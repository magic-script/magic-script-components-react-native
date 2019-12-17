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

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.getUserSpecifiedScale
import kotlin.math.min

/**
 * Grid layout's manager with flexible columns and rows size:
 * column and row will grow to fit the bounding (+ padding) of a child.
 */
class GridLayoutManagerImpl : GridLayoutManager {

    override var parentWidth: Float = WRAP_CONTENT_DIMENSION

    override var parentHeight: Float = WRAP_CONTENT_DIMENSION

    override var columns: Int = 1
        set(value) {
            if (value == 0 && rows == 0) {
                field = 1 // can't be 0 along with rows
            } else {
                field = value
            }

        }

    override var rows: Int = 0
        set(value) {
            if (value == 0 && columns == 0) {
                field = 1 // can't be 0 along with columns
            } else {
                field = value
            }
        }

    // default padding for each item [top, right, bottom, left]
    override var itemPadding = Padding(0F, 0F, 0F, 0F)

    override var itemHorizontalAlignment = Alignment.HorizontalAlignment.CENTER

    override var itemVerticalAlignment = Alignment.VerticalAlignment.CENTER

    // <column index, column width> pairs
    private val columnsWidthMap = mutableMapOf<Int, Float>()

    // <row index, row height> pairs
    private val rowsHeightMap = mutableMapOf<Int, Float>()

    override fun layoutChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
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

        if (parentWidth != WRAP_CONTENT_DIMENSION) {
            val columnsSumWidth = columnsWidthMap.values.sum()
            val columnsScale = parentWidth / columnsSumWidth
            columnsWidthMap.forEach {
                columnsWidthMap[it.key] = columnsScale * it.value
            }
        }

        if (parentHeight != WRAP_CONTENT_DIMENSION) {
            val rowsSumHeight = rowsHeightMap.values.sum()
            val rowsScale = parentHeight / rowsSumHeight
            rowsHeightMap.forEach {
                rowsHeightMap[it.key] = rowsScale * it.value
            }
        }

        rescaleChildren(children, childrenBounds)

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
        val x = when (itemHorizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                getColumnX(col) + nodeWidth / 2 + pivotOffsetX + itemPadding.left
            }

            Alignment.HorizontalAlignment.CENTER -> {
                val paddingDiff = itemPadding.left - itemPadding.right
                getColumnX(col) + columnWidth / 2 + pivotOffsetX + paddingDiff
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                getColumnX(col) + columnWidth - nodeWidth / 2 + pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val y = when (itemVerticalAlignment) {
            Alignment.VerticalAlignment.TOP -> {
                getRowY(row) - nodeHeight / 2 + pivotOffsetY - itemPadding.top
            }

            Alignment.VerticalAlignment.CENTER -> {
                val paddingDiff = itemPadding.top - itemPadding.bottom
                getRowY(row) - rowHeight / 2 + pivotOffsetY - paddingDiff
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                getRowY(row) - rowHeight + nodeHeight / 2 + pivotOffsetY + itemPadding.bottom
            }
        }

        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    private fun rescaleChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
        for (i in children.indices) {
            val child = children[i]
            val childSize = (childrenBounds[i] ?: Bounding()).size()
            if (child.localScale.x > 0 && child.localScale.y > 0) {
                val childWidth = childSize.x / child.localScale.x
                val childHeight = childSize.y / child.localScale.y
                if (childWidth > 0 && childHeight > 0) {
                    val maxChildWidth = calculateMaxChildWidth(i)
                    val maxChildHeight = calculateMaxChildHeight(i)
                    val userSpecifiedScale = child.getUserSpecifiedScale() ?: Vector3.one()
                    val scaleX = min(maxChildWidth / childWidth, userSpecifiedScale.x)
                    val scaleY = min(maxChildHeight / childHeight, userSpecifiedScale.y)
                    val scaleXY = min(scaleX, scaleY) // scale saving width / height ratio
                    child.localScale = Vector3(scaleXY, scaleXY, child.localScale.z)
                }
            }
        }
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
        return itemBounds.right - itemBounds.left + itemPadding.left + itemPadding.right
    }

    private fun calculateRowHeight(itemBounds: Bounding): Float {
        return itemBounds.top - itemBounds.bottom + itemPadding.top + itemPadding.bottom
    }

    private fun getColumnIndex(childIdx: Int): Int {
        return if (rows != 0) {
            childIdx / rows
        } else {
            childIdx % columns
        }
    }

    private fun getRowIndex(childIdx: Int): Int {
        return if (rows != 0) {
            childIdx % rows
        } else {
            childIdx / columns
        }
    }

    private fun calculateMaxChildWidth(childIdx: Int): Float {
        return if (parentWidth == WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            columnsWidthMap[getColumnIndex(childIdx)]!!
        }
    }

    private fun calculateMaxChildHeight(childIdx: Int): Float {
        return if (parentHeight == WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            rowsHeightMap[getRowIndex(childIdx)]!!
        }
    }

}