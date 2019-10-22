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

    private var padding: Padding? = Padding(0f,0f,0f,0f)

    companion object {
        // properties
        const val PROP_PADDING = "padding"
        const val PROP_DEFAULT_ITEM_ALIGNMENT = "contentAlignment"

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
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setItemPadding(props)
        setItemAlignment(props)
        val paddingHorizontal = padding?.left?.plus((padding?.right ?: 0f)) ?: 0f
        val paddingVertical = padding?.top?.plus((padding?.bottom ?: 0f)) ?: 0f
        maxChildHeight = height - paddingVertical
        maxChildWidth = width - paddingHorizontal
    }

    override fun adjustChildrenSize() {
        if(isSizeSet()) {
//            for (i in 0 until contentNode.children.size) {
//                val node = contentNode.children[i]
//                val bounding = childrenBounds[i]!!
//                val newBounding = rescaleChild(node, bounding)
//                if(newBounding != null) {
//                    Log.d("RescaleChild", "newBounding: $newBounding")
//                    childrenBounds[i] = newBounding
//                }
//            }
        }
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        val itemPadding = PropertiesReader.readPadding(properties, PROP_PADDING)
                ?: Padding()
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
            layoutManager.parentBounds = parentBounding
        }
        return parentBounding
    }

    private fun setItemPadding(props: Bundle) {
        padding = PropertiesReader.readPadding(props, PROP_PADDING)
        if (padding != null) {
            (layoutManager as RectLayoutManager).itemPadding = padding!!
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