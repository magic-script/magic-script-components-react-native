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

package com.reactlibrary.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.scene.nodes.layouts.LayoutManager
import com.reactlibrary.scene.nodes.layouts.UiLinearLayout
import com.reactlibrary.scene.nodes.props.Bounding

/**
 * Linear layout's manager with flexible columns or rows size:
 * column or row will grow to fit the bounding (+ padding) of a child.
 */
class FlexLinearManager(private val layout: UiLinearLayout) : LayoutManager {

    override fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>) {
        val itemsSpan = calculateSpan(childrenBounds)
        val itemsOffset = calculateOffset(childrenBounds)

        for (i in 0 until children.size) {
            layoutNode(children[i], childrenBounds.getValue(i), itemsSpan, itemsOffset[i])
        }
    }

    private fun calculateSpan(childrenBounds: Map<Int, Bounding>): Float {

        var itemsSpan = 0.0F
        for (i in 0 until childrenBounds.size) {

            val bounds = childrenBounds.getValue(i)
            val span = if (layout.isVertical()) {
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

        if (layout.isVertical()) {
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

        if (layout.isVertical()) {

            // calculating x position for a child
            val x = when (layout.itemHorizontalAlignment) {
                ViewRenderable.HorizontalAlignment.LEFT -> {
                    nodeWidth / 2 + pivotOffsetX + layout.itemPadding.left
                }

                ViewRenderable.HorizontalAlignment.CENTER -> {
                    val paddingDiff = layout.itemPadding.left - layout.itemPadding.right
                    span / 2 + pivotOffsetX + paddingDiff
                }

                ViewRenderable.HorizontalAlignment.RIGHT -> {
                    span - nodeWidth / 2 + pivotOffsetX - layout.itemPadding.right
                }
            }

            // calculating y position for a child
            val paddingDiffY = layout.itemPadding.top - layout.itemPadding.bottom
            val y = offset + nodeHeight / 2 + pivotOffsetY - paddingDiffY

            node.localPosition = Vector3(x, y, node.localPosition.z)
        } else {

            // calculating x position for a child
            val paddingDiffX = layout.itemPadding.left - layout.itemPadding.right
            val x = offset + nodeWidth / 2 + pivotOffsetX - paddingDiffX

            // calculating y position for a child
            val y = when (layout.itemVerticalAlignment) {
                ViewRenderable.VerticalAlignment.TOP -> {
                    nodeHeight / 2 + pivotOffsetY - layout.itemPadding.top
                }

                ViewRenderable.VerticalAlignment.CENTER -> {
                    val paddingDiff = layout.itemPadding.top - layout.itemPadding.bottom
                    span / 2 + pivotOffsetY - paddingDiff
                }

                ViewRenderable.VerticalAlignment.BOTTOM -> {
                    span + nodeHeight / 2 + pivotOffsetY + layout.itemPadding.bottom
                }
            }

            node.localPosition = Vector3(x, y, node.localPosition.z)
        }
    }

    private fun calculateColumnWidth(itemBounds: Bounding): Float {
        return itemBounds.right - itemBounds.left + layout.itemPadding.left + layout.itemPadding.right
    }

    private fun calculateRowHeight(itemBounds: Bounding): Float {
        return itemBounds.top - itemBounds.bottom + layout.itemPadding.top + layout.itemPadding.bottom
    }

}
