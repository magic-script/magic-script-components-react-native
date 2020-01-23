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

class UiProgressBarNodeSpec: QuickSpec {
    override func spec() {
        describe("UiProgressBarNode") {
            var node: UiProgressBarNode!

            beforeEach {
                node = UiProgressBarNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.width).to(beCloseTo(0.0))
                    expect(node.height).to(beCloseTo(0.0))
                    expect(node.min).to(beCloseTo(0.0))
                    expect(node.max).to(beCloseTo(1.0))
                    expect(node.value).to(beCloseTo(0.0))
                    expect(node.height).to(beCloseTo(0.0))
                    let referenceWhiteColor = UIColor.white
                    expect(node.beginColor).to(beCloseTo(referenceWhiteColor))
                    expect(node.endColor).to(beCloseTo(referenceWhiteColor))
                }
            }

            context("initialization") {
                it("should throw exception if 'setupNode' has been called more than once") {
                    expect(node.setupNode()).to(throwAssertion())
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

                it("should update 'width' prop") {
                    let referenceWidth = 2.75
                    node.update(["width" : referenceWidth])
                    expect(node.width).to(beCloseTo(referenceWidth))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    let referenceHeight = 2.75
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                context("when complementary values are correct (min < max)") {
                    it("should update 'min' prop") {
                        node.update(["min" : 0.75])
                        expect(node.min).to(beCloseTo(0.75))
                        expect(node.isLayoutNeeded).to(beTrue())
                    }

                    it("should update 'max' prop") {
                        node.update(["max" : 0.75])
                        expect(node.max).to(beCloseTo(0.75))
                        expect(node.isLayoutNeeded).to(beTrue())
                    }
                }

                context("when complementary values aren't correct (min > max or min == max)") {
                    it("should update 'min' prop") {
                        node.update(["max" : 0.5])
                        node.update(["min" : 0.75])
                        expect(node.min).to(beCloseTo(0.0))
                        expect(node.isLayoutNeeded).to(beTrue())
                    }

                    it("should update 'max' prop") {
                        node.update(["max" : 2.0])
                        node.update(["min" : 0.75])

                        node.update(["max" : 0.75])
                        expect(node.max).to(beCloseTo(2.0))
                        expect(node.isLayoutNeeded).to(beTrue())
                    }
                }

                it("should update 'value' prop (with clamped value rule)") {
                    node.update(["value" : 0.75])
                    expect(node.value).to(beCloseTo(0.75))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.update(["value" : 1.75])
                    expect(node.value).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.update(["value" : -0.5])
                    expect(node.value).to(beCloseTo(0.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'progressColor' (beginColor and endColor)") {
                    let whiteColor = UIColor.white
                    let orangeColor = UIColor.orange
                    let blueColor = UIColor.blue
                    let grayColor = UIColor.gray

                    node.update(["progressColor" : ["beginColor": orangeColor.toArrayOfCGFloat, "endColor": blueColor.toArrayOfCGFloat]])

                    expect(node.beginColor).to(beCloseTo(orangeColor))
                    expect(node.endColor).to(beCloseTo(blueColor))

                    node.update(["progressColor" : ["endColor": grayColor.toArrayOfCGFloat]])
                    expect(node.beginColor).to(beCloseTo(orangeColor))
                    expect(node.endColor).to(beCloseTo(grayColor))

                    node.update(["progressColor" : ["beginColor": whiteColor.toArrayOfCGFloat]])
                    expect(node.beginColor).to(beCloseTo(whiteColor))
                    expect(node.endColor).to(beCloseTo(grayColor))
                }
            }

            context("when asked for size") {
                context("when width/height set") {
                    it("should calculate it") {
                        node = UiProgressBarNode(props: ["width" : 0.75, "height": 0.5])
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.75, height: 0.5)))
                    }
                }

                context("when width set") {
                    it("should calculate it (default height)") {
                        node = UiProgressBarNode(props: ["width" : 0.75])
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.75, height: 0.004)))
                    }
                }

                context("when height set") {
                    it("should calculate it (default height)") {
                        node = UiProgressBarNode(props: ["height" : 0.1])
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.5, height: 0.1)))
                    }
                }

                context("when no width/height set") {
                    it("should return default") {
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.5, height: 0.004)))
                    }
                }
            }

            context("update progress bar") {
                it("should update progress bar to given length") {
                    let referenceWidth: CGFloat = 13.0
                    let referenceValue: CGFloat = 0.61
                    node.width = referenceWidth
                    node.value = referenceValue
                    node.layoutIfNeeded()

                    let size = node.getSize()
                    expect(size.width).to(beCloseTo(referenceWidth))
                    let progressBarNode: SCNNode = node.contentNode.childNodes[1]
                    let plane: SCNPlane = progressBarNode.geometry as! SCNPlane
                    expect(plane.width).to(beCloseTo(referenceWidth * referenceValue))
                }

                it("should be of length 0 if 'min' equals to 'max'") {
                    node.min = 0.29999999
                    node.max = 0.30000001
                    node.layoutIfNeeded()

                    let progressBarNode: SCNNode = node.contentNode.childNodes[1]
                    let plane: SCNPlane = progressBarNode.geometry as! SCNPlane
                    expect(plane.width).to(beCloseTo(UiProgressBarNode.defaultHeight))
                }
            }
        }
    }
}
