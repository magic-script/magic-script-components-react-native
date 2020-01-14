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
                    let items = self.prepareSampleDropdownList(node: node)
                    let referenceTextSize = 11.0
                    node.update(["textSize" : referenceTextSize])
                    expect(node.textSize).to(beCloseTo(referenceTextSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    expect(self.getLabelNode(node).textSize).to(beCloseTo(referenceTextSize))
                    for item in items {
                        expect(item.textSize).to(beCloseTo(referenceTextSize))
                    }
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
                    let items = self.prepareSampleDropdownList(node: node)
                    expect(node.multiSelectMode).to(beFalse())

                    node.update(["multiSelectMode" : true])
                    expect(node.multiSelectMode).to(beTrue())
                    expect(node.isLayoutNeeded).to(beFalse())

                    items.forEach { node.handleTap($0) }
                    items.forEach { expect($0.selected).to(beTrue()) }

                    node.multiSelectMode = false
                    items.forEach { expect($0.selected).to(beFalse()) }
                }
            }

            context("focus") {
                it("should not leave focus on behalf of UiDropdownListItemNode") {
                    node.enterFocus()
                    expect(node.hasFocus).to(beTrue())

                    let itemNode = UiDropdownListItemNode()
                    let result = node.leaveFocus(onBehalfOf: itemNode)
                    expect(result).to(beFalse())
                    expect(node.hasFocus).to(beTrue())
                }

                it("should not leave focus on behalf of self node") {
                    node.enterFocus()
                    expect(node.hasFocus).to(beTrue())

                    let result = node.leaveFocus(onBehalfOf: node)
                    expect(result).to(beFalse())
                    expect(node.hasFocus).to(beTrue())
                }

                it("should leave focus on behalf of other nodes") {
                    node.enterFocus()
                    expect(node.hasFocus).to(beTrue())

                    let result1 = node.leaveFocus(onBehalfOf: nil)
                    expect(result1).to(beTrue())
                    expect(node.hasFocus).to(beFalse())

                    node.enterFocus()
                    expect(node.hasFocus).to(beTrue())

                    let buttonNode = UiButtonNode()
                    let result2 = node.leaveFocus(onBehalfOf: buttonNode)
                    expect(result2).to(beTrue())
                    expect(node.hasFocus).to(beFalse())
                }

                context("when entering/leaving focus") {
                    it("should maintain list visibility") {
                        let listNode = self.getListNode(node)
                        node.enterFocus()
                        expect(listNode.isHidden).to(beFalse())
                        expect(node.isListExpanded).to(beTrue())

                        node.leaveFocus(onBehalfOf: nil)
                        expect(listNode.isHidden).to(beTrue())
                        expect(node.isListExpanded).to(beFalse())
                    }
                }
            }

            context("when item added") {
                it("should add UiDropdownListItemNode to the list") {
                    let initialNodesCount = node.contentNode.childNodes.count
                    let listNode = self.getListNode(node)
                    let gridLayoutNode = listNode.childNodes[0] as! UiGridLayoutNode
                    expect(gridLayoutNode.itemsCount).to(equal(0))
                    expect(node.contentNode.childNodes.count).to(equal(initialNodesCount))

                    let itemNode = UiDropdownListItemNode()
                    node.addChild(itemNode)
                    expect(gridLayoutNode.itemsCount).to(equal(1))
                    expect(node.contentNode.childNodes.count).to(equal(initialNodesCount))
                    expect(itemNode.tapHandler).toNot(beNil())
                }

                it("should add other type of nodes as standard sub-components") {
                    let initialNodesCount = node.contentNode.childNodes.count
                    let listNode = self.getListNode(node)
                    let gridLayoutNode = listNode.childNodes[0] as! UiGridLayoutNode
                    expect(gridLayoutNode.itemsCount).to(equal(0))
                    expect(node.contentNode.childNodes.count).to(equal(initialNodesCount))

                    let transformNode = TransformNode()
                    node.addChild(transformNode)
                    expect(gridLayoutNode.itemsCount).to(equal(0))
                    expect(node.contentNode.childNodes.count).to(equal(initialNodesCount + 1))
                }
            }

            context("when item removed") {
                it("should remove UiDropdownListItemNode from the list") {
                    let itemNode = UiDropdownListItemNode()
                    node.addChild(itemNode)

                    let listNode = self.getListNode(node)
                    let gridLayoutNode = listNode.childNodes[0] as! UiGridLayoutNode
                    expect(gridLayoutNode.itemsCount).to(equal(1))

                    let otherNode = TransformNode()
                    node.removeChild(otherNode)
                    expect(gridLayoutNode.itemsCount).to(equal(1))

                    node.removeChild(itemNode)
                    expect(gridLayoutNode.itemsCount).to(equal(0))
                    expect(itemNode.tapHandler).to(beNil())
                }

                it("should remove other type of nodes from the nodes hierarchy") {
                    let initialNodesCount = node.contentNode.childNodes.count
                    let transformNode = TransformNode()
                    node.addChild(transformNode)
                    expect(node.contentNode.childNodes.count).to(equal(initialNodesCount + 1))

                    node.removeChild(transformNode)
                    expect(node.contentNode.childNodes.count).to(equal(initialNodesCount))
                }
            }

            context("when handling item tap") {
                context("single selection mode") {
                    it("should select item") {
                        let items = self.prepareSampleDropdownList(node: node)
                        let dummyItemNode1 = items[0]
                        let dummyItemNode2 = items[1]

                        node.handleTap(dummyItemNode1)
                        expect(node.selectedItems).to(contain(dummyItemNode1))
                        expect(dummyItemNode1.selected).to(beTrue())

                        node.handleTap(dummyItemNode2)
                        expect(node.selectedItems).notTo(contain(dummyItemNode1))
                        expect(dummyItemNode1.selected).to(beFalse())

                        expect(node.selectedItems).to(contain(dummyItemNode2))
                        expect(dummyItemNode2.selected).to(beTrue())
                    }

                    it("should not deselect item") {
                        let items = self.prepareSampleDropdownList(node: node)
                        let dummyItemNode = items[0]
                        node.handleTap(dummyItemNode)
                        node.handleTap(dummyItemNode)

                        expect(node.selectedItems).to(contain(dummyItemNode))
                        expect(dummyItemNode.selected).to(beTrue())
                    }
                }

                context("multi selection mode") {
                    it("should select items") {
                        let items = self.prepareSampleDropdownList(node: node)
                        let dummyItemNode1 = items[0]
                        let dummyItemNode2 = items[1]
                        node.multiSelectMode = true

                        node.handleTap(dummyItemNode1)
                        expect(node.selectedItems).to(contain(dummyItemNode1))
                        expect(dummyItemNode1.selected).to(beTrue())

                        node.handleTap(dummyItemNode2)
                        expect(node.selectedItems).to(contain(dummyItemNode1))
                        expect(dummyItemNode1.selected).to(beTrue())

                        expect(node.selectedItems).to(contain(dummyItemNode2))
                        expect(dummyItemNode2.selected).to(beTrue())
                    }

                    it("should deselect item") {
                        node.multiSelectMode = true
                        let dummyItemNode = UiDropdownListItemNode()
                        node.handleTap(dummyItemNode)
                        node.handleTap(dummyItemNode)

                        expect(node.selectedItems).notTo(contain(dummyItemNode))
                        expect(dummyItemNode.selected).to(beFalse())
                    }
                }

                it("notify upper layer") {
                    var upperLayerNotified = false
                    node.onSelectionChanged = { sender, selectedItems in
                        upperLayerNotified = true
                    }
                    let dummyItemNode = UiDropdownListItemNode()
                    node.handleTap(dummyItemNode)
                    expect(upperLayerNotified).to(beTrue())
                }
            }

            context("when asked for size") {
                it("should return it based on width and height") {
                    let referenceSize = CGSize(width: 1.75, height: 0.25)
                    node.update(["width": referenceSize.width, "height" : referenceSize.height])
                    node.layoutIfNeeded()
                    expect(node.getSize()).to(beCloseTo(referenceSize))
                }
            }

            context("backgroundNode") {
                it("should reuse backgroundNode when layout is updated multiple times") {
                    self.prepareSampleDropdownList(node: node)
                    expect(self.getBackgroundNode(node)).to(beNil())
                    node.setNeedsLayout()
                    node.layoutIfNeeded()
                    expect(self.getBackgroundNode(node)).to(beNil())

                    // open dropdown list
                    node.enterFocus()
                    let backgroundNode = self.getBackgroundNode(node)
                    expect(backgroundNode).notTo(beNil())

                    // update background by updating layout
                    node.setNeedsLayout()
                    node.layoutIfNeeded()
                    expect(self.getBackgroundNode(node)).to(beIdenticalTo(backgroundNode))
                }
            }

            context("debug mode") {
                it("should set debug mode") {
                    let labelNode = self.getLabelNode(node)
                    expect(labelNode).notTo(beNil())
                    let referenceLabelNodeChildNodesCount: Int = labelNode.childNodes.count
                    node.setDebugMode(true)
                    expect(referenceLabelNodeChildNodesCount + 2).to(equal(labelNode.childNodes.count))
                }
            }

            context("hitTest") {
                it("should return nil") {
                    self.prepareSampleDropdownList(node: node)

                    let ray = Ray(begin: SCNVector3(0, 10, 1), direction: SCNVector3(1, 0, 0), length: 2)
                    expect(node.hitTest(ray: ray)).to(beNil())
                }

                it("should return self") {
                    self.prepareSampleDropdownList(node: node)
                    expect(node.isListExpanded).to(beFalse())

                    let ray = Ray(begin: SCNVector3(0, 0, 1), direction: SCNVector3(0, 0, -1), length: 2)
                    expect(node.hitTest(ray: ray)).to(beIdenticalTo(node))

                    node.enterFocus()
                    expect(node.isListExpanded).to(beTrue())
                    expect(node.hitTest(ray: ray)).to(beIdenticalTo(node))
                }

                it("should return item node") {
                    let items = self.prepareSampleDropdownList(node: node)
                    node.enterFocus()
                    let size = node.getSize()
                    let textHeight: CGFloat = self.getLabelNode(node).defaultTextSize
                    let x = -0.5 * size.width
                    let y = -0.5 * (size.height + textHeight)
                    let ray = Ray(begin: SCNVector3(x, y, 1), direction: SCNVector3(0, 0, -1), length: 2)
                    expect(node.hitTest(ray: ray)).to(beIdenticalTo(items[0]))
                }
            }
        }
    }

    fileprivate func getLabelNode(_ node: UiDropdownListNode) -> LabelNode {
        return node.contentNode.childNodes[0] as! LabelNode
    }

    fileprivate func getIconNode(_ node: UiDropdownListNode) -> SCNNode {
        return node.contentNode.childNodes[1]
    }

    fileprivate func getListNode(_ node: UiDropdownListNode) -> SCNNode {
        return node.contentNode.childNodes[2]
    }

    fileprivate func getBackgroundNode(_ node: UiDropdownListNode) -> SCNNode? {
        let childNodes = getListNode(node).childNodes
        return childNodes.count == 2 ? childNodes.first : nil
    }

    @discardableResult
    fileprivate func prepareSampleDropdownList(node: UiDropdownListNode) -> [UiDropdownListItemNode] {
        node.text = "Dropdown List"
        var items: [UiDropdownListItemNode] = []
        for i in 0..<10 {
            let itemNode = UiDropdownListItemNode(props: ["label": "Item \(i + 1)"])
            node.addChild(itemNode)
            items.append(itemNode)
        }
        node.layoutIfNeeded()

        return items
    }
}
