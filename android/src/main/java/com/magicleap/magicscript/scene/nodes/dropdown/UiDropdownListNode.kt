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
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.base.Layoutable
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.scene.nodes.button.UiButtonNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.views.CustomButton
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.read

open class UiDropdownListNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper,
    fontProvider: FontProvider,
    iconsRepo: IconsRepository
) : UiButtonNode(
    initProps,
    context,
    viewRenderableLoader,
    nodeClipper,
    fontProvider,
    iconsRepo
), Layoutable {

    companion object {
        const val PROP_LIST_MAX_HEIGHT = "listMaxHeight"
        const val PROP_LIST_TEXT_SIZE = "listTextSize"
        const val PROP_CHARACTERS_LIMIT = "maxCharacterLimit" // for list item
        const val PROP_MULTI_SELECT = "multiSelect"
        const val PROP_SHOW_LIST = "showList"

        const val Z_ORDER_OFFSET = 2e-5F
        const val ICON_NAME = "dropdown"
        const val Z_OFFSET_WHEN_EXPANDED = 0.05F
    }

    override val charactersSpacing = 0F

    // Events
    var onSelectionChangedListener: ((selectedItems: List<UiDropdownListItemNode>) -> Unit)? = null

    private val listNode: DropdownItemsListNode
    private var listNodeAdded = false

    private var lastContentBounds = AABB()

    private val onListVisibilityChangedListener: (visible: Boolean) -> Unit = { visible ->
        applyZTranslation(visible)
    }

    init {
        val listProps = JavaOnlyMap()
        listProps.putString(PROP_ALIGNMENT, "top-left")
        listNode = DropdownItemsListNode(
            listProps,
            context,
            viewRenderableLoader,
            nodeClipper
        )
        listNode.onListVisibilityChanged = onListVisibilityChangedListener

        properties.putString(PROP_ICON_TYPE, ICON_NAME)
    }

    override fun build() {
        super.build()

        if (!listNodeAdded) {
            setupListNode()
            addContent(listNode)
            listNodeAdded = true
        }
    }

    override fun provideDesiredSize(): Vector2 {
        return Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
    }

    override fun setupView() {
        super.setupView()
        (view as CustomButton).apply {
            iconPosition = CustomButton.IconPosition.RIGHT
            borderEnabled = false
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

    override fun addContent(child: ReactNode) {
        if (child is UiDropdownListItemNode) {
            configureListItems(listOf(child), properties)
            listNode.addContent(child)
        } else {
            super.addContent(child)
        }
    }

    override fun removeContent(child: ReactNode) {
        if (child is UiDropdownListItemNode) {
            listNode.removeContent(child)
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

        updateListPosition()
    }

    private fun updateListPosition() {
        val contentBounds = getContentBounding()
        if (!contentBounds.equalInexact(lastContentBounds)) {
            val listX = contentBounds.min.x
            val listY = contentBounds.min.y - (contentBounds.max.y - contentBounds.min.y) / 3
            // Moving content forward, so it'll receive touch events first
            listNode.localPosition = Vector3(listX, listY, Z_ORDER_OFFSET)
        }

        lastContentBounds = contentBounds
    }

    private fun configureListItems(items: List<UiDropdownListItemNode>, props: Bundle) {
        setListTextSize(props)
        setCharactersLimit(items, props)
    }

    private fun setListTextSize(props: Bundle) {
        if (props.containsKey(PROP_LIST_TEXT_SIZE)) {
            listNode.itemsTextSize = props.getDouble(PROP_LIST_TEXT_SIZE).toFloat()
            return
        }

        if (props.containsKey(PROP_TEXT_SIZE)) {
            listNode.itemsTextSize = props.getDouble(PROP_TEXT_SIZE).toFloat()
            return
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
            listNode.multiSelect = multiSelect
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

    private fun setupListNode() {
        listNode.onSelectionChangedListener = { selectedItems ->
            onSelectionChangedListener?.invoke(selectedItems)
        }
        listNode.build()
        listNode.hide()
    }

    private fun showList() {
        listNode.show()
    }

    private fun hideList() {
        listNode.hide()
    }

    private fun applyZTranslation(listVisible: Boolean) {
        val position = contentNode.localPosition
        position.z = if (listVisible) Z_OFFSET_WHEN_EXPANDED else 0f
        contentNode.localPosition = position
    }

}