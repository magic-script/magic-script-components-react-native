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

package com.magicleap.magicscript.scene.nodes.props

/**
 * Alignment that represents the pivot point (anchor) of a Node or layout's content alignment.
 */
data class Alignment(
    val vertical: VerticalAlignment = VerticalAlignment.TOP,
    val horizontal: HorizontalAlignment = HorizontalAlignment.LEFT
) {
    /**
     * Horizontal alignment
     * @param centerOffset center offset factor relative to a node width
     */
    enum class HorizontalAlignment(val centerOffset: Float) {
        LEFT(-0.5F),
        CENTER(0F),
        RIGHT(0.5F)
    }

    /**
     * Vertical alignment
     * @param centerOffset center offset factor relative to a node height
     */
    enum class VerticalAlignment(val centerOffset: Float) {
        TOP(0.5F),
        CENTER(0F),
        BOTTOM(-0.5F)
    }
}