package com.reactlibrary.scene.nodes.layouts

import android.os.Bundle
import android.util.Log
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.MaterialFactory
import com.reactlibrary.scene.nodes.base.UiLayout
import com.reactlibrary.scene.nodes.layouts.manager.GridLayoutManager
import com.reactlibrary.scene.nodes.layouts.manager.RectLayoutManager
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import com.reactlibrary.utils.*
import java.util.concurrent.CompletableFuture

class UiRectLayout(initProps: ReadableMap, layoutManager: RectLayoutManager)
: UiLayout(initProps, layoutManager) {

    var width: Float = 0f
    var height: Float = 0f

    companion object {
        // properties
        const val PROP_PADDING = "padding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "contentAlignment"
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"

        // default values
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_ITEM_ALIGNMENT = "center-center"
        // default padding for each item [top, right, bottom, left]
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    init {
        // set default values of properties

        // alignment of the grid itself (pivot)
        properties.putDefaultString(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefaultString(PROP_DEFAULT_ITEM_ALIGNMENT, DEFAULT_ITEM_ALIGNMENT)
        properties.putDefaultSerializable(PROP_PADDING, DEFAULT_ITEM_PADDING)
//        properties.putFloat(PROP_WIDTH, 0f)
//        properties.putFloat(PROP_HEIGHT, 0f)
    }

    override fun applyProperties(props: Bundle) {
        Log.d("RectLayout", "props: $props")
        super.applyProperties(props)
        setItemPadding(props)
        setItemAlignment(props)
        setLayoutSize(props)
    }

    private fun setLayoutSize(props: Bundle) {
        width = props.getDouble(PROP_WIDTH).toFloat()
        height = props.getDouble(PROP_HEIGHT).toFloat()
        Log.d("RectLayout", "width: $width, height: $height")
        if(isSizeSet()) {
            requestLayout()
        }
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        val itemPadding = PropertiesReader.readPadding(properties, PROP_PADDING)
                ?: Padding()
        Log.d("RectLayout", "child bounds: $childBounds")
        Log.d("RectLayout", "rect layout item padding: $itemPadding")
        Log.d("RectLayout", "content node point, x: ${contentNode.localPosition.x}, y: ${contentNode.localPosition.y}")
        val parentBounding = if (isSizeSet()) {
            val boundsCenterX = contentNode.localPosition.x + width / 2
            val boundsCenterY = contentNode.localPosition.y - height / 2
            val pivotOffsetX = contentNode.localPosition.x - boundsCenterX // aligning according to center
            val pivotOffsetY = contentNode.localPosition.y - boundsCenterY
            Bounding(
                    contentNode.localPosition.x - itemPadding.left - width / 2,
                    contentNode.localPosition.y - itemPadding.bottom - height / 2,
                    contentNode.localPosition.x + itemPadding.right + width / 2,
                    contentNode.localPosition.y + itemPadding.top + height / 2
            )
        } else {
            Bounding(
                    childBounds.left + contentNode.localPosition.x - itemPadding.left,
                    childBounds.bottom + contentNode.localPosition.y - itemPadding.bottom,
                    childBounds.right + contentNode.localPosition.x + itemPadding.right,
                    childBounds.top + contentNode.localPosition.y + itemPadding.top
            )
        }
        Log.d("RectLayout", "parent content bounds , " +
                "left: ${parentBounding.left}, " +
                "bottom: ${parentBounding.bottom}" +
                "right: ${parentBounding.right}" +
                "top: ${parentBounding.top}"
        )
        if(isSizeSet()) {
            Log.d("RectLayout", "set parent bounds in rect layout manager")
            (layoutManager as RectLayoutManager).parentBounds = parentBounding
        }
        return parentBounding
    }

    private fun isSizeSet(): Boolean {
        return (width > 0 && height > 0)
    }

    private fun setItemPadding(props: Bundle) {
        val padding = PropertiesReader.readPadding(props, PROP_PADDING)
        if (padding != null) {
            (layoutManager as RectLayoutManager).itemPadding = padding
            requestLayout()
        }
    }

    private fun setItemAlignment(props: Bundle) {
        val alignment = PropertiesReader.readAlignment(props, PROP_DEFAULT_ITEM_ALIGNMENT)
        if (alignment != null) {
            (layoutManager as RectLayoutManager)
            layoutManager.itemVerticalAlignment = alignment.vertical
            layoutManager.itemHorizontalAlignment = alignment.horizontal
        }
    }
}