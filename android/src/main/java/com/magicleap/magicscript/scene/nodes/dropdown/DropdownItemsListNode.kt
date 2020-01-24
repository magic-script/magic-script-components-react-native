package com.magicleap.magicscript.scene.nodes.dropdown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.RenderPriority
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

    var onSelectionChangedListener: ((selectedItems: List<UiDropdownListItemNode>) -> Unit)? = null

    var maxHeight = WRAP_CONTENT_DIMENSION
        set(value) {
            if (field != value) {
                field = value
                setNeedsRebuild(force = true)
            }
        }

    var itemsTextSize: Float = DEFAULT_ITEMS_TEXT_SIZE
        set(value) {
            if (field != value) {
                field = value
                setNeedsRebuild(force = true)
            }
        }

    var multiSelect = false

    private val items = mutableListOf<UiDropdownListItemNode>()
    private lateinit var itemsAdapter: DropdownItemsAdapter

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.dropdown_list, null)
    }

    override fun provideDesiredSize(): Vector2 {
        itemsAdapter = buildAdapter()
        val size = calculateItemsSize()
        if (maxHeight != WRAP_CONTENT_DIMENSION && size.y > maxHeight) {
            size.y = maxHeight
        }
        return size
    }

    override fun setupView() {
        super.setupView()
        val listView = view as ListView
        listView.adapter = itemsAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]
            handleSelectionChangeRequest(item, !item.selected)
        }
    }

    override fun onViewLoaded(viewRenderable: Renderable) {
        super.onViewLoaded(viewRenderable)
        // we draw at top of other views
        viewRenderable.renderPriority = RenderPriority.ABOVE_DEFAULT
    }

    override fun addContent(child: TransformNode) {
        super.addContent(child)
        if (child is UiDropdownListItemNode) {
            items.add(child)
            if (child.selectRequested) {
                handleSelectionChangeRequest(child, true)
            }
            child.onSelectionChangeRequest = { select ->
                handleSelectionChangeRequest(child, select)
            }
            setNeedsRebuild(force = true)
        }
    }

    override fun removeContent(child: TransformNode) {
        super.removeContent(child)
        if (child is UiDropdownListItemNode) {
            items.add(child)
            setNeedsRebuild(force = true)
        }
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
        return Vector2(width, height)
    }

    private fun buildAdapter(): DropdownItemsAdapter {
        val adapter = DropdownItemsAdapter(context, R.layout.dropdown_item, items)
        adapter.textSizePx = Utils.metersToFontPx(itemsTextSize, context)
        return adapter
    }

    private fun handleSelectionChangeRequest(item: UiDropdownListItemNode, select: Boolean) {
        var selectionChanged = false
        if (multiSelect) {
            selectionChanged = tryMultiSelectChange(item, select)
        } else {
            selectionChanged = trySingleSelectChange(item, select)
            if (isVisible) {
                hide()
            }
        }
        if (selectionChanged) {
            itemsAdapter.notifyDataSetChanged()
            (view as ListView).invalidateViews()
            val selectedItems = items.filter { it.selected }
            onSelectionChangedListener?.invoke(selectedItems)
        }

    }

    private fun trySingleSelectChange(item: UiDropdownListItemNode, select: Boolean): Boolean {
        if (select) {  // cannot deselect an item when multiSelect mode inactive
            val lastSelectedItem = items.find { it.selected }
            if (item != lastSelectedItem) {
                lastSelectedItem?.selected = false
                item.selected = true
                return true
            }
        }
        return false
    }

    private fun tryMultiSelectChange(item: UiDropdownListItemNode, select: Boolean): Boolean {
        if (item.selected == select) { // not changed
            return false
        }
        item.selected = select
        return true
    }

}