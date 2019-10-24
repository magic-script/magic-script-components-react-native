package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import android.util.Log
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.scene.nodes.layouts.manager.RectLayoutManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.putDefaultSerializable
import com.reactlibrary.utils.putDefaultString

class UiRectLayout(initProps: ReadableMap, layoutManager: RectLayoutManager)
: UiLayout(initProps, layoutManager) {

    private var padding: Padding = Padding(0f,0f,0f,0f)

    companion object {
        // properties
        const val PROP_PADDING = "padding"
        const val PROP_CONTENT_ALIGNMENT = "contentAlignment"

        // default values
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_CONTENT_ALIGNMENT = "center-center"
        // default padding for each item [top, right, bottom, left]
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    init {
        // set default values of properties

        // alignment of the grid itself (pivot)
        properties.putDefaultString(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefaultString(PROP_CONTENT_ALIGNMENT, DEFAULT_CONTENT_ALIGNMENT)
        properties.putDefaultSerializable(PROP_PADDING, DEFAULT_ITEM_PADDING)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setItemPadding(props)
        setContentAlignment(props)
        val paddingHorizontal = padding.left + padding.right
        val paddingVertical = padding.top + padding.bottom
        maxChildHeight = height - paddingVertical
        maxChildWidth = width - paddingHorizontal
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        val itemPadding = PropertiesReader.readPadding(properties, PROP_PADDING) ?: Padding()
        val parentBounding = if (isSizeSet()) {
            Bounding(
                    contentNode.localPosition.x - width / 2,
                    contentNode.localPosition.y - height / 2,
                    contentNode.localPosition.x + width / 2,
                    contentNode.localPosition.y + height / 2
            )
        } else {
            Bounding(
                    childBounds.left + contentNode.localPosition.x - itemPadding.left,
                    childBounds.bottom + contentNode.localPosition.y - itemPadding.bottom,
                    childBounds.right + contentNode.localPosition.x + itemPadding.right,
                    childBounds.top + contentNode.localPosition.y + itemPadding.top
            )
        }
        if(isSizeSet()) {
            if(layoutManager.parentBounds == null || !Bounding.equalInexact(layoutManager.parentBounds!!,parentBounding)) {
                layoutManager.parentBounds = parentBounding
                requestLayout()
            }
        } else {
            if(layoutManager.parentBounds != null) {
                layoutManager.parentBounds = null
                requestLayout()
            }
        }
        return parentBounding
    }

    private fun setItemPadding(props: Bundle) {
        val padding = PropertiesReader.readPadding(props, PROP_PADDING)
        if (padding != null) {
            this.padding = padding
            (layoutManager as RectLayoutManager).itemPadding = padding
            requestLayout()
        }
    }

    private fun setContentAlignment(props: Bundle) {
        val alignment = PropertiesReader.readAlignment(props, PROP_CONTENT_ALIGNMENT)
        if (alignment != null) {
            (layoutManager as RectLayoutManager)
            layoutManager.contentVerticalAlignment = alignment.vertical
            layoutManager.contentHorizontalAlignment = alignment.horizontal
            requestLayout()
        }
    }
}