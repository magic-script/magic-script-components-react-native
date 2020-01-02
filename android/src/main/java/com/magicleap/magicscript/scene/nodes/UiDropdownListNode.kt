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
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.base.Layoutable
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.UiLinearLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.views.CustomButton
import com.magicleap.magicscript.utils.logMessage

class UiDropdownListNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    fontProvider: FontProvider,
    iconsRepo: IconsRepository
) : UiButtonNode(initProps, context, viewRenderableLoader, fontProvider, iconsRepo), Layoutable {

    companion object {
        const val PROP_LIST_MAX_HEIGHT = "listMaxHeight"
        const val PROP_LIST_TEXT_SIZE = "listTextSize"
        const val PROP_CHARACTERS_LIMIT = "maxCharacterLimit" // for list item
        const val PROP_MULTI_SELECT = "multiSelect"
        const val PROP_SHOW_LIST = "showList"
        const val PROP_SELECTED = "selected"
    }

    // Events
    var onSelectionChangedListener: ((itemIndex: Int) -> Unit)? = null
    var onListVisibilityChanged: ((isVisible: Boolean) -> Unit)? = null

    private val listNode: UiLinearLayout
    private var lastSelectedItem: UiDropdownListItemNode? = null

    init {
        val listProps = JavaOnlyMap()
        listProps.putString(PROP_ALIGNMENT, "top-left")
        listProps.putString(UiLinearLayout.PROP_ORIENTATION, "vertical")
        listProps.putString(UiLinearLayout.PROP_DEFAULT_ITEM_ALIGNMENT, "top-left")
        properties.putString(PROP_ICON, "arrow-down")

        listNode = UiLinearLayout(listProps, VerticalLinearLayoutManager())
    }

    override fun build() {
        super.build()

        listNode.build()
        addContent(listNode)
        hideList()

        (view as CustomButton).iconPosition = CustomButton.IconPosition.RIGHT
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        val listItems = contentNode.children.filterIsInstance<UiDropdownListItemNode>()
        configureListItems(listItems, props)
        setShowList(props)
    }

    override fun addContent(child: TransformNode) {
        if (child is UiDropdownListItemNode) {
            configureListItems(listOf(child), properties)
            child.onSelectedListener = {
                lastSelectedItem?.isSelected = false
                lastSelectedItem = child
                val index = listNode.contentNode.children.size
                onSelectionChangedListener?.invoke(index)
                logMessage("on item selected, index= $index")
            }
            listNode.addContent(child)
        } else {
            super.addContent(child)
        }
    }

    override fun removeContent(child: TransformNode) {
        if (child is UiDropdownListItemNode) {
            listNode.removeContent(child)
            if (child == lastSelectedItem) {
                lastSelectedItem = null
            }
        } else {
            super.removeContent(child)
        }
    }

    override fun onViewClick() {
        super.onViewClick()
        if (listNode.isVisible) {
            hideList()
        } else {
            showList()
        }
    }

    override fun onUpdate(deltaSeconds: Float) {
        super.onUpdate(deltaSeconds)

        val bounding = getContentBounding()
        val listX = bounding.left
        val listY = bounding.bottom - (bounding.top - bounding.bottom) / 3
        listNode.localPosition = Vector3(listX, listY, 0F)
    }

    private fun configureListItems(items: List<UiDropdownListItemNode>, props: Bundle) {
        setListTextSize(items, props)
        setCharactersLimit(items, props)
    }

    private fun setListTextSize(items: List<UiDropdownListItemNode>, props: Bundle) {
        if (props.containsKey(PROP_LIST_TEXT_SIZE)) {
            val textSize = props.getDouble(PROP_LIST_TEXT_SIZE)
            items.forEach { item ->
                item.update(JavaOnlyMap.of(UiTextNode.PROP_TEXT_SIZE, textSize))
            }
        }
    }

    private fun setCharactersLimit(items: List<UiDropdownListItemNode>, props: Bundle) {
        if (props.containsKey(PROP_CHARACTERS_LIMIT)) {
            val charsLimit = props.getDouble(PROP_CHARACTERS_LIMIT).toInt()
            items.forEach { item ->
                item.maxCharacters = charsLimit
            }
        }
    }

    private fun setShowList(props: Bundle) {
        if (props.containsKey(PROP_SHOW_LIST)) {
            val show = props.getBoolean(PROP_SHOW_LIST)
            if (show && !listNode.isVisible) {
                showList()
            } else if (listNode.isVisible) {
                hideList()
            }
        }
    }

    private fun showList() {
        listNode.show()
        onListVisibilityChanged?.invoke(true)
    }

    private fun hideList() {
        listNode.hide()
        onListVisibilityChanged?.invoke(false)
    }

}