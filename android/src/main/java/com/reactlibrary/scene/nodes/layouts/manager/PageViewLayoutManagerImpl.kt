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

package com.reactlibrary.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.ContentNode
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import com.reactlibrary.utils.Vector2
import java.lang.Exception

class PageViewLayoutManagerImpl : PageViewLayoutManager {
    override var visiblePage: Int = 0

    override var itemPadding = Padding(0F, 0F, 0F, 0F)

    override var contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT

    override var contentVerticalAlignment = Alignment.VerticalAlignment.TOP

    override var parentWidth: Float = UiNode.WRAP_CONTENT_DIMENSION

    override var parentHeight: Float = UiNode.WRAP_CONTENT_DIMENSION

    override fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>) {
        if (children.isNotEmpty() && childrenBounds.isNotEmpty()) {
            if (children.any { it !is ContentNode }) {
                throw Exception("Only ContentNode type is accepted in the PageView!")
            }
            children.forEachIndexed { index, node ->
                if(index == visiblePage) {
                    (node as TransformNode).show()
                    childrenBounds[index]?.let {
                        childBounds ->
                        val childSize = childBounds.size()
                        val sizeLimitX = if (parentWidth != UiNode.WRAP_CONTENT_DIMENSION) parentWidth else childSize.x
                        val sizeLimitY = if (parentHeight != UiNode.WRAP_CONTENT_DIMENSION) parentHeight else childSize.y
                        val sizeLimit = Vector2(sizeLimitX, sizeLimitY)
                        layoutNode(node, childBounds, sizeLimit)
                    }
                } else {
                    (node as TransformNode).hide()
                }
            }
        }
    }

    private fun layoutNode(node: TransformNode, nodeBounds: Bounding, sizeLimit: Vector2) {
        val nodeWidth = nodeBounds.right - nodeBounds.left
        val nodeHeight = nodeBounds.top - nodeBounds.bottom
        val boundsCenterX = nodeBounds.left + nodeWidth / 2
        val boundsCenterY = nodeBounds.top - nodeHeight / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val pivotOffsetY = node.localPosition.y - boundsCenterY  // aligning according to center

        // calculating x position for a child
        val x = when (contentHorizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                -sizeLimit.x / 2 + nodeWidth / 2 + pivotOffsetX + itemPadding.left
            }

            Alignment.HorizontalAlignment.CENTER -> {
                val paddingDiff = itemPadding.right - itemPadding.left
                pivotOffsetX + paddingDiff
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                sizeLimit.x / 2 - nodeWidth / 2 + pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val y = when (contentVerticalAlignment) {
            Alignment.VerticalAlignment.TOP -> {
                sizeLimit.y / 2 - nodeHeight / 2 + pivotOffsetY - itemPadding.top
            }

            Alignment.VerticalAlignment.CENTER -> {
                val paddingDiff = itemPadding.top - itemPadding.bottom
                pivotOffsetY + paddingDiff
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                -sizeLimit.y / 2 + nodeHeight / 2 + pivotOffsetY + itemPadding.bottom
            }
        }

        node.localPosition = Vector3(x, y, node.localPosition.z)
    }
}