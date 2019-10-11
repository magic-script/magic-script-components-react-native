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

package com.reactlibrary.ar

import android.content.Context
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.reactlibrary.utils.logMessage

class CubeRenderableBuilderImpl(private val context: Context) : CubeRenderableBuilder {

    override fun buildRenderable(cubeSize: Vector3, cubeCenter: Vector3, color: Color, resultCallback: (result: RenderableResult) -> Unit) {
        MaterialFactory
                .makeOpaqueWithColor(context, color)
                .thenAccept { material ->
                    val renderable = ShapeFactory.makeCube(cubeSize, cubeCenter, material)
                    renderable.isShadowReceiver = false
                    renderable.isShadowCaster = false
                    resultCallback(RenderableResult.Success(renderable))
                }
                .exceptionally { throwable ->
                    resultCallback(RenderableResult.Error(throwable))
                    logMessage("error building cube material: $throwable")
                    null
                }
    }
}