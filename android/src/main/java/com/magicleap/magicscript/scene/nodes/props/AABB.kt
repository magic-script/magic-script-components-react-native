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

import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.utils.equalInexact
import com.magicleap.magicscript.utils.plus
import kotlin.math.max
import kotlin.math.min

/**
 * Axis aligned bounding box.
 */
data class AABB(
    /**
     * Edge located at most left - down - far corner, e.g. [-1, -1, -1]
     */
    val min: Vector3 = Vector3.zero(),

    /**
     *  Edge located at most right - top - close corner, e.g. [1, 1, 1]
     */
    val max: Vector3 = Vector3.zero()
) {

    companion object {
        private const val EPSILON = 1e-5f
    }

    /**
     * Compares the bounds with [other] and returns true if they are the same
     * with the accuracy of [EPSILON]
     */
    fun equalInexact(other: AABB): Boolean {
        return min.equalInexact(other.min, EPSILON) && max.equalInexact(other.max, EPSILON)
    }

    fun size(): Vector3 {
        return Vector3(max.x - min.x, max.y - min.y, max.z - min.z)
    }

    fun center(): Vector3 {
        val x = (min.x + max.x) / 2f
        val y = (min.y + max.y) / 2f
        val z = (min.z + max.z) / 2f
        return Vector3(x, y, z)
    }

    /**
     * Returns a new box translated by [translation] vector
     */
    fun translated(translation: Vector3): AABB {
        return AABB(min + translation, max + translation)
    }

    /**
     * Returns a new box scaled by [scale]
     */
    fun scaled(scaleX: Float, scaleY: Float, scaleZ: Float): AABB {
        val minScaled = Vector3(min.x * scaleX, min.y * scaleY, min.z * scaleZ)
        val maxScaled = Vector3(max.x * scaleX, max.y * scaleY, max.z * scaleZ)
        return AABB(minScaled, maxScaled)
    }

    fun toBounding2d(): Bounding {
        return Bounding(left = min.x, bottom = min.y, right = max.x, top = max.y)
    }

    fun intersection(other: AABB): AABB {
        val xMin = max(this.min.x, other.min.x)
        val xMax = min(this.max.x, other.max.x)

        val yMin = max(this.min.y, other.min.y)
        val yMax = min(this.max.y, other.max.y)

        val zMin = max(this.min.z, other.min.z)
        val zMax = min(this.max.z, other.max.z)

        if (xMin > xMax || yMin > yMax || zMin > zMax) {
            return AABB(min = Vector3.zero(), max = Vector3.zero())
        }

        return AABB(min = Vector3(xMin, yMin, zMin), max = Vector3(xMax, yMax, zMax))
    }

}
