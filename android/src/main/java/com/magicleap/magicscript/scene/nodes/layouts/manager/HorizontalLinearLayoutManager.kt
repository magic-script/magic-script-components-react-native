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
import com.magicleap.magicscript.scene.nodes.base.UiLayout
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.sumByFloat

class HorizontalLinearLayoutManager : BaseLinearLayoutManager() {

    override fun layoutNode(
        index: Int,
        childrenBounds: Map<Int, Bounding>,
        contentSize: Vector2,
        layoutSizeLimit: Vector2
    ) {
        val node = childrenList[index]
        val nodeBounds = childrenBounds.getValue(index)

        val nodeWidth = nodeBounds.right - nodeBounds.left
        val nodeHeight = nodeBounds.top - nodeBounds.bottom

        val boundsCenterX = nodeBounds.left + nodeWidth / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val boundsCenterY = nodeBounds.top - nodeHeight / 2
        val pivotOffsetY = node.localPosition.y - boundsCenterY // aligning according to center

        // calculating x position for a child
        val paddingSumX = itemPadding.left + index * (itemPadding.left + itemPadding.right)
        val offsetX = childrenBounds.values.take(index).sumByFloat {
            it.size().x
        } + paddingSumX

        val x = when (itemHorizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                offsetX + nodeWidth / 2 + pivotOffsetX
            }
            Alignment.HorizontalAlignment.CENTER -> {
                val inParentOffset = (layoutSizeLimit.x - contentSize.x) / 2
                offsetX + nodeWidth / 2 + pivotOffsetX + inParentOffset
            }
            Alignment.HorizontalAlignment.RIGHT -> {
                val inParentOffset = layoutSizeLimit.x - contentSize.x
                offsetX + nodeWidth / 2 + pivotOffsetX + inParentOffset
            }
        }

        // calculating y position for a child
        val y = when (itemVerticalAlignment) {
            Alignment.VerticalAlignment.TOP -> {
                -nodeHeight / 2 + pivotOffsetY - itemPadding.top
            }

            Alignment.VerticalAlignment.CENTER -> {
                val inParentOffset = -(layoutSizeLimit.y - contentSize.y) / 2
                val paddingDiff = itemPadding.top - itemPadding.bottom
                pivotOffsetY - paddingDiff + inParentOffset - contentSize.y / 2
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                -layoutSizeLimit.y + nodeHeight / 2 + pivotOffsetY + itemPadding.bottom
            }
        }

        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    override fun getContentWidth(childrenBounds: Map<Int, Bounding>): Float {
        val paddingHorizontal = itemPadding.left + itemPadding.right
        val paddingSum = childrenBounds.size * paddingHorizontal
        return childrenBounds.values.sumByFloat { it.size().x } + paddingSum
    }

    override fun getContentHeight(childrenBounds: Map<Int, Bounding>): Float {
        val highestChildSize = childrenBounds.values.maxBy { it.size().y }?.size()
        val paddingVertical = itemPadding.top + itemPadding.bottom
        return (highestChildSize?.y ?: 0f) + paddingVertical
    }

    override fun calculateMaxChildWidth(childIdx: Int, childrenBounds: Map<Int, Bounding>): Float {
        if (parentWidth == UiLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }
        val contentWidth = getContentWidth(childrenBounds)
        val scale = parentWidth / contentWidth
        return childrenBounds[childIdx]!!.size().x * scale
    }

    override fun calculateMaxChildHeight(childIdx: Int, childrenBounds: Map<Int, Bounding>): Float {
        return if (parentHeight == UiLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        } else {
            parentHeight - itemPadding.top - itemPadding.bottom
        }
    }

}
