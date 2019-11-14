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
import com.reactlibrary.utils.*
import com.reactlibrary.utils.PropertiesReader.Companion.readFilePath
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
        const val PROP_LOAD_FILE = "loadFile"
        const val PROP_ABSOLUTE_PATH = "absolutePath"
        const val PROP_DESCRIPTOR = "descriptor"
        //        const val PROP_BASE_PATH = "basePath"
        const val PROP_AUTO_DESTROY = "autoDestroy"
        const val PROP_DYNAMIC_DECODE = "dynamicDecode"

        const val PROP_ACTION = "action"
        const val PROP_SOUND_LOOPING = "soundLooping"
        const val PROP_SOUND_MUTE = "soundMute"
        const val PROP_SOUND_PITCH = "soundPitch"
        const val PROP_SOUND_VOLUME_LINEAR = "soundVolumeLinear"
        const val PROP_SPATIAL_SOUND_ENABLE = "spatialSoundEnable"
        //        const val PROP_STREAMED_FILE_OFFSET = "streamedFileOffset"
//        const val PROP_SPATIAL_SOUND_DIRECTION = "spatialSoundDirection"
//        const val PROP_SPATIAL_SOUND_DIRECTION_SEND_LEVELS = "spatialSoundDirectSendLevels"
        const val PROP_SPATIAL_SOUND_DISTANCE = "spatialSoundDistance"
        const val PROP_SPATIAL_SOUND_POSITION = "spatialSoundPosition"
//        const val PROP_SPATIAL_SOUND_RADIATION = "spatialSoundRadiation"
//        const val PROP_SPATIAL_ROOM_SEND_LEVELS = "spatialSoundRoomSendLevels"

        const val DEFAULT_LOAD_FILE = false
        const val DEFAULT_ABSOLUTE_PATH = false
        const val DEFAULT_DESCRIPTOR = -1.0
        const val DEFAULT_AUTO_DESTROY = false
        const val DEFAULT_DYNAMIC_DECODE = false

        const val DEFAULT_SOUND_LOOPING = false
        const val DEFAULT_SOUND_MUTE = false
        const val DEFAULT_SOUND_PITCH = 1.0
        const val DEFAULT_SPATIAL_SOUND_ENABLE = false
    }

    init {
        properties.apply {
            putDefault(PROP_LOAD_FILE, DEFAULT_LOAD_FILE)
            putDefault(PROP_ABSOLUTE_PATH, DEFAULT_ABSOLUTE_PATH)
            putDefault(PROP_DESCRIPTOR, DEFAULT_DESCRIPTOR)
            putDefault(PROP_AUTO_DESTROY, DEFAULT_AUTO_DESTROY)
            putDefault(PROP_DYNAMIC_DECODE, DEFAULT_DYNAMIC_DECODE)
            putDefault(PROP_SOUND_LOOPING, DEFAULT_SOUND_LOOPING)
            putDefault(PROP_SOUND_MUTE, DEFAULT_SOUND_MUTE)
            putDefault(PROP_SOUND_PITCH, DEFAULT_SOUND_PITCH)
            putDefault(PROP_SPATIAL_SOUND_ENABLE, DEFAULT_SPATIAL_SOUND_ENABLE)
        }
    }

    private var fileName: String? = null
    private var looping: Boolean = DEFAULT_SOUND_LOOPING
    private var sourceId: Int = GvrAudioEngine.INVALID_ID
    private var autoDestroy = DEFAULT_AUTO_DESTROY
    private var spatialSoundEnabled = DEFAULT_SPATIAL_SOUND_ENABLE
    private var volume = 0f

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        applyValues(props)
        applyActions(props)

        if (spatialSoundEnabled) {
            applySpatialSoundProperties()
        }
    }

    override fun onDestroy() {
        if (sourceId != GvrAudioEngine.INVALID_ID) {
            audioEngine.stopSound(sourceId)
        }
        audioEngine.pause()
        super.onDestroy()
    }

    private fun applyValues(props: Bundle) {
        props.run {
            val filePath = readFilePath(this, PROP_FILE_NAME, context)
            if (filePath != null) {
                fileDownloader.downloadFile(filePath.toString()) { file ->
                    fileName = file.path
                    setupAudioEngine(file)
                }
            }

            ifContainsBoolean(PROP_SOUND_LOOPING) { isLooping ->
                looping = isLooping
            }

            ifContainsBoolean(PROP_AUTO_DESTROY) { isAutoDestroy ->
                autoDestroy = isAutoDestroy
            }

            ifContainsBoolean(PROP_SPATIAL_SOUND_ENABLE) { isSpatialSoundEnabled ->
                spatialSoundEnabled = isSpatialSoundEnabled
            }

            ifContainsDouble(PROP_SOUND_VOLUME_LINEAR) { volume ->
                this@AudioNode.volume = volume.toFloat()
                audioEngine.setSoundVolume(sourceId, this@AudioNode.volume)
            }

            ifContainsBoolean(PROP_SOUND_MUTE) { isMuted ->
                if (isMuted) {
                    audioEngine.setSoundVolume(sourceId, 0f)
                } else {
                    audioEngine.setSoundVolume(sourceId, volume)
                }

            }
        }
    }

    private fun setupAudioEngine(file: File) {
        Thread(
            Runnable {
                audioEngine.preloadSoundFile(file.path)
                sourceId = audioEngine.createStereoSound(file.path)

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
        ).start()
    }

    private fun applyActions(props: Bundle) {
        props.ifContainsString(PROP_ACTION) { action ->
            when (action) {
                AudioAction.STOP -> audioEngine.stopSound(sourceId)
                AudioAction.PAUSE -> audioEngine.pause()
                AudioAction.RESUME -> audioEngine.resume()
                AudioAction.START -> audioEngine.playSound(sourceId, looping)
            }
        }
    }

    private fun applySpatialSoundProperties() {
        jsonProps.extract(PROP_SPATIAL_SOUND_POSITION, SpatialSoundPosition::class.java)
            ?.let { spatialSoundPosition ->
                spatialSoundPosition.channelPosition?.let { channelPosition ->
                    audioEngine.setSoundObjectPosition(
                        sourceId,
                        channelPosition.x,
                        channelPosition.y,
                        channelPosition.z
                    )
                }
            }

        jsonProps.extract(PROP_SPATIAL_SOUND_DISTANCE, SpatialSoundDistance::class.java)
            ?.let {
                logMessage("PROP_SPATIAL_SOUND_DISTANCE $it")
                audioEngine.setSoundObjectDistanceRolloffModel(
                    sourceId,
                    it.rolloffFactor.toInt(),
                    it.minDistance.toFloat(),
                    it.maxDistance.toFloat()
                )
            }
    }
}
