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
import com.reactlibrary.scene.nodes.props.Padding

/**
 * Linear layout's manager with flexible columns or rows size:
 * column or row will grow to fit the bounding (+ padding) of a child.
 */
class FlexLinearManager(private val linearLayout: UiLinearLayout) : LayoutManager {

    override fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>) {
        val layoutData = LayoutData(
            linearLayout.isVertical(),
            linearLayout.itemPadding,
            linearLayout.itemHorizontalAlignment,
            linearLayout.itemVerticalAlignment
        )
        _layoutChildren(children, childrenBounds, layoutData)
    }
}

internal data class LayoutData(
    var isVertical: Boolean,
    var itemPadding: Padding,
    var itemHorizontalAlignment: ViewRenderable.HorizontalAlignment,
    var itemVerticalAlignment: ViewRenderable.VerticalAlignment
)

// Functions are implemented as global for easy testing. 
// With this approach the functions can be tested without 
// instantiating or mocking UiLinearLayout object, with 
// which FlexLinearManager class is tightly coupled.
internal fun _layoutChildren(
    children: List<Node>, 
    childrenBounds: Map<Int, Bounding>, 
    layoutData: LayoutData) 
{
    val itemsSpan = calculateSpan(childrenBounds, layoutData)
    val itemsOffset = calculateOffset(childrenBounds, layoutData)

    for (i in 0 until children.size) {
        layoutNode(children[i], childrenBounds[i]!!, itemsSpan, itemsOffset[i], layoutData)
    }
}

internal fun calculateSpan(
    childrenBounds: Map<Int, Bounding>, 
    layoutData: LayoutData
): Float {

    var itemsSpan = 0.0F
    for (i in 0 until childrenBounds.size) {
        
        val bounds = childrenBounds[i]!!
        val span = if (layoutData.isVertical){
            calculateColumnWidth(bounds, layoutData.itemPadding)
        } else {
            calculateRowHeight(bounds, layoutData.itemPadding)
        }

        if (span > itemsSpan) {
            itemsSpan = span
        }
    }

    return itemsSpan
}

internal fun calculateOffset(
    childrenBounds: Map<Int, Bounding>, 
    layoutData: LayoutData
): Array<Float> {

    var itemsOffset = Array<Float>(childrenBounds.size){0F}
    var offsetSum = 0.0F

    if (layoutData.isVertical){
        for (i in (childrenBounds.size - 1) downTo 0) {
            val bounds = childrenBounds[i]!!
            itemsOffset[i] = offsetSum
            offsetSum += calculateRowHeight(bounds, layoutData.itemPadding)
        }
    } else {
        for (i in 0 until childrenBounds.size) {
            val bounds = childrenBounds[i]!!
            itemsOffset[i] = offsetSum
            offsetSum += calculateColumnWidth(bounds, layoutData.itemPadding)
        }
    }

    return itemsOffset
}

// sets the proper position for the child node
internal fun layoutNode(
    node: Node,
    nodeBounds: Bounding,
    span: Float,
    offset: Float,
    layoutData: LayoutData
){
    val nodeWidth = nodeBounds.right - nodeBounds.left
    val nodeHeight = nodeBounds.top - nodeBounds.bottom

    val boundsCenterX = nodeBounds.left + nodeWidth / 2
    val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
    val boundsCenterY = nodeBounds.top - nodeHeight / 2
    val pivotOffsetY = node.localPosition.y - boundsCenterY // aligning according to center

    if (layoutData.isVertical) {

        // calculating x position for a child
        val x = when (layoutData.itemHorizontalAlignment) {
            ViewRenderable.HorizontalAlignment.LEFT -> {
                nodeWidth / 2 + pivotOffsetX + layoutData.itemPadding.left
            }

            ViewRenderable.HorizontalAlignment.CENTER -> {
                val paddingDiff = layoutData.itemPadding.left - layoutData.itemPadding.right
                span / 2 + pivotOffsetX + paddingDiff
            }

            ViewRenderable.HorizontalAlignment.RIGHT -> {
                span - nodeWidth / 2 + pivotOffsetX - layoutData.itemPadding.right
            }
        }

        // calculating y position for a child
        val paddingDiffY = layoutData.itemPadding.top - layoutData.itemPadding.bottom
        val y = offset + nodeHeight / 2 + pivotOffsetY - paddingDiffY

        node.localPosition = Vector3(x, y, node.localPosition.z)
    } else {

        // calculating x position for a child
        val paddingDiffX = layoutData.itemPadding.left - layoutData.itemPadding.right
        val x = offset + nodeWidth / 2 + pivotOffsetX - paddingDiffX

        // calculating y position for a child
        val y = when (layoutData.itemVerticalAlignment) {
            ViewRenderable.VerticalAlignment.TOP -> {
                nodeHeight / 2 + pivotOffsetY - layoutData.itemPadding.top
            }

            ViewRenderable.VerticalAlignment.CENTER -> {
                val paddingDiff = layoutData.itemPadding.top - layoutData.itemPadding.bottom
                span / 2 + pivotOffsetY - paddingDiff
            }

            ViewRenderable.VerticalAlignment.BOTTOM -> {
                span + nodeHeight / 2 + pivotOffsetY + layoutData.itemPadding.bottom
            }
        }

        node.localPosition = Vector3(x, y, node.localPosition.z)
    }
}

internal fun calculateColumnWidth(itemBounds: Bounding, itemPadding: Padding): Float {
    return itemBounds.right - itemBounds.left + itemPadding.left + itemPadding.right
}

internal fun calculateRowHeight(itemBounds: Bounding, itemPadding: Padding): Float {
    return itemBounds.top - itemBounds.bottom + itemPadding.top + itemPadding.bottom
}
