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

package com.magicleap.magicscript.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.params.PageViewLayoutParams
import com.magicleap.magicscript.scene.nodes.props.*
import com.magicleap.magicscript.utils.*

class PageViewNode(props: ReadableMap, layoutManager: LayoutManager<PageViewLayoutParams>) :
    UiBaseLayout<PageViewLayoutParams>(props, layoutManager) {

    companion object {
        // properties
        const val PROP_VISIBLE_PAGE = "visiblePage"
        const val PROP_DEFAULT_PAGE_PADDING = "defaultPagePadding"
        const val PROP_DEFAULT_CONTENT_ALIGNMENT = "defaultPageAlignment"
        const val PROP_PAGE_PADDING = "pagePadding"
        const val PROP_PAGE_ALIGNMENT = "pageAlignment"

        // default values
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_CONTENT_ALIGNMENT = "center-center"
        const val DEFAULT_VISIBLE_PAGE = 0.0
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    init {
        // set default values of properties
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_DEFAULT_CONTENT_ALIGNMENT, DEFAULT_CONTENT_ALIGNMENT)
        properties.putDefault(PROP_DEFAULT_PAGE_PADDING, DEFAULT_ITEM_PADDING)
        properties.putDefault(PROP_VISIBLE_PAGE, DEFAULT_VISIBLE_PAGE)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (properties.containsAny(
                PROP_VISIBLE_PAGE,
                PROP_DEFAULT_PAGE_PADDING,
                PROP_DEFAULT_CONTENT_ALIGNMENT
            )
        ) {
            requestLayout()
        }
    }

    override fun getLayoutParams(): PageViewLayoutParams {
        val visiblePage = properties.getDouble(PROP_VISIBLE_PAGE, DEFAULT_VISIBLE_PAGE).toInt()
        val defaultItemsPadding = properties.read<Padding>(PROP_DEFAULT_PAGE_PADDING)!!
        val defaultItemsAlignment = properties.read<Alignment>(PROP_DEFAULT_CONTENT_ALIGNMENT)!!
        val itemsPadding = properties.read<ItemPaddingMap>(PROP_PAGE_PADDING)
        val itemsAlignment = properties.read<ItemAlignmentMap>(PROP_PAGE_ALIGNMENT)

        val childrenPadding =
            LayoutUtils.createChildrenPaddingMap(
                childrenList,
                defaultItemsPadding,
                itemsPadding?.paddings
            )
        val childrenAlignment =
            LayoutUtils.createChildrenAlignmentMap(
                childrenList,
                defaultItemsAlignment,
                itemsAlignment?.alignments
            )

        return PageViewLayoutParams(
            visiblePage = visiblePage,
            size = Vector2(width, height),
            itemsPadding = childrenPadding,
            itemsAlignment = childrenAlignment
        )
    }

    override fun getContentBounding(): AABB {
        val layoutBounds = layoutManager.getLayoutBounds(getLayoutParams())
        val minEdge = layoutBounds.min + contentNode.localPosition
        val maxEdge = layoutBounds.max + contentNode.localPosition

        return AABB(minEdge, maxEdge)
    }

}