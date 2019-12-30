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
import com.magicleap.magicscript.scene.nodes.base.LayoutParams
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.HorizontalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_VERTICAL
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read

class UiLinearLayout @JvmOverloads constructor(
    props: ReadableMap,
    layoutManager: LayoutManager<LayoutParams> = VerticalLinearLayoutManager()
) : UiBaseLayout<LayoutParams>(props, layoutManager) {

    companion object {
        // properties
        const val PROP_ORIENTATION = "orientation"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"

        // default values
        const val DEFAULT_ORIENTATION = ORIENTATION_VERTICAL
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_ITEM_ALIGNMENT = "top-left"
        // default padding for each item [top, right, bottom, left]
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    // default padding for each item [top, right, bottom, left]
    private var itemPadding = Padding(0F, 0F, 0F, 0F)

    private var itemHorizontalAlignment = Alignment.HorizontalAlignment.LEFT

    private var itemVerticalAlignment = Alignment.VerticalAlignment.TOP

    init {
        // set default values of properties

        // alignment of the layout itself (pivot)
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_ORIENTATION, DEFAULT_ORIENTATION)
        properties.putDefault(PROP_DEFAULT_ITEM_ALIGNMENT, DEFAULT_ITEM_ALIGNMENT)
        properties.putDefault(PROP_DEFAULT_ITEM_PADDING, DEFAULT_ITEM_PADDING)
    }

    override fun applyProperties(props: Bundle) {
        // to apply height first
        if (props.containsKey(PROP_ORIENTATION)) {
            val isVertical =
                props.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION) == ORIENTATION_VERTICAL

            if (isVertical && layoutManager !is VerticalLinearLayoutManager) {
                layoutManager = VerticalLinearLayoutManager()
                requestLayout()
            }

            if (!isVertical && layoutManager !is HorizontalLinearLayoutManager) {
                layoutManager = HorizontalLinearLayoutManager()
                requestLayout()
            }

        }

        super.applyProperties(props)
        setItemPadding(props)
        setItemAlignment(props)
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

    override fun getLayoutParams(): LayoutParams {
        return LayoutParams(
            size = Vector2(width, height),
            itemPadding = itemPadding,
            itemHorizontalAlignment = itemHorizontalAlignment,
            itemVerticalAlignment = itemVerticalAlignment
        )
    }

    private fun setItemPadding(props: Bundle) {
        val padding = props.read<Padding>(PROP_DEFAULT_ITEM_PADDING)
        if (padding != null) {
            this.itemPadding = padding
            requestLayout()
        }
    }

    private fun setItemAlignment(props: Bundle) {
        val alignment = props.read<Alignment>(PROP_DEFAULT_ITEM_ALIGNMENT)
        if (alignment != null) {
            this.itemVerticalAlignment = alignment.vertical
            this.itemHorizontalAlignment = alignment.horizontal
            requestLayout()
        }
    }

}
