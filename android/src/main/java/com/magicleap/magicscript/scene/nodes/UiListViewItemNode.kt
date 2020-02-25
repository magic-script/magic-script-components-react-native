package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.ar.RenderPriority
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.scene.nodes.base.Layoutable
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.logMessage
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.readColor
import kotlin.math.max

open class UiListViewItemNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper
) : UiNode(initProps, context, viewRenderableLoader, nodeClipper), Layoutable {

    companion object {
        const val PROP_BACKGROUND_COLOR = "backgroundColor"
        const val CONTENT_Z_OFFSET = 1e-5f

        val DEFAULT_BACKGROUND_COLOR = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    var minSize = Vector2(0f, 0f)
        set(value) {
            if (field != value) {
                field = value
                setNeedsRebuild(true)
            }
        }

    var padding = Padding()
        set(value) {
            if (field != value) {
                field = value
                setNeedsRebuild(true)
            }
        }

    private var lastContentBounds = AABB()

    init {
        properties.putDefault(PROP_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR)
    }

    override fun provideView(context: Context): View {
        return View(context) // basic view as a background
    }

    override fun provideDesiredSize(): Vector2 {
        val originalWidth = lastContentBounds.size().x + padding.left + padding.right
        val originalHeight = lastContentBounds.size().y + padding.top + padding.bottom
        val width = max(originalWidth, minSize.x)
        val height = max(originalHeight, minSize.y)
        return Vector2(width, height)
    }

    override fun onViewLoaded(viewRenderable: Renderable) {
        super.onViewLoaded(viewRenderable)
        // we draw background firstly
        viewRenderable.renderPriority = RenderPriority.UNDER_DEFAULT
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setBackgroundColor(props)
    }

    override fun addContent(child: TransformNode) {
        super.addContent(child)
        if (contentNode.children.size > 1) {
            logMessage("Only one node can be added as list item child", true)
        }
    }

    override fun onUpdate(deltaSeconds: Float) {
        super.onUpdate(deltaSeconds)

        // align the content node
        val content = contentNode.children.firstOrNull() as? TransformNode
        if (content != null) {
            val contentBounds = content.getBounding()

            if (!contentBounds.equalInexact(lastContentBounds)) {
                val paddingXDiff = padding.left - padding.right
                val paddingYDiff = padding.bottom - padding.top
                val offsetX = content.localPosition.x - contentBounds.center().x + paddingXDiff / 2
                val offsetY = content.localPosition.y - contentBounds.center().y + paddingYDiff / 2
                content.localPosition = Vector3(offsetX, offsetY, CONTENT_Z_OFFSET)
                lastContentBounds = contentBounds
                setNeedsRebuild(true) // need to create a new background
            }
        }
    }

    private fun setBackgroundColor(props: Bundle) {
        val androidColor = props.readColor(PROP_BACKGROUND_COLOR)
        if (androidColor != null) {
            view.setBackgroundColor(androidColor)
        }
    }
}