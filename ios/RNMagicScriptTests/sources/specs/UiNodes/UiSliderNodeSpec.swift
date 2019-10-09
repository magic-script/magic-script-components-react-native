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

class UiSliderNodeSpec: QuickSpec {
    override func spec() {
        describe("UiSliderNode") {
            var node: UiSliderNode!

            beforeEach {
                node = UiSliderNode(props: [:])
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.width).to(beCloseTo(0.0))
                    expect(node.height).to(beCloseTo(0.0))
                    expect(node.min).to(beCloseTo(0.0))
                    expect(node.minLabel).to(beNil())
                    expect(node.max).to(beCloseTo(1.0))
                    expect(node.maxLabel).to(beNil())
                    expect(node.value).to(beCloseTo(0.0))
                    expect(node.height).to(beCloseTo(0.0))
                    let referenceWhiteColor = UIColor.white
                    expect(node.foregroundColor).to(beCloseTo(referenceWhiteColor))
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
                    expect(node.isLayoutNeeded).to(beFalse())

                    node.update(["value" : 1.75])
                    expect(node.value).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beFalse())

                    node.update(["value" : -0.5])
                    expect(node.value).to(beCloseTo(0.0))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'foregroundColor'") {
                    let orangeColor = UIColor.orange
                    let grayColor = UIColor.gray

                    node.update(["foregroundColor" : orangeColor.toArrayOfCGFloat])
                    expect(node.foregroundColor).to(beCloseTo(orangeColor))

                    node.update(["foregroundColor" : grayColor.toArrayOfCGFloat])
                    expect(node.foregroundColor).to(beCloseTo(grayColor))
                }

                it("shouldn't update 'canHaveFocus' prop" /* it's hardcoded */) {
                    node = UiSliderNode(props: ["canHaveFocus" : false])
                    expect(node.canHaveFocus).to(beTrue())
                    node.update(["canHaveFocus" : true])
                    expect(node.canHaveFocus).to(beTrue())
                }
            }

            context("when asked for size") {
                context("when width/height set") {
                    it("should calculate it") {
                        node = UiSliderNode(props: ["width" : 0.75, "height": 0.5])
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.75, height: 0.5)))
                    }
                }

                context("when width set") {
                    it("should calculate it (default height)") {
                        node = UiSliderNode(props: ["width" : 0.75])
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.75, height: 0.018)))
                    }
                }

                context("when height set") {
                    it("should calculate it (default height)") {
                        node = UiSliderNode(props: ["height" : 0.1])
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.5, height: 0.1)))
                    }
                }

                context("when no width/height set") {
                    it("should return default") {
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.5, height: 0.018)))
                    }
                }
            }

            context("update slider bar") {
                it("should update slider bar to given length") {
                    let referenceWidth: CGFloat = 13.0
                    let referenceValue: CGFloat = 0.61
                    node.width = referenceWidth
                    node.value = referenceValue
                    node.layoutIfNeeded()

                    let size = node.getSize()
                    expect(size.width).to(beCloseTo(referenceWidth))
                    let foregroundNode: SCNNode = node.contentNode.childNodes[2]
                    let plane: SCNPlane = foregroundNode.geometry as! SCNPlane
                    expect(plane.width).to(beCloseTo(referenceWidth * referenceValue))
                }

                it("should be of length 0 if 'min' equals to 'max'") {
                    node.min = 0.29999999
                    node.max = 0.30000001
                    node.layoutIfNeeded()

                    let foregroundNode: SCNNode = node.contentNode.childNodes[2]
                    let plane: SCNPlane = foregroundNode.geometry as! SCNPlane
                    expect(plane.width).to(beCloseTo(0))
                }
            }

            context("when has focus") {
                context("when enters focus") {
                    it("should reflect this state") {
                        node.enterFocus()

                        expect(node.hasFocus).to(beTrue())
                    }
                }

                context("when leaves focus") {
                    it("should reflect this state") {
                        node.leaveFocus()

                        expect(node.hasFocus).to(beFalse())
                    }
                }
            }

            context("when update min/max label") {
                it("should update related nodes") {
                    let minLabelReferenceText = "minLabelText"
                    let maxLabelReferenceText = "maxLabelText"
                    node.update(["minLabel" : minLabelReferenceText])
                    node.update(["maxLabel" : maxLabelReferenceText])

                    let minLabelNode = node.contentNode.childNodes[0] as! LabelNode
                    expect(minLabelNode.text).to(equal(minLabelReferenceText))
                    let maxLabelNode = node.contentNode.childNodes[3] as! LabelNode
                    expect(maxLabelNode.text).to(equal(maxLabelReferenceText))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }
        }
    }
}
