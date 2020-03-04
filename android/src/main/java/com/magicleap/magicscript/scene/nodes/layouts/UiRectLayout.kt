package com.magicleap.magicscript.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
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

    override fun getContentBounding(): AABB {
        val layoutBounds = layoutManager.getLayoutBounds(getLayoutParams())
        val minEdge = layoutBounds.min + contentNode.localPosition
        val maxEdge = layoutBounds.max + contentNode.localPosition

        return AABB(minEdge, maxEdge)
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

        val childrenPadding =
            LayoutUtils.createChildrenPaddingMap(
                childrenList,
                padding
            )
        val childrenAlignment =
            LayoutUtils.createChildrenAlignmentMap(
                childrenList,
                contentAlignment
            )

        return LayoutParams(
            size = Vector2(width, height),
            itemsPadding = childrenPadding,
            itemsAlignment = childrenAlignment
        )
    }

}