package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.ar.RenderPriority
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.scene.nodes.base.Layoutable
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
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

    var contentPadding = Padding()
        set(value) {
            if (field != value) {
                field = value
                setNeedsRebuild(true)
            }
        }

    var contentAlignment = Alignment()

    private var lastContentBounds = AABB()

    init {
        properties.putDefault(PROP_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR)
    }

    override fun provideView(context: Context): View {
        return View(context) // basic view as a background
    }

    override fun provideDesiredSize(): Vector2 {
        return calculateSize()
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

    override fun addContent(child: ReactNode) {
        super.addContent(child)
        if (contentNode.children.size > 1) {
            logMessage("Only one node can be added as list item child", true)
            return
        }
        if (child is TransformNode) {
            lastContentBounds = child.getBounding()
            adjustContentPosition(child, lastContentBounds)
        }
    }

    override fun onUpdate(deltaSeconds: Float) {
        super.onUpdate(deltaSeconds)

        val content = contentNode.children.firstOrNull() as? TransformNode
        if (content != null) {
            val contentBounds = content.getBounding()
            if (!contentBounds.equalInexact(lastContentBounds)) {
                setNeedsRebuild(true) // need to create a new background
                lastContentBounds = contentBounds
            }

            adjustContentPosition(content, contentBounds)
        }
    }

    private fun adjustContentPosition(content: TransformNode, contentBounds: AABB) {
        val centerOffsetX = content.localPosition.x - contentBounds.center().x
        val centerOffsetY = content.localPosition.y - contentBounds.center().y

        val parentSize = calculateSize()
        val spaceX = parentSize.x - contentBounds.size().x
        val spaceY = parentSize.y - contentBounds.size().y

        val alignmentOffsetX = spaceX * contentAlignment.horizontal.centerOffset
        val alignmentOffsetY = spaceY * contentAlignment.vertical.centerOffset

        val paddingOffsetX = when (contentAlignment.horizontal) {
            Alignment.Horizontal.LEFT -> contentPadding.left
            Alignment.Horizontal.CENTER -> (contentPadding.left - contentPadding.right) / 2
            Alignment.Horizontal.RIGHT -> -contentPadding.right
        }

        val paddingOffsetY = when (contentAlignment.vertical) {
            Alignment.Vertical.TOP -> -contentPadding.top
            Alignment.Vertical.CENTER -> (contentPadding.bottom - contentPadding.top) / 2
            Alignment.Vertical.BOTTOM -> contentPadding.bottom
        }

        val posX = centerOffsetX + alignmentOffsetX + paddingOffsetX
        val posY = centerOffsetY + alignmentOffsetY + paddingOffsetY
        content.localPosition = Vector3(posX, posY, CONTENT_Z_OFFSET)
    }

    private fun setBackgroundColor(props: Bundle) {
        val androidColor = props.readColor(PROP_BACKGROUND_COLOR)
        if (androidColor != null) {
            view.setBackgroundColor(androidColor)
        }
    }

    private fun calculateSize(): Vector2 {
        val originalWidth = lastContentBounds.size().x + contentPadding.left + contentPadding.right
        val originalHeight = lastContentBounds.size().y + contentPadding.top + contentPadding.bottom
        val width = max(originalWidth, minSize.x)
        val height = max(originalHeight, minSize.y)
        return Vector2(width, height)
    }
}