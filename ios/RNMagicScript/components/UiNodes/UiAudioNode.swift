//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
// 

import SceneKit

@objc open class UiAudioNode: TransformNode {

    @objc var action: AudioAction = .stop
    @objc var soundLooping: Bool = false
    @objc var soundMute: Bool = false
    // The range of the pitch is 0.5 to 2.0, with 0.5 being one octave down
    // and 2.0 being one octave up (i.e., the pitch is a frequency multiple).
    // A pitch of 1.0 is the default and means no change.
    @objc var soundPitch: CGFloat = 1
    // The range of the volume is 0 to 8, with 0 for silence, 1 for unity gain,
    // and 8 for 8x gain.
    @objc var soundVolumeLinear: CGFloat = 0
    @objc var spatialSoundEnable: Bool = false
    @objc var streamedFileOffset: CGFloat = 0

//    @objc var spatialSoundPosition: SCNVector3 = SCNVector3Zero
//    @objc var spatialSoundDirection: SCNVector3 = SCNVector3Zero
//    @objc var spatialSoundDistanceProperties: SpatialSoundDistanceProperties
//    SpatialSoundRadiationProperties: SpatialSoundRadiationProperties
//    SpatialSoundDirectSendLevels: SpatialSoundSendLevels
//    SpatialSoundRoomSendLevels: SpatialSoundSendLevels

    @objc override func setupNode() {
        super.setupNode()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let action = Convert.toAudioAction(props["action"]) {
            self.action = action
        }

        if let soundLooping = Convert.toBool(props["soundLooping"]) {
            self.soundLooping = soundLooping
        }

        if let soundMute = Convert.toBool(props["soundMute"]) {
            self.soundMute = soundMute
        }

        if let soundPitch = Convert.toCGFloat(props["soundPitch"]) {
            self.soundPitch = soundPitch
        }

        if let soundVolumeLinear = Convert.toCGFloat(props["soundVolumeLinear"]) {
            self.soundVolumeLinear = soundVolumeLinear
        }

        if let spatialSoundEnable = Convert.toBool(props["spatialSoundEnable"]) {
            self.spatialSoundEnable = spatialSoundEnable
        }

        if let streamedFileOffset = Convert.toCGFloat(props["streamedFileOffset"]) {
            self.streamedFileOffset = streamedFileOffset
        }
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)

    }
}
