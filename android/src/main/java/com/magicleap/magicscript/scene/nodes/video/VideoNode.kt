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

package com.magicleap.magicscript.scene.nodes.video

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.RenderableLoadRequest
import com.magicleap.magicscript.ar.renderable.RenderableResult
import com.magicleap.magicscript.ar.renderable.VideoRenderableLoader
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.logMessage
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.readFilePath

class VideoNode(
    initProps: ReadableMap,
    private val context: Context,
    private val videoPlayer: VideoPlayer,
    private val videoRenderableLoader: VideoRenderableLoader,
    private val nodeClipper: Clipper,
    private val arResourcesProvider: ArResourcesProvider
) : TransformNode(initProps, useContentNodeAlignment = true) {

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

    override var clipBounds: AABB?
        get() = super.clipBounds
        set(value) {
            super.clipBounds = value
            applyClipBounds()
        }

    private var renderableLoadRequest: RenderableLoadRequest? = null
    private var renderableCopy: Renderable? = null
    // width and height are determined by ExternalTexture size which is 1m x 1m
    // (video is stretched to fit the 1m x 1m square, no matter what resolution it has)
    private val initialWidth = 1F // meters
    private val initialHeight = 1F // meters

    private var lastUserAction: String = ""

    init {
        // set default values of properties
        properties.putDefault(PROP_VOLUME, DEFAULT_VOLUME)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        if (props.containsKey(PROP_VIDEO_PATH) || props.containsKey(PROP_SIZE)) {
            loadVideo()
        }

        setSize(props)
        setAction(props)
        setLooping(props)
        setVolume(props)
    }

    override fun onVisibilityChanged(visibility: Boolean) {
        super.onVisibilityChanged(visibility)
        if (visibility) {
            contentNode.renderable = renderableCopy
            applyClipBounds()
        } else {
            contentNode.renderable = null
        }
    }

    override fun onTransformedLocally() {
        super.onTransformedLocally()
        applyClipBounds()
    }

    override fun onPause() {
        super.onPause()
        if (videoPlayer.isPlaying) {
            try {
                videoPlayer.pause()
            } catch (exception: IllegalStateException) {
                logMessage("onPause cannot pause video: $exception", warn = true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (lastUserAction == ACTION_START) {
            try {
                videoPlayer.start()
            } catch (exception: IllegalStateException) {
                logMessage("onResume cannot resume video: $exception", warn = true)
            }
        }
    }

    // destroying media player when node is detached (e.g. on scene change)
    override fun onDestroy() {
        super.onDestroy()
        videoPlayer.release()
        renderableLoadRequest?.let {
            videoRenderableLoader.cancel(it)
        }
    }

    private fun applyClipBounds() {
        nodeClipper.applyClipBounds(this, clipBounds)
    }

    private fun loadVideo() {
        if (!arResourcesProvider.isArLoaded()) {
            // ar must be load to create ExternalTexture object
            return
        }

        val videoUri = properties.readFilePath(PROP_VIDEO_PATH, context)
        if (videoUri != null) {
            val texture = ExternalTexture()
            try {
                videoPlayer.loadVideo(videoUri, texture.surface, onLoadedListener = {
                    onVideoPreparedListener?.invoke()
                })
            } catch (exception: Exception) {
                logMessage("video load exception: $exception", warn = true)
            }

            // cancel previous load request if exists
            renderableLoadRequest?.let {
                videoRenderableLoader.cancel(it)
            }

            renderableLoadRequest = RenderableLoadRequest { result ->
                if (result is RenderableResult.Success<Renderable>) {
                    result.renderable.material.setExternalTexture("videoTexture", texture)
                    if (isVisible) {
                        contentNode.renderable = result.renderable
                    }
                    renderableCopy = result.renderable
                    applyClipBounds()
                }
            }.also {
                videoRenderableLoader.loadRenderable(it)
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
            applyClipBounds()
        }
    }

    private fun setAction(props: Bundle) {
        val action = props.getString(PROP_ACTION)
        lastUserAction = action ?: lastUserAction
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