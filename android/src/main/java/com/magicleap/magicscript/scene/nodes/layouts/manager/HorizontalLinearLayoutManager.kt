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

class HorizontalLinearLayoutManager<T : LayoutParams> : SizedLayoutManager<T>() {

    override fun layoutChildren(
        layoutParams: T,
        children: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>
    ) {
        super.layoutChildren(layoutParams, children, childrenBounds)

        val contentSize = Vector2(
            getContentWidth(layoutParams),
            getContentHeight(layoutParams)
        )

        val layoutSizeLimit = LayoutUtils.calculateLayoutSizeLimit(contentSize, layoutParams.size)

        for (i in children.indices) {
            val node = childrenList[i]
            val nodeBounds = childrenBounds.getValue(node)
            val nodeInfo = LayoutUtils.createNodeInfo(i, node, nodeBounds)
            layoutNode(nodeInfo, layoutParams, childrenBounds, layoutSizeLimit, contentSize)
        }
    }

    private fun layoutNode(
        nodeInfo: NodeInfo,
        layoutParams: LayoutParams,
        childrenBounds: Map<TransformNode, AABB>,
        layoutSizeLimit: Vector2,
        contentSize: Vector2
    ) {
        val itemPadding = layoutParams.itemsPadding[nodeInfo.node] ?: Padding()

        // calculating x position for a child
        val childrenOnTheLeft = childrenList.take(nodeInfo.index)

        val summaryItemsWidthOnTheLeft =
            LayoutUtils.getHorizontalBoundsSumOf(childrenOnTheLeft, childrenBounds)

        val paddingSumOnTheLeft =
            LayoutUtils.getHorizontalPaddingSumOf(childrenOnTheLeft, layoutParams.itemsPadding) +
                    itemPadding.left

        val offsetX = summaryItemsWidthOnTheLeft + paddingSumOnTheLeft

        val itemAlignment = layoutParams.itemsAlignment[nodeInfo.node] ?: Alignment()
        val x = when (itemAlignment.horizontal) {
            Alignment.Horizontal.LEFT -> {
                offsetX + nodeInfo.width / 2 + nodeInfo.pivotOffsetX
            }
            Alignment.Horizontal.CENTER -> {
                val inParentOffset = (layoutSizeLimit.x - contentSize.x) / 2
                offsetX + nodeInfo.width / 2 + nodeInfo.pivotOffsetX + inParentOffset
            }
            Alignment.Horizontal.RIGHT -> {
                val inParentOffset = layoutSizeLimit.x - contentSize.x
                offsetX + nodeInfo.width / 2 + nodeInfo.pivotOffsetX + inParentOffset
            }
        }

        // calculating y position for a child
        val y = when (itemAlignment.vertical) {
            Alignment.Vertical.TOP -> {
                -nodeInfo.height / 2 + nodeInfo.pivotOffsetY - itemPadding.top
            }

            Alignment.Vertical.CENTER -> {
                val inParentOffset = -(layoutSizeLimit.y - contentSize.y) / 2
                val paddingDiff = itemPadding.top - itemPadding.bottom
                nodeInfo.pivotOffsetY - paddingDiff + inParentOffset - contentSize.y / 2
            }

            Alignment.Vertical.BOTTOM -> {
                -layoutSizeLimit.y + nodeInfo.height / 2 + nodeInfo.pivotOffsetY + itemPadding.bottom
            }
        }

        val node = nodeInfo.node
        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    override fun getLayoutBounds(layoutParams: T): AABB {
        val width = if (layoutParams.size.x == UiBaseLayout.WRAP_CONTENT_DIMENSION)
            getContentWidth(layoutParams)
        else {
            layoutParams.size.x
        }

        val height = if (layoutParams.size.y == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            getContentHeight(layoutParams)
        } else {
            layoutParams.size.y
        }

        val minZ = LayoutUtils.getMinZ(childrenList, childrenBounds)
        val maxZ = LayoutUtils.getMaxZ(childrenList, childrenBounds)

        return AABB(min = Vector3(0f, -height, minZ), max = Vector3(width, 0f, maxZ))
    }

    override fun getMaxChildWidth(childIdx: Int, layoutParams: T): Float {
        val parentWidth = layoutParams.size.x
        if (parentWidth == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }

        val contentWidthNoPadding =
            LayoutUtils.getHorizontalBoundsSumOf(childrenList, childrenBounds)

        val paddingSum =
            LayoutUtils.getHorizontalPaddingSumOf(childrenList, layoutParams.itemsPadding)

        val scale = (parentWidth - paddingSum) / contentWidthNoPadding

        val child = childrenList[childIdx]
        val childWidth = childrenBounds[child]?.size()?.x ?: 0f
        return childWidth * scale
    }

    override fun getMaxChildHeight(childIdx: Int, layoutParams: T): Float {
        val parentHeight = layoutParams.size.y
        return if (parentHeight == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        } else {
            val child = childrenList[childIdx]
            val padding = layoutParams.itemsPadding[child] ?: Padding()
            parentHeight - padding.top - padding.bottom
        }
    }

    private fun getContentWidth(layoutParams: T): Float {
        val paddingSum =
            LayoutUtils.getHorizontalPaddingSumOf(childrenList, layoutParams.itemsPadding)

        val boundsSum = LayoutUtils.getHorizontalBoundsSumOf(childrenList, childrenBounds)

        return boundsSum + paddingSum
    }

    private fun getContentHeight(layoutParams: T): Float {
        var maxHeight = 0f
        for (child in childrenList) {
            val padding = layoutParams.itemsPadding[child] ?: Padding()
            val childHeight = childrenBounds[child]?.size()?.y ?: 0f
            val height = childHeight + padding.top + padding.bottom
            if (height > maxHeight) {
                maxHeight = height
            }
        }
        return maxHeight
    }

}
