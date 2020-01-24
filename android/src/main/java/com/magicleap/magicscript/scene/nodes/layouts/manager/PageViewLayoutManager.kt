/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.params.PageViewLayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Utils

class PageViewLayoutManager : VerticalLinearLayoutManager<PageViewLayoutParams>() {
    override fun layoutChildren(
        layoutParams: PageViewLayoutParams,
        children: List<TransformNode>,
        childrenBounds: Map<Int, Bounding>
    ) {
        val visiblePage = layoutParams.visiblePage
        children.forEachIndexed { index, node ->
            if (index == visiblePage) {
                node.show()
            } else {
                node.hide()
            }
        }
        val itemPadding = layoutParams.itemsPadding[visiblePage] ?: Padding()
        val itemAlignment = layoutParams.itemsAlignment[visiblePage] ?: Alignment()
        val singleItemParams = PageViewLayoutParams(
            visiblePage = visiblePage,
            size = layoutParams.size,
            itemsPadding = mapOf(0 to itemPadding),
            itemsAlignment = mapOf(0 to itemAlignment)
        )
        if (children.size > visiblePage) {
            val activeChild = children[visiblePage]
            val bounds = mapOf(0 to childrenBounds[visiblePage]!!)
            super.layoutChildren(singleItemParams, listOf(activeChild), bounds)
        }
    }

    override fun getLayoutBounds(layoutParams: PageViewLayoutParams): Bounding {
        val childrenBounds = Utils.calculateSumBounds(childrenList)
        val parentSize = layoutParams.size
        var sizeX = parentSize.x
        var sizeY = parentSize.y
        val padding = layoutParams.itemsPadding[layoutParams.visiblePage] ?: Padding()
        var leftOffset = -padding.left
        var bottomOffset = -padding.bottom
        var rightOffset = padding.right
        var topOffset = padding.top
        if (parentSize.x == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            sizeX = childrenBounds.size().x
        } else {
            leftOffset = 0f
            rightOffset = 0f
        }
        if (parentSize.y == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            sizeY = childrenBounds.size().y
        } else {
            topOffset = 0f
            bottomOffset = 0f
        }
        return Bounding(
            left = leftOffset,
            bottom = -sizeY + bottomOffset,
            right = sizeX + rightOffset,
            top = topOffset
        )
    }
}