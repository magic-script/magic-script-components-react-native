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

class GridLayoutSpec: QuickSpec {
    override func spec() {
        describe("GridLayout") {
            var layout: GridLayout!

            beforeEach() {
                layout = GridLayout()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(layout.columns).to(equal(0))
                    expect(layout.rows).to(equal(0))
                    expect(layout.defaultItemAlignment).to(equal(Alignment.centerCenter))
                    expect(layout.defaultItemPadding).to(beCloseTo(UIEdgeInsets.zero))
                    expect(layout.skipInvisibleItems).to(beFalse())
                    expect(layout.itemsCount).to(equal(0))
                    expect(layout.container).notTo(beNil())
                    expect(layout.recalculateNeeded).to(beTrue())
                }
            }

            context("update properties") {
                it("should update 'columns' prop") {
                    layout.columns = 2
                    expect(layout.columns).to(equal(2))
                    expect(layout.recalculateNeeded).to(beTrue())
                }

                it("should update 'rows' prop") {
                    layout.rows = 2
                    expect(layout.rows).to(equal(2))
                    expect(layout.recalculateNeeded).to(beTrue())
                }

                it("should update 'defaultItemAlignment' prop") {
                    layout.defaultItemAlignment = Alignment.bottomRight
                    expect(layout.defaultItemAlignment).to(equal(Alignment.bottomRight))
                    expect(layout.recalculateNeeded).to(beTrue())
                }

                it("should update 'defaultItemPadding' prop") {
                    let referencePadding = UIEdgeInsets(top: 0.1, left: 0.2, bottom: 0.3, right: 0.4)
                    layout.defaultItemPadding = referencePadding
                    expect(layout.defaultItemPadding).to(beCloseTo(referencePadding))
                    expect(layout.recalculateNeeded).to(beTrue())
                }

                it("should update 'skipInvisibleItems' prop") {
                    layout.skipInvisibleItems = true
                    expect(layout.skipInvisibleItems).to(beTrue())
                    expect(layout.recalculateNeeded).to(beTrue())
                }
            }

            context("add/remove items") {
                it("should add item correctly") {
                    let referenceNode = TransformNode(props: [:])
                    layout.addItem(referenceNode)
                    expect(layout.itemsCount).to(equal(1))
                    expect(layout.container.childNodes.count).to(equal(1))

                    guard let proxyChild = layout.container.childNodes.first,
                        let childChildNode = proxyChild.childNodes.first else { fail("Sth wrong with child nodes structure."); return }
                    expect(childChildNode).to(beIdenticalTo(referenceNode))
                }

                it("should return true if removed item exists as a child node") {
                    let referenceNode = TransformNode(props: [:])
                    layout.addItem(referenceNode)
                    let result = layout.removeItem(referenceNode)
                    expect(result).to(beTrue())
                    expect(layout.itemsCount).to(equal(0))
                    expect(layout.container.childNodes.count).to(equal(0))
                }

                it("should return false if removed item does not exist as a child node") {
                    let referenceNode1 = TransformNode(props: [:])
                    let referenceNode2 = TransformNode(props: [:])
                    layout.addItem(referenceNode1)
                    let result = layout.removeItem(referenceNode2)
                    expect(result).to(beFalse())
                    expect(layout.itemsCount).to(equal(1))
                    expect(layout.container.childNodes.count).to(equal(1))
                }
            }

            context("items count") {
                it("should return proper number of items") {
                    for i in 1...10 {
                        let referenceNode = TransformNode(props: [:])
                        layout.addItem(referenceNode)
                        expect(layout.itemsCount).to(equal(i))
                        expect(layout.container.childNodes.count).to(equal(i))
                    }
                }
            }

            context("hitTest") {
                it("should return nil if ray does not hit the area of layout") {
                    let referenceSize: CGFloat = 0.08
                    let referenceNode = UiImageNode(props: ["alignment": "center-center", "icon": "address-book", "height": referenceSize])
                    layout.addItem(referenceNode)
                    layout.recalculate()

                    let ray = Ray(begin: SCNVector3(-1, 0, 1), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = layout.hitTest(ray: ray, node: referenceNode)
                    expect(result).to(beNil())
                }

                it("should return hit node if ray hits the area of layout") {
                    let referenceSize: CGFloat = 0.08
                    let referenceNode = UiImageNode(props: ["alignment": "center-center", "icon": "address-book", "height": referenceSize])
                    layout.addItem(referenceNode)
                    layout.recalculate()

                    let ray = Ray(begin: SCNVector3(0, 0, 1), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = layout.hitTest(ray: ray, node: referenceNode)
                    expect(result).to(beIdenticalTo(referenceNode))
                }
            }

            context("when asked for size") {
                it("should calculate it according to configuration") {
                    layout.columns = 0
                    layout.rows = 0
                    expect(layout.getSize()).to(beCloseTo(CGSize.zero))

                    var referenceWidth: CGFloat = 0.0
                    var referenceHeight: CGFloat = 0.0

                    for number in 1...4 {
                        let referenceNode = UiButtonNode(props: [:])
                        referenceNode.name = "button_\(number)"
                        referenceNode.text = "Default text"
                        layout.addItem(referenceNode)
                        referenceWidth = referenceNode.getSize().width
                        referenceHeight = referenceNode.getSize().height
                    }

                    // If neither rows or columns are set, the grid layout will have 1 row and add columns as needed.
                    layout.recalculate()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 4 * referenceWidth, height: referenceHeight)))

                    layout.columns = 2
                    layout.rows = 2
                    layout.recalculate()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 2 * referenceWidth, height: 2 * referenceHeight)))

                    layout.columns = 3
                    layout.rows = 3
                    layout.recalculate()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 3 * referenceWidth, height: 2 * referenceHeight)))

                    // If both 'columns' and 'rows' props are set to be non-zero, the columns will take precedence.
                    layout.columns = 1
                    layout.rows = 1
                    layout.recalculate()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: referenceWidth, height: 4 * referenceHeight)))

                    layout.columns = 0
                    layout.rows = 1
                    layout.recalculate()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 4 * referenceWidth, height: referenceHeight)))

                    layout.columns = 1
                    layout.rows = 0
                    layout.recalculate()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: referenceWidth, height: 4 * referenceHeight)))
                }

                it("should ignore invisible items in calculation") {
                    layout.columns = 0
                    layout.rows = 1
                    layout.skipInvisibleItems = false
                    expect(layout.getSize()).to(beCloseTo(CGSize.zero))

                    var referenceWidth: CGFloat = 0.0
                    var referenceHeight: CGFloat = 0.0

                    for number in 1...4 {
                        let referenceNode = UiButtonNode(props: [:])
                        referenceNode.name = "button_\(number)"
                        referenceNode.text = "Default text"
                        referenceNode.visible = (number % 2 == 0)
                        layout.addItem(referenceNode)
                        referenceWidth = referenceNode.getSize().width
                        referenceHeight = referenceNode.getSize().height
                    }

                    layout.recalculate()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 4 * referenceWidth, height: referenceHeight)))

                    layout.skipInvisibleItems = true
                    layout.recalculate()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 2 * referenceWidth, height: referenceHeight)))
                }
            }
        }
    }
}
