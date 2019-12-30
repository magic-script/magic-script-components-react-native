package com.magicleap.magicscript.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.base.LayoutParams
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read

class UiRectLayout(
    initProps: ReadableMap,
    layoutManager: VerticalLinearLayoutManager<LayoutParams>
) : UiBaseLayout<LayoutParams>(initProps, layoutManager) {

    companion object {
        // properties
        const val PROP_PADDING = "padding"
        const val PROP_CONTENT_ALIGNMENT = "contentAlignment"

        // default values
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_CONTENT_ALIGNMENT = "top-left"
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    private var padding: Padding = Padding(0f, 0f, 0f, 0f)
    private var contentVerticalAlignment = Alignment.VerticalAlignment.TOP
    private var contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT

    init {
        // set default values of properties
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_CONTENT_ALIGNMENT, DEFAULT_CONTENT_ALIGNMENT)
        properties.putDefault(PROP_PADDING, DEFAULT_ITEM_PADDING)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setItemPadding(props)
        setContentAlignment(props)
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

    override fun getLayoutParams(): LayoutParams {
        return LayoutParams(
            size = Vector2(width, height),
            itemPadding = padding,
            itemHorizontalAlignment = contentHorizontalAlignment,
            itemVerticalAlignment = contentVerticalAlignment
        )
    }

    private fun setItemPadding(props: Bundle) {
        val padding = props.read<Padding>(PROP_PADDING)
        if (padding != null) {
            this.padding = padding
            requestLayout()
        }
    }

    private fun setContentAlignment(props: Bundle) {
        val alignment = props.read<Alignment>(PROP_CONTENT_ALIGNMENT)
        if (alignment != null) {
            this.contentVerticalAlignment = alignment.vertical
            this.contentHorizontalAlignment = alignment.horizontal
            requestLayout()
        }
    }

}