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
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.LayoutUtils
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.sumByFloat

class HorizontalLinearLayoutManager<T : LayoutParams> : SizedLayoutManager<T>() {

    override fun layoutChildren(
        layoutParams: T,
        children: List<TransformNode>,
        childrenBounds: Map<Int, AABB>
    ) {
        super.layoutChildren(layoutParams, children, childrenBounds)

        val contentSize = Vector2(
            getContentWidth(childrenBounds, layoutParams),
            getContentHeight(childrenBounds, layoutParams)
        )

        val layoutSizeLimit = LayoutUtils.calculateLayoutSizeLimit(contentSize, layoutParams.size)

        for (i in children.indices) {
            val node = childrenList[i]
            val nodeBounds = childrenBounds.getValue(i)
            val nodeInfo = LayoutUtils.createNodeInfo(i, node, nodeBounds)
            layoutNode(nodeInfo, layoutParams, childrenBounds, layoutSizeLimit, contentSize)
        }
    }

    private fun layoutNode(
        nodeInfo: NodeInfo,
        layoutParams: LayoutParams,
        childrenBounds: Map<Int, AABB>,
        layoutSizeLimit: Vector2,
        contentSize: Vector2
    ) {
        val index = nodeInfo.index
        val itemPadding = layoutParams.itemsPadding[index] ?: Padding()

        // calculating x position for a child
        val summaryItemsWidthOnTheLeft =
            childrenBounds.values.take(index).sumByFloat { it.size().x }

        val paddingSumOnTheLeft =
            LayoutUtils.getSummaryHorizontalPaddingBeforeItem(layoutParams.itemsPadding, index) +
                    itemPadding.left

        val offsetX = summaryItemsWidthOnTheLeft + paddingSumOnTheLeft

        val itemAlignment = layoutParams.itemsAlignment[index] ?: Alignment()
        val x = when (itemAlignment.horizontal) {
            Alignment.HorizontalAlignment.LEFT -> {
                offsetX + nodeInfo.width / 2 + nodeInfo.pivotOffsetX
            }
            Alignment.HorizontalAlignment.CENTER -> {
                val inParentOffset = (layoutSizeLimit.x - contentSize.x) / 2
                offsetX + nodeInfo.width / 2 + nodeInfo.pivotOffsetX + inParentOffset
            }
            Alignment.HorizontalAlignment.RIGHT -> {
                val inParentOffset = layoutSizeLimit.x - contentSize.x
                offsetX + nodeInfo.width / 2 + nodeInfo.pivotOffsetX + inParentOffset
            }
        }

        // calculating y position for a child
        val y = when (itemAlignment.vertical) {
            Alignment.VerticalAlignment.TOP -> {
                -nodeInfo.height / 2 + nodeInfo.pivotOffsetY - itemPadding.top
            }

            Alignment.VerticalAlignment.CENTER -> {
                val inParentOffset = -(layoutSizeLimit.y - contentSize.y) / 2
                val paddingDiff = itemPadding.top - itemPadding.bottom
                nodeInfo.pivotOffsetY - paddingDiff + inParentOffset - contentSize.y / 2
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                -layoutSizeLimit.y + nodeInfo.height / 2 + nodeInfo.pivotOffsetY + itemPadding.bottom
            }
        }

        val node = nodeInfo.node
        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    override fun getMaxChildSize(
        childIdx: Int,
        childrenBounds: Map<Int, AABB>,
        layoutParams: T
    ): Vector2 {
        val width = calculateMaxChildWidth(childIdx, childrenBounds, layoutParams)
        val height = calculateMaxChildHeight(childIdx, childrenBounds, layoutParams)
        return Vector2(width, height)
    }

    private fun getContentWidth(childrenBounds: Map<Int, AABB>, layoutParams: T): Float {
        val itemsPadding = layoutParams.itemsPadding

        val paddingHorizontal = LayoutUtils.getMaxHorizontalPadding(itemsPadding)
        val paddingSum = childrenBounds.size * paddingHorizontal
        return childrenBounds.values.sumByFloat { it.size().x } + paddingSum
    }

    private fun getContentHeight(childrenBounds: Map<Int, AABB>, layoutParams: T): Float {
        val highestChildSize = childrenBounds.values.maxBy { it.size().y }?.size()
        val itemsPadding = layoutParams.itemsPadding

        val paddingVertical = LayoutUtils.getMaxVerticalPadding(itemsPadding)
        return (highestChildSize?.y ?: 0f) + paddingVertical
    }

    private fun calculateMaxChildWidth(
        childIdx: Int,
        childrenBounds: Map<Int, AABB>,
        layoutParams: T
    ): Float {
        val parentWidth = layoutParams.size.x
        if (parentWidth == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }

        val contentWidthNoPadding = childrenBounds.values.sumByFloat { it.size().x }
        val itemsPadding = layoutParams.itemsPadding

        val paddingHorizontal = LayoutUtils.getMaxHorizontalPadding(itemsPadding)
        val paddingSum = childrenBounds.size * paddingHorizontal
        val scale = (parentWidth - paddingSum) / contentWidthNoPadding

        return childrenBounds[childIdx]!!.size().x * scale
    }

    private fun calculateMaxChildHeight(
        childIdx: Int,
        childrenBounds: Map<Int, AABB>,
        layoutParams: T
    ): Float {
        val parentHeight = layoutParams.size.y
        return if (parentHeight == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        } else {
            val itemsPadding = layoutParams.itemsPadding

            val maxTopPadding = LayoutUtils.getMaxTopPadding(itemsPadding)
            val maxBottomPadding = LayoutUtils.getMaxBottomPadding(itemsPadding)

            parentHeight - maxTopPadding - maxBottomPadding
        }
    }

}
