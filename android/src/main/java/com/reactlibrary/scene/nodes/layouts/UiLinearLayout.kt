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
import com.reactlibrary.scene.nodes.layouts.manager.FlexLinearManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.*
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.putDefaultString

class UiLinearLayout(props: ReadableMap) : UiLayout(props) {

    companion object {
        // properties
        const val PROP_ORIENTATION = "orientation"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"

        const val ORIENTATION_VERTICAL = "vertical"
        const val ORIENTATION_HORIZONTAL = "horizontal"

        // default values
        const val ORIENTATION_DEFAULT = ORIENTATION_VERTICAL
        const val DEFAULT_ALIGNMENT = "top-left"
    }

    init {
        layoutManager = FlexLinearManager(this)

        // set default values of properties

        // alignment of the layout itself (pivot)
        properties.putDefaultString(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
    }

    var orientation = properties.getString(PROP_ORIENTATION, ORIENTATION_DEFAULT)
        private set

    // default padding for each item [top, right, bottom, left]
    var itemPadding = Padding(0F, 0F, 0F, 0F)
        private set

    var itemHorizontalAlignment = ViewRenderable.HorizontalAlignment.CENTER
        private set

    var itemVerticalAlignment = ViewRenderable.VerticalAlignment.CENTER
        private set

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setOrientation(props)
        setItemPadding(props)
        setItemAlignment(props)
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        // logMessage("linear child bounds " + childBounds.toString())
        // logMessage("linear child localPosition " + contentNode.localPosition.toString())
        return Bounding(
                childBounds.left + contentNode.localPosition.x - itemPadding.left,
                childBounds.bottom + contentNode.localPosition.y - itemPadding.bottom,
                childBounds.right + contentNode.localPosition.x + itemPadding.right,
                childBounds.top + contentNode.localPosition.y + itemPadding.top
        )
    }

    private fun setOrientation(props: Bundle) {
        if (props.containsKey(PROP_ORIENTATION)) {
            orientation = props.getString(PROP_ORIENTATION)
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

    fun isVertical(): Boolean {
        return orientation == ORIENTATION_VERTICAL
    }
}
