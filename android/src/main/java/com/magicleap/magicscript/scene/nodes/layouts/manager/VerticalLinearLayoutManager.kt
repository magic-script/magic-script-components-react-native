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

open class VerticalLinearLayoutManager<T : LayoutParams> : SizedLayoutManager<T>() {

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
        val itemAlignment = layoutParams.itemsAlignment[nodeInfo.node] ?: Alignment()
        val x = when (itemAlignment.horizontal) {
            Alignment.Horizontal.LEFT -> {
                nodeInfo.width / 2 + nodeInfo.pivotOffsetX + itemPadding.left
            }

            Alignment.Horizontal.CENTER -> {
                val inParentOffset = (layoutSizeLimit.x - contentSize.x) / 2
                val paddingDiff = itemPadding.left - itemPadding.right
                nodeInfo.pivotOffsetX + paddingDiff + inParentOffset + contentSize.x / 2
            }

            Alignment.Horizontal.RIGHT -> {
                layoutSizeLimit.x - nodeInfo.width / 2 + nodeInfo.pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child

        val childrenAbove = childrenList.take(nodeInfo.index)

        val summaryItemsHeightAbove =
            LayoutUtils.getVerticalBoundsSumOf(childrenAbove, childrenBounds)

        val paddingSumAbove =
            LayoutUtils.getVerticalPaddingSumOf(childrenAbove, layoutParams.itemsPadding) +
                    itemPadding.top

        val offsetY = -(summaryItemsHeightAbove + paddingSumAbove)

        val y = when (itemAlignment.vertical) {
            Alignment.Vertical.TOP -> {
                offsetY - nodeInfo.height / 2 + nodeInfo.pivotOffsetY
            }
            Alignment.Vertical.CENTER -> {
                val inParentOffset = -(layoutSizeLimit.y - contentSize.y) / 2
                offsetY - nodeInfo.height / 2 + nodeInfo.pivotOffsetY + inParentOffset
            }
            Alignment.Vertical.BOTTOM -> {
                val inParentOffset = -(layoutSizeLimit.y - contentSize.y)
                offsetY - nodeInfo.height / 2 + nodeInfo.pivotOffsetY + inParentOffset
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
        return if (parentWidth == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            val child = childrenList[childIdx]
            val padding = layoutParams.itemsPadding[child] ?: Padding()
            parentWidth - padding.left - padding.right
        }
    }

    override fun getMaxChildHeight(childIdx: Int, layoutParams: T): Float {
        val parentHeight = layoutParams.size.y
        if (parentHeight == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }

        val contentHeightNoPadding =
            LayoutUtils.getVerticalBoundsSumOf(childrenList, childrenBounds)

        val paddingSum =
            LayoutUtils.getVerticalPaddingSumOf(childrenList, layoutParams.itemsPadding)
        val scale = (parentHeight - paddingSum) / contentHeightNoPadding

        val child = childrenList[childIdx]
        val childHeight = childrenBounds[child]?.size()?.y ?: 0f
        return childHeight * scale
    }

    private fun getContentWidth(layoutParams: LayoutParams): Float {
        var maxWidth = 0f
        for (child in childrenList) {
            val padding = layoutParams.itemsPadding[child] ?: Padding()
            val childWidth = childrenBounds[child]?.size()?.x ?: 0f
            val width = childWidth + padding.left + padding.right
            if (width > maxWidth) {
                maxWidth = width
            }
        }
        return maxWidth
    }

    private fun getContentHeight(layoutParams: LayoutParams): Float {
        val paddingSum =
            LayoutUtils.getVerticalPaddingSumOf(childrenList, layoutParams.itemsPadding)

        val boundsSum = LayoutUtils.getVerticalBoundsSumOf(childrenList, childrenBounds)

        return boundsSum + paddingSum
    }

}
