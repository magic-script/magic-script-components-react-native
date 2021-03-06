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

package com.magicleap.magicscript.ar.renderable

import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ModelRenderable
import com.magicleap.magicscript.utils.DataResult

interface CubeRenderableBuilder {

    /**
     * Builds cube asynchronously
     */
    fun buildRenderable(request: LoadRequest)

    fun cancel(request: LoadRequest)

    /**
     * @param roughness material roughness in range 0 - 1
     * @param reflectance material reflectance in range 0 - 1
     */
    class LoadRequest(
        val cubeSize: Vector3,
        val cubeCenter: Vector3,
        val color: Color,
        val roughness: Float = 0.4f,
        val reflectance: Float = 0.5f,
        listener: (result: DataResult<ModelRenderable>) -> Unit
    ) : RenderableLoadRequest<ModelRenderable>(listener)
}