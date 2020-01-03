/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.UiLinearLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.HorizontalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.LinearLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_VERTICAL
import com.magicleap.magicscript.utils.Vector2

open class UiListViewNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader
) : UiScrollViewNode(initProps, context, viewRenderableLoader) {

    private val containerNode: UiLinearLayout

    companion object {
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_ORIENTATION = "orientation"

        const val DEFAULT_ORIENTATION = ORIENTATION_VERTICAL
        const val DEFAULT_ITEM_ALIGNMENT = "top-left"
    }

    private var contentAdded = false
    private var contentSize = Vector2()

    init {
        val containerProps = JavaOnlyMap()
        val orientation = properties.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION)
        containerProps.putString(UiLinearLayout.PROP_ORIENTATION, orientation)
        containerProps.putString(UiLinearLayout.PROP_DEFAULT_ITEM_ALIGNMENT, DEFAULT_ITEM_ALIGNMENT)
        val linearLayoutManager =
            LinearLayoutManager(VerticalLinearLayoutManager(), HorizontalLinearLayoutManager())
        containerNode = UiLinearLayout(containerProps, linearLayoutManager)

        onContentSizeChangedListener = { contentSize ->
            this.contentSize = contentSize
            forceSameItemsWidth()

            val size = readSize()

            if (size.x == WRAP_CONTENT_DIMENSION || size.y == WRAP_CONTENT_DIMENSION) {
                setNeedsRebuild(true)
            }
        }
    }

    override fun applyProperties(props: Bundle) {
        if (props.containsKey(PROP_ORIENTATION)) {
            val orientation = props.getString(PROP_ORIENTATION)
            properties.putString(PROP_SCROLL_DIRECTION, orientation)
            props.putString(PROP_SCROLL_DIRECTION, orientation)
            containerNode.update(JavaOnlyMap.of(UiLinearLayout.PROP_ORIENTATION, orientation))
        }
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }
    }

    override fun provideDesiredSize(): Vector2 {
        val size = readSize()

        val width = if (size.x != WRAP_CONTENT_DIMENSION) {
            size.x
        } else {
            val vBarThickness = if (vBarNode != null) calculateBarThickness(contentSize) else 0F
            contentSize.x + vBarThickness
        }

        val height = if (size.y != WRAP_CONTENT_DIMENSION) {
            size.y
        } else {
            val hBarThickness = if (hBarNode != null) calculateBarThickness(contentSize) else 0F
            contentSize.y + hBarThickness
        }
        return Vector2(width, height)
    }

    override fun build() {
        super.build()
        // in case of rebuild, we don't want to attach again the content
        if (!contentAdded) {
            containerNode.build()
            addContent(containerNode)
            contentAdded = true
        }
    }

    override fun addContent(child: TransformNode) {
        when (child) {
            is UiListViewItemNode -> containerNode.addContent(child)
            is UiScrollBarNode -> {
                super.addContent(child)
                setNeedsRebuild(true) // we may need to update the size
            }
            else -> super.addContent(child)
        }
    }

    private fun readSize(): Vector2 {
        val width = properties.getDouble(PROP_WIDTH, WRAP_CONTENT_DIMENSION.toDouble())
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        return Vector2(width.toFloat(), height.toFloat())
    }

    private fun forceSameItemsWidth() {
        val orientation = properties.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION)
        val minItemSize = if (orientation == ORIENTATION_VERTICAL) {
            Vector2(contentSize.x, 0f)
        } else {
            Vector2(0f, contentSize.y)
        }
        containerNode.childrenList
            .filterIsInstance<UiListViewItemNode>()
            .forEach { item ->
                item.minSize = minItemSize
            }
    }
}