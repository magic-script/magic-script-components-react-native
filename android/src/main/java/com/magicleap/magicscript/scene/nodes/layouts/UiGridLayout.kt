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
import com.magicleap.magicscript.scene.nodes.layouts.params.GridLayoutParams
import com.magicleap.magicscript.scene.nodes.props.*
import com.magicleap.magicscript.utils.*

class UiGridLayout(initProps: ReadableMap, layoutManager: LayoutManager<GridLayoutParams>) :
    UiBaseLayout<GridLayoutParams>(initProps, layoutManager) {

    companion object {
        // properties
        const val PROP_COLUMNS = "columns"
        const val PROP_ROWS = "rows"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"
        const val PROP_ITEM_PADDING = "itemPadding"
        const val PROP_ITEM_ALIGNMENT = "itemAlignment"

        // default values
        const val DYNAMIC_VALUE = 0 // 0 means that number of columns / rows can grow
        const val COLUMNS_DEFAULT = DYNAMIC_VALUE.toDouble()
        const val ROWS_DEFAULT = 1.0
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_ITEM_ALIGNMENT = "top-left"
        // default padding for each item [top, right, bottom, left]
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    // the actual number of columns
    val columns: Int get() = properties.getDouble(PROP_COLUMNS, COLUMNS_DEFAULT).toInt()

    // the actual number of rows
    val rows: Int
        get() {
            val userSpecifiedRows = properties.getDouble(PROP_ROWS, ROWS_DEFAULT).toInt()
            val userSpecifiedColumns = properties.getDouble(PROP_COLUMNS, COLUMNS_DEFAULT).toInt()

            // if both columns and rows numbers are dynamic, 1 row should be used
            return if (userSpecifiedRows == DYNAMIC_VALUE && userSpecifiedColumns == DYNAMIC_VALUE) {
                1
            } else if (userSpecifiedColumns == DYNAMIC_VALUE) {
                userSpecifiedRows
            } else {
                DYNAMIC_VALUE
            }
        }

    init {
        // set default values of properties

        // alignment of the grid itself (pivot)
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_COLUMNS, COLUMNS_DEFAULT)
        properties.putDefault(PROP_ROWS, ROWS_DEFAULT)
        properties.putDefault(PROP_DEFAULT_ITEM_ALIGNMENT, DEFAULT_ITEM_ALIGNMENT)
        // default padding for each item [top, right, bottom, left]
        properties.putDefault(PROP_DEFAULT_ITEM_PADDING, DEFAULT_ITEM_PADDING)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsAny(
                PROP_COLUMNS,
                PROP_ROWS,
                PROP_DEFAULT_ITEM_PADDING,
                PROP_DEFAULT_ITEM_ALIGNMENT
            )
        ) {
            requestLayout()
        }
    }

    override fun getContentBounding(): AABB {
        val layoutBounds = layoutManager.getLayoutBounds(getLayoutParams())
        val minEdge = layoutBounds.min + contentNode.localPosition
        val maxEdge = layoutBounds.max + contentNode.localPosition

        return AABB(minEdge, maxEdge)
    }

    override fun getLayoutParams(): GridLayoutParams {
        val defaultItemsPadding = properties.read<Padding>(PROP_DEFAULT_ITEM_PADDING)!!
        val defaultItemsAlignment = properties.read<Alignment>(PROP_DEFAULT_ITEM_ALIGNMENT)!!
        val itemsPadding = properties.read<ItemGridPaddingMap>(PROP_ITEM_PADDING)
        val itemsAlignment = properties.read<ItemGridAlignmentMap>(PROP_ITEM_ALIGNMENT)

        val childrenPadding =
            LayoutUtils.createChildrenPaddingMap(
                columns = columns,
                rows = rows,
                children = childrenList,
                defaultPadding = defaultItemsPadding,
                childPaddings = itemsPadding?.paddings
            )

        val childrenAlignment =
            LayoutUtils.createChildrenAlignmentMap(
                columns = columns,
                rows = rows,
                children = childrenList,
                defaultAlignment = defaultItemsAlignment,
                childAlignments = itemsAlignment?.alignments
            )

        return GridLayoutParams(
            columns = columns,
            rows = rows,
            size = Vector2(width, height),
            itemsPadding = childrenPadding,
            itemsAlignment = childrenAlignment
        )
    }

}