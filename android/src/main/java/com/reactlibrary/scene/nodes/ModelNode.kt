package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

class ModelNode(props: ReadableMap, private val context: Context) : TransformNode(props) {

    companion object {
        // properties
        private const val PROP_MODEL_PATH = "modelPath"
    }

    private var modelPath: String? = null

    override fun applyProperties(properties: Bundle, update: Boolean) {
        super.applyProperties(properties, update)
        setModelPath(properties, update)
    }

    override fun loadRenderable(): Boolean {
        loadModel()
        return true
    }

    private fun setModelPath(properties: Bundle, update: Boolean) {
        if (properties.containsKey(PROP_MODEL_PATH)) {
            modelPath = properties.getString(PROP_MODEL_PATH)

            // cannot update the ModelRenderable before [isRenderableAttached],
            // because Sceneform may be uninitialized yet
            if (update && isRenderableAttached) {
                loadModel()
            }
        }

    }

    private fun loadModel() {
        val path = this.modelPath
        if (path != null) {
            val androidPathUri = Utils.getFilePath(path, context)
            ModelRenderable.builder()
                    .setSource(context, RenderableSource.builder().setSource(
                            context,
                            androidPathUri,
                            RenderableSource.SourceType.GLB) // GLB (binary) or GLTF (text)
                            // .setScale(2.5f)
                            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                            .build())
                    .setRegistryId(path)
                    .build()
                    .thenAccept {
                        this.renderable = it
                        logMessage("loaded ModelRenderable")
                    }
                    .exceptionally { throwable ->
                        logMessage("error loading model: $throwable")
                        null
                    }
        }

    }

}