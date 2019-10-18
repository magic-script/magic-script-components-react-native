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
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.reactlibrary.ar.RenderableResult
import com.reactlibrary.ar.VideoRenderableLoader
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.logMessage
import com.reactlibrary.utils.putDefaultDouble

class VideoNode(initProps: ReadableMap,
                private val context: Context,
                private val videoPlayer: VideoPlayer,
                private val videoRenderableLoader: VideoRenderableLoader)
    : TransformNode(initProps, hasRenderable = true, useContentNodeAlignment = true) {

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

    var onVideoPreparedListener: (() -> Unit)? = null

    // width and height are determined by ExternalTexture size which is 1m x 1m
    // (video is stretched to fit the 1m x 1m square, no matter what resolution it has)
    private val initialWidth = 1F // meters
    private val initialHeight = 1F // meters

    init {
        // set default values of properties
        properties.putDefaultDouble(PROP_VOLUME, DEFAULT_VOLUME)
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

        setSize(props)
        setAction(props)
        setLooping(props)
        setVolume(props)
    }

    override fun loadRenderable() {
        loadVideo()
    }

    // destroying media player when node is detached (e.g. on scene change)
    override fun onDestroy() {
        super.onDestroy()
        videoPlayer.release()
    }

    private fun loadVideo() {
        val videoUri = PropertiesReader.readFilePath(properties, PROP_VIDEO_PATH, context)
        if (videoUri != null) {
            val texture = ExternalTexture()
            try {
                videoPlayer.loadVideo(videoUri, texture.surface, onLoadedListener = {
                    onVideoPreparedListener?.invoke()
                })
            } catch (exception: Exception) {
                logMessage("video load exception: $exception", warn = true)
            }

            videoRenderableLoader.loadRenderable { result ->
                if (result is RenderableResult.Success) {
                    result.renderable.material.setExternalTexture("videoTexture", texture)
                    contentNode.renderable = result.renderable
                }
            }
        }
    }

    // changing video size by scaling the content node
    private fun setSize(props: Bundle) {
        val sizeArray = props.getSerializable(PROP_SIZE) as? ArrayList<Double>
        if (sizeArray != null && sizeArray.size == 2) {
            val widthMeters = sizeArray[0].toFloat()
            val heightMeters = sizeArray[1].toFloat()

            val scaleX = widthMeters / initialWidth
            val scaleY = heightMeters / initialHeight
            contentNode.localScale = Vector3(scaleX, scaleY, 1F)
        }
    }

    private fun setAction(props: Bundle) {
        val action = props.getString(PROP_ACTION)
        if (!videoPlayer.isReady) {
            return
        }
        try {
            when (action) {
                ACTION_START -> {
                    videoPlayer.start()
                }
                ACTION_STOP -> {
                    if (videoPlayer.isPlaying) {
                        // using stop() would require to prepare() before next playback
                        videoPlayer.stop()
                    }
                }
                ACTION_PAUSE -> {
                    if (videoPlayer.isPlaying) {
                        videoPlayer.pause()
                    }
                }
            }
        } catch (e: Exception) {
            logMessage("Error setting video action $action: $e", true)
        }
    }

    private fun setLooping(props: Bundle) {
        if (props.containsKey(PROP_LOOPING)) {
            videoPlayer.looping = props.getBoolean(PROP_LOOPING)
        }
    }

    private fun setVolume(props: Bundle) {
        if (props.containsKey(PROP_VOLUME)) {
            videoPlayer.volume = props.getDouble(PROP_VOLUME).toFloat()
        }
    }
}