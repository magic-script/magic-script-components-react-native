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
import com.magicleap.magicscript.scene.nodes.layouts.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.getUserSpecifiedScale
import kotlin.math.min

abstract class SizedLayoutManager<T : LayoutParams> : LayoutManager<T> {

    protected var childrenList = listOf<TransformNode>()
        private set

    protected var childrenBounds = mapOf<TransformNode, AABB>()
        private set

    override fun layoutChildren(
        layoutParams: T,
        children: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>
    ) {
        this.childrenList = children
        this.childrenBounds = childrenBounds
        onPreLayout(children, childrenBounds, layoutParams)
        rescaleChildren(children, layoutParams)
    }

    open fun onPreLayout(
        children: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>,
        layoutParams: LayoutParams
    ) {
    }

    /**
     * Should return max allowed width of child with [childIdx]
     */
    abstract fun getMaxChildWidth(childIdx: Int, layoutParams: T): Float

    /**
     * Should return max allowed height of child with [childIdx]
     */
    abstract fun getMaxChildHeight(
        childIdx: Int,
        layoutParams: T
    ): Float

    private fun rescaleChildren(children: List<TransformNode>, layoutParams: T) {
        for (i in children.indices) {
            val child = children[i]
            val childSize = (childrenBounds[child] ?: AABB()).size()
            if (child.localScale.x > 0 && child.localScale.y > 0) {
                val childWidth = childSize.x / child.localScale.x
                val childHeight = childSize.y / child.localScale.y
                if (childWidth > 0 && childHeight > 0) {
                    val maxChildWidth = getMaxChildWidth(i, layoutParams)
                    val maxChildHeight = getMaxChildHeight(i, layoutParams)
                    val userSpecifiedScale = child.getUserSpecifiedScale() ?: Vector3.one()
                    val scaleX = min(maxChildWidth / childWidth, userSpecifiedScale.x)
                    val scaleY = min(maxChildHeight / childHeight, userSpecifiedScale.y)
                    val scaleXY = min(scaleX, scaleY) // scale saving width / height ratio
                    child.localScale = Vector3(scaleXY, scaleXY, child.localScale.z)
                }
            }
        }
    }

}
