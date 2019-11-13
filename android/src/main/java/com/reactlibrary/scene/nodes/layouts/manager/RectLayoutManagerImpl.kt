package com.reactlibrary.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.base.UiNode.Companion.WRAP_CONTENT_DIMENSION
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import com.reactlibrary.utils.Vector2

class RectLayoutManagerImpl : RectLayoutManager {

    override var parentWidth: Float = WRAP_CONTENT_DIMENSION

    override var parentHeight: Float = WRAP_CONTENT_DIMENSION

    override var itemPadding = Padding(0F, 0F, 0F, 0F)

    override var contentHorizontalAlignment = Alignment.HorizontalAlignment.CENTER

    override var contentVerticalAlignment = Alignment.VerticalAlignment.CENTER

    override fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>) {
        if (children.size > 1 || childrenBounds.size > 1) {
            throw Exception("RectLayout can only have one child!")
        }
        if (children.isNotEmpty() && childrenBounds.isNotEmpty()) {
            childrenBounds[0]?.let { childBounds ->
                val childSize = childBounds.size()
                val sizeLimitX = if (parentWidth != WRAP_CONTENT_DIMENSION) parentWidth else childSize.x
                val sizeLimitY = if (parentHeight != WRAP_CONTENT_DIMENSION) parentHeight else childSize.y
                val sizeLimit = Vector2(sizeLimitX, sizeLimitY)
                layoutNode(children[0], childBounds, sizeLimit)
            }
        }
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
}