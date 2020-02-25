/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magicleap.magicscript.utils

import android.content.Context
import android.net.Uri
import android.util.TypedValue
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.collision.CollisionShape
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ModelType
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Bounding
import kotlin.math.max
import kotlin.math.min

/**
 * Class containing general purpose utility functions
 */
class Utils {

    companion object {

        // Each text should be scaled down with this factor
        // to match Lumin's implementation
        const val FONT_SCALE_FACTOR = 0.8f

        // By default, every 250dp for the view becomes 1 meter for the renderable
        // https://developers.google.com/ar/develop/java/sceneform/create-renderables
        const val DP_TO_METER_RATIO = 250

        // One dp is a virtual pixel unit that's roughly equal to one pixel on a medium-density screen
        // 160dpi is the "baseline" density
        const val BASELINE_DENSITY = 160F

        /**
         *  Converts ARCore's meters to pixels
         *  (Uses an average of horizontal and vertical density -
         *  usually they are almost the same)
         */
        fun metersToPx(meters: Float, context: Context): Int {
            val xdpi = context.resources.displayMetrics.xdpi
            val ydpi = context.resources.displayMetrics.ydpi
            val averageDensity = (xdpi + ydpi) / 2
            val densityAvgFactor = averageDensity / BASELINE_DENSITY
            return (meters * DP_TO_METER_RATIO * densityAvgFactor).toInt()
        }

        /**
         *  Converts native pixels to ARCore's meters
         *  (Uses an average of horizontal and vertical density -
         *  usually they are almost the same)
         */
        fun pxToMeters(px: Int, context: Context): Float {
            val xdpi = context.resources.displayMetrics.xdpi
            val ydpi = context.resources.displayMetrics.ydpi
            val averageDensity = (xdpi + ydpi) / 2
            val densityAvgFactor = averageDensity / BASELINE_DENSITY
            return px / (DP_TO_METER_RATIO * densityAvgFactor)
        }

        /**
         * Converts ARCore's meters to "font" pixels (font size is scaled
         * to match Lumin's implementation)
         */
        fun metersToFontPx(meters: Float, context: Context): Int {
            return (FONT_SCALE_FACTOR * metersToPx(meters, context)).toInt()
        }

        /**
         * Calculates local bounds of a basic node [Node] using its collision shape.
         * @param collShape collision shape of a node (or its renderable) or null if
         * node has no renderable
         */
        fun calculateBoundsOfNode(node: Node, collShape: CollisionShape?): AABB {
            // TODO (optionally) add Sphere collision shape support (currently never used)
            val offsetX = node.localPosition.x
            val offsetY = node.localPosition.y
            val offsetZ = node.localPosition.z

            return if (collShape is Box) { // may be also null
                val scaleX = node.localScale.x
                val scaleY = node.localScale.y
                val scaleZ = node.localScale.z
                val xMin = collShape.center.x * scaleX - (collShape.size.x * scaleX) / 2 + offsetX
                val xMax = collShape.center.x * scaleX + (collShape.size.x * scaleX) / 2 + offsetX
                val yMin = collShape.center.y * scaleY - (collShape.size.y * scaleY) / 2 + offsetY
                val yMax = collShape.center.y * scaleY + (collShape.size.y * scaleY) / 2 + offsetY
                val zMin = collShape.center.z * scaleZ - (collShape.size.z * scaleZ) / 2 + offsetZ
                val zMax = collShape.center.z * scaleZ + (collShape.size.z * scaleZ) / 2 + offsetZ

                AABB(min = Vector3(xMin, yMin, zMin), max = Vector3(xMax, yMax, zMax))
            } else {
                // default
                AABB(
                    min = Vector3(offsetX, offsetY, offsetZ),
                    max = Vector3(offsetX, offsetY, offsetZ)
                )
            }
        }

        /**
         * Calculates local bounds of group of nodes
         * (minimum possible frame that contains all [nodes])
         */
        fun calculateSumBounds(nodes: List<Node>): AABB {
            if (nodes.isEmpty()) {
                return AABB()
            }

            val minEdge = Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE)
            val maxEdge = Vector3(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)

            for (node in nodes) {
                val childBounds = if (node is TransformNode) {
                    node.getBounding()
                } else {
                    calculateBoundsOfNode(node, node.collisionShape)
                }

                minEdge.x = min(childBounds.min.x, minEdge.x)
                maxEdge.x = max(childBounds.max.x, maxEdge.x)
                minEdge.y = min(childBounds.min.y, minEdge.y)
                maxEdge.y = max(childBounds.max.y, maxEdge.y)
                minEdge.z = min(childBounds.min.z, minEdge.z)
                maxEdge.z = max(childBounds.max.z, maxEdge.z)
            }

            return AABB(minEdge, maxEdge)
        }

        fun findMinimumBounding(points: List<Vector3>): AABB {
            if (points.isEmpty()) {
                return AABB()
            }

            val minEdge = Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE)
            val maxEdge = Vector3(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)

