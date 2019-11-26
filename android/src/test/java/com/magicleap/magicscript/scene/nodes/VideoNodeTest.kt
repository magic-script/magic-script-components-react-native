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
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.magicleap.magicscript.scene.nodes.video.VideoNode
import com.magicleap.magicscript.scene.nodes.video.VideoPlayer
import com.magicleap.magicscript.ar.VideoRenderableLoader
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
        val props = JavaOnlyMap.of(VideoNode.PROP_VIDEO_PATH, videoUri)
        videoNode = VideoNode(props, context, videoPlayer, videoReadableLoader)
        videoNode.build()
    }

    @Test
    fun shouldHaveDefaultVolume() {
        val volume = videoNode.getProperty(VideoNode.PROP_VOLUME)

        assertEquals(VideoNode.DEFAULT_VOLUME, volume)
    }

    @Test
    fun shouldAdjustContentNodeScaleWhenSizePropertySent() {
        val size = JavaOnlyArray.of(3, 2)
        val props = JavaOnlyMap.of(VideoNode.PROP_SIZE, size)

        videoNode.update(props)

        assertEquals(Vector3(3F, 2F, 1F), videoNode.contentNode.localScale)
    }

    @Test
    fun shouldStartPlayerWhenStartActionSentAndPlayerIsReady() {
        whenever(videoPlayer.isReady).thenReturn(true)
        val props = JavaOnlyMap.of(VideoNode.PROP_ACTION, VideoNode.ACTION_START)

        videoNode.update(props)

        verify(videoPlayer).start()
    }

    @Test
    fun shouldNotStartPlayerWhenNotReady() {
        whenever(videoPlayer.isReady).thenReturn(false)
        val props = JavaOnlyMap.of(VideoNode.PROP_ACTION, VideoNode.ACTION_START)

        videoNode.update(props)

        verify(videoPlayer, never()).start()
    }

    @Test
    fun shouldPausePlayerWhenPauseActionSentAndIsPlaying() {
        whenever(videoPlayer.isReady).thenReturn(true)
        whenever(videoPlayer.isPlaying).thenReturn(true)
        val props = JavaOnlyMap.of(VideoNode.PROP_ACTION, VideoNode.ACTION_PAUSE)

        videoNode.update(props)

        verify(videoPlayer).pause()
    }

    @Test
    fun shouldStopPlayerWhenStopActionSentAndIsPlaying() {
        whenever(videoPlayer.isReady).thenReturn(true)
        whenever(videoPlayer.isPlaying).thenReturn(true)
        val props = JavaOnlyMap.of(VideoNode.PROP_ACTION, VideoNode.ACTION_STOP)

        videoNode.update(props)

        verify(videoPlayer).stop()
    }

    @Test
    fun shouldSetLoopingWhenLoopingPropertySent() {
        val props = JavaOnlyMap.of(VideoNode.PROP_LOOPING, true)

        videoNode.update(props)

        verify(videoPlayer).looping = true
    }

    @Test
    fun shouldReleasePlayerWhenResourcesCleared() {
        videoNode.onDestroy()

        verify(videoPlayer).release()
    }

}