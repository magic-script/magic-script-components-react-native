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

    override fun applyProperties(props: ReadableMap, update: Boolean) {
        super.applyProperties(props, update)
        // cannot update before [isRenderableAttached],
        // because Sceneform may be uninitialized
        if (update && isRenderableAttached) {
            loadModel()
        }
    }

    override fun loadRenderable(): Boolean {
        loadModel()
        return true
    }

    private fun loadModel() {
        val path = props.getStringSafely(PROP_MODEL_PATH)
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
                    }
                    .exceptionally { throwable ->
                        logMessage("error loading model: $throwable")
                        null
                    }
        }

    }

}