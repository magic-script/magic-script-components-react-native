/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
import android.net.Uri
import android.os.Bundle
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.RenderableLoadRequest
import com.magicleap.magicscript.ar.renderable.VideoRenderableLoader
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.scene.nodes.UiTextNode
import com.magicleap.magicscript.scene.nodes.UiTextNode.Companion.PROP_BOUNDS_SIZE
import com.magicleap.magicscript.scene.nodes.UiTextNode.Companion.PROP_TEXT_ALIGNMENT
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.*

class VideoNode(
    initProps: ReadableMap,
    private val context: Context,
    private val videoPlayer: VideoPlayer,
    private val videoRenderableLoader: VideoRenderableLoader,
    private val viewRenderableLoader: ViewRenderableLoader,
    private val nodeClipper: Clipper,
    private val fontProvider: FontProvider,
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
        const val PROP_TIMED_TEXT_PATH = "timedTextPath"
        const val PROP_SEEK_TO = "seekTo"

        const val ACTION_START = "start"
        const val ACTION_STOP = "stop"
        const val ACTION_PAUSE = "pause"

        const val DEFAULT_VOLUME = 1.0

        private const val SUBTITLES_MARGIN_BOTTOM = 0.05 // in meters
    }

    var onVideoPreparedListener: (() -> Unit)? = null

    override var clipBounds: AABB?
        get() = super.clipBounds
        set(value) {
            super.clipBounds = value
            applyClipBounds()
        }

    private var renderableLoadRequest: RenderableLoadRequest<ModelRenderable>? = null
    private var renderableCopy: Renderable? = null
    // width and height are determined by ExternalTexture size which is 1m x 1m
    // (video is stretched to fit the 1m x 1m square, no matter what resolution it has)
    private val initialWidth = 1F // meters
    private val initialHeight = 1F // meters

    private var lastUserAction: String = ""
    private var subtitles: UiSubtitlesNode? = null

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
        setSeekTo(props)

        if (props.containsAny(PROP_SIZE)) {
            updateSubtitlesProps()
        }
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
        try {
            if (videoPlayer.isPlaying) {
                videoPlayer.pause()
            }
        } catch (exception: IllegalStateException) {
            logMessage("onPause cannot pause video: $exception", warn = true)
        }
    }

    override fun onResume() {
        super.onResume()
        if (lastUserAction == ACTION_START) {
            try {
                videoPlayer.start()
            } catch (exception: IllegalStateException) {
                logMessage("onResume cannot resume video, trying reload: $exception", warn = true)
                try {
                    if (arResourcesProvider.isArLoaded()) {
                        loadVideo()
                        videoPlayer.start()
                    } else {
                        arResourcesProvider.addArLoadedListener(object :
                            ArResourcesProvider.ArLoadedListener {
                            override fun onArLoaded(firstTime: Boolean) {
                                loadVideo()
                                videoPlayer.start()
                                arResourcesProvider.removeArLoadedListener(this)
                            }
                        })
                    }
                } catch (exception: IllegalStateException) {
                    logMessage("reload onResume Failed: $exception", warn = true)
                }
            }
        }
    }

    // destroying media player when node is detached (e.g. on scene change)
    override fun onDestroy() {
        super.onDestroy()
        if (subtitles != null) {
            contentNode.removeChild(subtitles)
            subtitles = null
        }
        videoPlayer.release()
        renderableLoadRequest?.let {
            videoRenderableLoader.cancel(it)
        }
    }

    private fun applyClipBounds() {
        nodeClipper.applyClipBounds(this, clipBounds)
    }

    private fun createSubtitles() {
        if (subtitles == null) {
            subtitles = UiSubtitlesNode(
                props = getSubtitleProps(),
                context = context,
                viewRenderableLoader = viewRenderableLoader,
                nodeClipper = nodeClipper,
                fontProvider = fontProvider
            )
            contentNode.addChild(subtitles)
            subtitles?.build()
        }
    }

    private fun getSubtitleProps(): JavaOnlyMap {
        val videoSize = readVideoSize(properties) ?: Vector2(initialWidth, initialHeight)

        // we have to apply reverted content scale, so the subtitles are not stretched
        val contentScale = contentNode.localScale
        val scaleX = if (contentScale.x > 0) 1f / contentScale.x else 0f
        val scaleY = if (contentScale.y > 0) 1f / contentScale.y else 0f
        val scaleZ = if (contentScale.x > 0) 1f / contentScale.z else 0f

        return JavaOnlyMap.of(
            UiTextNode.PROP_TEXT_SIZE, 0.1,
            PROP_LOCAL_POSITION, JavaOnlyArray.of(0f, SUBTITLES_MARGIN_BOTTOM, 0.01f),
            PROP_LOCAL_SCALE, JavaOnlyArray.of(scaleX, scaleY, scaleZ),
            PROP_ALIGNMENT, "bottom-center",
            PROP_TEXT_ALIGNMENT, "bottom-center",
            PROP_BOUNDS_SIZE, JavaOnlyMap.of(
                PROP_BOUNDS_SIZE, JavaOnlyArray.of(videoSize.x, videoSize.y)
            )
        )
    }

    private fun updateSubtitlesProps() {
        subtitles?.update(getSubtitleProps())
    }

    private fun getTimedTextPath(props: Bundle): Uri? {
        if (props.containsKey(PROP_TIMED_TEXT_PATH)) {
            return props.readFilePath(PROP_TIMED_TEXT_PATH, context)
        }
        return null
    }

    private fun setSeekTo(props: Bundle) {
        if (props.containsKey(PROP_SEEK_TO)) {
            videoPlayer.seekTo(props.getDouble(PROP_SEEK_TO).toInt())
        }
    }

    private fun loadVideo() {
        if (!arResourcesProvider.isArLoaded()) {
            // ar must be load to create ExternalTexture object
            return
        }

        val videoUri = properties.readFilePath(PROP_VIDEO_PATH, context)
        if (videoUri != null) {
            subtitles?.update(
                // add empty text to hide last subtitles
                JavaOnlyMap.of(UiTextNode.PROP_TEXT, "")
            )
            val texture = ExternalTexture()
            try {
                val timedTextUri = getTimedTextPath(properties)
                if (timedTextUri != null) {
                    videoPlayer.loadVideo(
                        uri = videoUri,
                        subtitlesPath = timedTextUri,
                        onSubtitleChangeListener = {
                            subtitles?.update(JavaOnlyMap.of(UiTextNode.PROP_TEXT, it))
                        },
                        surface = texture.surface,
                        onLoadedListener = {
                            onVideoPreparedListener?.invoke()
                            createSubtitles()
                        })
                } else {
                    if (subtitles != null) {
                        contentNode.removeChild(subtitles)
                        subtitles = null
                    }
                    videoPlayer.loadVideo(
                        uri = videoUri,
                        surface = texture.surface,
                        onLoadedListener = {
                            onVideoPreparedListener?.invoke()
                        })
                }

            } catch (exception: Exception) {
                logMessage("video load exception: $exception", warn = true)
            }

            // cancel previous load request if exists
            renderableLoadRequest?.let {
                videoRenderableLoader.cancel(it)
            }

            renderableLoadRequest = RenderableLoadRequest<ModelRenderable> { result ->
                if (result is DataResult.Success) {
                    result.data.material.setExternalTexture("videoTexture", texture)
                    if (isVisible) {
                        contentNode.renderable = result.data
                    }
                    renderableCopy = result.data
                    applyClipBounds()
                }
            }.also {
                videoRenderableLoader.loadRenderable(it)
            }

        }
    }

    // changing video size by scaling the content node
    private fun setSize(props: Bundle) {
        readVideoSize(props)?.let { videoSize ->
            val scaleX = videoSize.x / initialWidth
            val scaleY = videoSize.y / initialHeight
            contentNode.localScale = Vector3(scaleX, scaleY, 1F)
            applyClipBounds()
        }
    }

    private fun readVideoSize(props: Bundle): Vector2? {
        val sizeArray = props.getSerializable(PROP_SIZE) as? ArrayList<Double>
        if (sizeArray != null && sizeArray.size == 2) {
            val widthMeters = sizeArray[0].toFloat()
            val heightMeters = sizeArray[1].toFloat()
            return Vector2(widthMeters, heightMeters)
        }
        return null
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