            for (point in points) {
                minEdge.x = min(point.x, minEdge.x)
                maxEdge.x = max(point.x, maxEdge.x)
                minEdge.y = min(point.y, minEdge.y)
                maxEdge.y = max(point.y, maxEdge.y)
                minEdge.z = min(point.z, minEdge.z)
                maxEdge.z = max(point.z, maxEdge.z)
            }
            return AABB(minEdge, maxEdge)
        }

        /**
         * Rotates vector by a quaternion
         */
        fun rotateVector(v: Vector3, quat: Quaternion): Vector3 {
            val u = Vector3(quat.x, quat.y, quat.z)
            val s = quat.w

            val a = 2.0f * Vector3.dot(u, v)
            val p1 = Vector3(u.x * a, u.y * a, u.z * a)

            val b = s * s - Vector3.dot(u, u)
            val p2 = Vector3(v.x * b, v.y * b, v.z * b)

            val c = 2.0f * s
            val cross = Vector3.cross(u, v)
            val p3 = Vector3(cross.x * c, cross.y * c, cross.z * c)

            val sum1 = Vector3.add(p1, p2)
            return Vector3.add(sum1, p3)
        }

        fun calculateMaterialClipping(nodeBounds: AABB, clipBounds: AABB): Bounding {
            val intersecting = !nodeBounds.intersection(clipBounds).equalInexact(AABB())
            if (!intersecting) {
                return Bounding()
            }

            // calculate 2d clipping
            val nodeBounds2d = nodeBounds.toBounding2d()
            val clipBounds2d = clipBounds.toBounding2d()
            val sizeX = nodeBounds2d.size().x
            val sizeY = nodeBounds2d.size().y
            if (sizeX <= 0f || sizeY <= 0f) {
                return Bounding()
            }

            val materialClip = Bounding()

            val offsetLeft = nodeBounds2d.left - clipBounds2d.left
            materialClip.left = max(-0.5f - offsetLeft / sizeX, -0.5f)

            val offsetRight = nodeBounds2d.right - clipBounds2d.right
            materialClip.right = min(0.5f - offsetRight / sizeX, 0.5f)

            val offsetBottom = nodeBounds2d.bottom - clipBounds2d.bottom
            materialClip.bottom = max(-offsetBottom / sizeY, 0.0f)

            val offsetTop = nodeBounds2d.top - clipBounds2d.top
            materialClip.top = min(1.0f - offsetTop / sizeY, 1.0f)

            return materialClip
        }

        fun detectModelType(modelUri: Uri, context: Context): ModelType {
            if (modelUri.toString().contains("android.resource://")) { // release build
                val resourceName = modelUri.lastPathSegment
                val resourceId =
                    context.resources.getIdentifier(resourceName, "raw", context.packageName)
                if (resourceId == 0) { // does not exists
                    return ModelType.UNKNOWN
                }

                val value = TypedValue()
                context.resources.getValue(resourceId, value, true)
                val resWithExtension = value.string
                if (resWithExtension.endsWith(".glb")) {
                    return ModelType.GLB
                }
                if (resWithExtension.endsWith(".sfb")) {
                    return ModelType.SFB
                }
                return ModelType.UNKNOWN
            } else { // localhost path
                if (modelUri.toString().contains(".glb")) {
                    return ModelType.GLB
                }
                if (modelUri.toString().contains(".sfb")) {
                    return ModelType.SFB
                }
                return ModelType.UNKNOWN
            }
        }

        /**
         * Sets correct content node position based on [node] alignment
         */
        fun applyContentNodeAlignment(node: TransformNode) {
            val bounds = node.getBounding().toBounding2d()

            val rot = node.localRotation.inverted()
            val p1 = Vector3(bounds.left, bounds.top, node.localPosition.z).rotatedBy(rot)
            val p2 = Vector3(bounds.left, bounds.bottom, node.localPosition.z).rotatedBy(rot)
            val p3 = Vector3(bounds.right, bounds.bottom, node.localPosition.z).rotatedBy(rot)
            val p4 = Vector3(bounds.right, bounds.top, node.localPosition.z).rotatedBy(rot)
            val boundsBeforeRotation = findMinimumBounding(listOf(p1, p2, p3, p4))

            val contentNode = node.contentNode

            val nodeWidth = boundsBeforeRotation.max.x - boundsBeforeRotation.min.x
            val nodeHeight = boundsBeforeRotation.max.y - boundsBeforeRotation.min.y
            val boundsCenterX = boundsBeforeRotation.min.x + nodeWidth / 2
            val pivotOffsetX = node.localPosition.x - boundsCenterX // aligning according to center
            val boundsCenterY = boundsBeforeRotation.max.y - nodeHeight / 2
            val pivotOffsetY = node.localPosition.y - boundsCenterY  // aligning according to center

            // calculating x and y position for content
            val x =
                contentNode.localPosition.x - node.horizontalAlignment.centerOffset * nodeWidth + pivotOffsetX

            val y =
                contentNode.localPosition.y - node.verticalAlignment.centerOffset * nodeHeight + pivotOffsetY

            contentNode.localPosition = Vector3(x, y, contentNode.localPosition.z)
        }

        fun clipChildren(parent: TransformNode, clipBounds: AABB) {
            val scale = parent.localScale
            if (scale.x <= 0 || scale.y <= 0 || scale.z <= 0) {
                return
            }

            val scaleX = 1 / scale.x
            val scaleY = 1 / scale.y
            val scaleZ = 1 / scale.z

            val localBounds = clipBounds
                .translated(-parent.getContentPosition())
                .scaled(scaleX, scaleY, scaleZ)

            parent.contentNode.children
                .filterIsInstance<TransformNode>()
                .forEach { it.clipBounds = localBounds }
        }

    }

}
