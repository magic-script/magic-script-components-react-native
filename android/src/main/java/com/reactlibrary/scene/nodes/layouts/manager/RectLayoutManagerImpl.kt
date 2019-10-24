package com.reactlibrary.scene.nodes.layouts.manager

import android.util.Log
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding

class RectLayoutManagerImpl: RectLayoutManager {

    override var parentBounds: Bounding? = null

    override var itemPadding = Padding(0F, 0F, 0F, 0F)

    override var contentHorizontalAlignment = Alignment.HorizontalAlignment.CENTER

    override var contentVerticalAlignment = Alignment.VerticalAlignment.CENTER

    override fun layoutChildren(children: List<Node>, childrenBounds: Map<Int, Bounding>) {
        if(children.size > 1 || childrenBounds.size > 1) {
            throw Exception("RectLayout can only have one child!")
        }
        if(children.isNotEmpty() && childrenBounds.isNotEmpty()) {
            if(parentBounds == null) {
                layoutNode(children[0], childrenBounds[0]!!)
            } else {
                layoutNodeWithinParentSize(children[0], childrenBounds[0]!!, parentBounds!!)
            }
        }
    }

    private fun layoutNodeWithinParentSize(node: Node, nodeBounds: Bounding, parentBounds: Bounding) {
        val nodeWidth = nodeBounds.right - nodeBounds.left
        val nodeHeight = nodeBounds.top - nodeBounds.bottom
        val boundsCenterX = nodeBounds.left + nodeWidth / 2
        val boundsCenterY = nodeBounds.top - nodeHeight / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val pivotOffsetY = node.localPosition.y - boundsCenterY  // aligning according to center

        // calculating x position for a child
        val x = when (contentHorizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                ((parentBounds.left - parentBounds.right) / 2) + nodeWidth / 2 + pivotOffsetX + itemPadding.left
            }

            Alignment.HorizontalAlignment.CENTER -> {
                val paddingDiff = itemPadding.right - itemPadding.left
                pivotOffsetX + paddingDiff
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                ((parentBounds.right - parentBounds.left) / 2) - nodeWidth / 2 + pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val y = when (contentVerticalAlignment) {
            Alignment.VerticalAlignment.TOP -> {
                ((parentBounds.top - parentBounds.bottom) / 2) - nodeHeight / 2 + pivotOffsetY - itemPadding.top
            }

            Alignment.VerticalAlignment.CENTER -> {
                val paddingDiff = itemPadding.top - itemPadding.bottom
                pivotOffsetY + paddingDiff
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                ((parentBounds.bottom - parentBounds.top) / 2) + nodeHeight / 2 + pivotOffsetY + itemPadding.bottom
            }
        }

        node.localPosition = Vector3(x, y, node.localPosition.z)
    }

    private fun layoutNode(node: Node, nodeBounds: Bounding) {
        val nodeWidth = nodeBounds.right - nodeBounds.left

        val nodeHeight = nodeBounds.top - nodeBounds.bottom

        val boundsCenterX = nodeBounds.left + nodeWidth / 2
        val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
        val boundsCenterY = nodeBounds.top - nodeHeight / 2
        val pivotOffsetY = node.localPosition.y - boundsCenterY  // aligning according to center
        // calculating x position for a child
        val x = when (contentHorizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                nodeWidth / 2 + pivotOffsetX + itemPadding.left
            }

            Alignment.HorizontalAlignment.CENTER -> {
                val paddingDiff = itemPadding.left - itemPadding.right
                pivotOffsetX + paddingDiff
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                nodeWidth / 2 - pivotOffsetX - itemPadding.right
            }
        }

        // calculating y position for a child
        val y = when (contentVerticalAlignment) {
            Alignment.VerticalAlignment.TOP -> {
                nodeHeight / 2 + pivotOffsetY - itemPadding.top
            }

            Alignment.VerticalAlignment.CENTER -> {
                val paddingDiff = itemPadding.top - itemPadding.bottom
                pivotOffsetY - paddingDiff
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                nodeHeight / 2 - pivotOffsetY + itemPadding.bottom
            }
        }

        node.localPosition = Vector3(x, y, node.localPosition.z)
    }
}