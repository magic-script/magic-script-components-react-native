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
import com.magicleap.magicscript.scene.nodes.base.UiLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Vector2

class PageViewLayoutManagerImpl : RectLayoutManagerImpl(), PageViewLayoutManager {

    override var contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT

    override var contentVerticalAlignment = Alignment.VerticalAlignment.TOP

    override var visiblePage: Int = 0

    override fun layoutChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
        if (children.isNotEmpty() && childrenBounds.isNotEmpty()) {
            children.forEachIndexed { index, node ->
                if (index == visiblePage) {
                    node.show()
                    childrenBounds[index]?.let { childBounds ->
                        val childSize = childBounds.size()
                        val sizeLimitX =
                            if (parentWidth != WRAP_CONTENT_DIMENSION) parentWidth else childSize.x
                        val sizeLimitY =
                            if (parentHeight != WRAP_CONTENT_DIMENSION) parentHeight else childSize.y
                        val sizeLimit = Vector2(sizeLimitX, sizeLimitY)
                        layoutNode(node, childBounds, sizeLimit)
                    }
                } else {
                    node.hide()
                }
            }
        }
    }
}