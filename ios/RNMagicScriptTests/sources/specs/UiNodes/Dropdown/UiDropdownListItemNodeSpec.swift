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

class UiDropdownListItemNodeSpec: QuickSpec {
    override func spec() {
        describe("UiDropdownListItemNode") {
            var node: UiDropdownListItemNode!
            let shortReferenceText: String = "Info text"

            beforeEach {
                node = UiDropdownListItemNode()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.label).to(beNil())
                    expect(node.id).to(equal(0))
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.textSize).to(beCloseTo(0.0))
                    expect(node.canHaveFocus).to(beFalse())
                    expect(node.selected).to(beFalse())
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

                it("should update 'label' prop") {
                    node.update(["label" : shortReferenceText])
                    expect(node.label).to(equal(shortReferenceText))
                }

                it("should update 'id' prop") {
                    let referenceId: Int = 17
                    node.update(["id" : referenceId])
                    expect(node.id).to(equal(referenceId))
                }

                it("should update 'selected' prop") {
                    node.update(["selected": true])
                    expect(node.selected).to(beTrue())
                    node.update(["selected": false])
                    expect(node.selected).to(beFalse())
                }

                context("when editing 'textColor' prop") {
                    it("should not update 'textColor' prop") {
                        let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                        node.update(["textColor" : referenceTextColor.toArrayOfFloat])
                        expect(node.textColor).notTo(beCloseTo(referenceTextColor))
                        expect(node.isLayoutNeeded).to(beFalse())
                    }

                    it("should set 'textColor' value") {
                        let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                        node.textColor = referenceTextColor
                        expect(node.textColor).to(beCloseTo(referenceTextColor))
                        expect(node.isLayoutNeeded).to(beTrue())
                    }
                }

                context("when editing 'textSize' prop") {
                    it("should not update 'textSize' prop") {
                        let referenceTextSize = 11.0
                        node.update(["textSize" : referenceTextSize])
                        expect(node.textSize).notTo(beCloseTo(referenceTextSize))
                        expect(node.textSize).to(beCloseTo(0))
                        expect(node.isLayoutNeeded).to(beFalse())
                    }

                    it("should set 'textSize' value") {
                        let referenceTextSize: CGFloat = 11.0
                        node.textSize = referenceTextSize
                        expect(node.textSize).to(beCloseTo(referenceTextSize))
                        expect(node.isLayoutNeeded).to(beTrue())
                    }
                }

                context("when updating 'maxCharacterLimit' prop") {
                    it("should not update stored value") {
                        let referenceLimit = 11
                        node.update(["maxCharacterLimit" : referenceLimit])
                        expect(node.maxCharacterLimit).notTo(equal(referenceLimit))
                        expect(node.maxCharacterLimit).to(equal(0))
                        expect(node.isLayoutNeeded).to(beFalse())
                    }

                    it("should update label text according") {
                        let referenceText = "1234567890"
                        let referenceLimit = 7
                        node.label = referenceText
                        node.maxCharacterLimit = referenceLimit

                        let labelNode: LabelNode = node.contentNode.childNodes[0] as! LabelNode
                        expect(labelNode.text).to(equal(referenceText.prefix(referenceLimit) + "..."))
                        expect(node.label).to(equal(referenceText))
                    }
                }
            }

            context("selection") {
                it("should maintain selection state") {
                    node.toggleSelection()
                    expect(node.isLayoutNeeded).to(beFalse())
                    expect(node.selected).to(beTrue())
                    node.toggleSelection()
                    expect(node.selected).to(beFalse())
                }
            }

            context("focus") {
                it("should not have focus") {
                    node.enterFocus()
                    expect(node.hasFocus).to(beFalse())
                }
            }

            context("activate") {
                it("should call onActivate callback") {
                    var activateCalled: Bool = false
                    node.onActivate = { _ in
                        activateCalled = true
                    }

                    expect(activateCalled).to(beFalse())
                    node.activate()
                    expect(activateCalled).to(beTrue())
                }
            }
        }
    }
}
