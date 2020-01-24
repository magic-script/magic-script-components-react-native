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
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.sumByFloat

open class VerticalLinearLayoutManager<T : LayoutParams> : SizedLayoutManager<T>() {

    override fun <T : LayoutParams> layoutNode(
        nodeInfo: NodeInfo,
        layoutInfo: LayoutInfo<T>
    ) {
        val index = nodeInfo.index
        val params = layoutInfo.params

        val itemPadding = params.itemsPadding[index] ?: Padding()

        val layoutSizeLimit = layoutInfo.sizeLimit
        val contentSize = layoutInfo.contentSize

        // calculating x position for a child
        val itemAlignment = params.itemsAlignment[index] ?: Alignment()
        val x = when (itemAlignment.horizontal) {
            Alignment.HorizontalAlignment.LEFT -> {
                nodeInfo.width / 2 + nodeInfo.pivotOffsetX + itemPadding.left
            }

            Alignment.HorizontalAlignment.CENTER -> {
                val inParentOffset = (layoutSizeLimit.x - contentSize.x) / 2
                val paddingDiff = itemPadding.left - itemPadding.right
                nodeInfo.pivotOffsetX + paddingDiff + inParentOffset + contentSize.x / 2
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                layoutSizeLimit.x - nodeInfo.width / 2 + nodeInfo.pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val paddingSumY = itemPadding.top + index * (itemPadding.top + itemPadding.bottom)
        val offsetY = -(layoutInfo.childrenBounds.values.take(index).sumByFloat {
            it.size().y
        } + paddingSumY)

        val y = when (itemAlignment.vertical) {
            Alignment.VerticalAlignment.TOP -> {
                offsetY - nodeInfo.height / 2 + nodeInfo.pivotOffsetY
            }
            Alignment.VerticalAlignment.CENTER -> {
                val inParentOffset = -(layoutSizeLimit.y - contentSize.y) / 2
                offsetY - nodeInfo.height / 2 + nodeInfo.pivotOffsetY + inParentOffset
            }
            Alignment.VerticalAlignment.BOTTOM -> {
                val inParentOffset = -(layoutSizeLimit.y - contentSize.y)
                offsetY - nodeInfo.height / 2 + nodeInfo.pivotOffsetY + inParentOffset
            }
        }

        val node = nodeInfo.node
        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    override fun getContentWidth(childrenBounds: Map<Int, Bounding>, layoutParams: T): Float {
        val itemsPadding = layoutParams.itemsPadding

        val paddingHorizontal = getMaxHorizontalPadding(itemsPadding)
        val widestChildSize = childrenBounds.values.maxBy { it.size().x }?.size()

        return (widestChildSize?.x ?: 0f) + paddingHorizontal
    }

    override fun getContentHeight(childrenBounds: Map<Int, Bounding>, layoutParams: T): Float {
        val itemsPadding = layoutParams.itemsPadding

        val paddingVertical = getMaxVerticalPadding(itemsPadding)
        val paddingSum = childrenBounds.size * paddingVertical

        return childrenBounds.values.sumByFloat { it.size().y } + paddingSum
    }

    override fun calculateMaxChildWidth(
        childIdx: Int,
        childrenBounds: Map<Int, Bounding>,
        layoutParams: T
    ): Float {
        val parentWidth = layoutParams.size.x
        return if (parentWidth == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            val itemsPadding = layoutParams.itemsPadding

            val maxLeftPaddings = getMaxLeftPadding(itemsPadding)
            val maxRightPaddings = getMaxRightPadding(itemsPadding)

            parentWidth - maxLeftPaddings - maxRightPaddings
        }
    }

    override fun calculateMaxChildHeight(
        childIdx: Int,
        childrenBounds: Map<Int, Bounding>,
        layoutParams: T
    ): Float {
        val parentHeight = layoutParams.size.y
        if (parentHeight == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }

        val contentHeightNoPadding = childrenBounds.values.sumByFloat { it.size().y }
        val itemsPadding = layoutParams.itemsPadding

        val paddingVertical = getMaxVerticalPadding(itemsPadding)
        val paddingSum = childrenBounds.size * paddingVertical
        val scale = (parentHeight - paddingSum) / contentHeightNoPadding

        return childrenBounds[childIdx]!!.size().y * scale
    }

}
