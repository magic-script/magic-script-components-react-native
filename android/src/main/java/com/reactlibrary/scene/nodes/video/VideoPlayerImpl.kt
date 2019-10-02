/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
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

package com.reactlibrary.scene.nodes.video

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import com.reactlibrary.utils.logMessage

class VideoPlayerImpl(private val context: Context) : VideoPlayer, MediaPlayer.OnPreparedListener {

    private val mediaPlayer = MediaPlayerPool.createMediaPlayer()

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

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer.seekTo(0) // to show first video frame instead of black texture
        // TODO listener callback
    }

    override fun loadVideo(uri: Uri, surface: Surface, onLoadedListener: () -> Unit) {
        val path = uri.toString()
        try {
            if (path.startsWith("http")) {
                mediaPlayer.setDataSource(path) // load from URL
            } else {
                // load the video from a local directory, e.g. from res/raw
                mediaPlayer.setDataSource(context, uri)
            }
            mediaPlayer.setSurface(surface)
            mediaPlayer.setOnPreparedListener(this)
            mediaPlayer.prepareAsync() // loading the video asynchronously
        } catch (exception: Exception) {
            logMessage("video player exception: $exception", warn = true)
        }
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun release() {
        mediaPlayer.release()
    }
}