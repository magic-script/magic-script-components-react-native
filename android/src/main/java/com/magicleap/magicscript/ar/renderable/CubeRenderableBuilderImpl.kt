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

import android.content.Context
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.utils.DataResult
import com.magicleap.magicscript.utils.logMessage

class CubeRenderableBuilderImpl(
    private val context: Context,
    private val arResourcesProvider: ArResourcesProvider
) : CubeRenderableBuilder,
    ArResourcesProvider.ArLoadedListener {

    init {
        arResourcesProvider.addArLoadedListener(this)
    }

    private val pendingRequests = mutableListOf<CubeRenderableBuilder.LoadRequest>()

    override fun buildRenderable(request: CubeRenderableBuilder.LoadRequest) {
        if (arResourcesProvider.isArLoaded()) {
            load(request)
        } else {
            pendingRequests.add(request)
        }
    }

    override fun onArLoaded(firstTime: Boolean) {
        val requestIterator = pendingRequests.iterator()
        while (requestIterator.hasNext()) {
            load(requestIterator.next())
            requestIterator.remove()
        }
    }

    override fun cancel(request: CubeRenderableBuilder.LoadRequest) {
        request.cancel()
        pendingRequests.remove(request)
    }

    private fun load(request: CubeRenderableBuilder.LoadRequest) {
        MaterialFactory
            .makeTransparentWithColor(context, request.color)
            .thenAccept { material ->
                material.setFloat(MaterialFactory.MATERIAL_REFLECTANCE, request.reflectance)
                material.setFloat(MaterialFactory.MATERIAL_ROUGHNESS, request.roughness)

                if (!request.isCancelled) {
                    val renderable =
                        ShapeFactory.makeCube(request.cubeSize, request.cubeCenter, material)
                    renderable.isShadowReceiver = false
                    renderable.isShadowCaster = false
                    request.listener.invoke(DataResult.Success(renderable))
                }
            }
            .exceptionally { throwable ->
                request.listener.invoke(DataResult.Error(throwable))
                logMessage("error building cube material: $throwable")
                null
            }
    }

}