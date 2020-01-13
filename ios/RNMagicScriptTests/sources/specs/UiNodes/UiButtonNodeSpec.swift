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

class UiButtonNodeSpec: QuickSpec {
    override func spec() {
        describe("UiButtonNode") {
            var node: UiButtonNode!
            let shortReferenceText: String = "Info text"

            beforeEach {
                node = UiButtonNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.text).to(beNil())
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    let referenceIconColor = UIColor.white
                    expect(node.iconColor).to(equal(referenceIconColor))
                    expect(node.textSize).to(beCloseTo(0.0))
                    expect(node.iconSize).to(beCloseTo(0.1))
                    expect(node.width).to(beCloseTo(0.0))
                    expect(node.height).to(beCloseTo(0.0))
                    expect(node.roundness).to(beCloseTo(1.0))
                    expect(node.canHaveFocus).to(beFalse())
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
                
                it("should update 'text' prop") {
                    node.update(["text" : shortReferenceText])
                    expect(node.text).to(equal(shortReferenceText))

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.text).to(equal(shortReferenceText))
                }

                it("should update 'textColor' prop") {
                    let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["textColor" : referenceTextColor.toArrayOfFloat])
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textColor).to(beCloseTo(referenceTextColor))
                }

                it("should update 'iconColor' prop") {
                    let referenceIconColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["iconColor" : referenceIconColor.toArrayOfFloat])
                    expect(node.iconColor).to(beCloseTo(referenceIconColor))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'textSize' prop") {
                    let referenceTextSize = 11.0
                    node.update(["textSize" : referenceTextSize])
                    expect(node.textSize).to(beCloseTo(referenceTextSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textSize).to(beCloseTo(referenceTextSize))
                }

                it("should update 'iconSize' prop") {
                    let referenceIconSize = 2.75
                    node.update(["iconSize" : referenceIconSize])
                    expect(node.iconSize).to(beCloseTo(referenceIconSize))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'width' prop") {
                    let referenceWidth = 2.75
                    node.update(["width" : referenceWidth])
                    expect(node.width).to(beCloseTo(referenceWidth))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    let referenceHeight = 3.75
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'roundness' prop") {
                    let referenceRoundness = 0.75
                    node.update(["roundness" : referenceRoundness])
                    expect(node.roundness).to(beCloseTo(referenceRoundness))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    node.update(["roundness" : referenceRoundness])
                    expect(node.roundness).to(beCloseTo(referenceRoundness))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should clamp and update 'roundness' prop") {
                    node.roundness = 0.5
                    node.layoutIfNeeded()

                    let referenceRoundness1 = 2.75
                    node.update(["roundness" : referenceRoundness1])
                    expect(node.roundness).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    node.update(["roundness" : referenceRoundness1])
                    expect(node.roundness).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beFalse())

                    let referenceRoundness2 = -2.75
                    node.update(["roundness" : referenceRoundness2])
                    expect(node.roundness).to(beCloseTo(0.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    node.update(["roundness" : referenceRoundness2])
                    expect(node.roundness).to(beCloseTo(0.0))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            context("debug mode") {
                it("should set debug mode") {
                    let labelNode: LabelNode! = node.contentNode.childNodes.first as? LabelNode
                    expect(labelNode).notTo(beNil())
                    let referenceLabelNodeChildNodesCount: Int = labelNode.childNodes.count
                    node.setDebugMode(true)
                    expect(referenceLabelNodeChildNodesCount + 2).to(equal(labelNode.childNodes.count))
                }
            }
        }
    }
}
