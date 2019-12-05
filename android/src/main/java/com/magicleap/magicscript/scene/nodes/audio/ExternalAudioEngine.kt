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

interface ExternalAudioEngine {
    fun stopSound(sourceId: Int)
    fun unloadSoundFile(path: String?)
    fun setSoundVolume(sourceId: Int, volume: Float)
    fun playSound(sourceId: Int, looping: Boolean)
    fun pause()
    fun resume()
    fun setSoundObjectPosition(sourceId: Int, x: Float, y: Float, z: Float)
    fun setSoundObjectDistanceRolloffModel(
        sourceId: Int,
        rolloffFactor: Int,
        minDistance: Float,
        maxDistance: Float
    )

    fun preloadSoundFile(path: String?)
    fun setRoomProperties(
        fl: Float,
        fl1: Float,
        fl2: Float,
        plasterSmooth: Int,
        plasterSmooth1: Int,
        curtainHeavy: Int
    )

    fun createSoundObject(path: String?): Int
    fun createStereoSound(path: String?): Int
}