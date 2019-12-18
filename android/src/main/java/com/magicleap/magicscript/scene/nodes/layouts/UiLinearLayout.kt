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
import com.magicleap.magicscript.scene.nodes.base.UiLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.LinearLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.LinearLayoutManagerImpl
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_VERTICAL
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.logMessage
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read

class UiLinearLayout @JvmOverloads constructor(
    props: ReadableMap,
    layoutManager: LinearLayoutManager = LinearLayoutManagerImpl()
) : UiLayout(props, layoutManager) {

    companion object {
        // properties
        const val PROP_ORIENTATION = "orientation"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"

        // default values
        const val DEFAULT_ORIENTATION = ORIENTATION_VERTICAL
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_ITEM_ALIGNMENT = "center-center"
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
        setOrientation(props)
        setItemPadding(props)
        setItemAlignment(props)
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        val itemPadding = properties.read(PROP_DEFAULT_ITEM_PADDING) ?: Padding()
        return Bounding(
            childBounds.left + contentNode.localPosition.x - itemPadding.left,
            childBounds.bottom + contentNode.localPosition.y - itemPadding.bottom,
            childBounds.right + contentNode.localPosition.x + itemPadding.right,
            childBounds.top + contentNode.localPosition.y + itemPadding.top
        )
    }

    private fun setOrientation(props: Bundle) {
        if (props.containsKey(PROP_ORIENTATION)) {
            val isVertical =
                props.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION) == ORIENTATION_VERTICAL
            (layoutManager as LinearLayoutManager).isVertical = isVertical
            requestLayout()
        }
    }

    private fun setItemPadding(props: Bundle) {
        val padding = props.read<Padding>(PROP_DEFAULT_ITEM_PADDING)
        if (padding != null) {
            (layoutManager as LinearLayoutManager).itemPadding = padding
            requestLayout()
        }
    }

    private fun setItemAlignment(props: Bundle) {
        val alignment = props.read<Alignment>(PROP_DEFAULT_ITEM_ALIGNMENT)
        if (alignment != null) {
            (layoutManager as LinearLayoutManager)
            layoutManager.itemVerticalAlignment = alignment.vertical
            layoutManager.itemHorizontalAlignment = alignment.horizontal
            requestLayout()
        }
    }

}
