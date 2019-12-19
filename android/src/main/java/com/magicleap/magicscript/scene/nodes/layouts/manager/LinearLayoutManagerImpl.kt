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
import com.magicleap.magicscript.scene.nodes.base.UiLayout
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.getUserSpecifiedScale
import kotlin.math.min

class LinearLayoutManagerImpl : LinearLayoutManager {
    override var parentWidth: Float = 0F

    override var parentHeight: Float = 0F

    override var itemPadding = Padding(0F, 0F, 0F, 0F)

    override var itemHorizontalAlignment = Alignment.HorizontalAlignment.CENTER

    override var itemVerticalAlignment = Alignment.VerticalAlignment.CENTER

    override var isVertical = true

    private var childrenList = listOf<TransformNode>()

    override fun layoutChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
        this.childrenList = children
        rescaleChildren(children, childrenBounds)

        val itemsSpan = calculateSpan(childrenBounds)
        val itemsOffset = calculateOffset(childrenBounds)

        for (i in 0 until children.size) {
            layoutNode(children[i], childrenBounds.getValue(i), itemsSpan, itemsOffset[i])
        }
    }

    // sets the proper position for the child node
    private fun layoutNode(
        node: Node,
        nodeBounds: Bounding,
        span: Float,
        offset: Float
    ) {
        val nodeWidth = nodeBounds.right - nodeBounds.left
        val nodeHeight = nodeBounds.top - nodeBounds.bottom

        val boundsCenterX = nodeBounds.left + nodeWidth / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val boundsCenterY = nodeBounds.top - nodeHeight / 2
        val pivotOffsetY = node.localPosition.y - boundsCenterY // aligning according to center

        if (isVertical) {

            // calculating x position for a child
            val x = when (itemHorizontalAlignment) {
                Alignment.HorizontalAlignment.LEFT -> {
                    nodeWidth / 2 + pivotOffsetX + itemPadding.left
                }

                Alignment.HorizontalAlignment.CENTER -> {
                    val paddingDiff = itemPadding.left - itemPadding.right
                    span / 2 + pivotOffsetX + paddingDiff
                }

                Alignment.HorizontalAlignment.RIGHT -> {
                    span - nodeWidth / 2 + pivotOffsetX - itemPadding.right
                }
            }

            // calculating y position for a child
            val paddingDiffY = itemPadding.top - itemPadding.bottom
            val y = offset + nodeHeight / 2 + pivotOffsetY - paddingDiffY

            node.localPosition = Vector3(x, y, node.localPosition.z)
        } else {

            // calculating x position for a child
            val paddingDiffX = itemPadding.left - itemPadding.right
            val x = offset + nodeWidth / 2 + pivotOffsetX - paddingDiffX

            // calculating y position for a child
            val y = when (itemVerticalAlignment) {
                Alignment.VerticalAlignment.TOP -> {
                    nodeHeight / 2 + pivotOffsetY - itemPadding.top
                }

                Alignment.VerticalAlignment.CENTER -> {
                    val paddingDiff = itemPadding.top - itemPadding.bottom
                    span / 2 + pivotOffsetY - paddingDiff
                }

                Alignment.VerticalAlignment.BOTTOM -> {
                    span + nodeHeight / 2 + pivotOffsetY + itemPadding.bottom
                }
            }

            node.localPosition = Vector3(x, y, node.localPosition.z)
        }
    }

    override fun getLayoutBounds(): Bounding {
        val childBounds = Utils.calculateSumBounds(childrenList)
        return Bounding(
            childBounds.left - itemPadding.left,
            childBounds.bottom - itemPadding.bottom,
            childBounds.right + itemPadding.right,
            childBounds.top + itemPadding.top
        )
    }

    private fun rescaleChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
        for (i in children.indices) {
            val child = children[i]
            val childSize = (childrenBounds[i] ?: Bounding()).size()
            if (child.localScale.x > 0 && child.localScale.y > 0) {
                val childWidth = childSize.x / child.localScale.x
                val childHeight = childSize.y / child.localScale.y
                if (childWidth > 0 && childHeight > 0) {
                    val maxChildWidth = calculateMaxChildWidth(i, childrenBounds)
                    val maxChildHeight = calculateMaxChildHeight(i, childrenBounds)
                    val userSpecifiedScale = child.getUserSpecifiedScale() ?: Vector3.one()
                    val scaleX = min(maxChildWidth / childWidth, userSpecifiedScale.x)
                    val scaleY = min(maxChildHeight / childHeight, userSpecifiedScale.y)
                    val scaleXY = min(scaleX, scaleY) // scale saving width / height ratio
                    child.localScale = Vector3(scaleXY, scaleXY, child.localScale.z)
                }
            }
        }
    }

    private fun calculateMaxChildWidth(childIdx: Int, childrenBounds: Map<Int, Bounding>): Float {
        if (parentWidth == UiLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }

        return if (isVertical) {
            parentWidth - itemPadding.left - itemPadding.right
        } else {
            val sumWidth = calculateSumWidth(childrenBounds)
            val scale = parentWidth / sumWidth
            return childrenBounds[childIdx]!!.size().x * scale
        }
    }

    private fun calculateMaxChildHeight(childIdx: Int, childrenBounds: Map<Int, Bounding>): Float {
        if (parentHeight == UiLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }

        return if (isVertical) {
            val sumHeight = calculateSumHeight(childrenBounds)
            val scale = parentHeight / sumHeight
            return childrenBounds[childIdx]!!.size().y * scale
        } else {
            parentHeight - itemPadding.top - itemPadding.bottom
        }
    }

    private fun calculateSpan(childrenBounds: Map<Int, Bounding>): Float {
        var itemsSpan = 0.0F
        for (i in 0 until childrenBounds.size) {
            val bounds = childrenBounds.getValue(i)
            val span = if (isVertical) {
                calculateColumnWidth(bounds)
            } else {
                calculateRowHeight(bounds)
            }

            if (span > itemsSpan) {
                itemsSpan = span
            }
        }
        return itemsSpan
    }

    private fun calculateOffset(childrenBounds: Map<Int, Bounding>): Array<Float> {
        val itemsOffset = Array(childrenBounds.size) { 0F }
        var offsetSum = 0.0F

        if (isVertical) {
            for (i in (childrenBounds.size - 1) downTo 0) {
                val bounds = childrenBounds.getValue(i)
                itemsOffset[i] = offsetSum
                offsetSum += calculateRowHeight(bounds)
            }
        } else {
            for (i in 0 until childrenBounds.size) {
                val bounds = childrenBounds.getValue(i)
                itemsOffset[i] = offsetSum
                offsetSum += calculateColumnWidth(bounds)
            }
        }
        return itemsOffset
    }

    private fun calculateColumnWidth(itemBounds: Bounding): Float {
        return itemBounds.right - itemBounds.left + itemPadding.left + itemPadding.right
    }

    private fun calculateRowHeight(itemBounds: Bounding): Float {
        return itemBounds.top - itemBounds.bottom + itemPadding.top + itemPadding.bottom
    }

    // returns sum children width including padding
    private fun calculateSumWidth(childrenBounds: Map<Int, Bounding>): Float {
        var sumWidth = 0f
        childrenBounds.forEach {
            sumWidth += it.value.size().x + itemPadding.left + itemPadding.right
        }
        return sumWidth
    }

    // returns sum children height including padding
    private fun calculateSumHeight(childrenBounds: Map<Int, Bounding>): Float {
        var sumHeight = 0f
        childrenBounds.forEach {
            sumHeight += it.value.size().y + itemPadding.top + itemPadding.bottom
        }
        return sumHeight
    }

}
