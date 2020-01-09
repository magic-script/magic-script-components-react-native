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
import android.util.TypedValue
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.magicleap.magicscript.utils.logMessage

class ModelRenderableLoaderImpl(private val context: Context) : ModelRenderableLoader {

    override fun loadRenderable(
        modelUri: Uri,
        resultCallback: (
            result: RenderableResult<ModelRenderable>
        ) -> Unit
    ) {
        val modelType = detectModelType(modelUri)
        val builder = ModelRenderable.builder()

        if (modelType == ModelType.GLB) {
            builder.setSource(
                context, RenderableSource.builder()
                    .setSource(
                        context,
                        modelUri,
                        RenderableSource.SourceType.GLB // GLB (binary) or GLTF (text)
                    )
                    .setRecenterMode(RenderableSource.RecenterMode.CENTER)
                    .build()
            )
        } else {
            builder.setSource(context, modelUri)
        }

        builder.setRegistryId(modelUri)
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

    private fun detectModelType(uri: Uri): ModelType {
        if (uri.toString().contains("android.resource://")) { // release build
            val resourceName = uri.lastPathSegment
            logMessage("model res name=$resourceName")

            val resourceId =
                context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resourceId == 0) { // does not exists
                return ModelType.UNKNOWN
            }

            val value = TypedValue()
            context.resources.getValue(resourceId, value, true)
            val resWithExtension = value.string
            if (resWithExtension.endsWith(".glb")) {
                return ModelType.GLB
            }
            if (resWithExtension.endsWith(".sfb")) {
                return ModelType.SFB
            }
            return ModelType.UNKNOWN
        } else { // localhost path
            if (uri.toString().contains(".glb")) {
                return ModelType.GLB
            }
            if (uri.toString().contains(".sfb")) {
                return ModelType.SFB
            }
            return ModelType.UNKNOWN
        }

    }

}