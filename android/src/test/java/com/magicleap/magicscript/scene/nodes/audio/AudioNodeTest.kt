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

    private val FILE_URL = "http://localhost:8081/assets/resources/bg_stereo.mp3"
    private val audioEngine: AudioEngine = mock()
    private lateinit var audioProvider: AudioFileProvider
    private lateinit var tested: AudioNode

    @Before
    fun setup() {
        audioProvider = spy(object : AudioFileProvider {
            override fun provideFile(uri: Uri, result: (File) -> Unit) {
                result.invoke(File(""))
            }

            override fun onDestroy() {}
        })

        tested = AudioNode(
            initProps = reactMapOf(),
            context = ApplicationProvider.getApplicationContext(),
            audioEngine = audioEngine,
            fileProvider = audioProvider
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

        verify(audioProvider).provideFile(eq(Uri.parse(FILE_URL)), any())
    }

    @Test
    fun `should load audio file when path updated`() {
        tested.update(
            reactMapOf().fileName(FILE_URL)
        )

        verify(audioEngine).load(any())
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
    fun `should pause audio when node is paused`() {
        tested.onPause()

        verify(audioEngine).pause()
    }

    @Test
    fun `should resume audio when node is resumed after pausing and last user action is start`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.START)
        )

        tested.onPause()
        tested.onResume()

        verify(audioEngine).resume()
    }

    @Test
    fun `should resume audio when node is resumed after pausing and last user action is resume`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.PAUSE)
        )

        tested.update(
            reactMapOf()
                .action(AudioAction.RESUME)
        )

        tested.onPause()
        tested.onResume()

        verify(audioEngine, times(2)).resume()
    }

    @Test
    fun `should not resume audio when node is resumed after pausing and last user action is pause`() {
        tested.update(
            reactMapOf()
                .action(AudioAction.START)
        )

        tested.update(
            reactMapOf()
                .action(AudioAction.PAUSE)
        )

        tested.onPause()
        tested.onResume()

        verify(audioEngine, never()).resume()
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

        verify(audioProvider).onDestroy()
        verify(audioEngine).onDestroy()
    }
}
