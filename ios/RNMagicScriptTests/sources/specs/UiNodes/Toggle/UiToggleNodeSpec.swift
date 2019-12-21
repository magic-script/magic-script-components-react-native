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

class UiToggleNodeSpec: QuickSpec {
    override func spec() {
        describe("UiToggleNode") {
            var node: UiToggleNode!
            let referenceText: String = "Info text"

            beforeEach {
                node = UiToggleNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.text).to(beNil())
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.textSize).to(beCloseTo(0.0))
                    expect(node.height).to(beCloseTo(0.0))
                    expect(node.on).to(beFalse())
                    expect(node.type).to(equal(ToggleType.default))
                    expect(node.canHaveFocus).to(beTrue())
                }

                it("should have set default size") {
                    expect(node.getSize()).to(beCloseTo(UiToggleNode.defaultRectangleSize))
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
                    node.update(["text" : referenceText])
                    expect(node.text).to(equal(referenceText))

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.text).to(equal(referenceText))
                }

                it("should update 'textColor' prop") {
                    let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["textColor" : referenceTextColor.toArrayOfFloat])
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.isLayoutNeeded).to(beFalse())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textColor).to(beCloseTo(referenceTextColor))
                }

                it("should update 'textSize' prop") {
                    let referenceTextSize = 11.0
                    node.update(["textSize" : referenceTextSize])
                    expect(node.textSize).to(beCloseTo(referenceTextSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textSize).to(beCloseTo(referenceTextSize))
                }

                it("should update 'height' prop") {
                    let referenceHeight = 0.75
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    expect(node.getSize().height).to(beCloseTo(referenceHeight))
                }

                it("should update 'on' prop") {
                    node.update(["on" : true])
                    expect(node.on).to(beTrue())
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'type' prop") {
                    node.update(["type" : "checkbox"])
                    expect(node.type).to(equal(ToggleType.checkbox))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("focus") {
                it("should be focusable") {
                    expect(node.canHaveFocus).to(beTrue())

                    node.enabled = false
                    expect(node.canHaveFocus).to(beFalse())
                }

                it("should leave focus immediately") {
                    node.enterFocus()
                    expect(node.hasFocus).to(beFalse())
                }

                it("should switch value on focus") {
                    node.on = false

                    node.enterFocus()
                    expect(node.on).to(beTrue())

                    node.enterFocus()
                    expect(node.on).to(beFalse())
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

            context("assets") {
                it("should display 'toggleOn/Off' image") {
                    let diffuse: SCNMaterialProperty! = node.contentNode.childNodes[1].geometry?.firstMaterial?.diffuse
                    node.type = ToggleType.default

                    node.on = true
                    expect(diffuse.contents).to(beIdenticalTo(ImageAsset.toggleOn.image))

                    node.on = false
                    expect(diffuse.contents).to(beIdenticalTo(ImageAsset.toggleOff.image))
                }

                it("should display 'radioChecked/Unchecked' image") {
                    let diffuse: SCNMaterialProperty! = node.contentNode.childNodes[1].geometry?.firstMaterial?.diffuse
                    node.type = ToggleType.radio

                    node.on = true
                    expect(diffuse.contents).to(beIdenticalTo(ImageAsset.radioChecked.image))

                    node.on = false
                    expect(diffuse.contents).to(beIdenticalTo(ImageAsset.radioUnchecked.image))
                }

                it("should display 'checkboxChecked/Unchecked' image") {
                    let diffuse: SCNMaterialProperty! = node.contentNode.childNodes[1].geometry?.firstMaterial?.diffuse
                    node.type = ToggleType.checkbox

                    node.on = true
                    expect(diffuse.contents).to(beIdenticalTo(ImageAsset.checkboxChecked.image))

                    node.on = false
                    expect(diffuse.contents).to(beIdenticalTo(ImageAsset.checkboxUnchecked.image))
                }
            }
        }
    }
}
