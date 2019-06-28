package com.reactlibrary.scene.nodes

import android.content.Context
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.getStringSafely
import com.reactlibrary.utils.logMessage

class ModelNode(props: ReadableMap, private val context: Context) : TransformNode(props) {

    companion object {
        // properties
        private const val PROP_MODEL_PATH = "modelPath"
    }

    private lateinit var modelPath: String

    init {
        readModelPath(props)
    }

    override fun loadRenderable(): Boolean {

        val androidPathUri = Utils.getFilePath(modelPath, context)

        logMessage("path: $modelPath")
        logMessage("android path: $androidPathUri")

        ModelRenderable.builder()
                .setSource(context, RenderableSource.builder().setSource(
                        context,
                        androidPathUri,
                        RenderableSource.SourceType.GLB) // GLB (binary) or GLTF (text)
                        // .setScale(2.5f)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(modelPath)
                .build()
                .thenAccept {
                    this.renderable = it
                }
                .exceptionally { throwable ->
                    logMessage("error loading model: $throwable")
                    null
                }

        return true
    }

    private fun readModelPath(props: ReadableMap) {
        props.getStringSafely(PROP_MODEL_PATH)?.let { this.modelPath = it }
    }

}