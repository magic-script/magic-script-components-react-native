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
import com.google.ar.sceneform.rendering.Material
import com.magicleap.magicscript.ar.ModelType
import com.magicleap.magicscript.scene.nodes.base.TransformNode
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

        private const val CLIP_LEFT_PARAM = "left"
        private const val CLIP_RIGHT_PARAM = "right"
        private const val CLIP_TOP_PARAM = "top"
        private const val CLIP_BOTTOM_PARAM = "bottom"

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
        fun calculateBoundsOfNode(node: Node, collShape: CollisionShape?): Bounding {
            // TODO (optionally) add Sphere collision shape support (currently never used)
            val offsetX = node.localPosition.x
            val offsetY = node.localPosition.y
            return if (collShape is Box) { // may be also null
                val scaleX = node.localScale.x
                val scaleY = node.localScale.y
                val left = collShape.center.x * scaleX - (collShape.size.x * scaleX) / 2 + offsetX
                val right = collShape.center.x * scaleX + (collShape.size.x * scaleX) / 2 + offsetX
                val top = collShape.center.y * scaleY + (collShape.size.y * scaleY) / 2 + offsetY
                val bottom = collShape.center.y * scaleY - (collShape.size.y * scaleY) / 2 + offsetY
                Bounding(left, bottom, right, top)
            } else {
                // default
                Bounding(offsetX, offsetY, offsetX, offsetY)
            }
        }

        /**
         * Calculates local bounds of group of nodes
         * (minimum possible frame that contains all [nodes])
         */
        fun calculateSumBounds(nodes: List<Node>): Bounding {
            if (nodes.isEmpty()) {
                return Bounding()
            }

            val sumBounds =
                Bounding(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)

            for (node in nodes) {
                val childBounds = if (node is TransformNode) {
                    node.getBounding()
                } else {
                    calculateBoundsOfNode(node, node.collisionShape)
                }

                sumBounds.left = min(childBounds.left, sumBounds.left)
                sumBounds.right = max(childBounds.right, sumBounds.right)
                sumBounds.top = max(childBounds.top, sumBounds.top)
                sumBounds.bottom = min(childBounds.bottom, sumBounds.bottom)
            }

            return sumBounds
        }

        fun findMinimumBounding(points: List<Vector3>): Bounding {
            if (points.isEmpty()) {
                return Bounding()
            }

            val bounds =
                Bounding(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)

            for (point in points) {
                bounds.left = min(point.x, bounds.left)
                bounds.right = max(point.x, bounds.right)
                bounds.top = max(point.y, bounds.top)
                bounds.bottom = min(point.y, bounds.bottom)
            }
            return bounds
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

        fun calculateMaterialClipping(clipBounds: Bounding, nodeBounds: Bounding): Bounding {
            val materialClip = Bounding(-0.5f, 0.0f, 0.5f, 1.0f)
            val sizeX = nodeBounds.size().x
            val sizeY = nodeBounds.size().y

            if (sizeX > 0) {
                val offsetLeft = nodeBounds.left - clipBounds.left
                materialClip.left = max(-0.5f - offsetLeft / sizeX, -0.5f)

                val offsetRight = nodeBounds.right - clipBounds.right
                materialClip.right = min(0.5f - offsetRight / sizeX, 0.5f)
            }

            if (sizeY > 0) {
                val offsetBottom = nodeBounds.bottom - clipBounds.bottom
                materialClip.bottom = max(-offsetBottom / sizeY, 0.0f)

                val offsetTop = nodeBounds.top - clipBounds.top
                materialClip.top = min(1.0f - offsetTop / sizeY, 1.0f)
            }
            return materialClip
        }

        /**
         * Clips the material with the [materialClip] bounds (relative to model size,
         * with the origin at bottom-center)
         */
        fun applyMaterialClipping(material: Material, materialClip: Bounding) {
            material.apply {
                setFloat(CLIP_LEFT_PARAM, materialClip.left)
                setFloat(CLIP_RIGHT_PARAM, materialClip.right)
                setFloat(CLIP_TOP_PARAM, materialClip.top)
                setFloat(CLIP_BOTTOM_PARAM, materialClip.bottom)
            }
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
    }

}
