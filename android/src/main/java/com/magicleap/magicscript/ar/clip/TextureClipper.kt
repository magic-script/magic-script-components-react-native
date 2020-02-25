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

package com.magicleap.magicscript.ar.clip

import com.google.ar.sceneform.rendering.Material
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Utils

/**
 * Texture clipper intended to use with nodes containing a 2d textured object as a renderable
 */
class TextureClipper : Clipper {

    companion object {
        private const val CLIP_LEFT_PARAM = "left"
        private const val CLIP_RIGHT_PARAM = "right"
        private const val CLIP_TOP_PARAM = "top"
        private const val CLIP_BOTTOM_PARAM = "bottom"

        /**
         * Default material clipping makes all the node visible.
         * Origin (0, 0) is at bottom-center. Values are relative to width and height.
         */
        private val defaultMaterialClipping = Bounding(-0.5f, 0.0f, 0.5f, 1.0f)
    }

    override fun applyClipBounds(node: TransformNode, clipBounds: AABB?) {
        node.contentNode.renderable?.material?.let { material ->
            val materialClip = if (clipBounds != null) {
                Utils.calculateMaterialClipping(node.getBounding(), clipBounds)
            } else {
                defaultMaterialClipping
            }
            setMaterialClipping(material, materialClip)
        }
    }

    /**
     * Setting texture clipping at a shader level (shader is contained inside material
     * file from which android_view.sfb is built)
     *
     * https://google.github.io/filament/Materials.md.html
     * By default getPosition() returns NDC - Normalized Device Coordinates;
     * for "vertexDomain" : object it means that coordinates are normalized relative
     * to a model size (0 - 1), origin (0, 0) is at bottom-center.
     */
    private fun setMaterialClipping(material: Material, materialClip: Bounding) {
        material.apply {
            setFloat(CLIP_LEFT_PARAM, materialClip.left)
            setFloat(CLIP_RIGHT_PARAM, materialClip.right)
            setFloat(CLIP_TOP_PARAM, materialClip.top)
            setFloat(CLIP_BOTTOM_PARAM, materialClip.bottom)
        }
    }

}