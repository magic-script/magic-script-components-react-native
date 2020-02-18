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

package com.magicleap.magicscript.ar

import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Utils
import kotlin.math.max
import kotlin.math.min

class SimpleMaterialClipper : MaterialClipper {

    /**
     * Default material clipping makes all the node visible.
     * Origin (0, 0) is at bottom-center. Values are relative to width and height.
     */
    private val defaultMaterialClipping = Bounding(-0.5f, 0.0f, 0.5f, 1.0f)

    override fun applyClipBounds(node: TransformNode, clipBounds: AABB?) {
        node.contentNode.renderable?.material?.let { material ->
            val materialClip = if (clipBounds != null) {
                calculateMaterialClipping(node.getBounding(), clipBounds)
            } else {
                defaultMaterialClipping
            }
            Utils.applyMaterialClipping(material, materialClip)
        }
    }

    private fun calculateMaterialClipping(nodeBounds: AABB, clipBounds: AABB): Bounding {
        val intersecting = !nodeBounds.intersection(clipBounds).equalInexact(AABB())
        if (!intersecting) {
            return Bounding()
        }

        // calculate 2d clipping
        val nodeBounds2d = nodeBounds.toBounding2d()
        val clipBounds2d = clipBounds.toBounding2d()
        val sizeX = nodeBounds2d.size().x
        val sizeY = nodeBounds2d.size().y
        if (sizeX <= 0f || sizeY <= 0f) {
            return Bounding()
        }

        val materialClip = Bounding()

        val offsetLeft = nodeBounds2d.left - clipBounds2d.left
        materialClip.left = max(-0.5f - offsetLeft / sizeX, -0.5f)

        val offsetRight = nodeBounds2d.right - clipBounds2d.right
        materialClip.right = min(0.5f - offsetRight / sizeX, 0.5f)

        val offsetBottom = nodeBounds2d.bottom - clipBounds2d.bottom
        materialClip.bottom = max(-offsetBottom / sizeY, 0.0f)

        val offsetTop = nodeBounds2d.top - clipBounds2d.top
        materialClip.top = min(1.0f - offsetTop / sizeY, 1.0f)

        return materialClip
    }

}