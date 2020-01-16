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

package com.magicleap.magicscript.scene.nodes.dropdown

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.ar.ViewRenderableLoaderImpl
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.UiButtonNode
import com.magicleap.magicscript.scene.nodes.base.Layoutable
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.views.CustomButton
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read

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

        const val Z_ORDER_OFFSET = 2e-5F
    }

    // Events
    var onSelectionChangedListener: ((selectedItems: List<UiDropdownListItemNode>) -> Unit)? = null
    var onListVisibilityChanged: ((isVisible: Boolean) -> Unit)? = null

    private val listNode: DropdownItemsListNode
    private var selectedItems = mutableListOf<UiDropdownListItemNode>()

    private var multiSelect = false
    private var listNodeAdded = false

    init {
        val listProps = JavaOnlyMap()
        listProps.putString(PROP_ALIGNMENT, "top-left")
        listNode = DropdownItemsListNode(listProps, context, ViewRenderableLoaderImpl(context))

        properties.putString(PROP_ICON, "arrow-down")
        properties.putDefault(PROP_SHOW_LIST, false)
    }

    override fun build() {
        super.build()

        if (!listNodeAdded) {
            listNode.build()
            addContent(listNode)
            listNodeAdded = true
        }

        (view as CustomButton).iconPosition = CustomButton.IconPosition.RIGHT
    }

    override fun loadRenderable() {
        super.loadRenderable()
        if (!listNode.renderableRequested) {
            listNode.attachRenderable()
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        val listItems = contentNode.children.filterIsInstance<UiDropdownListItemNode>()
        configureListItems(listItems, props)

        setShowList(props)
        setMultiSelect(props)
        setMaxListHeight(props)
    }


    override fun addContent(child: TransformNode) {
        if (child is UiDropdownListItemNode) {
            configureListItems(listOf(child), properties)
            if (child.isSelected) {
                selectedItems.add(child)
            }
            child.onSelectionChanged = { isSelected ->
                handleChildSelectionChanged(child, isSelected)
            }
            listNode.addContent(child)
        } else {
            super.addContent(child)
        }
    }

    override fun removeContent(child: TransformNode) {
        if (child is UiDropdownListItemNode) {
            listNode.removeContent(child)
            if (selectedItems.contains(child)) {
                selectedItems.remove(child)
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
        // Moving content forward, so it'll receive touch events first
        listNode.localPosition = Vector3(listX, listY, Z_ORDER_OFFSET)
    }

    private fun configureListItems(items: List<UiDropdownListItemNode>, props: Bundle) {
        setListTextSize(items, props)
        setCharactersLimit(items, props)
    }

    private fun setListTextSize(items: List<UiDropdownListItemNode>, props: Bundle) {
        if (props.containsKey(PROP_LIST_TEXT_SIZE)) {
            listNode.itemsTextSize = props.getDouble(PROP_LIST_TEXT_SIZE).toFloat()
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

    private fun setMultiSelect(props: Bundle) {
        val multiSelect = props.read<Boolean>(PROP_MULTI_SELECT)
        if (multiSelect != null) {
            this.multiSelect = multiSelect
        }
    }

    private fun setShowList(props: Bundle) {
        if (props.containsKey(PROP_SHOW_LIST)) {
            val show = props.getBoolean(PROP_SHOW_LIST)
            if (show && !listNode.isVisible) {
                showList()
            } else if (!show && listNode.isVisible) {
                hideList()
            }
        }
    }

    private fun setMaxListHeight(props: Bundle) {
        val maxListHeight = props.read<Double>(PROP_LIST_MAX_HEIGHT)
        if (maxListHeight != null) {
            listNode.maxHeight = maxListHeight.toFloat()
        }
    }

    private fun showList() {
        listNode.show()
        properties.putBoolean(PROP_SHOW_LIST, true)
        onListVisibilityChanged?.invoke(true)
    }

    private fun hideList() {
        listNode.hide()
        properties.putBoolean(PROP_SHOW_LIST, false)
        onListVisibilityChanged?.invoke(false)
    }

    private fun handleChildSelectionChanged(child: UiDropdownListItemNode, isSelected: Boolean) {
        if (multiSelect) {
            if (isSelected) {
                selectedItems.add(child)
            } else {
                selectedItems.remove(child)
            }
        } else {
            val lastSelectedItem = selectedItems.firstOrNull()
            lastSelectedItem?.deselect(silently = true)
            child.select(silently = true)
            selectedItems = mutableListOf(child)
            if (listNode.isVisible) {
                hideList()
            }
        }
        onSelectionChangedListener?.invoke(selectedItems)
    }

}