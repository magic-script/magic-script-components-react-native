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

import com.google.ar.sceneform.math.Vector3
import com.google.vr.sdk.audio.GvrAudioEngine
import com.google.vr.sdk.audio.GvrAudioEngine.MaterialName.CURTAIN_HEAVY
import com.google.vr.sdk.audio.GvrAudioEngine.MaterialName.PLASTER_SMOOTH
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundDistance
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class VrAudioEngine(
    private val executorService: ExecutorService,
    private var engine: ExternalAudioEngine
) : AudioEngine {

    private var engineReady: Boolean = false
    private var spatialSound: Boolean = false
    private var spatialSoundDistance: SpatialSoundDistance? = null
    private var channelPosition: Vector3? = null
    private var sourceId = GvrAudioEngine.INVALID_ID
    private var file: File? = null
    private var volume: Float = 0f
    private var lastAction: String = AudioAction.STOP
    private var looping: Boolean = false
    private var engineFuture: Future<*>? = null

    override fun stop() {
        lastAction = AudioAction.STOP
        if (isLoaded()) {
            engine.stopSound(sourceId)
        }
    }

    override fun unload() {
        file?.let {
            engine.stopSound(sourceId)
            engine.unloadSoundFile(it.path)
        }
    }

    override fun setSoundVolume(volume: Float) {
        this.volume = volume
        if (isLoaded()) {
            engine.setSoundVolume(sourceId, volume)
        }
    }

    override fun mute(muted: Boolean) {
        if (isLoaded()) {
            if (muted) {
                engine.setSoundVolume(sourceId, 0f)
            } else {
                engine.setSoundVolume(sourceId, volume)
            }
        }
    }

    override fun load(file: File) {
        this.file = file
        engineFuture = executorService.submit {
            setupAudioEngine(file)
        }
    }

    override fun play() {
        lastAction = AudioAction.START
        if (isLoaded()) {
            engine.playSound(sourceId, looping)
        }
    }

    override fun pause() {
        lastAction = AudioAction.PAUSE
        if (isLoaded()) {
            engine.pause()
        }
    }

    override fun resume() {
        lastAction = AudioAction.RESUME
        if (isLoaded()) {
            engine.resume()
        }
    }

    override fun spatialSoundEnabled(spatialSoundEnabled: Boolean) {
        if (spatialSoundEnabled != spatialSound) {
            this.spatialSound = spatialSoundEnabled
            file?.let { file ->
                if (engineReady) {
                    setupAudioEngine(file)
                }
            }
        }
    }

    override fun setSoundObjectPosition(channelPosition: Vector3) {
        this.channelPosition = channelPosition
        if (isLoaded()) {
            engine.setSoundObjectPosition(
                sourceId,
                channelPosition.x,
                channelPosition.y,
                channelPosition.z
            )
        }
    }

    override fun setSoundObjectDistanceRolloffModel(spatialSoundDistance: SpatialSoundDistance) {
        this.spatialSoundDistance = spatialSoundDistance
        if (isLoaded()) {
            engine.setSoundObjectDistanceRolloffModel(
                sourceId,
                spatialSoundDistance.rolloffFactor,
                spatialSoundDistance.minDistance,
                spatialSoundDistance.maxDistance
            )
        }
    }

    override fun looping(looping: Boolean) {
        this.looping = looping
    }

    override fun onDestroy() {
        engineFuture?.cancel(true)
        engineReady = false
        lastAction = AudioAction.STOP
        unload()
    }

    private fun setupAudioEngine(file: File) {
        unload()
        engine.preloadSoundFile(file.path)
        createAudioSource(file)
        engine.setRoomProperties(
            15f,
            15f,
            15f,
            PLASTER_SMOOTH,
            PLASTER_SMOOTH,
            CURTAIN_HEAVY
        )
        autoplayAudio()
        engineReady = true
    }

    private fun autoplayAudio() {
        if (AudioAction.shouldPlay(lastAction)) {
            play()
        }
    }

    private fun createAudioSource(file: File) {
        sourceId = if (spatialSound) {
            engine.createSoundObject(file.path)
        } else {
            engine.createStereoSound(file.path)
        }
    }

    private fun isLoaded() = sourceId != GvrAudioEngine.INVALID_ID
}