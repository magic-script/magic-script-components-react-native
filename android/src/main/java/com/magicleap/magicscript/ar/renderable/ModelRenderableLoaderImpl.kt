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
import android.net.Uri
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.ModelType
import com.magicleap.magicscript.utils.DataResult
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.logMessage

class ModelRenderableLoaderImpl(
    private val context: Context,
    private val arResourcesProvider: ArResourcesProvider
) : ModelRenderableLoader,
    ArResourcesProvider.ArLoadedListener {

    init {
        arResourcesProvider.addArLoadedListener(this)
    }

    private val pendingRequests = mutableListOf<ModelRenderableLoader.LoadRequest>()

    override fun loadRenderable(request: ModelRenderableLoader.LoadRequest) {
        if (arResourcesProvider.isArLoaded()) {
            load(request)
        } else {
            pendingRequests.add(request)
        }
    }

    override fun cancel(request: ModelRenderableLoader.LoadRequest) {
        request.cancel()
        pendingRequests.remove(request)
    }

    override fun onArLoaded() {
        val requestIterator = pendingRequests.iterator()
        while (requestIterator.hasNext()) {
            load(requestIterator.next())
            requestIterator.remove()
        }
    }

    private fun load(request: ModelRenderableLoader.LoadRequest) {
        val modelUri = request.modelUri
        val builder = ModelRenderable.builder()
        val modelType = Utils.detectModelType(modelUri, context)
        when (modelType) {
            ModelType.GLB -> setGLBSource(builder, modelUri)
            ModelType.SFB -> setSFBSource(builder, modelUri)
            ModelType.UNKNOWN -> {
                val errorMessage = "Unresolved model type"
                logMessage(errorMessage, true)
                request.listener.invoke(DataResult.Error(Exception(errorMessage)))
                return
            }
        }

        builder
            .setRegistryId(modelUri)
            .build()
            .thenAccept { renderable ->
                if (!request.isCancelled) {
                    renderable.isShadowReceiver = false
                    renderable.isShadowCaster = false
                    request.listener.invoke(DataResult.Success(renderable))
                }
            }
            .exceptionally { throwable ->
                logMessage("error loading ModelRenderable: $throwable")
                request.listener.invoke(DataResult.Error(throwable))
                null
            }
    }

    private fun setGLBSource(builder: ModelRenderable.Builder, uri: Uri) {
        val source = RenderableSource.builder()
            .setSource(context, uri, RenderableSource.SourceType.GLB)
            .setRecenterMode(RenderableSource.RecenterMode.CENTER)
            .build()
        builder.setSource(context, source)
    }

    private fun setSFBSource(builder: ModelRenderable.Builder, uri: Uri) {
        builder.setSource(context, uri)
    }

}