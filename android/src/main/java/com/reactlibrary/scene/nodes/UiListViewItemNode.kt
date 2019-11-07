package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.Layoutable
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.putDefaultSerializable

class UiListViewItemNode(initProps: ReadableMap,
                         context: Context,
                         viewRenderableLoader: ViewRenderableLoader)
    : UiNode(initProps, context, viewRenderableLoader), Layoutable {

    companion object {
        const val PROP_BACKGROUND_COLOR = "backgroundColor"
        const val CONTENT_Z_OFFSET = 1e-5f
        const val RENDER_PRIORITY = 3 // 4 is default, 3 means we draw background firstly

        val DEFAULT_BACKGROUND_COLOR = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    private var lastContentBounds = Bounding()

    init {
        properties.putDefaultSerializable(PROP_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR)

        onViewLoadedListener = { renderable ->
            renderable.renderPriority = RENDER_PRIORITY
        }
    }

    override fun provideView(context: Context): View {
        return View(context) // basic view as a background
    }

    override fun provideDesiredSize(): Vector2 {
        return lastContentBounds.size()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setBackgroundColor(props)
    }

    override fun addContent(child: Node) {
        if (child !is TransformNode) {
            return
        }

        // only one child can be added
        if (contentNode.children.isEmpty()) {
            super.addContent(child)
        }
    }

    override fun setClipBounds(clipBounds: Bounding) {
        super.setClipBounds(clipBounds)
        // clip child item
        val localBounds = clipBounds.translate(-getContentPosition())
        contentNode.children
                .filterIsInstance<TransformNode>()
                .forEach { it.setClipBounds(localBounds) }
    }

    override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)
        // align the content node
        val content = contentNode.children.firstOrNull() as? TransformNode
        if (content != null) {
            val contentBounds = content.getBounding()
            if (!Bounding.equalInexact(contentBounds, lastContentBounds)) {
                val offsetX = content.localPosition.x - contentBounds.center().x
                val offsetY = content.localPosition.y - contentBounds.center().y
                content.localPosition = Vector3(offsetX, offsetY, CONTENT_Z_OFFSET)
                lastContentBounds = contentBounds
                setNeedsRebuild(true) // need to create a new background
            }
        }
    }

    private fun setBackgroundColor(props: Bundle) {
        val androidColor = PropertiesReader.readColor(props, PROP_BACKGROUND_COLOR)
        if (androidColor != null) {
            view.setBackgroundColor(androidColor)
        }
    }
}