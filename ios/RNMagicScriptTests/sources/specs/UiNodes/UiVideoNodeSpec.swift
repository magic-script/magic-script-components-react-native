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
import SwiftyMocky
import SceneKit
import AVKit
@testable import RNMagicScriptHostApplication

class UiVideoNodeSpec: QuickSpec {
    override func spec() {
        describe("UiVideoNode") {
            var node: UiVideoNode!

            beforeEach {
                node = UiVideoNode(props: [:])
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.looping).to(beTrue())
                    expect(node.width).to(beCloseTo(512.0))
                    expect(node.height).to(beCloseTo(512.0))
                    expect(node.size).to(beCloseTo(CGSize(width: 1.0, height: 1.0)))
                    expect(node.videoPath).to(beNil())
                    expect(node.viewMode).to(equal(VideoViewMode.fullArea))
                    expect(node.volume).to(beCloseTo(0.5))
                    expect(node.action).to(equal(VideoAction.stop))
                }
            }

            context("update properties") {
                it("should update 'alignment' prop") {
                    let referenceAlignment = Alignment.topLeft
                    node.update(["alignment" : referenceAlignment.rawValue])
                    expect(node.alignment).to(equal(referenceAlignment))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'size' prop") {
                    let referenceSize = CGSize(width: 1.25, height: 0.95)
                    node.update(["size" : referenceSize.toArrayOfCGFloat])
                    expect(node.size).to(beCloseTo(referenceSize))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'width' prop") {
                    // value should be clamped in range from 1 to 2048
                    var referenceWidth = -100.0
                    node.update(["width" : referenceWidth])
                    expect(node.width).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beTrue())

                    referenceWidth = 3.0
                    node.update(["width" : referenceWidth])
                    expect(node.width).to(beCloseTo(3.0))
                    expect(node.isLayoutNeeded).to(beTrue())

                    referenceWidth = 4096.0
                    node.update(["width" : referenceWidth])
                    expect(node.width).to(beCloseTo(2048.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    // value should be clamped in range from 1 to 2048
                    var referenceHeight = -100.0
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beTrue())

                    referenceHeight = 3.0
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(3.0))
                    expect(node.isLayoutNeeded).to(beTrue())

                    referenceHeight = 4096.0
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(2048.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'action' prop") {
                    var referenceAction = VideoAction.pause
                    node.update(["action" : referenceAction.rawValue])
                    expect(node.action).to(equal(referenceAction))
                    expect(node.isLayoutNeeded).to(beFalse())

                    referenceAction = VideoAction.start
                    node.update(["action" : referenceAction.rawValue])
                    expect(node.action).to(equal(referenceAction))
                    expect(node.isLayoutNeeded).to(beFalse())

                    referenceAction = VideoAction.stop
                    node.update(["action" : referenceAction.rawValue])
                    expect(node.action).to(equal(referenceAction))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'viewMode' prop") {
                    let referenceVideoViewMode = VideoViewMode.leftRight
                    node.update(["viewMode" : referenceVideoViewMode.rawValue])
                    expect(node.viewMode).to(equal(referenceVideoViewMode))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'volume' prop") {
                    // value should be clamped in range from 0 to 1.0
                    var referenceVolume = -1.0
                    node.update(["volume" : referenceVolume])
                    expect(node.volume).to(beCloseTo(0.0))
                    expect(node.isLayoutNeeded).to(beFalse())

                    referenceVolume = 0.75
                    node.update(["volume" : referenceVolume])
                    expect(node.volume).to(beCloseTo(referenceVolume))
                    expect(node.isLayoutNeeded).to(beFalse())

                    referenceVolume = 1.0
                    node.update(["volume" : referenceVolume])
                    expect(node.volume).to(beCloseTo(referenceVolume))
                    expect(node.isLayoutNeeded).to(beFalse())

                    referenceVolume = 1.25
                    node.update(["volume" : referenceVolume])
                    expect(node.volume).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'looping' prop") {
                    let referenceLooping = false
                    node.update(["looping" : referenceLooping])
                    expect(node.looping).to(beFalse())
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'videoPath' prop") {
                    let referenceVideoPath = "file://video_path"
                    node.update(["videoPath" : referenceVideoPath])
                    expect(node.videoPath).to(equal(URL(string: referenceVideoPath)!))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("when asked for size") {
                it("should calculate it") {
                    node = UiVideoNode(props: ["size": [0.75, 0.45]])
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 0.75, height: 0.45)))
                    node = UiVideoNode(props: ["size": [1.25, 0.15]])
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 1.25, height: 0.15)))
                }
            }

            context("when update 'action' prop") {
                it("should trigger correct action on player (pause)") {
                    let videoPlayerMock = AVPlayerProtocolMock()
                    node.videoPlayer = videoPlayerMock
                    node.update(["action": "pause"])
                    Verify(videoPlayerMock, .pause())
                }

                it("should trigger correct action on player (stop)") {
                    let videoPlayerMock = AVPlayerProtocolMock()
                    node.videoPlayer = videoPlayerMock
                    node.update(["action": "stop"])
                    Verify(videoPlayerMock, .pause())
                    Verify(videoPlayerMock, .seek(to: .value(CMTime.zero)))
                }

                it("should trigger correct action on player (start)") {
                    let videoPlayerMock = AVPlayerProtocolMock()
                    node.videoPlayer = videoPlayerMock
                    node.update(["action": "start"])
                    Verify(videoPlayerMock, .play())
                }
            }

            context("when receive 'player reached end' notification") {
                context("when in looping mode") {
                    it("should rewind to the beginning") {
                        let videoPlayerMock = AVPlayerProtocolMock()
                        node.videoPlayer = videoPlayerMock
                        node.playerDidFinishPlaying(note: NSNotification(name: .AVPlayerItemDidPlayToEndTime, object: nil))
                        Verify(videoPlayerMock, .seek(to: .value(CMTime.zero)))
                        Verify(videoPlayerMock, .play())
                    }
                }
            }

            context("when update 'volume' prop") {
                it("should set it on player") {
                    let videoPlayerMock = AVPlayerProtocolMock()
                    node.videoPlayer = videoPlayerMock
                    let referenceVolume = 0.75
                    node.update(["volume": referenceVolume])
                    expect(videoPlayerMock.volume).to(beCloseTo(referenceVolume))
                }
            }

            context("when updating 'anchorPosition' prop") {
                it("should update video plane position") {
                    let referenceAnchorPosition = SCNVector3(0.1, 0.2, 0.3)
                    node.update(["anchorPosition" : referenceAnchorPosition.toArrayOfFloat])
                    expect(node.anchorPosition).to(beCloseTo(referenceAnchorPosition))
                    expect(node.planeNode.position).to(beCloseTo(referenceAnchorPosition))
                }
            }
        }
    }
}
