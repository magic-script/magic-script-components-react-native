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
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.layouts.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.LayoutUtils
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.getUserSpecifiedScale
import kotlin.math.min

abstract class SizedLayoutManager<T : LayoutParams> :
    LayoutManager<T> {

    protected var childrenList = listOf<TransformNode>()
        private set

    override fun layoutChildren(
        layoutParams: T,
        children: List<TransformNode>,
        childrenBounds: Map<Int, AABB>
    ) {
        this.childrenList = children
        onPreLayout(children, childrenBounds, layoutParams)
        rescaleChildren(children, childrenBounds, layoutParams)
    }

    /**
     * Should return max allowed size of child with [childIdx]
     */
    abstract fun getMaxChildSize(
        childIdx: Int,
        childrenBounds: Map<Int, AABB>,
        layoutParams: T
    ): Vector2

    open fun onPreLayout(
        children: List<TransformNode>,
        childrenBounds: Map<Int, AABB>,
        layoutParams: LayoutParams
    ) {
    }

    override fun getLayoutBounds(layoutParams: T): AABB {
        // TODO not max padding including item position
        val childrenBounds = Utils.calculateSumBounds(childrenList)
        val parentSize = layoutParams.size
        var sizeX = parentSize.x
        var sizeY = parentSize.y

        val maxPadding = Padding(
            LayoutUtils.getMaxTopPadding(layoutParams.itemsPadding),
            LayoutUtils.getMaxRightPadding(layoutParams.itemsPadding),
            LayoutUtils.getMaxBottomPadding(layoutParams.itemsPadding),
            LayoutUtils.getMaxLeftPadding(layoutParams.itemsPadding)
        )

        var paddingOffsetX = maxPadding.left + maxPadding.right
        var paddingOffsetY = -maxPadding.top - maxPadding.bottom

        if (parentSize.x == WRAP_CONTENT_DIMENSION) {
            sizeX = childrenBounds.size().x
        } else {
            paddingOffsetX = 0f
        }

        if (parentSize.y == WRAP_CONTENT_DIMENSION) {
            sizeY = childrenBounds.size().y
        } else {
            paddingOffsetY = 0f
        }

        val minEdge = Vector3(0f, -sizeY + paddingOffsetY, childrenBounds.min.z)
        val maxEdge = Vector3(sizeX + paddingOffsetX, 0f, childrenBounds.max.z)

        return AABB(minEdge, maxEdge)
    }

    private fun rescaleChildren(
        children: List<TransformNode>,
        childrenBounds: Map<Int, AABB>,
        layoutParams: T
    ) {
        for (i in children.indices) {
            val child = children[i]
            val childSize = (childrenBounds[i] ?: AABB()).size()
            if (child.localScale.x > 0 && child.localScale.y > 0) {
                val childWidth = childSize.x / child.localScale.x
                val childHeight = childSize.y / child.localScale.y
                if (childWidth > 0 && childHeight > 0) {
                    val maxChildSize = getMaxChildSize(i, childrenBounds, layoutParams)
                    val userSpecifiedScale = child.getUserSpecifiedScale() ?: Vector3.one()
                    val scaleX = min(maxChildSize.x / childWidth, userSpecifiedScale.x)
                    val scaleY = min(maxChildSize.y / childHeight, userSpecifiedScale.y)
                    val scaleXY = min(scaleX, scaleY) // scale saving width / height ratio
                    child.localScale = Vector3(scaleXY, scaleXY, child.localScale.z)
                }
            }
        }
    }

}
