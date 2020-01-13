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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.VideoRenderableLoader
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.video.VideoNode
import com.magicleap.magicscript.scene.nodes.video.VideoPlayer
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class VideoNodeTest {

    private lateinit var videoNode: VideoNode
    private lateinit var context: Context
    private lateinit var videoPlayer: VideoPlayer
    private lateinit var videoReadableLoader: VideoRenderableLoader

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.videoPlayer = mock()
        this.videoReadableLoader = mock()

        val videoUri = "http://video.com/sample.mp4"
        val props = reactMapOf(VideoNode.PROP_VIDEO_PATH, videoUri)
        videoNode = VideoNode(props, context, videoPlayer, videoReadableLoader)
        videoNode.build()
    }

    @Test
    fun `should have default volume`() {
        val volume = videoNode.getProperty(VideoNode.PROP_VOLUME)

        assertEquals(VideoNode.DEFAULT_VOLUME, volume)
    }

    @Test
    fun `should adjust content node scale when size property sent`() {
        val size = reactArrayOf(3, 2)
        val props = reactMapOf(VideoNode.PROP_SIZE, size)

        videoNode.update(props)

        assertEquals(Vector3(3F, 2F, 1F), videoNode.contentNode.localScale)
    }

    @Test
    fun `should start player when start action sent and player is ready`() {
        whenever(videoPlayer.isReady).thenReturn(true)
        val props = reactMapOf(VideoNode.PROP_ACTION, VideoNode.ACTION_START)

        videoNode.update(props)

        verify(videoPlayer).start()
    }

    @Test
    fun `should not be possible to start player when it is not ready`() {
        whenever(videoPlayer.isReady).thenReturn(false)
        val props = reactMapOf(VideoNode.PROP_ACTION, VideoNode.ACTION_START)

        videoNode.update(props)

        verify(videoPlayer, never()).start()
    }

    @Test
    fun `should pause player when pause action sent and is playing`() {
        whenever(videoPlayer.isReady).thenReturn(true)
        whenever(videoPlayer.isPlaying).thenReturn(true)
        val props = reactMapOf(VideoNode.PROP_ACTION, VideoNode.ACTION_PAUSE)

        videoNode.update(props)

        verify(videoPlayer).pause()
    }

    @Test
    fun `should stop player when stop action sent and is playing`() {
        whenever(videoPlayer.isReady).thenReturn(true)
        whenever(videoPlayer.isPlaying).thenReturn(true)
        val props = reactMapOf(VideoNode.PROP_ACTION, VideoNode.ACTION_STOP)

        videoNode.update(props)

        verify(videoPlayer).stop()
    }

    @Test
    fun `should set looping when looping property updated to true`() {
        val props = reactMapOf(VideoNode.PROP_LOOPING, true)

        videoNode.update(props)

        verify(videoPlayer).looping = true
    }

    @Test
    fun `should pause player when node is paused`() {
        whenever(videoPlayer.isPlaying).thenReturn(true)

        videoNode.onPause()

        verify(videoPlayer).pause()
    }

    @Test
    fun `should resume player when node is resumed after pausing and last user action is start`() {
        whenever(videoPlayer.isReady).thenReturn(true)
        whenever(videoPlayer.isPlaying).thenReturn(false)
        val props = reactMapOf(VideoNode.PROP_ACTION, VideoNode.ACTION_START)
        videoNode.update(props)

        videoNode.onPause()
        videoNode.onResume()

        verify(videoPlayer, times(2)).start()
    }

    @Test
    fun `should release player when node is destroyed`() {
        videoNode.onDestroy()

        verify(videoPlayer).release()
    }

}