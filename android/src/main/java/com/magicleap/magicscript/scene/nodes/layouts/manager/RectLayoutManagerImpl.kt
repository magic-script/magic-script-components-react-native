package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.getUserSpecifiedScale
import com.magicleap.magicscript.utils.logMessage
import kotlin.math.min

open class RectLayoutManagerImpl : RectLayoutManager {

    override var parentWidth: Float = WRAP_CONTENT_DIMENSION
        set(value) {
            field = value
            calculateMaxChildWidth()
        }

    override var parentHeight: Float = WRAP_CONTENT_DIMENSION
        set(value) {
            field = value
            calculateMaxChildHeight()
        }

    override var itemPadding = Padding(0F, 0F, 0F, 0F)
        set(value) {
            field = value
            calculateMaxChildWidth()
            calculateMaxChildHeight()
        }

    override var contentHorizontalAlignment = Alignment.HorizontalAlignment.CENTER

    override var contentVerticalAlignment = Alignment.VerticalAlignment.CENTER

    private var maxChildWidth: Float = Float.MAX_VALUE
    private var maxChildHeight: Float = Float.MAX_VALUE
    private var childrenList = listOf<TransformNode>()

    override fun layoutChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
        this.childrenList = children
        if (children.size > 1) {
            logMessage("RectLayout can only have one child!", true)
        }

        rescaleChildren(children, childrenBounds)

        if (children.isNotEmpty() && childrenBounds.isNotEmpty()) {
            childrenBounds[0]?.let { childBounds ->
                val childSize = childBounds.size()
                val sizeLimitX =
                    if (parentWidth != WRAP_CONTENT_DIMENSION) parentWidth else childSize.x
                val sizeLimitY =
                    if (parentHeight != WRAP_CONTENT_DIMENSION) parentHeight else childSize.y
                val sizeLimit = Vector2(sizeLimitX, sizeLimitY)
                layoutNode(children[0], childBounds, sizeLimit)
            }
        }
    }

    override fun getLayoutBounds(): Bounding {
        val childrenBounds = Utils.calculateSumBounds(childrenList)
        var sizeX = parentWidth
        var sizeY = parentHeight

        var leftOffset = -itemPadding.left
        var bottomOffset = -itemPadding.bottom
        var rightOffset = itemPadding.right
        var topOffset = itemPadding.top

        if (parentWidth == WRAP_CONTENT_DIMENSION) {
            sizeX = childrenBounds.size().x
        } else {
            leftOffset = 0f
            rightOffset = 0f
        }

        if (parentHeight == WRAP_CONTENT_DIMENSION) {
            sizeY = childrenBounds.size().y
        } else {
            topOffset = 0f
            bottomOffset = 0f
        }

        return Bounding(
            left = -sizeX / 2 + leftOffset,
            bottom = -sizeY / 2 + bottomOffset,
            right = sizeX / 2 + rightOffset,
            top = sizeY / 2 + topOffset
        )
    }

    private fun layoutNode(node: Node, nodeBounds: Bounding, sizeLimit: Vector2) {
        val nodeWidth = nodeBounds.right - nodeBounds.left
        val nodeHeight = nodeBounds.top - nodeBounds.bottom
        val boundsCenterX = nodeBounds.left + nodeWidth / 2
        val boundsCenterY = nodeBounds.top - nodeHeight / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val pivotOffsetY = node.localPosition.y - boundsCenterY  // aligning according to center

        // calculating x position for a child
        val x = when (contentHorizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                -sizeLimit.x / 2 + nodeWidth / 2 + pivotOffsetX + itemPadding.left
            }

            Alignment.HorizontalAlignment.CENTER -> {
                val paddingDiff = itemPadding.right - itemPadding.left
                pivotOffsetX + paddingDiff
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                sizeLimit.x / 2 - nodeWidth / 2 + pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val y = when (contentVerticalAlignment) {
            Alignment.VerticalAlignment.TOP -> {
                sizeLimit.y / 2 - nodeHeight / 2 + pivotOffsetY - itemPadding.top
            }

            Alignment.VerticalAlignment.CENTER -> {
                val paddingDiff = itemPadding.top - itemPadding.bottom
                pivotOffsetY + paddingDiff
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                -sizeLimit.y / 2 + nodeHeight / 2 + pivotOffsetY + itemPadding.bottom
            }
        }

        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    private fun rescaleChildren(children: List<TransformNode>, childrenBounds: Map<Int, Bounding>) {
        for (i in children.indices) {
            val child = children[i]
            val childSize = (childrenBounds[i] ?: Bounding()).size()
            if (child.localScale.x > 0 && child.localScale.y > 0) {
                val childWidth = childSize.x / child.localScale.x
                val childHeight = childSize.y / child.localScale.y
                if (childWidth > 0 && childHeight > 0) {
                    val userSpecifiedScale = child.getUserSpecifiedScale() ?: Vector3.one()
                    val scaleX = min(maxChildWidth / childWidth, userSpecifiedScale.x)
                    val scaleY = min(maxChildHeight / childHeight, userSpecifiedScale.y)
                    val scaleXY = min(scaleX, scaleY) // scale saving width / height ratio
                    child.localScale = Vector3(scaleXY, scaleXY, child.localScale.z)
                }
            }
        }
    }

    private fun calculateMaxChildWidth() {
        if (parentWidth != WRAP_CONTENT_DIMENSION) {
            val paddingHorizontal = itemPadding.left + itemPadding.right
            maxChildWidth = parentWidth - paddingHorizontal
        } else {
            maxChildWidth = Float.MAX_VALUE
        }
    }

    private fun calculateMaxChildHeight() {
        if (parentHeight != WRAP_CONTENT_DIMENSION) {
            val paddingVertical = itemPadding.top + itemPadding.bottom
            maxChildHeight = parentHeight - paddingVertical
        } else {
            maxChildHeight = Float.MAX_VALUE
        }
    }

}