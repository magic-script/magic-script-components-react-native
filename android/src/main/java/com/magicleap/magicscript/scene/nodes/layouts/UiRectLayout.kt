package com.magicleap.magicscript.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.LayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.*

class UiRectLayout(
    initProps: ReadableMap,
    layoutManager: LayoutManager<LayoutParams>
) : UiBaseLayout<LayoutParams>(initProps, layoutManager) {

    companion object {
        // properties
        const val PROP_ITEM_PADDING = "padding"
        const val PROP_CONTENT_ALIGNMENT = "contentAlignment"

        // default values
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_CONTENT_ALIGNMENT = "top-left"
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    init {
        // set default values of properties
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_CONTENT_ALIGNMENT, DEFAULT_CONTENT_ALIGNMENT)
        properties.putDefault(PROP_ITEM_PADDING, DEFAULT_ITEM_PADDING)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsAny(PROP_ITEM_PADDING, PROP_CONTENT_ALIGNMENT)) {
            requestLayout()
        }
    }

    override fun getContentBounding(): Bounding {
        val layoutBounds = layoutManager.getLayoutBounds(getLayoutParams())
        return Bounding(
            layoutBounds.left + contentNode.localPosition.x,
            layoutBounds.bottom + contentNode.localPosition.y,
            layoutBounds.right + contentNode.localPosition.x,
            layoutBounds.top + contentNode.localPosition.y
        )
    }

    override fun addContent(child: TransformNode) {
        super.addContent(child)

        if (childrenList.size > 1) {
            logMessage("RectLayout can only have one child!", true)
        }
    }

    override fun getLayoutParams(): LayoutParams {
        val padding = properties.read(PROP_ITEM_PADDING) ?: Padding()
        val contentAlignment = properties.read<Alignment>(PROP_CONTENT_ALIGNMENT)!!
        val contentHorizontalAlignment = contentAlignment.horizontal
        val contentVerticalAlignment = contentAlignment.vertical

        return LayoutParams(
            size = Vector2(width, height),
            itemPadding = padding,
            itemHorizontalAlignment = contentHorizontalAlignment,
            itemVerticalAlignment = contentVerticalAlignment
        )
    }

}