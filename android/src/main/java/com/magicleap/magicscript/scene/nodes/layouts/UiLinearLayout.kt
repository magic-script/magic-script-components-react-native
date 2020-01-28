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

package com.magicleap.magicscript.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.params.LinearLayoutParams
import com.magicleap.magicscript.scene.nodes.props.*
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.containsAny
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read

class UiLinearLayout(props: ReadableMap, layoutManager: LayoutManager<LinearLayoutParams>) :
    UiBaseLayout<LinearLayoutParams>(props, layoutManager) {

    companion object {
        // properties
        const val PROP_ORIENTATION = "orientation"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"
        const val PROP_ITEM_PADDING = "itemPadding"
        const val PROP_ITEM_ALIGNMENT = "itemAlignment"

        // default values
        const val DEFAULT_ORIENTATION = ORIENTATION_VERTICAL
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_ITEM_ALIGNMENT = "top-left"
        // default padding for each item [top, right, bottom, left]
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    init {
        // set default values of properties

        // alignment of the layout itself (pivot)
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_ORIENTATION, DEFAULT_ORIENTATION)
        properties.putDefault(PROP_DEFAULT_ITEM_ALIGNMENT, DEFAULT_ITEM_ALIGNMENT)
        properties.putDefault(PROP_DEFAULT_ITEM_PADDING, DEFAULT_ITEM_PADDING)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsAny(
                PROP_ORIENTATION,
                PROP_DEFAULT_ITEM_ALIGNMENT,
                PROP_DEFAULT_ITEM_PADDING,
                PROP_ITEM_ALIGNMENT,
                PROP_ITEM_PADDING
            )
        ) {
            requestLayout()
        }
    }

    override fun getContentBounding(): Bounding {
        val layoutBounds = layoutManager.getLayoutBounds(getLayoutParams())
        return Bounding(
            layoutBounds.left + contentNode.localPosition.x,
            layoutBounds.bottom + contentNode.localPosition.y,
            layoutBounds.right + contentNode.localPosition.x,
            layoutBounds.top + contentNode.localPosition.y
        )
    }

    override fun getLayoutParams(): LinearLayoutParams {
        val orientation = properties.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION)
        val defaultItemsPadding = properties.read<Padding>(PROP_DEFAULT_ITEM_PADDING)!!
        val defaultItemsAlignment = properties.read<Alignment>(PROP_DEFAULT_ITEM_ALIGNMENT)!!
        val itemsPadding = properties.read<ItemListPaddingMap>(PROP_ITEM_PADDING)
        val itemsAlignment = properties.read<ItemListAlignmentMap>(PROP_ITEM_ALIGNMENT)

        val childrenPadding =
            LayoutUtils.createChildrenPaddingMap(
                childrenList.size,
                defaultItemsPadding,
                itemsPadding?.paddings
            )
        val childrenAlignment =
            LayoutUtils.createChildrenAlignmentMap(
                childrenList.size,
                defaultItemsAlignment,
                itemsAlignment?.alignments
            )


        return LinearLayoutParams(
            orientation = orientation,
            size = Vector2(width, height),
            itemsPadding = childrenPadding,
            itemsAlignment = childrenAlignment
        )
    }

}
