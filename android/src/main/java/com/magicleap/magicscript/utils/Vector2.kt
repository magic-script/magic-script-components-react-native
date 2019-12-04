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

import com.google.ar.sceneform.math.Vector3

data class Vector2(var x: Float = 0F, var y: Float = 0F) {

    operator fun unaryMinus(): Vector2 {
        return Vector2(-x, -y)
    }

    operator fun plus(other: Vector2): Vector2 {
        return Vector2(x + other.x, y + other.y)
    }

    operator fun minus(other: Vector2): Vector2 {
        return Vector2(x - other.x, y - other.y)
    }

    operator fun minus(other: Float): Vector2 {
        return Vector2(x - other, y - other)
    }

    operator fun times(other: Vector2): Vector2 {
        return Vector2(x * other.x, y * other.y)
    }

    operator fun div(other: Vector2): Vector2 {
        return Vector2(
            if (other.x != 0F) {
                x / other.x
            } else {
                0F
            }, if (other.y != 0F) {
                y / other.y
            } else {
                0F
            }
        )
    }

    operator fun div(other: Float): Vector2 {
        return if (other != 0F) {
            Vector2(x / other, y / other)
        } else {
            Vector2()
        }
    }

    fun coerceIn(min: Float, max: Float): Vector2 {
        return Vector2(x.coerceIn(min, max), y.coerceIn(min, max))
    }

    fun coerceAtLeast(min: Float): Vector2 {
        return Vector2(x.coerceAtLeast(min), y.coerceAtLeast(min))
    }

    fun toVector3(): Vector3 {
        return Vector3(x, y, 0F)
    }
}

