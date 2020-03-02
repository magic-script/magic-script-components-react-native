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

import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.params.PageViewLayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Padding

class PageViewLayoutManager : VerticalLinearLayoutManager<PageViewLayoutParams>() {

    override fun layoutChildren(
        layoutParams: PageViewLayoutParams,
        children: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>
    ) {
        val visiblePage = layoutParams.visiblePage
        children.forEachIndexed { index, node ->
            if (index == visiblePage) {
                node.show()
            } else {
                node.hide()
            }
        }

        if (children.size > visiblePage) {
            val activeChild = children[visiblePage]
            super.layoutChildren(layoutParams, listOf(activeChild), childrenBounds)
        }
    }

    override fun getLayoutBounds(layoutParams: PageViewLayoutParams): AABB {
        val child = childrenList.firstOrNull()
        val childBounds = childrenBounds[child] ?: AABB()
        val parentSize = layoutParams.size
        var sizeX = parentSize.x
        var sizeY = parentSize.y
        val padding = layoutParams.itemsPadding[child] ?: Padding()
        var leftOffset = -padding.left
        var bottomOffset = -padding.bottom
        var rightOffset = padding.right
        var topOffset = padding.top
        if (parentSize.x == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            sizeX = childBounds.size().x
        } else {
            leftOffset = 0f
            rightOffset = 0f
        }
        if (parentSize.y == UiBaseLayout.WRAP_CONTENT_DIMENSION) {
            sizeY = childBounds.size().y
        } else {
            topOffset = 0f
            bottomOffset = 0f
        }

        val minEdge = Vector3(leftOffset, -sizeY + bottomOffset, childBounds.min.z)
        val maxEdge = Vector3(sizeX + rightOffset, topOffset, childBounds.max.z)

        return AABB(minEdge, maxEdge)
    }
}