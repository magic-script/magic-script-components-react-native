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

class UiTabNodeSpec: QuickSpec {
    override func spec() {
        describe("UiTabNode") {
            var node: UiTabNode!

            beforeEach {
                node = UiTabNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.text).to(beNil())
                    expect(node.textColor).to(beCloseTo(UIColor(white: 0.75, alpha: 1.0)))
                    expect(node.textSize).to(beCloseTo(0.0))
                    expect(node.canHaveFocus).to(beTrue())
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
                    expect(node.getSize()).to(beCloseTo(CGSize.zero))

                    let referenceText: String = "Info text"
                    node.update(["text" : referenceText])
                    expect(node.text).to(equal(referenceText))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())
                    expect(node.getSize()).notTo(beCloseTo(CGSize.zero))

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.text).to(equal(referenceText))
                }

                it("should update 'textColor' prop") {
                    let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["textColor" : referenceTextColor.toArrayOfFloat])
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.isLayoutNeeded).to(beTrue())

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
            }

            context("focus") {
                it("should be focusable") {
                    expect(node.canHaveFocus).to(beTrue())
                    node.enabled = false
                    expect(node.canHaveFocus).to(beFalse())
                }

                it("should has focus") {
                    node.enterFocus()
                    // UiTabNode gets a momentary focus, which means that as soon as
                    // it enters focus, it calls leaveFocus()
                    expect(node.hasFocus).to(beFalse())
                }
            }

            context("debug mode") {
                it("should set debug mode") {
                    let labelNode = node.contentNode.childNodes.first!
                    expect(labelNode.childNodes.count).to(equal(1))
                    node.setDebugMode(true)
                    expect(labelNode.childNodes.count).to(equal(3))
                    node.setDebugMode(true)
                    expect(labelNode.childNodes.count).to(equal(3))
                    node.setDebugMode(false)
                    expect(labelNode.childNodes.count).to(equal(1))
                }
            }
        }
    }
}
