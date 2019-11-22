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

import Quick
import Nimble
import SceneKit
import SwiftyMocky
@testable import RNMagicScriptHostApplication

class SoundNodeSpec: QuickSpec {
    override func spec() {
        describe("SoundNode") {
            var node: SoundNode!
            var downloaderMock: DownloadingMock!
            let mp3AudioPath = "resources/assets/sounds/bg_mono.mp3"

            beforeEach {
                node = SoundNode()
                downloaderMock = DownloadingMock()
                downloaderMock.perform(.download(remoteURL: .any, completion: .any, perform: { (inputURL, completion) in
                    completion(inputURL)
                }))
                node.downloader = downloaderMock
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.url).to(beNil())
                    expect(node.action).to(equal(AudioAction.stop))
                    expect(node.stream).to(beFalse())
                    expect(node.volume).to(beCloseTo(1))
                    expect(node.pitch).to(beCloseTo(1))
                    expect(node.mute).to(beFalse())
                    expect(node.loop).to(beFalse())
                    expect(node.spatial).to(beFalse())
                    expect(node.spatialMinDistance).to(beCloseTo(0))
                    expect(node.spatialMaxDistance).to(beCloseTo(1))
                    expect(node.direction).to(beCloseTo(SCNQuaternionIdentity))

                    expect(node.isLoaded).to(beFalse())
                    expect(node.isPlaying).to(beFalse())
                }
            }

            context("update properties") {
                it("should update url value") {
                    let referenceValue: URL = urlForRelativePath(mp3AudioPath)!
                    node.url = referenceValue
                    expect(node.url).to(beIdenticalTo(referenceValue))
                }
                it("should update action value") {
                    let referenceValue: AudioAction = .pause
                    node.action = referenceValue
                    expect(node.action).to(equal(referenceValue))
                }
                it("should update stream value") {
                    let referenceValue: Bool = true
                    node.stream = referenceValue
                    expect(node.stream).to(equal(referenceValue))
                }
                it("should update volume value") {
                    let referenceValue: CGFloat = 0.7
                    node.volume = referenceValue
                    expect(node.volume).to(beCloseTo(referenceValue))
                }
                it("should update pitch value") {
                    let referenceValue: CGFloat = 1.4
                    node.pitch = referenceValue
                    expect(node.pitch).to(beCloseTo(referenceValue))
                }
                it("should update mute value") {
                    let referenceValue: Bool = true
                    node.mute = referenceValue
                    expect(node.mute).to(equal(referenceValue))
                }
                it("should update loop value") {
                    let referenceValue: Bool = true
                    node.loop = referenceValue
                    expect(node.loop).to(equal(referenceValue))
                }
                it("should update spatial value") {
                    let referenceValue: Bool = true
                    node.spatial = referenceValue
                    expect(node.spatial).to(equal(referenceValue))
                }
                it("should update spatialMinDistance value") {
                    let referenceValue: CGFloat = 0.3
                    node.spatialMinDistance = referenceValue
                    expect(node.spatialMinDistance).to(beCloseTo(referenceValue))
                }
                it("should update spatialMaxDistance value") {
                    let referenceValue: CGFloat = 3.7
                    node.spatialMaxDistance = referenceValue
                    expect(node.spatialMaxDistance).to(beCloseTo(referenceValue))
                }

                it("should update direction value") {
                    let referenceValue: SCNQuaternion = SCNQuaternion.fromAxis(SCNVector3(0, 1, 0), andAngle: 0.5 * Float.pi)
                    node.direction = referenceValue
                    expect(node.direction).to(beCloseTo(referenceValue))
                }
            }

            context("actions") {
                it("should load local audio file") {
                    expect(node.isLoaded).to(beFalse())
                    node.url = urlForRelativePath(mp3AudioPath)
                    expect(node.isLoaded).to(beTrue())
                }

                it("should start playing audio file") {
                    node.url = urlForRelativePath(mp3AudioPath)
                    expect(node.isPlaying).to(beFalse())
                    node.start()
                    expect(node.isPlaying).to(beTrue())
                }

                it("should stop playing audio file") {
                    node.url = urlForRelativePath(mp3AudioPath)
                    node.start()
                    expect(node.isPlaying).to(beTrue())
                    node.stop()
                    expect(node.isPlaying).to(beFalse())
                }

                it("should pause/resume playing audio file") {
                    node.url = urlForRelativePath(mp3AudioPath)
                    node.start()
                    expect(node.isPlaying).to(beTrue())
                    node.pause()
                    expect(node.isPlaying).to(beFalse())
                    node.resume()
                    expect(node.isPlaying).to(beTrue())
                }
            }

            context("reloadPlayerIfNeeded") {
                it("should reload a stopped player") {
                    node.url = urlForRelativePath(mp3AudioPath)
                    node.loop = !node.loop // force sound node to set needsReloadPlayer flag
                    expect(node.isPlaying).to(beFalse())
                    node.stop()
                    expect(node.isPlaying).to(beFalse())
                    node.reloadPlayerIfNeeded()
                    expect(node.isPlaying).to(beFalse())
                }

                it("should reload a paused player") {
                    node.url = urlForRelativePath(mp3AudioPath)
                    node.loop = !node.loop // force sound node to set needsReloadPlayer flag
                    node.start()
                    expect(node.isPlaying).to(beTrue())
                    node.pause()
                    expect(node.isPlaying).to(beFalse())
                    node.reloadPlayerIfNeeded()
                    expect(node.isPlaying).to(beFalse())
                }

                it("should reload a resumed player") {
                    node.url = urlForRelativePath(mp3AudioPath)
                    node.loop = !node.loop // force sound node to set needsReloadPlayer flag
                    node.start()
                    expect(node.isPlaying).to(beTrue())
                    node.pause()
                    expect(node.isPlaying).to(beFalse())
                    node.resume()
                    expect(node.isPlaying).to(beTrue())
                    node.reloadPlayerIfNeeded()
                    expect(node.isPlaying).to(beTrue())
                }

                it("should reload a started player") {
                    node.url = urlForRelativePath(mp3AudioPath)
                    node.loop = !node.loop // force sound node to set needsReloadPlayer flag
                    node.start()
                    expect(node.isPlaying).to(beTrue())
                    node.reloadPlayerIfNeeded()
                    expect(node.isPlaying).to(beTrue())
                }
            }

            context("debug mode") {
                it("should set debug mode") {
                    expect(node.childNodes.isEmpty).to(beTrue())
                    node.setDebugMode(true)
                    expect(node.childNodes.count).to(equal(1))
                    node.setDebugMode(true)
                    expect(node.childNodes.count).to(equal(1))
                    node.setDebugMode(false)
                    expect(node.childNodes.count).to(equal(0))
                }
            }
        }
    }
}
