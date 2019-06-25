package com.reactlibrary.scene

import android.content.Context
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.UiButtonNode
import com.reactlibrary.scene.nodes.UiGroupNode
import com.reactlibrary.scene.nodes.UiImageNode
import com.reactlibrary.scene.nodes.UiTextNode

/**
 * Utility class with methods that create nodes.
 */
class NodesFactory(private val context: Context) {

    fun createViewGroup(props: ReadableMap): UiNode {
        return UiGroupNode(props, context)
    }

    fun createButton(props: ReadableMap): UiNode {
        return UiButtonNode(props, context)
    }

    fun createText(props: ReadableMap): UiNode {
        return UiTextNode(props, context)
    }

    fun createImageView(props: ReadableMap): UiNode {
        return UiImageNode(props, context)
    }

}