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
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.UiLinearLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.HorizontalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.LinearLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.ItemAlignmentMap
import com.magicleap.magicscript.scene.nodes.props.ItemPaddingMap
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_VERTICAL
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.scene.nodes.views.CustomScrollView
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.containsAny
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read

open class UiListViewNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper
) : UiScrollViewNode(initProps, context, viewRenderableLoader, nodeClipper) {

    private val containerNode: UiLinearLayout

    companion object {
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_ORIENTATION = "orientation"
        const val PROP_DEFAULT_ITEM_PADDING = "defaultItemPadding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "defaultItemAlignment"
        const val PROP_ITEM_PADDING = "itemPadding"
        const val PROP_ITEM_ALIGNMENT = "itemAlignment"
        const val PROP_SCROLLING_ENABLED = "scrollingEnabled"
        const val PROP_SCROLL_TO_ITEM = "scrollToItem"
        const val PROP_SKIP_INVISIBLE_ITEMS = "skipInvisibleItems"

        const val DEFAULT_ORIENTATION = ORIENTATION_VERTICAL
        const val DEFAULT_ITEM_ALIGNMENT = "top-left"
        // default padding for each item [top, right, bottom, left]
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    private var contentAdded = false
    private var requestedScrollIndex = 0
    private var itemsPaddingMap: Map<Int, Padding>? = null
    private var defaultItemsPadding = Padding()
    private var contentSize = Vector3()

    init {
        // setting default values of properties
        properties.putDefault(PROP_ORIENTATION, DEFAULT_ORIENTATION)
        properties.putDefault(PROP_DEFAULT_ITEM_ALIGNMENT, DEFAULT_ITEM_ALIGNMENT)
        properties.putDefault(PROP_DEFAULT_ITEM_PADDING, DEFAULT_ITEM_PADDING)

        val containerProps = extractContainerProps(properties)
        val linearLayoutManager =
            LinearLayoutManager(VerticalLinearLayoutManager(), HorizontalLinearLayoutManager())
        containerNode = UiLinearLayout(containerProps, linearLayoutManager)

        onContentSizeChangedListener = { contentSize ->
            this.contentSize = contentSize
            forceSameItemsWidth()

            val size = readSize()

            if (size.x == WRAP_CONTENT_DIMENSION || size.y == WRAP_CONTENT_DIMENSION) {
                setNeedsRebuild(true)
            }
        }
    }

    override fun applyProperties(props: Bundle) {
        if (props.containsKey(PROP_ORIENTATION)) {
            val orientation = props.getString(PROP_ORIENTATION)
            properties.putString(PROP_SCROLL_DIRECTION, orientation)
            props.putString(PROP_SCROLL_DIRECTION, orientation)
        }

        super.applyProperties(props)


        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        if (props.containsAny(PROP_ITEM_PADDING, PROP_DEFAULT_ITEM_PADDING)) {
            refreshItemsPadding()
        }

        if (updatingProperties) {
            val containerProps = extractContainerProps(props)
            containerNode.update(containerProps)
        }

        setScrollingEnabled(props)
        setScrollToItem(props)
    }

    override fun provideDesiredSize(): Vector2 {
        val size = readSize()

        val width = if (size.x != WRAP_CONTENT_DIMENSION) {
            size.x
        } else {
            val vBarThickness = if (vBarNode != null) {
                calculateBarThickness(contentSize.x, contentSize.y)
            } else 0F
            contentSize.x + vBarThickness
        }

        val height = if (size.y != WRAP_CONTENT_DIMENSION) {
            size.y
        } else {
            val hBarThickness = if (hBarNode != null) {
                calculateBarThickness(contentSize.x, contentSize.y)
            } else 0F
            contentSize.y + hBarThickness
        }
        return Vector2(width, height)
    }

    override fun build() {
        super.build()
        // in case of rebuild, we don't want to attach again the content
        if (!contentAdded) {
            containerNode.build()
            addContent(containerNode)
            contentAdded = true
        }
    }

    override fun addContent(child: TransformNode) {
        when (child) {
            is UiListViewItemNode -> {
                addItem(child)
                val itemIndex = getItems().size - 1
                child.padding = itemsPaddingMap?.get(itemIndex) ?: defaultItemsPadding

                if (itemIndex == requestedScrollIndex) {
                    scrollToItem(requestedScrollIndex)
                }
            }
            is UiScrollBarNode -> {
                super.addContent(child)
                setNeedsRebuild(true) // we may need to update the size
            }
            else -> super.addContent(child)
        }
    }

    private fun readSize(): Vector2 {
        val width = properties.getDouble(PROP_WIDTH, WRAP_CONTENT_DIMENSION.toDouble())
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        return Vector2(width.toFloat(), height.toFloat())
    }

    private fun forceSameItemsWidth() {
        val orientation = properties.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION)
        val items = getItems()

        val minItemSize = if (orientation == ORIENTATION_VERTICAL) {
            val maxItemWidth = findMaxItemWidth(items)
            Vector2(maxItemWidth, 0f)
        } else {
            val maxItemHeight = findMaxItemHeight(items)
            Vector2(0f, maxItemHeight)
        }
        items.forEach { item ->
            item.minSize = minItemSize
        }
    }

    private fun setScrollingEnabled(props: Bundle) {
        val scrollingEnabled = props.read<Boolean>(PROP_SCROLLING_ENABLED) ?: return
        (view as CustomScrollView).scrollingEnabled = scrollingEnabled
    }

    private fun setScrollToItem(props: Bundle) {
        val itemIndex = props.read<Int>(PROP_SCROLL_TO_ITEM) ?: return
        scrollToItem(itemIndex)
    }

    private fun scrollToItem(itemIndex: Int) {
        val items = getItems()
        if (itemIndex > items.size - 1) {
            requestedScrollIndex = itemIndex
            return
        }

        val orientation = properties.read(PROP_ORIENTATION) ?: DEFAULT_ORIENTATION
        if (orientation == ORIENTATION_VERTICAL) {
            val sumHeightBeforeItem = calculateItemsHeight(items, itemIndex)
            val allItemsHeight = calculateItemsHeight(items, items.size)
            val maxTravel = allItemsHeight - getBounding().size().y

            if (maxTravel > 0) {
                val positionY = sumHeightBeforeItem / maxTravel
                (view as CustomScrollView).position = Vector2(0f, positionY)
            }
        } else {
            val sumWidthBeforeItem = calculateItemsWidth(items, itemIndex)
            val allItemsWidth = calculateItemsWidth(items, items.size)
            val maxTravel = allItemsWidth - getBounding().size().x

            if (maxTravel > 0) {
                val positionX = sumWidthBeforeItem / maxTravel
                (view as CustomScrollView).position = Vector2(positionX, 0f)
            }
        }
    }

    private fun addItem(listItem: UiListViewItemNode) {
        containerNode.addContent(listItem)
    }

    private fun getItems() = containerNode.childrenList.filterIsInstance<UiListViewItemNode>()

    private fun findMaxItemWidth(items: List<UiListViewItemNode>): Float {
        return items.maxBy { it.getBounding().size().x }?.getBounding()?.size()?.x ?: 0f
    }

    private fun findMaxItemHeight(items: List<UiListViewItemNode>): Float {
        return items.maxBy { it.getBounding().size().y }?.getBounding()?.size()?.y ?: 0f
    }

    /**
     * Calculates summary height of items before item at [toIndex]
     *
     * @param toIndex exclusive index of item
     */
    private fun calculateItemsHeight(items: List<UiListViewItemNode>, toIndex: Int): Float {
        var sum = 0f
        for (i in 0 until toIndex) {
            sum += items[i].getBounding().size().y
        }
        return sum
    }

    /**
     * Calculates summary width of items before item at [toIndex]
     *
     * @param toIndex exclusive index of item
     */
    private fun calculateItemsWidth(items: List<UiListViewItemNode>, toIndex: Int): Float {
        var sum = 0f
        for (i in 0 until toIndex) {
            sum += items[i].getBounding().size().x
        }
        return sum
    }

    private fun extractContainerProps(props: Bundle): JavaOnlyMap {
        val containerProps = JavaOnlyMap()

        props.read<String>(PROP_ORIENTATION)?.let { orientation ->
            containerProps.putString(UiLinearLayout.PROP_ORIENTATION, orientation)
        }

        props.read<String>(PROP_DEFAULT_ITEM_ALIGNMENT)?.let { alignment ->
            containerProps.putString(UiLinearLayout.PROP_DEFAULT_ITEM_ALIGNMENT, alignment)
        }

        props.read<Boolean>(PROP_SKIP_INVISIBLE_ITEMS)?.let { skip ->
            containerProps.putBoolean(UiBaseLayout.PROP_SKIP_INVISIBLE_ITEMS, skip)
        }

        props.read<ItemAlignmentMap>(PROP_ITEM_ALIGNMENT)?.let { map ->
            val alignmentsArray = JavaOnlyArray()
            map.alignments.forEach { (index, alignment) ->
                val alignmentMap = JavaOnlyMap.of("index", index, "alignment", alignment)
                alignmentsArray.pushMap(alignmentMap)
            }
            containerProps.putArray(UiLinearLayout.DEFAULT_ITEM_ALIGNMENT, alignmentsArray)
        }

        return containerProps
    }

    private fun refreshItemsPadding() {
        this.itemsPaddingMap = properties.read<ItemPaddingMap>(PROP_ITEM_PADDING)?.paddings
        this.defaultItemsPadding = properties.read<Padding>(PROP_DEFAULT_ITEM_PADDING)!!

        getItems().forEachIndexed { index, item ->
            item.padding = itemsPaddingMap?.get(index) ?: defaultItemsPadding
        }
    }

}