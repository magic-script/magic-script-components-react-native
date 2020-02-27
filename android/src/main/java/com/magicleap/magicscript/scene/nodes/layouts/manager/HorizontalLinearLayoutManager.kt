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
            getContentWidth(layoutParams),
            getContentHeight(layoutParams)
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
            LayoutUtils.getHorizontalPaddingSumUntil(layoutParams.itemsPadding, index) +
                    itemPadding.left

        val offsetX = summaryItemsWidthOnTheLeft + paddingSumOnTheLeft

        val itemAlignment = layoutParams.itemsAlignment[index] ?: Alignment()
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

        val minZ = childrenBounds.values.minBy { it.min.z }?.min?.z ?: 0f
        val maxZ = childrenBounds.values.maxBy { it.max.z }?.max?.z ?: 0f

        return AABB(min = Vector3(0f, -height, minZ), max = Vector3(width, 0f, maxZ))
    }

    override fun getMaxChildWidth(childIdx: Int, layoutParams: T): Float {
        val parentWidth = layoutParams.size.x
        if (parentWidth == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }

        val contentWidthNoPadding = childrenBounds.values.sumByFloat { it.size().x }
        val paddingSum =
            LayoutUtils.getHorizontalPaddingSumUntil(layoutParams.itemsPadding, childrenList.size)
        val scale = (parentWidth - paddingSum) / contentWidthNoPadding

        val childWidth = childrenBounds[childIdx]?.size()?.x ?: 0f
        return childWidth * scale
    }

    override fun getMaxChildHeight(childIdx: Int, layoutParams: T): Float {
        val parentHeight = layoutParams.size.y
        return if (parentHeight == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        } else {
            val padding = layoutParams.itemsPadding[childIdx] ?: Padding()
            parentHeight - padding.top - padding.bottom
        }
    }

    private fun getContentWidth(layoutParams: T): Float {
        val paddingSum =
            LayoutUtils.getHorizontalPaddingSumUntil(layoutParams.itemsPadding, childrenList.size)

        return childrenBounds.values.sumByFloat { it.size().x } + paddingSum
    }

    private fun getContentHeight(layoutParams: T): Float {
        var maxHeight = 0f
        for (i in childrenList.indices) {
            val padding = layoutParams.itemsPadding[i] ?: Padding()
            val childHeight = childrenBounds[i]?.size()?.y ?: 0f
            val height = childHeight + padding.top + padding.bottom
            if (height > maxHeight) {
                maxHeight = height
            }
        }
        return maxHeight
    }

}
