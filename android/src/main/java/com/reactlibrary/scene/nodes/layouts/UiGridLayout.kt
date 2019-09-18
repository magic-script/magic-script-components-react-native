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

package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.scene.nodes.layouts.manager.FlexGridManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils

class UiGridLayout(initProps: ReadableMap) : UiLayout(initProps) {

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
    }

    init {
        layoutManager = FlexGridManager(this)

        // set default values of properties

        // alignment of the grid itself (pivot)
        if (!properties.containsKey(PROP_ALIGNMENT)) {
            properties.putString(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        }
    }

    var columns: Int = properties.getDouble(PROP_COLUMNS, COLUMNS_DEFAULT.toDouble()).toInt()
        private set(value) {
            if (value == 0 && rows == 0) {
                field = 1 // can't be 0 along with rows
            } else {
                field = value
            }

        }

    var rows: Int = properties.getDouble(PROP_ROWS, ROWS_DEFAULT.toDouble()).toInt()
        private set(value) {
            if (value == 0 && columns == 0) {
                field = 1 // can't be 0 along with columns
            } else {
                field = value
            }
        }

    // default padding for each item [top, right, bottom, left]
    var itemPadding = Padding(0F, 0F, 0F, 0F)
        private set

    var itemHorizontalAlignment = ViewRenderable.HorizontalAlignment.CENTER
        private set

    var itemVerticalAlignment = ViewRenderable.VerticalAlignment.CENTER
        private set

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setColumns(props)
        setRows(props)
        setItemPadding(props)
        setItemAlignment(props)
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        return Bounding(
                childBounds.left + contentNode.localPosition.x - itemPadding.left,
                childBounds.bottom + contentNode.localPosition.y - itemPadding.bottom,
                childBounds.right + contentNode.localPosition.x + itemPadding.right,
                childBounds.top + contentNode.localPosition.y + itemPadding.top
        )
    }

    private fun setColumns(props: Bundle) {
        if (props.containsKey(PROP_COLUMNS)) {
            columns = props.getDouble(PROP_COLUMNS).toInt()
            requestLayout()
        }
    }

    private fun setRows(props: Bundle) {
        if (props.containsKey(PROP_ROWS)) {
            rows = props.getDouble(PROP_ROWS).toInt()
            requestLayout()
        }
    }

    private fun setItemPadding(props: Bundle) {
        val padding = PropertiesReader.readPadding(props, PROP_DEFAULT_ITEM_PADDING)
        if (padding != null) {
            itemPadding = padding
            requestLayout()
        }
    }

    private fun setItemAlignment(props: Bundle) {
        val alignment = props.getString(PROP_DEFAULT_ITEM_ALIGNMENT)
        if (alignment != null) {
            val alignmentArray = alignment.split("-")
            if (alignmentArray.size == 2) {
                val verticalAlign = alignmentArray[0]
                val horizontalAlign = alignmentArray[1]
                itemVerticalAlignment = ViewRenderable.VerticalAlignment.valueOf(verticalAlign.toUpperCase())
                itemHorizontalAlignment = ViewRenderable.HorizontalAlignment.valueOf(horizontalAlign.toUpperCase())
            }
        }
    }

}