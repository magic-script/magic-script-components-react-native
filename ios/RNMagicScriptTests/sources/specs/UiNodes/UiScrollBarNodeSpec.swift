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
@testable import RNMagicScriptHostApplication

import SceneKit

class UiScrollBarNodeSpec: QuickSpec {
    override func spec() {
        describe("UiScrollBarNode") {
            var node: UiScrollBarNode!

            beforeEach {
                node = UiScrollBarNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.length).to(beCloseTo(0.0))
                    expect(node.thickness).to(beCloseTo(0.0))
                    expect(node.thumbSize).to(beCloseTo(0.1))
                    expect(node.thumbPosition).to(beCloseTo(0.0))
                    expect(node.scrollOrientation).to(equal(.vertical))
                }

                context("initialization") {
                    it("should throw exception if 'setupNode' has been called more than once") {
                        expect(node.setupNode()).to(throwAssertion())
                    }
                }

                it("should have set default size") {
                    expect(node.getSize()).to(beCloseTo(CGSize(width: UiScrollBarNode.defaultThickness, height: UiScrollBarNode.defaultLength)))
                }
            }

            context("update properties") {
                it("should not update 'alignment' prop") {
                    let referenceAlignment = Alignment.bottomRight
                    node.update(["alignment" : referenceAlignment.rawValue])
                    expect(node.alignment).notTo(equal(referenceAlignment))
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'length' prop") {
                    let referenceLength: CGFloat = 0.6
                    node.update(["length" : referenceLength])
                    expect(node.length).to(beCloseTo(referenceLength))
                    expect(node.getSize()).to(beCloseTo(CGSize(width: UiScrollBarNode.defaultThickness, height: referenceLength)))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'thickness' prop") {
                    let referenceThickness: CGFloat = 0.2
                    node.update(["thickness" : referenceThickness])
                    expect(node.thickness).to(beCloseTo(referenceThickness))
                    expect(node.getSize()).to(beCloseTo(CGSize(width: referenceThickness, height: UiScrollBarNode.defaultLength)))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'thumbSize' prop") {
                    let referenceThumbSize1: CGFloat = 0.75
                    let referenceThumbSize2: CGFloat = 1.25
                    let referenceThumbSize3: CGFloat = -0.25
                    node.update(["thumbSize" : referenceThumbSize1])
                    expect(node.thumbSize).to(beCloseTo(referenceThumbSize1))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()

                    node.thumbSize = referenceThumbSize2
                    expect(node.thumbSize).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()

                    node.thumbSize = referenceThumbSize3
                    expect(node.thumbSize).to(beCloseTo(0.1))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'thumbPosition' prop") {
                    let referenceThumbPosition1: CGFloat = 0.75
                    let referenceThumbPosition2: CGFloat = 1.25
                    let referenceThumbPosition3: CGFloat = -0.25
                    node.update(["thumbPosition" : referenceThumbPosition1])
                    expect(node.thumbPosition).to(beCloseTo(referenceThumbPosition1))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()

                    node.thumbPosition = referenceThumbPosition2
                    expect(node.thumbPosition).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()

                    node.thumbPosition = referenceThumbPosition3
                    expect(node.thumbPosition).to(beCloseTo(0.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'scrollOrientation' prop") {
                    let referenceOrientation: Orientation = .horizontal
                    node.update(["orientation" : referenceOrientation.rawValue])
                    expect(node.scrollOrientation).to(equal(referenceOrientation))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("orientation") {
                it("should support horizontal orientation") {
                    node.scrollOrientation = .horizontal
                    node.layoutIfNeeded()
                    expect(node.scrollOrientation).to(equal(.horizontal))
                    expect(node.getSize()).to(beCloseTo(CGSize(width: UiScrollBarNode.defaultLength, height: UiScrollBarNode.defaultThickness)))

                    let bgNode = node.contentNode.childNodes[0]
                    expect(bgNode.transform).to(beCloseTo(SCNMatrix4Identity))
                }

                it("should support vertical orientation") {
                    node.scrollOrientation = .vertical
                    node.layoutIfNeeded()
                    expect(node.scrollOrientation).to(equal(.vertical))
                    expect(node.getSize()).to(beCloseTo(CGSize(width: UiScrollBarNode.defaultThickness, height: UiScrollBarNode.defaultLength)))

                    let bgNode = node.contentNode.childNodes[0]
                    expect(bgNode.transform).to(beCloseTo(SCNMatrix4MakeRotation(-0.5 * Float.pi, 0, 0, 1)))
                }
            }

            context("setVisible") {
                it("should show/hide scroll bar node") {
                    expect(node.hasActions).to(beFalse())
                    node.setVisible(true, animated: false, delay: 0.01)
                    expect(node.hasActions).toEventually(beTrue())
                }
            }
        }
    }
}
