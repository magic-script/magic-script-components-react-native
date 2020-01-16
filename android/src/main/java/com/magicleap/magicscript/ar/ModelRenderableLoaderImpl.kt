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

package com.magicleap.magicscript.ar

import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.logMessage

class ModelRenderableLoaderImpl(private val context: Context) : ModelRenderableLoader {

    override fun loadRenderable(
        modelUri: Uri,
        resultCallback: (result: RenderableResult<ModelRenderable>) -> Unit
    ) {
        val builder = ModelRenderable.builder()
        val modelType = Utils.detectModelType(modelUri, context)
        when (modelType) {
            ModelType.GLB -> setGLBSource(builder, modelUri)
            ModelType.SFB -> setSFBSource(builder, modelUri)
            ModelType.UNKNOWN -> {
                val errorMessage = "Unresolved model type"
                logMessage(errorMessage, true)
                resultCallback(RenderableResult.Error(Exception(errorMessage)))
                return
            }
        }

        builder
            .setRegistryId(modelUri)
            .build()
            .thenAccept { renderable ->
                renderable.isShadowReceiver = false
                renderable.isShadowCaster = false
                resultCallback(RenderableResult.Success(renderable))
            }
            .exceptionally { throwable ->
                logMessage("error loading ModelRenderable: $throwable")
                resultCallback(RenderableResult.Error(throwable))
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