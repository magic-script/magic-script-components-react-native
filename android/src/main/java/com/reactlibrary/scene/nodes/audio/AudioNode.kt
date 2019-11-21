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

package com.reactlibrary.scene.nodes.audio

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.vr.sdk.audio.GvrAudioEngine
import com.google.vr.sdk.audio.GvrAudioEngine.MaterialName.CURTAIN_HEAVY
import com.google.vr.sdk.audio.GvrAudioEngine.MaterialName.PLASTER_SMOOTH
import com.reactlibrary.scene.nodes.audio.model.SpatialSoundDistance
import com.reactlibrary.scene.nodes.audio.model.SpatialSoundPosition
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.FileDownloader
import com.reactlibrary.utils.PropertiesReader.Companion.readFilePath
import com.reactlibrary.utils.ifContains
import com.reactlibrary.utils.putDefault
import java.io.File


open class AudioNode @JvmOverloads constructor(
    initProps: ReadableMap,
    private val context: Context,
    private val audioEngine: GvrAudioEngine = GvrAudioEngine(
        context,
        GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY
    ),
    private val fileDownloader: FileDownloader = FileDownloader(context)
) : TransformNode(initProps, false, false) {

    companion object {
        const val PROP_FILE_NAME = "fileName"
        const val PROP_ACTION = "action"
        const val PROP_SOUND_LOOPING = "soundLooping"
        const val PROP_SOUND_MUTE = "soundMute"
        const val PROP_SOUND_VOLUME_LINEAR = "soundVolumeLinear"
        const val PROP_SPATIAL_SOUND_ENABLE = "spatialSoundEnable"
        const val PROP_SPATIAL_SOUND_DISTANCE = "spatialSoundDistance"
        const val PROP_SPATIAL_SOUND_POSITION = "spatialSoundPosition"

        const val DEFAULT_SOUND_LOOPING = false
        const val DEFAULT_SOUND_MUTE = false
        const val DEFAULT_SPATIAL_SOUND_ENABLE = false
    }

    init {
        properties.apply {
            putDefault(PROP_SOUND_LOOPING, DEFAULT_SOUND_LOOPING)
            putDefault(PROP_SOUND_MUTE, DEFAULT_SOUND_MUTE)
            putDefault(PROP_SPATIAL_SOUND_ENABLE, DEFAULT_SPATIAL_SOUND_ENABLE)
        }
    }

    private var file: File? = null
    private var looping: Boolean = DEFAULT_SOUND_LOOPING
    private var sourceId: Int = GvrAudioEngine.INVALID_ID
    private var spatialSoundEnabled = DEFAULT_SPATIAL_SOUND_ENABLE
    private var volume = 0f

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        applyValues(props)

        if (spatialSoundEnabled) {
            applySpatialSoundProperties(props)
        }

        applyActions(props)
    }

    override fun onDestroy() {
        if (sourceId != GvrAudioEngine.INVALID_ID) {
            audioEngine.stopSound(sourceId)
        }
        file?.let { file ->
            audioEngine.unloadSoundFile(file.path)
        }

        super.onDestroy()
    }

    private fun applyValues(props: Bundle) {
        props.run {
            val filePath = readFilePath(this, PROP_FILE_NAME, context)
            if (filePath != null) {
                fileDownloader.downloadFile(filePath.toString()) { file ->
                    this@AudioNode.file = file
                    setupAudioEngine()
                }
            }

            ifContains(PROP_SOUND_LOOPING) { isLooping: Boolean ->
                looping = isLooping
            }

            ifContains(PROP_SPATIAL_SOUND_ENABLE) { isSpatialSoundEnabled: Boolean ->
                spatialSoundEnabled = isSpatialSoundEnabled
                setupAudioEngine()
            }

            ifContains(PROP_SOUND_VOLUME_LINEAR) { volume: Double ->
                this@AudioNode.volume = volume.toFloat()
                audioEngine.setSoundVolume(sourceId, this@AudioNode.volume)
            }

            ifContains(PROP_SOUND_MUTE) { isMuted: Boolean ->
                if (isMuted) {
                    audioEngine.setSoundVolume(sourceId, 0f)
                } else {
                    audioEngine.setSoundVolume(sourceId, volume)
                }

            }
        }
    }

    private fun setupAudioEngine() {
        Thread(
            Runnable {
                file?.let { file ->
                    if (sourceId != GvrAudioEngine.INVALID_ID) {
                        audioEngine.stopSound(sourceId)
                        audioEngine.unloadSoundFile(file.path)
                    }
                    audioEngine.preloadSoundFile(file.path)
                    sourceId = if (spatialSoundEnabled) {
                        audioEngine.createSoundObject(file.path)
                    } else {
                        audioEngine.createStereoSound(file.path)
                    }
                    audioEngine.setRoomProperties(
                        15f,
                        15f,
                        15f,
                        PLASTER_SMOOTH,
                        PLASTER_SMOOTH,
                        CURTAIN_HEAVY
                    )
                    audioEngine.playSound(sourceId, looping)
                }
            }
        ).start()
    }

    private fun applyActions(props: Bundle) {
        props.ifContains(PROP_ACTION) { action: String ->
            when (action) {
                AudioAction.STOP -> audioEngine.stopSound(sourceId)
                AudioAction.PAUSE -> audioEngine.pause()
                AudioAction.RESUME -> audioEngine.resume()
                AudioAction.START -> audioEngine.playSound(sourceId, looping)
            }
        }
    }

    private fun applySpatialSoundProperties(props: Bundle) {
        props.ifContains(PROP_SPATIAL_SOUND_POSITION) { spatialSoundPosition: SpatialSoundPosition ->
            spatialSoundPosition.channelPosition?.let { channelPosition ->

                audioEngine.setSoundObjectPosition(
                    sourceId,
                    channelPosition.x,
                    channelPosition.y,
                    channelPosition.z
                )
            }
        }

        props.ifContains(PROP_SPATIAL_SOUND_DISTANCE) { spatialSoundDistance: SpatialSoundDistance ->
            audioEngine.setSoundObjectDistanceRolloffModel(
                sourceId,
                spatialSoundDistance.rolloffFactor,
                spatialSoundDistance.minDistance,
                spatialSoundDistance.maxDistance
            )
        }
    }
}
