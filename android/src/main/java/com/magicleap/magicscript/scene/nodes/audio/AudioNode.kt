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

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundDistance
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundPosition
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read
import com.magicleap.magicscript.utils.readFilePath

open class AudioNode(
    initProps: ReadableMap,
    private val context: Context,
    private var audioEngine: AudioEngine,
    private val fileProvider: AudioFileProvider
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

    private var spatialSoundEnabled = DEFAULT_SPATIAL_SOUND_ENABLE
    private var lastUserAction: String = ""

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        applyValues(props)

        if (spatialSoundEnabled) {
            applySpatialSoundProperties(props)
        }

        applyActions(props)
    }

    override fun onDestroy() {
        fileProvider.onDestroy()
        audioEngine.onDestroy()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        audioEngine.pause()
    }

    override fun onResume() {
        super.onResume()
        if (lastUserAction == AudioAction.START || lastUserAction == AudioAction.RESUME) {
            audioEngine.resume()
        }
    }

    private fun applyValues(props: Bundle) {
        props.run {
            read<Boolean>(PROP_SOUND_LOOPING)?.let { isLooping ->
                audioEngine.looping(isLooping)
            }

            val filePath = this.readFilePath(PROP_FILE_NAME, context)
            if (filePath != null) {
                fileProvider.provideFile(filePath) { file ->
                    audioEngine.load(file)
                }
            }

            read<Boolean>(PROP_SPATIAL_SOUND_ENABLE)?.let { isSpatialSoundEnabled ->
                if (spatialSoundEnabled != isSpatialSoundEnabled) {
                    spatialSoundEnabled = isSpatialSoundEnabled
                    audioEngine.spatialSoundEnabled(spatialSoundEnabled)
                }
            }

            read<Double>(PROP_SOUND_VOLUME_LINEAR)?.let { volume ->
                audioEngine.setSoundVolume(volume.toFloat())
            }

            read<Boolean>(PROP_SOUND_MUTE)?.let { isMuted ->
                audioEngine.mute(isMuted)
            }
        }
    }

    private fun applyActions(props: Bundle) {
        val action = props.read<String>(PROP_ACTION)
        lastUserAction = action ?: lastUserAction
        when (action) {
            AudioAction.STOP -> audioEngine.stop()
            AudioAction.PAUSE -> audioEngine.pause()
            AudioAction.RESUME -> audioEngine.resume()
            AudioAction.START -> audioEngine.play()
        }
    }

    private fun applySpatialSoundProperties(props: Bundle) {
        props.read<SpatialSoundPosition>(PROP_SPATIAL_SOUND_POSITION)?.let { spatialSoundPosition ->
            spatialSoundPosition.channelPosition?.let { channelPosition ->
                audioEngine.setSoundObjectPosition(channelPosition)
            }
        }

        props.read<SpatialSoundDistance>(PROP_SPATIAL_SOUND_DISTANCE)?.let { spatialSoundDistance ->
            audioEngine.setSoundObjectDistanceRolloffModel(spatialSoundDistance)
        }
    }
}
