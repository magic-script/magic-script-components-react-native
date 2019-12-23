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
import com.magicleap.magicscript.scene.nodes.base.UiLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.getUserSpecifiedScale
import kotlin.math.min

abstract class BaseLinearLayoutManager : LinearLayoutManager {
    override var parentWidth: Float = 0F

    override var parentHeight: Float = 0F

    override var itemPadding = Padding(0F, 0F, 0F, 0F)

    override var itemHorizontalAlignment = Alignment.HorizontalAlignment.CENTER

    override var itemVerticalAlignment = Alignment.VerticalAlignment.CENTER

    protected var childrenList = listOf<TransformNode>()

    override fun layoutChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
        this.childrenList = children
        rescaleChildren(children, childrenBounds)

        val contentWidth = getContentWidth(childrenBounds)
        val contentHeight = getContentHeight(childrenBounds)

        val sizeLimitX = if (parentWidth == WRAP_CONTENT_DIMENSION) {
            contentWidth
        } else {
            parentWidth
        }

        val sizeLimitY = if (parentHeight == WRAP_CONTENT_DIMENSION) {
            contentHeight
        } else {
            parentHeight
        }

        val layoutSizeLimit = Vector2(sizeLimitX, sizeLimitY)
        // sum of children size including padding
        val contentSize = Vector2(contentWidth, contentHeight)
        for (i in 0 until children.size) {
            layoutNode(i, childrenBounds, contentSize, layoutSizeLimit)
        }
    }

    // sets the proper position for the child node
    protected abstract fun layoutNode(
        index: Int,
        childrenBounds: Map<Int, Bounding>,
        contentSize: Vector2,
        layoutSizeLimit: Vector2
    )

    /**
     * Should return width of layout's content (including items padding)
     */
    protected abstract fun getContentWidth(childrenBounds: Map<Int, Bounding>): Float

    /**
     * Should return height of layout's content (including items padding)
     */
    protected abstract fun getContentHeight(childrenBounds: Map<Int, Bounding>): Float

    override fun getLayoutBounds(): Bounding {
        val childBounds = Utils.calculateSumBounds(childrenList)
        val spacing = itemPadding

        var sizeX = parentWidth
        var sizeY = parentHeight

        if (parentWidth == WRAP_CONTENT_DIMENSION) {
            sizeX = childBounds.size().x
        } else {
            spacing.left = 0f
            spacing.right = 0f
        }

        if (parentHeight == WRAP_CONTENT_DIMENSION) {
            sizeY = childBounds.size().y
        } else {
            spacing.top = 0f
            spacing.bottom = 0f
        }

        return Bounding(
            -spacing.left,
            -sizeY - spacing.bottom,
            sizeX + spacing.right,
            spacing.top
        )
    }

    private fun rescaleChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
        for (i in children.indices) {
            val child = children[i]
            val childSize = (childrenBounds[i] ?: Bounding()).size()
            if (child.localScale.x > 0 && child.localScale.y > 0) {
                val childWidth = childSize.x / child.localScale.x
                val childHeight = childSize.y / child.localScale.y
                if (childWidth > 0 && childHeight > 0) {
                    val maxChildWidth = calculateMaxChildWidth(i, childrenBounds)
                    val maxChildHeight = calculateMaxChildHeight(i, childrenBounds)
                    val userSpecifiedScale = child.getUserSpecifiedScale() ?: Vector3.one()
                    val scaleX = min(maxChildWidth / childWidth, userSpecifiedScale.x)
                    val scaleY = min(maxChildHeight / childHeight, userSpecifiedScale.y)
                    val scaleXY = min(scaleX, scaleY) // scale saving width / height ratio
                    child.localScale = Vector3(scaleXY, scaleXY, child.localScale.z)
                }
            }
        }
    }

    abstract fun calculateMaxChildWidth(childIdx: Int, childrenBounds: Map<Int, Bounding>): Float

    abstract fun calculateMaxChildHeight(childIdx: Int, childrenBounds: Map<Int, Bounding>): Float
}
