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

class UiListViewNodeSpec: QuickSpec {
    override func spec() {
        describe("UiListViewNode") {
            var node: UiListViewNode!

            beforeEach {
                node = UiListViewNode(props: [:])
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.width).to(beCloseTo(0))
                    expect(node.height).to(beCloseTo(0))
                    expect(node.layoutOrientation).to(equal(Orientation.vertical))
                    expect(node.defaultItemAlignment).to(equal(Alignment.centerCenter))
                    expect(node.defaultItemPadding).to(beCloseTo(UIEdgeInsets.zero))
                    expect(node.scrollingEnabled).to(beTrue())
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
                    let referenceHeight = 3.85
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                
                it("should update 'orientation' prop") {
                    node = UiListViewNode(props: ["orientation": "horizontal"])
                    expect(node.layoutOrientation).to(equal(Orientation.horizontal))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'defaultItemAlignment' prop") {
                    node = UiListViewNode(props: ["defaultItemAlignment": "top-right"])
                    expect(node.defaultItemAlignment).to(equal(Alignment.topRight))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    node.update(["defaultItemAlignment" : "bottom-left"])
                    expect(node.defaultItemAlignment).to(equal(Alignment.bottomLeft))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'defaultItemPadding' prop") {
                    let referencePadding = UIEdgeInsets(top: 0.1, left: 0.2, bottom: 0.3, right: 0.4)
                    node = UiListViewNode(props: ["defaultItemPadding": referencePadding.toArrayOfFloat])
                    expect(node.defaultItemPadding).to(beCloseTo(referencePadding))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'scrollingEnabled' prop") {
                    node = UiListViewNode(props: ["scrollingEnabled": false])
                    expect(node.scrollingEnabled).to(beFalse())
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("when item added") {
                context("when item is ListView item") {
                    it("should add it to the list node") {
                        let itemNode = UiListViewItemNode(props: [:])
                        node.addChild(itemNode)
                        expect(node.items.count).to(equal(1))

                        let otherNode = TransformNode(props: [:])
                        node.addChild(otherNode)
                        expect(node.items.count).to(equal(1))
                    }
                }
            }

            context("when updating layout") {
                context("when vertical layout orientation") {
                    it("should set its width according to width of all elements - #1") {
                        let buttonNode = UiButtonNode(props: ["width": 0.25, "height": 0.125])
                        let itemNode = UiListViewItemNode(props: [:])
                        itemNode.addChild(buttonNode)
                        node.addChild(itemNode)
                        node.updateLayout() // calculation trigger
                        expect(node.items.count).to(equal(1))
                        let item = node.items.first
                        expect(item?.preferredWidth).to(beCloseTo(0.25))
                    }

                    it("should set its width according to width of all elements - #2") {
                        let buttonNode1 = UiButtonNode(props: ["width": 0.25, "height": 0.125])
                        let itemNode1 = UiListViewItemNode(props: [:])
                        itemNode1.addChild(buttonNode1)
                        node.addChild(itemNode1)
                        node.updateLayout()
                        expect(node.items.count).to(equal(1))
                        var item1 = node.items[0]
                        expect(item1.preferredWidth).to(beCloseTo(0.25))

                        let buttonNode2 = UiButtonNode(props: ["width": 0.75, "height": 0.0625])
                        let itemNode2 = UiListViewItemNode(props: [:])
                        itemNode2.addChild(buttonNode2)
                        node.addChild(itemNode2)
                        node.updateLayout() // calculation trigger
                        expect(node.items.count).to(equal(2))
                        item1 = node.items[0]
                        expect(item1.preferredWidth).to(beCloseTo(0.75))
                        expect(item1.preferredHeight).to(beCloseTo(0.0))
                        let item2 = node.items[1]
                        expect(item2.preferredWidth).to(beCloseTo(0.75))
                        expect(item2.preferredHeight).to(beCloseTo(0.0))
                    }
                }

                context("when horizontal layout orientation") {
                    beforeEach {
                        node.layoutOrientation = .horizontal
                    }

                    it("should set its height according to width of all elements - #1") {
                        let buttonNode = UiButtonNode(props: ["width": 0.25, "height": 0.125])
                        let itemNode = UiListViewItemNode(props: [:])
                        itemNode.addChild(buttonNode)
                        node.addChild(itemNode)
                        node.updateLayout() // calculation trigger
                        expect(node.items.count).to(equal(1))
                        let item = node.items.first
                        expect(item?.preferredHeight).to(beCloseTo(0.125))
                    }

                    it("should set its htight according to width of all elements - #2") {
                        let buttonNode1 = UiButtonNode(props: ["width": 0.25, "height": 0.125])
                        let itemNode1 = UiListViewItemNode(props: [:])
                        itemNode1.addChild(buttonNode1)
                        node.addChild(itemNode1)
                        node.updateLayout()
                        expect(node.items.count).to(equal(1))
                        var item1 = node.items[0]
                        expect(item1.preferredHeight).to(beCloseTo(0.125))

                        let buttonNode2 = UiButtonNode(props: ["width": 0.5, "height": 0.0625])
                        let itemNode2 = UiListViewItemNode(props: [:])
                        itemNode2.addChild(buttonNode2)
                        node.addChild(itemNode2)
                        node.updateLayout() // calculation trigger
                        expect(node.items.count).to(equal(2))
                        item1 = node.items[0]
                        expect(item1.preferredHeight).to(beCloseTo(0.125))
                        expect(item1.preferredWidth).to(beCloseTo(0.0))
                        let item2 = node.items[1]
                        expect(item2.preferredHeight).to(beCloseTo(0.125))
                        expect(item2.preferredWidth).to(beCloseTo(0.0))
                    }
                }
            }

            context("when item removed") {
                it("should remove it from the list node") {
                    let itemNode = UiListViewItemNode(props: [:])
                    node.addChild(itemNode)
                    expect(node.items.count).to(equal(1))

                    let otherNode = TransformNode(props: [:])
                    node.removeChild(otherNode)
                    expect(node.items.count).to(equal(1))

                    node.removeChild(itemNode)
                    expect(node.items.count).to(equal(0))
                }
            }

            context("when asked for size") {
                context("when width & height set") {
                    it("should return them") {
                        let node = UiListViewNode(props: ["width": 0.75, "height": 0.25])
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.75, height: 0.25)))
                    }
                }
            }
        }
    }
}
