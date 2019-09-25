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

package com.reactlibrary.scene.nodes.video

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.logMessage

class VideoNode(initProps: ReadableMap, private val context: Context)
    : TransformNode(initProps, hasRenderable = true, useContentNodeAlignment = true), MediaPlayer.OnPreparedListener {

    companion object {
        const val PROP_VIDEO_PATH = "videoPath"
        const val PROP_SIZE = "size" // width and height in meters
        const val PROP_WIDTH = "width" // horizontal resolution
        const val PROP_HEIGHT = "height" // vertical resolution
        const val PROP_LOOPING = "looping"
        const val PROP_ACTION = "action"
        const val PROP_VOLUME = "volume"

        const val ACTION_START = "start"
        const val ACTION_STOP = "stop"
        const val ACTION_PAUSE = "pause"

        const val DEFAULT_VOLUME = 1.0
    }

    // width and height are determined by ExternalTexture size which is 1m x 1m
    // (video is stretched to fit the 1m x 1m square, no matter what resolution it has)
    private val initialWidth = 1F // meters
    private val initialHeight = 1F // meters


    private var mediaPlayer: MediaPlayer? = null
    private var readyToPlay = false

    init {
        // set default values of properties
        if (!properties.containsKey(PROP_VOLUME)) {
            properties.putDouble(PROP_VOLUME, DEFAULT_VOLUME)
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        if (props.containsKey(PROP_VIDEO_PATH) || props.containsKey(PROP_SIZE)) {
            // cannot update the ModelRenderable before [renderableRequested],
            // because Sceneform may be uninitialized yet
            // (loadRenderable may have not been called)
            if (renderableRequested) {
                loadVideo()
            }
        }
        setAction(props)
        setLooping(props)
        setVolume(props)
    }

    override fun loadRenderable() {
        loadVideo()
    }

    // called when media player is ready to play
    override fun onPrepared(mp: MediaPlayer) {
        readyToPlay = true
        mp.seekTo(0) // to show first video frame instead of black texture
    }

    // destroying media player when node is detached (e.g. on scene change)
    override fun clearResources() {
        super.clearResources()
        mediaPlayer?.release()
    }

    private fun loadVideo() {
        mediaPlayer?.release() // release old media player (user can change the video path)

        val texture = ExternalTexture()
        val videoUri = PropertiesReader.readFilePath(properties, PROP_VIDEO_PATH, context)
        if (videoUri != null) {
            readyToPlay = false
            val player = MediaPlayerPool.createMediaPlayer()
            this.mediaPlayer = player
            val path = videoUri.toString()
            try {
                if (path.startsWith("http")) {
                    player.setDataSource(path) // load from URL
                } else {
                    // load the video from a local directory, e.g. from res/raw
                    player.setDataSource(context, videoUri)
                }
                player.setSurface(texture.surface)
                player.setOnPreparedListener(this)
                player.isLooping = properties.getBoolean(PROP_LOOPING)
                val volume = properties.getDouble(PROP_VOLUME, DEFAULT_VOLUME).toFloat()
                player.setVolume(volume, volume)
                player.prepareAsync() // load video asynchronously
            } catch (exception: Exception) {
                logMessage("video player exception: $exception", warn = true)
            }

            ModelRenderable.builder()
                    .setSource(context, R.raw.chroma_key_video)
                    .build()
                    .thenAccept { renderable ->
                        renderable.material.setExternalTexture("videoTexture", texture)
                        renderable.material.setBoolean("disableChromaKey", true)
                        // renderable.material.setFloat4("keyColor", CHROMA_KEY_COLOR)
                        renderable.isShadowCaster = false
                        renderable.isShadowReceiver = false
                        contentNode.renderable = renderable
                    }
                    .exceptionally { throwable ->
                        logMessage("error loading ModelRenderable: $throwable")
                        null
                    }

            adjustScale()
        }
    }

    // changing video size by scaling the content node
    private fun adjustScale() {
        val sizeArray = properties.getSerializable(PROP_SIZE) as? ArrayList<Double>
        if (sizeArray != null && sizeArray.size == 2) {
            val widthMeters = sizeArray[0].toFloat()
            val heightMeters = sizeArray[1].toFloat()

            val scaleX = widthMeters / initialWidth
            val scaleY = heightMeters / initialHeight
            contentNode.localScale = Vector3(scaleX, scaleY, 1F)
        } else {
            logMessage("video size not specified", true)
        }
    }

    private fun setAction(props: Bundle) {
        val action = props.getString(PROP_ACTION)
        val player = mediaPlayer ?: return
        if (!readyToPlay) {
            return
        }
        try {
            when (action) {
                ACTION_START -> {
                    player.start()
                }
                ACTION_STOP -> {
                    if (player.isPlaying) {
                        // using stop() would require to prepare() before next playback
                        player.pause()
                        player.seekTo(0)
                    }
                }
                ACTION_PAUSE -> {
                    if (player.isPlaying) {
                        player.pause()
                    }
                }
            }
        } catch (e: Exception) {
            logMessage("Error setting action $action: $e", true)
        }
    }

    private fun setLooping(props: Bundle) {
        if (props.containsKey(PROP_LOOPING)) {
            mediaPlayer?.isLooping = props.getBoolean(PROP_LOOPING)
        }
    }

    private fun setVolume(props: Bundle) {
        if (props.containsKey(PROP_VOLUME)) {
            val volume = props.getDouble(PROP_VOLUME).toFloat()
            mediaPlayer?.setVolume(volume, volume)
        }
    }
}