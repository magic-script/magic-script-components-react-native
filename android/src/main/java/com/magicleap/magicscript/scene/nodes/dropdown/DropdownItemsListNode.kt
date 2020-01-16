package com.magicleap.magicscript.scene.nodes.dropdown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.getSizeInMeters


class DropdownItemsListNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader
) : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        const val DEFAULT_ITEMS_TEXT_SIZE = 0.025F
    }

    var maxHeight = WRAP_CONTENT_DIMENSION
        set(value) {
            field = value
            setNeedsRebuild(true)
        }

    var itemsTextSize: Float = DEFAULT_ITEMS_TEXT_SIZE
        set(value) {
            field = value
            setNeedsRebuild(true)
        }

    private val items = mutableListOf<UiDropdownListItemNode>()
    private var itemsAdapter = DropdownItemsAdapter(context, R.layout.dropdown_item, items)

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.dropdown_list_view, null)
    }

    override fun provideDesiredSize(): Vector2 {
        itemsAdapter = DropdownItemsAdapter(context, R.layout.dropdown_item, items)
        val size = calculateItemsSize()
        if (maxHeight != WRAP_CONTENT_DIMENSION && size.y > maxHeight) {
            size.y = maxHeight
        }
        return size
    }

    override fun setupView() {
        super.setupView()
        refreshAdapter()
    }

    override fun addContent(child: TransformNode) {
        super.addContent(child)
        if (child is UiDropdownListItemNode) {
            items.add(child)
            // refreshAdapter()
            setNeedsRebuild(true)
        }
    }

    override fun removeContent(child: TransformNode) {
        super.removeContent(child)
        if (child is UiDropdownListItemNode) {
            items.add(child)
            // refreshAdapter()
            setNeedsRebuild(true)
        }
    }

    private fun refreshAdapter() {
        val adapter = DropdownItemsAdapter(context, R.layout.dropdown_item, items)
        adapter.textSizePx = Utils.metersToFontPx(itemsTextSize, context)
        (view as ListView).adapter = adapter
    }

    // Calculates list view size based on list items size
    private fun calculateItemsSize(): Vector2 {
        var width = 0f
        var height = 0f
        val listView = view as ListView
        for (i in 0 until itemsAdapter.count) {
            val childView = itemsAdapter.getView(i, null, listView)
            val size =
                childView.getSizeInMeters(context, WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
            if (size.x > width) {
                width = size.x
            }
            height += size.y
        }
        height += Utils.pxToMeters(listView.dividerHeight * itemsAdapter.count, context)
        return Vector2(width, height)
    }

}