/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.utils

import kotlin.math.abs

data class Vector2(var x: Float = 0F, var y: Float = 0F) {

    companion object {
        const val EPSILON = 1e-5f
    }

    operator fun plus(other: Vector2): Vector2 {
        return Vector2(x + other.x, y + other.y)
    }

    operator fun minus(other: Vector2): Vector2 {
        return Vector2(x - other.x, y - other.y)
    }

    fun coerceIn(min: Float, max: Float): Vector2 {
        return Vector2(x.coerceIn(min, max), y.coerceIn(min, max))
    }

    fun coerceAtLeast(min: Float): Vector2 {
        return Vector2(x.coerceAtLeast(min), y.coerceAtLeast(min))
    }

    fun equalInexact(other: Vector2): Boolean {
        val xDiff = abs(this.x - other.x)
        val yDiff = abs(this.y - other.y)
        return xDiff < EPSILON && yDiff < EPSILON
    }
}

