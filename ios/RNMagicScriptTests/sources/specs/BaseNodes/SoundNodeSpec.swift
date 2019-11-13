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
@testable import RNMagicScriptHostApplication

class SoundNodeSpec: QuickSpec {
    override func spec() {
        describe("SoundNode") {
            var node: SoundNode!
            let mp3AudioPath = "resources/assets/sounds/bg_sound_mono.mp3"

            beforeEach {
                node = SoundNode()
//                node.downloader = DownloadingMock()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.url).to(beNil())
                    expect(node.stream).to(beFalse())
                    expect(node.volume).to(beCloseTo(1))
                    expect(node.pitch).to(beCloseTo(1)) // defined as defaultTextSizeInMeters: CGFloat = 0.015
                    expect(node.mute).to(beFalse())
                    expect(node.loop).to(beFalse())
                    expect(node.spatial).to(beFalse())
                    expect(node.spatialMinDistance).to(beCloseTo(0))
                    expect(node.spatialMaxDistance).to(beCloseTo(1))
                    expect(node.direction).to(beCloseTo(SCNQuaternionIdentity))

                    expect(node.soundLoaded).to(beNil())

                    expect(node.isLoaded).to(beFalse())
                    expect(node.isPlaying).to(beFalse())
                }
            }

            context("actions") {
                it("should load local audio file") {
                    expect(node.isLoaded).to(beFalse())
                    node.url = urlForRelativePath(mp3AudioPath)
//                    waitUntil(timeout: 0.1) { done in
//                        done()
//                    }
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
                    expect(node.isPlaying).to(beFalse())
                }
            }
        }
    }
}
