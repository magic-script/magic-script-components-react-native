package com.reactlibrary.scene.nodes.video

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage

class UiVideoView(props: ReadableMap, private val context: Context) : TransformNode(props) {

    companion object {
        const val PROP_VIDEO_PATH = "videoPath"
        const val PROP_IS_LOOPING = "isLooping"
    }

    private lateinit var mediaPlayer: MediaPlayer
    private val CHROMA_KEY_COLOR = Color(0.1843f, 1.0f, 0.098f)

    // Controls the height of the video in world space.
    private val VIDEO_HEIGHT_METERS = 0.85f

    override fun getBounding(): Bounding {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadRenderable(): Boolean {

    }


    fun setVideoPath() {
        if (renderableRequested) {
            loadModel()
        }
    }

    fun setAction(videoAction: VideoAction) {
        when(videoAction) {
            VideoAction.START -> start()
            VideoAction.PAUSE -> pause()
            VideoAction.STOP -> stop()
        }
    }

    private fun start() {
        mediaPlayer.start()
    }

    private fun stop() {
        mediaPlayer.stop()
    }

    private fun pause() {
        mediaPlayer.pause()
    }

    private fun loadModel() {
        val texture = ExternalTexture()
        val path = properties.getString(PROP_VIDEO_PATH)
        if(path != null) {
            mediaPlayer = MediaPlayer.create(context, Utils.getFilePath(path, context))
            mediaPlayer.setSurface(texture.surface)
            mediaPlayer.isLooping = properties.getBoolean(PROP_IS_LOOPING)

            ModelRenderable.builder()
                    .setSource(context, R.raw.chroma_key_video)
                    .build()
                    .thenAccept { renderable ->
                        renderable.material.setExternalTexture("videoTexture", texture)
                        renderable.material.setFloat4("keyColor", CHROMA_KEY_COLOR)
                        renderable.isShadowCaster = false
                        renderable.isShadowReceiver = false
                        this.renderable = renderable
                    }
                    .exceptionally { throwable ->
                        logMessage("error loading ModelRenderable: $throwable")
                        null
                    }
        }
    }
}