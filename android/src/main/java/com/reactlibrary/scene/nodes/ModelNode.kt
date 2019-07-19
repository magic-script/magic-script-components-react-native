package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

class ModelNode(props: ReadableMap, private val context: Context) : TransformNode(props) {

    companion object {
        // properties
        private const val PROP_MODEL_PATH = "modelPath"
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setModelPath(props)
    }

    override fun loadRenderable(): Boolean {
        loadModel()
        return true
    }

    override fun getBounding(): Bounding {
        // TODO calculate bounding the same way as inside UiNode
        return Bounding()
    }

    private fun setModelPath(props: Bundle) {
        if (props.containsKey(PROP_MODEL_PATH)) {
            // cannot update the ModelRenderable before [isRenderableAttached],
            // because Sceneform may be uninitialized yet
            // (loadRenderable may have not been called)
            if (isRenderableAttached) {
                loadModel()
            }
        }
    }

    private fun loadModel() {
        val path = properties.getString(PROP_MODEL_PATH)
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
                        logMessage("error loading ModelRenderable: $throwable")
                        null
                    }
        }
    }

}