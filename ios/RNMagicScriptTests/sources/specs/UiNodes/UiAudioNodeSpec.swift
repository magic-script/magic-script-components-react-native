//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import Quick
import Nimble
import SceneKit
import SwiftyMocky
@testable import RNMagicScriptHostApplication

class UiAudioNodeSpec: QuickSpec {
    override func spec() {
        describe("UiAudioNode") {
            var node: UiAudioNode!
            let mp3AudioPath = "resources/assets/sounds/bg_mono.mp3"

            beforeEach {
                node = UiAudioNode()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.fileName).to(beNil())
                    expect(node.action).to(equal(AudioAction.stop))
                    expect(node.soundLooping).to(beFalse())
                    expect(node.soundMute).to(beFalse())
                    expect(node.soundPitch).to(beCloseTo(1))
                    expect(node.soundVolumeLinear).to(beCloseTo(8))
                    expect(node.spatialSoundEnable).to(beFalse())
                    expect(node.streamedFileOffset).to(beCloseTo(0))
                    expect(node.spatialSoundPosition.isEmpty).to(beTrue())
                    expect(node.spatialSoundDirection.isEmpty).to(beTrue())
                }
            }

            context("initialization") {
                it("should add sound node to the hierarchy") {
                    expect(node.contentNode.childNodes.isEmpty).to(beFalse())
                }
            }

            context("update properties") {
                it("should update fileName value") {
                    let referenceValue: URL = urlForRelativePath(mp3AudioPath)!
                    node.update(["fileName" : referenceValue.absoluteString])
                    expect(node.fileName).to(equal(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update action value") {
                    let referenceValue: AudioAction = .pause
                    node.update(["action" : referenceValue.rawValue])
                    expect(node.action).to(equal(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update soundLooping value") {
                    let referenceValue: Bool = true
                    node.update(["soundLooping" : referenceValue])
                    expect(node.soundLooping).to(equal(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update soundMute value") {
                    let referenceValue: Bool = true
                    node.update(["soundMute" : referenceValue])
                    expect(node.soundMute).to(equal(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update soundPitch value") {
                    let referenceValue: CGFloat = 1.4
                    node.update(["soundPitch" : referenceValue])
                    expect(node.soundPitch).to(beCloseTo(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update soundVolumeLinear value") {
                    let referenceValue: CGFloat = 0.7
                    node.update(["soundVolumeLinear" : referenceValue])
                    expect(node.soundVolumeLinear).to(beCloseTo(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update spatialSoundEnable value") {
                    let referenceValue: Bool = true
                    node.update(["spatialSoundEnable" : referenceValue])
                    expect(node.spatialSoundEnable).to(equal(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update streamedFileOffset value") {
                    let referenceValue: CGFloat = 0.3
                    node.update(["streamedFileOffset" : referenceValue])
                    expect(node.streamedFileOffset).to(beCloseTo(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update spatialSoundPosition value") {
                    let referenceChannel: Int = 0
                    let referencePosition = SCNVector3(1, 2, 3)
                    node.update([
                        "spatialSoundPosition" : [[
                            "channel": referenceChannel,
                            "channelPosition": [referencePosition.x, referencePosition.y, referencePosition.z]
                        ]]
                    ])
                    expect(node.spatialSoundPosition[0].channel).to(equal(referenceChannel))
                    expect(node.spatialSoundPosition[0].position).to(beCloseTo(referencePosition))
                    expect(node.isLayoutNeeded).to(beFalse())

                    let soundNode = self.getSoundNode(node)
                    expect(soundNode.position).to(beCloseTo(referencePosition))

                    node.spatialSoundPosition = []
                    expect(soundNode.position).to(beCloseTo(SCNVector3Zero))
                }
                it("should update spatialSoundDirection value") {
                    let referenceChannel: Int = 0
                    let referenceDirection = SCNQuaternion.fromAxis(SCNVector3(0.707, 0.707, 0), andAngle: Float.pi)
                    node.update([
                        "spatialSoundDirection" : [[
                            "channel": referenceChannel,
                            "channelDirection": [referenceDirection.x, referenceDirection.y, referenceDirection.z, referenceDirection.w]
                        ]]
                    ])
                    expect(node.spatialSoundDirection[0].channel).to(equal(referenceChannel))
                    expect(node.spatialSoundDirection[0].direction).to(beCloseTo(referenceDirection))
                    expect(node.isLayoutNeeded).to(beFalse())

                    let soundNode = self.getSoundNode(node)
                    node.spatialSoundDirection = []
                    expect(soundNode.direction).to(beCloseTo(SCNQuaternionIdentity))
                }
            }

            context("updateLayout") {
                it("should update layout") {
                    expect(node.isLayoutNeeded).to(beFalse())
                    node.soundLooping = true
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            context("debug mode") {
                it("should set debug mode") {
                    let soundNode = self.getSoundNode(node)
                    expect(soundNode).notTo(beNil())
                    expect(soundNode.childNodes.isEmpty).to(beTrue())
                    node.setDebugMode(true)
                    expect(soundNode.childNodes.count).to(equal(1))
                    node.setDebugMode(true)
                    expect(soundNode.childNodes.count).to(equal(1))
                    node.setDebugMode(false)
                    expect(soundNode.childNodes.count).to(equal(0))
                }
            }
        }
    }

    fileprivate func getSoundNode(_ node: UiAudioNode) -> SoundNode {
        return node.contentNode.childNodes.first as! SoundNode
    }
}
