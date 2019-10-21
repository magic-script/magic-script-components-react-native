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
import kotlin.Float.Companion.MAX_VALUE
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
         * Calculates local bounds of a node using its collision shape
         */
        fun calculateBoundsOfNode(node: Node): Bounding {
            // TODO (optionally) add Sphere collision shape support (currently never used)
            var offsetX = node.localPosition.x
            var offsetY = node.localPosition.y
            val collShape = if (node is TransformNode) {
                offsetX += node.contentNode.localPosition.x
                offsetY += node.contentNode.localPosition.y
                node.contentNode.collisionShape
            } else {
                node.collisionShape
            }
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

            val sumBounds = Bounding(MAX_VALUE, MAX_VALUE, -MAX_VALUE, -MAX_VALUE)

            for (i in 0 until nodes.size) {
                val node = nodes[i]
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

    }

}
