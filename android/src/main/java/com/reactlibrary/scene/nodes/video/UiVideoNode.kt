package com.reactlibrary.scene.nodes.video

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

class UiVideoNode(props: ReadableMap, private val context: Context) : TransformNode(props) {

    companion object {
        const val PROP_VIDEO_PATH = "videoPath"
        const val PROP_IS_LOOPING = "isLooping"
        private val CHROMA_KEY_COLOR = Color(0.1843f, 1.0f, 0.098f)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        if(renderableRequested) loadModel()
    }

    override fun loadRenderable(): Boolean {
        loadModel()
        return true
    }

    override fun getBounding(): Bounding {
        return Utils.calculateBoundsOfNode(this)
    }

    private fun loadModel() {
        logMessage("load video model")
        val texture = ExternalTexture()
        val path = properties.getString(PROP_VIDEO_PATH)
        if(path != null) {
            val mediaPlayer = MediaPlayerPool.createMediaPlayer(path, context)
            mediaPlayer.setSurface(texture.surface)
            mediaPlayer.isLooping = properties.getBoolean(PROP_IS_LOOPING)

            ModelRenderable.builder()
                    .setSource(context, R.raw.chroma_key_video)
                    .build()
                    .thenAccept { renderable ->
                        val width = mediaPlayer.videoWidth.toFloat()
                        val height = mediaPlayer.videoHeight.toFloat()
                        localScale = when {
                            width > height -> Vector3(1f, 1f*(height/width), 0f)
                            height > width -> Vector3(1f*(width/height), 1f, 0f)
                            else -> Vector3(1f, 1f, 0f)
                        }
                        renderable.material.setExternalTexture("videoTexture", texture)
                        renderable.material.setFloat4("keyColor", CHROMA_KEY_COLOR)
                        renderable.isShadowCaster = false
                        renderable.isShadowReceiver = false
                        this.renderable = renderable
                        mediaPlayer.start()
                    }
                    .exceptionally { throwable ->
                        logMessage("error loading ModelRenderable: $throwable")
                        null
                    }
        }
    }
}