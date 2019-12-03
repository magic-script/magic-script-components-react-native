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
package com.magicleap.magicscript.scene.nodes.audio

import androidx.test.core.app.ApplicationProvider
import com.google.vr.sdk.audio.GvrAudioEngine
import com.magicleap.magicscript.*
import com.magicleap.magicscript.utils.FileDownloader
import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class AudioNodeTest {

    val FILE_URL = "http://localhost:8081/assets/resources/bg_stereo.mp3"
    val FILE_LOCAL_PATH = "/asd/dsa/bg_stereo.mp3"
    val TIMEOUT = 500L

    lateinit var downloadedFile: File
    lateinit var audioEngine: GvrAudioEngine
    lateinit var fileDownloader: FileDownloader
    lateinit var tested: AudioNode

    @Before
    fun setup() {
        audioEngine = mock {
            on { createSoundObject(any()) } doReturn 5
            on { createStereoSound(any()) } doReturn 5
        }

        downloadedFile = mock {
            on { path } doReturn FILE_LOCAL_PATH
        }

        fileDownloader = mock {
            on { downloadFile(any(), any()) } doAnswer {
                val argument = it.arguments[1]
                val result = argument as ((File) -> Unit)
                result.invoke(downloadedFile)
                true
            }
        }

        tested = AudioNode(
            initProps = reactMapOf(),
            context = ApplicationProvider.getApplicationContext(),
            audioEngine = audioEngine,
            fileDownloader = fileDownloader
        )
    }

    @After
    fun validate() {
        tested.onDestroy()
        Mockito.validateMockitoUsage()
    }

    @Test
    fun `should apply correct default properties`() {
        AudioNode.DEFAULT_SOUND_LOOPING shouldBe false
        AudioNode.DEFAULT_SOUND_MUTE shouldBe false
        AudioNode.DEFAULT_SPATIAL_SOUND_ENABLE shouldBe false

        tested.getProperty(AudioNode.PROP_SOUND_LOOPING) shouldEqual AudioNode.DEFAULT_SOUND_LOOPING
        tested.getProperty(AudioNode.PROP_SOUND_MUTE) shouldEqual AudioNode.DEFAULT_SOUND_MUTE
        tested.getProperty(AudioNode.PROP_SPATIAL_SOUND_ENABLE) shouldEqual AudioNode.DEFAULT_SPATIAL_SOUND_ENABLE
    }

    @Test
    fun `should download file when file path is updated`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
        )

        verify(fileDownloader, timeout(TIMEOUT)).downloadFile(eq(FILE_URL), any())
    }

    @Test
    fun `should preload audio when when file is ready`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
        )

        verify(audioEngine, timeout(TIMEOUT)).preloadSoundFile(any())
    }

    @Test
    fun `should create stereo audio for non spatial`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
                .spatialSoundEnable(false)
        )

        verify(audioEngine, timeout(TIMEOUT).atLeastOnce()).createStereoSound(eq(FILE_LOCAL_PATH))
    }

    @Test
    fun `should unload and stop and preload audio when spatial changed`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
                .spatialSoundEnable(false)
        )

        tested.update(
            reactMapOf()
                .spatialSoundEnable(true)
        )

        verify(audioEngine, timeout(TIMEOUT)).stopSound(5)
        verify(audioEngine, timeout(TIMEOUT)).unloadSoundFile(FILE_LOCAL_PATH)
        verify(audioEngine, timeout(TIMEOUT).atLeastOnce()).preloadSoundFile(FILE_LOCAL_PATH)
    }

    @Test
    fun `should create mono audio when spatial`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
                .spatialSoundEnable(true)
        )

        verify(audioEngine, timeout(TIMEOUT).atLeastOnce()).createSoundObject(eq(FILE_LOCAL_PATH))
    }

    @Test
    fun `should autoplay when last action is start`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
                .action(AudioAction.START)
        )

        tested.update(
            reactMapOf()
                .spatialSoundEnable(true)
        )

        verify(audioEngine, timeout(TIMEOUT).atLeast(2)).playSound(5, false)
    }

    @Test
    fun `should autoplay when last action is resume`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
                .action(AudioAction.RESUME)
        )

        tested.update(
            reactMapOf()
                .spatialSoundEnable(true)
        )

        verify(audioEngine, timeout(TIMEOUT).atLeast(2)).playSound(5, false)
    }

    @Test
    fun `should play with looping`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
                .spatialSoundEnable(true)
                .soundLooping(true)
        )

        verify(audioEngine, timeout(TIMEOUT)).playSound(5, true)
    }

    @Test
    fun `should play without looping`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
                .spatialSoundEnable(true)
                .soundLooping(false)
        )

        verify(audioEngine, timeout(TIMEOUT)).playSound(5, false)
    }

    @Test
    fun `should start audio`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.START)
        )

        verify(audioEngine, timeout(TIMEOUT).atLeastOnce()).playSound(any(), any())
    }

    @Test
    fun `should stop audio`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.STOP)
        )
        verify(audioEngine, timeout(TIMEOUT).atLeastOnce()).stopSound(any())
    }

    @Test
    fun `should resume audio`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.RESUME)
        )
        verify(audioEngine, timeout(TIMEOUT).atLeastOnce()).resume()
    }

    @Test
    fun `should pause audio`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.PAUSE)
        )
        verify(audioEngine, timeout(TIMEOUT).atLeastOnce()).pause()
    }

    @Test
    fun `should apply SpatialSoundPosition`() {
        tested.update(
            reactMapOf()
                .spatialSoundEnable(true)
                .spatialSoundPosition(4, arrayOf(0.0, 1.0, 2.0))
        )

        verify(audioEngine).setSoundObjectPosition(-1, 0f, 1f, 2f)
    }

    @Test
    fun `should apply SpatialSoundDistance`() {
        tested.update(
            reactMapOf()
                .spatialSoundEnable(true)
                .spatialSoundDistance(4, 1.0, 3.0, 2)
        )

        verify(audioEngine).setSoundObjectDistanceRolloffModel(-1, 2, 1f, 3f)
    }

    @Test
    fun `onDestroy should destroy file downloader and unload sound`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
                .action(AudioAction.START)
        )

        tested.onDestroy()

        verify(fileDownloader).onDestroy()
        verify(audioEngine, timeout(TIMEOUT)).unloadSoundFile(any())
    }


}
