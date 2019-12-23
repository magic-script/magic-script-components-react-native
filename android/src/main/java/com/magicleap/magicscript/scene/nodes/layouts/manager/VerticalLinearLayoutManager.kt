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

class VerticalLinearLayoutManager : BaseLinearLayoutManager() {

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
        val x = when (itemHorizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                nodeWidth / 2 + pivotOffsetX + itemPadding.left
            }

            Alignment.HorizontalAlignment.CENTER -> {
                val paddingDiff = itemPadding.left - itemPadding.right
                pivotOffsetX + paddingDiff
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                nodeWidth / 2 + pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val paddingDiffY = itemPadding.top - itemPadding.bottom
        val y = nodeHeight / 2 + pivotOffsetY - paddingDiffY

        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    override fun getContentWidth(childrenBounds: Map<Int, Bounding>): Float {
        val paddingHorizontal = itemPadding.left + itemPadding.right
        val widestChildSize = childrenBounds.values.maxBy { it.size().x }?.size()
        return (widestChildSize?.x ?: 0f) + paddingHorizontal
    }

    override fun getContentHeight(childrenBounds: Map<Int, Bounding>): Float {
        val paddingVertical = itemPadding.top + itemPadding.bottom
        val paddingSum = childrenBounds.size * paddingVertical
        return childrenBounds.values.sumByFloat { it.size().y } + paddingSum
    }

    override fun calculateMaxChildWidth(childIdx: Int, childrenBounds: Map<Int, Bounding>): Float {
        return if (parentWidth == UiLayout.WRAP_CONTENT_DIMENSION) {
            Float.MAX_VALUE
        } else {
            parentWidth - itemPadding.left - itemPadding.right
        }
    }

    override fun calculateMaxChildHeight(childIdx: Int, childrenBounds: Map<Int, Bounding>): Float {
        if (parentHeight == UiLayout.WRAP_CONTENT_DIMENSION) {
            return Float.MAX_VALUE
        }
        val contentHeight = getContentHeight(childrenBounds)
        val scale = parentHeight / contentHeight
        return childrenBounds[childIdx]!!.size().y * scale
    }

}
