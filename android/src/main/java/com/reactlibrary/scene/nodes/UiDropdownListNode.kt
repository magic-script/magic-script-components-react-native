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

package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.font.FontProvider
import com.reactlibrary.scene.nodes.layouts.UiLinearLayout
import com.reactlibrary.scene.nodes.layouts.manager.LinearLayoutManagerImpl

class UiDropdownListNode(initProps: ReadableMap,
                         context: Context,
                         viewRenderableLoader: ViewRenderableLoader,
                         fontProvider: FontProvider)
    : UiButtonNode(initProps, context, viewRenderableLoader, fontProvider) {


    companion object {
        const val PROP_LIST_MAX_HEIGHT = "listMaxHeight"
        const val PROP_LIST_TEXT_SZIE = "listTextSize"
        const val PROP_CHARACTERS_LIMIT = "maxCharacterLimit" // for list item
        const val PROP_MULTI_SELECT = "multiSelect"
        const val PROP_SHOW_LIST = "showList"
        const val PROP_SELECTED = "selected"

    }

    private val listNode: UiLinearLayout

    init {
        val listProps = JavaOnlyMap()
        listProps.putString(UiLinearLayout.PROP_ORIENTATION, "vertical")
        listProps.putString(PROP_ALIGNMENT, "top-left")
        listNode = UiLinearLayout(listProps, LinearLayoutManagerImpl())
    }

    override fun build() {
        super.build()
        listNode.build()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        setListPosition(props)
    }

    override fun addContent(child: Node) {
        if (child is UiDropdownListItemNode) {
            listNode.addContent(child)
        } else {
            super.addContent(child)
        }
    }

    override fun removeContent(child: Node) {
        if (child is UiDropdownListItemNode) {
            listNode.removeContent(child)
        } else {
            super.removeContent(child)
        }
    }

    override fun onViewClick() {
        super.onViewClick()
        if (contentNode.children.contains(listNode)) {
            contentNode.children.remove(listNode)
        } else {
            addContent(listNode)
        }
    }

    private fun setListPosition(props: Bundle) {
        if (props.containsKey(PROP_HEIGHT)) {
            val height = props.getDouble(PROP_HEIGHT).toFloat()
            if (height != WRAP_CONTENT_DIMENSION) {
                listNode.localPosition = Vector3(0F, -height, 0F)
            }
        } else if (props.containsKey(PROP_TEXT_SIZE)) {
            val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
            if (height != WRAP_CONTENT_DIMENSION.toDouble()) {
                val textSize = props.getDouble(PROP_TEXT_SIZE).toFloat()
                val posY = -textSize * PADDING_FACTOR_VERTICAL * 2
                listNode.localPosition = Vector3(0F, posY, 0F)
            }
        }
    }

}