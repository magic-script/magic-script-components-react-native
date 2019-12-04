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
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundDistance
import java.io.File

interface AudioEngine {
    fun stop()
    fun unload()
    fun setSoundVolume(volume: Float)
    fun mute(muted: Boolean)
    fun load(file: File)
    fun play()
    fun pause()
    fun resume()
    fun setSoundObjectPosition(channelPosition: Vector3)
    fun setSoundObjectDistanceRolloffModel(spatialSoundDistance: SpatialSoundDistance)
    fun onDestroy()
    fun spatialSoundEnabled(spatialSoundEnabled: Boolean)
    fun looping(looping: Boolean)
}

