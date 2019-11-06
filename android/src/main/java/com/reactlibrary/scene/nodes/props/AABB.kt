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

import com.google.ar.sceneform.math.Vector3

/**
 * Axis aligned bounding box.
 */
data class AABB(
        val min: Vector3 = Vector3.zero(),
        val max: Vector3 = Vector3.zero()
) {

    fun getWidth(): Float {
        return max.x - min.x
    }

    fun getHeight(): Float {
        return max.y - min.y
    }
}
