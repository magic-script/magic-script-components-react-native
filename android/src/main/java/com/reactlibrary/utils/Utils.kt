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

package com.reactlibrary.utils

import android.content.Context
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Bounding
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

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
        fun pxToMeters(px: Float, context: Context): Float {
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
         */
        fun calculateBoundsOfNode(node: Node): Bounding {
            // TODO (optionally) add Sphere collision shape support (currently never used)
            val offsetX = node.localPosition.x
            val offsetY = node.localPosition.y
            val collShape = node.collisionShape
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

            val sumBounds = Bounding(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)

            for (node in nodes) {
                val childBounds = if (node is TransformNode) {
                    node.getBounding()
                } else {
                    calculateBoundsOfNode(node)
                }

                sumBounds.left = min(childBounds.left, sumBounds.left)
                sumBounds.right = max(childBounds.right, sumBounds.right)
                sumBounds.top = max(childBounds.top, sumBounds.top)
                sumBounds.bottom = min(childBounds.bottom, sumBounds.bottom)
            }

            return sumBounds
        }

        fun findMinimumBounding(points: List<Vector2>): Bounding {
            if (points.isEmpty()) {
                return Bounding()
            }

            val bounds = Bounding(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)

            for (point in points) {
                bounds.left = min(point.x, bounds.left)
                bounds.right = max(point.x, bounds.right)
                bounds.top = max(point.y, bounds.top)
                bounds.bottom = min(point.y, bounds.bottom)
            }
            return bounds
        }

        /**
         * Rotates the [point] around [cx] and [cy] by [angle] in radians
         */
        fun rotatePoint(point: Vector2, cx: Float, cy: Float, angle: Float): Vector2 {
            // move to origin
            point.x = point.x - cx
            point.y = point.y - cy

            val x = point.x * cos(angle) - point.y * sin(angle)
            val y = point.x * sin(angle) + point.y * cos(angle)

            // move back
            point.x = x + cx
            point.y = y + cy
            return point
        }
    }

}
