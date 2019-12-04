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
import com.magicleap.magicscript.scene.nodes.layouts.manager.GridLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.*

class UiGridLayout(initProps: ReadableMap, layoutManager: GridLayoutManager) :
    UiLayout(initProps, layoutManager) {

    companion object {
        // properties
        const val PROP_COLUMNS = "columns"
        const val PROP_ROWS = "rows"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"

        // default values
        const val COLUMNS_DEFAULT = 1
        const val ROWS_DEFAULT = 0 // 0 means unspecified (will grow with content)
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_ITEM_ALIGNMENT = "center-center"
        // default padding for each item [top, right, bottom, left]
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    init {
        // set default values of properties

        // alignment of the grid itself (pivot)
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_COLUMNS, COLUMNS_DEFAULT.toDouble())
        properties.putDefault(PROP_ROWS, ROWS_DEFAULT.toDouble())
        properties.putDefault(PROP_DEFAULT_ITEM_ALIGNMENT, DEFAULT_ITEM_ALIGNMENT)
        properties.putDefault(PROP_DEFAULT_ITEM_PADDING, DEFAULT_ITEM_PADDING)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setColumns(props)
        setRows(props)
        setItemPadding(props)
        setItemAlignment(props)
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        val itemPadding = PropertiesReader.readPadding(properties, PROP_DEFAULT_ITEM_PADDING)
            ?: Padding()
        return Bounding(
            childBounds.left + contentNode.localPosition.x - itemPadding.left,
            childBounds.bottom + contentNode.localPosition.y - itemPadding.bottom,
            childBounds.right + contentNode.localPosition.x + itemPadding.right,
            childBounds.top + contentNode.localPosition.y + itemPadding.top
        )
    }

    override fun setLayoutSize(props: Bundle) {
        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            logMessage("width and height properties are not supported yet", true)
        }
    }

    private fun setColumns(props: Bundle) {
        if (props.containsKey(PROP_COLUMNS)) {
            (layoutManager as GridLayoutManager).columns = props.getDouble(PROP_COLUMNS).toInt()
            requestLayout()
        }
    }

    private fun setRows(props: Bundle) {
        if (props.containsKey(PROP_ROWS)) {
            (layoutManager as GridLayoutManager).rows = props.getDouble(PROP_ROWS).toInt()
            requestLayout()
        }
    }

    private fun setItemPadding(props: Bundle) {
        val padding = PropertiesReader.readPadding(props, PROP_DEFAULT_ITEM_PADDING)
        if (padding != null) {
            (layoutManager as GridLayoutManager).itemPadding = padding
            requestLayout()
        }
    }

    private fun setItemAlignment(props: Bundle) {
        val alignment = PropertiesReader.readAlignment(props, PROP_DEFAULT_ITEM_ALIGNMENT)
        if (alignment != null) {
            (layoutManager as GridLayoutManager)
            layoutManager.itemVerticalAlignment = alignment.vertical
            layoutManager.itemHorizontalAlignment = alignment.horizontal
            requestLayout()
        }
    }

}