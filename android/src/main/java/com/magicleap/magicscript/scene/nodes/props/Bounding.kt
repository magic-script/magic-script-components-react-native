/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes.props

import com.magicleap.magicscript.utils.Vector2
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Represents bounds of a node
 */
data class Bounding(
    var left: Float = 0f,
    var bottom: Float = 0f,
    var right: Float = 0f,
    var top: Float = 0f
) {
    companion object {
        private const val EPSILON = 1e-5f
    }

    /**
     * Compares the bounds with [other] and returns true if they are the same
     * with the accuracy of [EPSILON]
     */
    fun equalInexact(other: Bounding): Boolean {
        return abs(left - other.left) < EPSILON
                && abs(right - other.right) < EPSILON
                && abs(bottom - other.bottom) < EPSILON
                && abs(top - other.top) < EPSILON
    }

    fun size(): Vector2 {
        val width = right - left
        val height = top - bottom
        return Vector2(width, height)
    }

    fun center(): Vector2 {
        val x = (right + left) / 2F
        val y = (top + bottom) / 2F
        return Vector2(x, y)
    }

    // Get new Bounding equal to this translated by 2D vector.
    fun translated(translation: Vector2): Bounding {
        return Bounding(
            left + translation.x,
            bottom + translation.y,
            right + translation.x,
            top + translation.y
        )
    }

    fun intersection(other: Bounding): Bounding {
        val xMin = max(left, other.left)
        val xMax = min(right, other.right)
        val yMin = max(bottom, other.bottom)
        val yMax = min(top, other.top)

        if (xMin > xMax || yMin > yMax) {
            return Bounding()
        }

        return Bounding(left = xMin, bottom = yMin, right = xMax, top = yMax)
    }

}