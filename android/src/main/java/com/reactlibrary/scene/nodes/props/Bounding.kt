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

package com.reactlibrary.scene.nodes.props

import android.graphics.PointF
import com.google.ar.sceneform.math.Vector3
import kotlin.math.abs

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

        private const val eps = 1e-5 // epsilon

        /**
         * Compares 2 bounds and returns true if they are the same
         * with the accuracy of [eps]
         */
        fun equalInexact(a: Bounding, b: Bounding): Boolean {
            return abs(a.left - b.left) < eps
                    && abs(a.right - b.right) < eps
                    && abs(a.bottom - b.bottom) < eps
                    && abs(a.top - b.top) < eps

        }
    }

    fun size(): PointF {
        val width = right - left
        val height = top - bottom
        return PointF(width, height)
    }
}