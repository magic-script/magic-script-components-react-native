package com.magicleap.magicscript.scene.nodes.audio

import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundDistance
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.util.concurrent.ExecutorService


class VrAudioEngineTest {

    val path = "asd"
    val file = File(path)

    val executor = mock<ExecutorService> {
        on { submit(any()) } doAnswer
                { invocation ->
                    val args = invocation.arguments
                    val runnable = args[0] as Runnable
                    runnable.run()
                    null
                }
    }
    val engine: ExternalAudioEngine = mock()

    val tested = VrAudioEngine(executorService = executor, engine = engine)

    @Test
    fun `when load file should prepare engine`() {
        tested.load(file)

        verify(executor).submit(any())
        verify(engine).preloadSoundFile(path)
        verify(engine).stopSound(any())
        verify(engine).unloadSoundFile(path)
    }

    @Test
    fun `when sound is spatial should create mono file`() {
        tested.spatialSoundEnabled(true)
        tested.load(file)

        verify(engine).createSoundObject(path)
    }

    @Test
    fun `when sound is not spatial should create stereo file`() {
        tested.spatialSoundEnabled(false)
        tested.load(file)

        verify(engine).createStereoSound(path)
    }

    @Test
    fun `should autoplay if last action was PLAY`() {
        tested.play()
        tested.load(file)

        verify(engine).playSound(any(), any())
    }

    @Test
    fun `should autoplay if last action was RESUME`() {
        tested.resume()
        tested.load(file)

        verify(engine).playSound(any(), any())
    }

    @Test
    fun `should not autoplay if last action was PAUSE`() {
        tested.pause()
        tested.load(file)

        verify(engine, never()).playSound(any(), any())
    }


    @Test
    fun `should not autoplay if last action was STOP`() {
        tested.stop()
        tested.load(file)

        verify(engine, never()).playSound(any(), any())
    }

    @Test
    fun `should not play when file is not loaded`() {
        tested.play()

        verify(engine, never()).playSound(any(), any())
    }


    @Test
    fun `should not resume when file is not loaded`() {
        tested.resume()

        verify(engine, never()).playSound(any(), any())
    }


    @Test
    fun `should not stop when file is not loaded`() {
        tested.stop()

        verify(engine, never()).playSound(any(), any())
    }


    @Test
    fun `should not pause when file is not loaded`() {
        tested.pause()

        verify(engine, never()).playSound(any(), any())
    }

    @Test
    fun `unload should stop and unload file`() {
        tested.load(file)
        Mockito.reset(engine)

        tested.unload()

        verify(engine).stopSound(any())
        verify(engine).unloadSoundFile(path)
    }

    @Test
    fun `should set volume`() {
        tested.load(file)
        tested.setSoundVolume(2f)

        verify(engine).setSoundVolume(any(), eq(2f))
    }

    @Test
    fun `if muted should set volume to 0`() {
        tested.load(file)
        tested.setSoundVolume(2f)
        tested.mute(true)

        verify(engine).setSoundVolume(any(), eq(0f))
    }

    @Test
    fun `when unmuted should apply previous volume`() {
        tested.load(file)
        tested.setSoundVolume(2f)
        tested.mute(true)

        Mockito.reset(engine)
        tested.mute(false)

        verify(engine).setSoundVolume(any(), eq(2f))
    }

    @Test
    fun `when spatial sound changed should reload file`() {
        tested.load(file)
        tested.spatialSoundEnabled(true)

        Mockito.reset(engine)
        tested.spatialSoundEnabled(false)

        verify(engine).preloadSoundFile(path)
        verify(engine).stopSound(any())
        verify(engine).unloadSoundFile(path)
        verify(engine).preloadSoundFile(path)
    }

    @Test
    fun `should set setSoundObjectPosition`() {
        tested.load(file)
        tested.setSoundObjectPosition(Vector3(1f, 2f, 3f))

        verify(engine).setSoundObjectPosition(any(), eq(1f), eq(2f), eq(3f))
    }

    @Test
    fun `should set SoundObjectDistanceRolloffModel`() {
        tested.load(file)
        tested.setSoundObjectDistanceRolloffModel(SpatialSoundDistance(4.0, 1f, 2f, 5))

        verify(engine).setSoundObjectDistanceRolloffModel(any(), eq(5), eq(1f), eq(2f))
    }

    @Test
    fun `by default play without looping`() {
        tested.load(file)
        tested.play()

        verify(engine).playSound(any(), looping = eq(false))
    }

    @Test
    fun `should play with looping`() {
        tested.load(file)
        tested.looping(true)
        tested.play()

        verify(engine).playSound(any(), looping = eq(true))
    }

    @Test
    fun `should play without looping`() {
        tested.load(file)
        tested.looping(false)
        tested.play()

        verify(engine).playSound(any(), looping = eq(false))
    }

    @Test
    fun `should stop and unload audio onDestroy`() {
        tested.load(file)
        tested.play()

        Mockito.reset(engine)

        tested.onDestroy()

        verify(engine).stopSound(any())
        verify(engine).unloadSoundFile(path)
    }
}

