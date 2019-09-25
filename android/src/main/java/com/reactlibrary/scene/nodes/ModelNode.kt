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

package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.logMessage

class ModelNode(initProps: ReadableMap, private val context: Context)
    : TransformNode(initProps, hasRenderable = true, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_MODEL_PATH = "modelPath"
        // initial resource scale applied when loading model (cannot be updated)
        const val PROP_IMPORT_SCALE = "importScale"

        const val DEFAULT_IMPORT_SCALE = 1.0
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setModelPath(props)
    }

    override fun loadRenderable() {
        loadModel()
    }

    private fun setModelPath(props: Bundle) {
        if (props.containsKey(PROP_MODEL_PATH)) {
            // cannot update the ModelRenderable before [renderableRequested],
            // because Sceneform may be uninitialized yet
            // (loadRenderable may have not been called)
            if (renderableRequested) {
                loadModel()
            }
        }
    }

    override fun setLocalScale(props: Bundle) {
        val localScale = PropertiesReader.readVector3(props, PROP_LOCAL_SCALE)
        if (localScale != null) {
            applyNodeScale(localScale)
        }
    }

    private fun loadModel() {
        val modelUri = PropertiesReader.readFilePath(properties, PROP_MODEL_PATH, context)
        if (modelUri != null) {
            ModelRenderable.builder()
                    .setSource(context, RenderableSource.builder().setSource(
                            context,
                            modelUri,
                            RenderableSource.SourceType.GLB) // GLB (binary) or GLTF (text)
                            .setRecenterMode(RenderableSource.RecenterMode.CENTER)
                            .build())
                    .setRegistryId(modelUri)
                    .build()
                    .thenAccept { renderable ->
                        renderable.isShadowReceiver = false
                        renderable.isShadowCaster = false
                        contentNode.renderable = renderable
                        logMessage("loaded ModelRenderable")
                    }
                    .exceptionally { throwable ->
                        logMessage("error loading ModelRenderable: $throwable")
                        null
                    }
        }
        applyNodeScale(localScale)
    }

    private fun applyNodeScale(scale: Vector3) {
        val importScale = properties.getDouble(PROP_IMPORT_SCALE, DEFAULT_IMPORT_SCALE).toFloat()
        localScale = Vector3(scale.x * importScale, scale.y * importScale, scale.z * importScale)
    }

}