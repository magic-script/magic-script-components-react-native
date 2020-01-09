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

class UiDropdownListNodeSpec: QuickSpec {
    override func spec() {
        describe("UiDropdownListNode") {
            var node: UiDropdownListNode!
            let shortReferenceText: String = "Info text"

            beforeEach {
                node = UiDropdownListNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.text).to(beNil())
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.textSize).to(beCloseTo(0.0))
                    expect(node.width).to(beCloseTo(0.0))
                    expect(node.height).to(beCloseTo(0.0))
                    expect(node.canHaveFocus).to(beTrue())
                    expect(node.maxHeight).to(beCloseTo(0.0))
                    expect(node.maxCharacterLimit).to(equal(0))
                    expect(node.multiSelectMode).to(beFalse())
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

                    expect(self.getLabelNode(node).text).to(equal(shortReferenceText))
                }

                it("should update 'textColor' prop") {
                    let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["textColor" : referenceTextColor.toArrayOfFloat])
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.isLayoutNeeded).to(beTrue())

                    expect(self.getLabelNode(node).textColor).to(beCloseTo(referenceTextColor))
                }

                it("should update 'textSize' prop") {
                    let referenceTextSize = 11.0
                    node.update(["textSize" : referenceTextSize])
                    expect(node.textSize).to(beCloseTo(referenceTextSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    expect(self.getLabelNode(node).textSize).to(beCloseTo(referenceTextSize))
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

                it("should update 'maxHeight' prop") {
                    let referenceHeight = 3.75
                    node.update(["maxHeight" : referenceHeight])
                    expect(node.maxHeight).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                context("when updating 'maxCharacterLimit' prop") {
                    it("should maintain the value") {
                        let referenceMaxCharacterLimit = 11
                        node.update(["maxCharacterLimit" : referenceMaxCharacterLimit])
                        expect(node.maxCharacterLimit).to(equal(referenceMaxCharacterLimit))
                        expect(node.isLayoutNeeded).to(beTrue())
                    }

                    it("should propagate new value to item nodes") {
                        let itemNode1 = UiDropdownListItemNode()
                        node.addChild(itemNode1)
                        let itemNode2 = UiDropdownListItemNode()
                        node.addChild(itemNode2)

                        let referenceMaxCharacterLimit = 11
                        node.update(["maxCharacterLimit" : referenceMaxCharacterLimit])
                        expect(itemNode1.maxCharacterLimit).to(equal(referenceMaxCharacterLimit))
                        expect(itemNode2.maxCharacterLimit).to(equal(referenceMaxCharacterLimit))
                    }
                }

                it("should update 'multiSelectMode' prop") {
                    node.update(["multiSelectMode" : true])
                    expect(node.multiSelectMode).to(beTrue())
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            /*context("focus") {
                it("should maintaing focus state") {
                    node.enterFocus()
                    expect(node.hasFocus).to(beTrue())

                    node.leaveFocus()
                    expect(node.hasFocus).to(beFalse())
                }

                context("when entering/leaving focus") {
                    it("should maintain list visibility") {
                        node.enterFocus()
                        expect(node.listGridLayoutNode.visible).to(beTrue())

                        node.leaveFocus()
                        expect(node.listGridLayoutNode.visible).to(beFalse())
                    }
                }
            }

            context("when item added") {
                context("when item is DropdownList item") {
                    it("should add it to the list node") {
                        let itemNode = UiDropdownListItemNode()
                        node.addChild(itemNode)
                        expect(node.listGridLayoutNode.contentNode.childNodes.count).to(equal(1))
                        expect(itemNode.tapHandler).toNot(beNil())

                        let otherNode = TransformNode()
                        node.addChild(otherNode)
                        expect(node.listGridLayoutNode.contentNode.childNodes.count).to(equal(1))
                    }
                }
            }

            context("when item removed") {
                it("should remove it from the list node") {
                    let itemNode = UiDropdownListItemNode()
                    node.addChild(itemNode)
                    expect(node.listGridLayoutNode.itemsCount).to(equal(1))

                    let otherNode = TransformNode()
                    node.removeChild(otherNode)
                    expect(node.listGridLayoutNode.itemsCount).to(equal(1))

                    node.removeChild(itemNode)
                    expect(node.listGridLayoutNode.itemsCount).to(equal(0))
                    expect(itemNode.tapHandler).to(beNil())
                }
            }

            context("when handling item tap") {
                it("should maintain selection state - selection") {
                    let dummyItemNode = UiDropdownListItemNode()
                    node.handleTap(dummyItemNode)

                    expect(node.selectedItem).to(equal(dummyItemNode))
                    expect(dummyItemNode.isSelected).to(beTrue())
                }

                it("should maintain selection state - deselection") {
                    let dummyItemNode = UiDropdownListItemNode()
                    node.handleTap(dummyItemNode)
                    node.handleTap(dummyItemNode)

                    expect(node.selectedItem).to(beNil())
                    expect(dummyItemNode.isSelected).to(beFalse())
                }

                it("notify upper layer") {
                    var upperLayerNotified = false
                    node.onSelectionChanged = { sender, selecctedItems in
                        upperLayerNotified = true
                    }
                    let dummyItemNode = UiDropdownListItemNode()
                    node.handleTap(dummyItemNode)
                    expect(upperLayerNotified).to(beTrue())
                }
            }

            context("when asked for size") {
                it("should calculate return it based on width and height") {
                    node.update(["width": 1.75, "height" : 0.25])
                    /* correctness of calculation should be checked in spec for derived classes */
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 1.75, height: 0.25)))
                }
            }*/
        }
    }

    fileprivate func getLabelNode(_ node: UiDropdownListNode) -> LabelNode {
        return node.contentNode.childNodes[0] as! LabelNode
    }

    fileprivate func getIconNode(_ node: UiDropdownListNode) -> SCNNode {
        return node.contentNode.childNodes[1]
    }
}
