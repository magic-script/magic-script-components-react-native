/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.video

import android.content.Context
import android.media.MediaFormat
import android.media.MediaPlayer
import android.media.MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP
import android.media.MediaPlayer.TrackInfo
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Surface


class VideoPlayerImpl(private val context: Context) : VideoPlayer, MediaPlayer.OnPreparedListener {

    private val mediaPlayer = GlobalMediaPlayerPool.createMediaPlayer()
    private var onLoadedListener: (() -> Unit)? = null
    private var ready = false
    private var timedTextUri: Uri? = null
    private var onSubtitleChangeListener: ((String) -> Unit)? = null

    override var volume: Float = 1.0F
        set(value) {
            field = value.coerceIn(0F, 1F)
            mediaPlayer.setVolume(field, field)
        }

    override var looping: Boolean = false
        set(value) {
            field = value
            mediaPlayer.isLooping = value
        }

    override val isPlaying: Boolean
        @Throws(IllegalStateException::class)
        get() = mediaPlayer.isPlaying


    override val isReady: Boolean
        get() = ready

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer.seekTo(0) // to show first video frame instead of black texture
        ready = true
        onLoadedListener?.invoke()
    }

    @Throws(Exception::class)
    override fun loadVideo(uri: Uri, surface: Surface, onLoadedListener: () -> Unit) {
        ready = false
        mediaPlayer.reset()
        val path = uri.toString()
        if (path.startsWith("http")) {
            mediaPlayer.setDataSource(path) // load from URL
        } else {
            // load the video from a local directory, e.g. from res/raw
            mediaPlayer.setDataSource(context, uri)
        }
        this.onLoadedListener = onLoadedListener
        mediaPlayer.setSurface(surface)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.prepareAsync() // loading the video asynchronously
    }

    @Throws(IllegalStateException::class)
    override fun start() {
        mediaPlayer.start()
    }

    @Throws(IllegalStateException::class)
    override fun pause() {
        mediaPlayer.pause()
    }

    @Throws(IllegalStateException::class)
    override fun stop() {
        mediaPlayer.pause()
        mediaPlayer.seekTo(0)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun addTimedTextPath(path: Uri, onTextChangedListener: (String) -> Unit) {
        mediaPlayer.setOnTimedTextListener(null)
        if(Build.VERSION.SDK_INT >= 28) {
            mediaPlayer.addTimedTextSource(context, path, MediaFormat.MIMETYPE_TEXT_SUBRIP)
        } else {
            mediaPlayer.addTimedTextSource(context, path, MEDIA_MIMETYPE_TEXT_SUBRIP)
        }
        Log.d("VideoNode", "mediaPlayer track info: ${mediaPlayer.trackInfo}")
        val textTrackIndex: Int = findTrackIndexFor(mediaPlayer.trackInfo)
        if(textTrackIndex >= 0) {
            Log.d("VideoNode", "mediaPlayer selected track index: $textTrackIndex")
            mediaPlayer.selectTrack(textTrackIndex)
        }
        mediaPlayer.setOnTimedTextListener { _, text ->
            Log.d("VideoNode", "timed text changed: $text")
            onTextChangedListener(text.text)
        }
    }

    private fun findTrackIndexFor(trackInfo: Array<TrackInfo>): Int {
        return trackInfo.find { it.trackType == TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT }?.trackType ?: -1
    }
    override fun seekTo(millis: Int) {
        mediaPlayer.seekTo(millis)
    }

    override fun clearTimedTextListener() {
        mediaPlayer.setOnTimedTextListener(null)
    }
}