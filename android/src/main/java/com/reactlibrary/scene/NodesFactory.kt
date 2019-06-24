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

    companion object {
        // By default, every 250dp for the view becomes 1 meter for the renderable
        // https://developers.google.com/ar/develop/java/sceneform/create-renderables
        const val DP_TO_METER_RATIO = 250
    }

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