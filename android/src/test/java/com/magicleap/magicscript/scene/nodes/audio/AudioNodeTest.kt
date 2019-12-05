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

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.*
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundDistance
import com.magicleap.magicscript.utils.UriAudioProvider
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

    lateinit var downloadedFile: File
    var audioEngine: AudioEngine = mock()
    lateinit var fileDownloader: UriAudioProvider
    lateinit var tested: AudioNode

    @Before
    fun setup() {
        downloadedFile = mock {
            on { path } doReturn FILE_LOCAL_PATH
        }

        fileDownloader = mock {
            on { provideFile(any(), any()) } doAnswer {
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
            fileProvider = fileDownloader
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

        verify(fileDownloader).provideFile(eq(Uri.parse(FILE_URL)), any())
    }

    @Test
    fun `should load engine when file is downloaded`() {
        tested.update(
            reactMapOf()
                .fileName(FILE_URL)
        )

        verify(audioEngine).load(File(FILE_LOCAL_PATH))
    }

    @Test
    fun `should start audio`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.START)
        )

        verify(audioEngine).play()
    }

    @Test
    fun `should stop audio`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.STOP)
        )
        verify(audioEngine).stop()
    }

    @Test
    fun `should resume audio`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.RESUME)
        )
        verify(audioEngine).resume()
    }

    @Test
    fun `should pause audio`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.PAUSE)
        )
        verify(audioEngine).pause()
    }

    @Test
    fun `should apply SpatialSoundPosition`() {
        tested.update(
            reactMapOf()
                .spatialSoundEnable(true)
                .spatialSoundPosition(4, arrayOf(0.0, 1.0, 2.0))
        )

        verify(audioEngine).setSoundObjectPosition(Vector3(0f, 1f, 2f))
    }

    @Test
    fun `should apply SpatialSoundDistance`() {
        tested.update(
            reactMapOf()
                .spatialSoundEnable(true)
                .spatialSoundDistance(4, 1.0, 3.0, 2)
        )

        verify(audioEngine).setSoundObjectDistanceRolloffModel(
            SpatialSoundDistance(4.0, 1f, 3f, 2)
        )
    }

    @Test
    fun `onDestroy should destroy file downloader and audio engine`() {
        tested.onDestroy()

        verify(fileDownloader).onDestroy()
        verify(audioEngine).onDestroy()
    }
}
