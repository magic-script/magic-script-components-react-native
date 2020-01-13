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
                    expect(layout.width).to(beCloseTo(0))
                    expect(layout.height).to(beCloseTo(0))
                    expect(layout.columns).to(equal(0))
                    expect(layout.rows).to(equal(0))
                    expect(layout.defaultItemAlignment).to(equal(Alignment.topLeft))
                    expect(layout.defaultItemPadding).to(beCloseTo(UIEdgeInsets.zero))
                    expect(layout.skipInvisibleItems).to(beFalse())
                    expect(layout.itemsCount).to(equal(0))
                    expect(layout.container).notTo(beNil())
                    expect(layout.recalculateNeeded).to(beTrue())
                }
            }

            context("update properties") {
                it("should update 'width' prop") {
                    let referenceWidth: CGFloat = 0.7
                    layout.width = referenceWidth
                    expect(layout.width).to(beCloseTo(referenceWidth))
                    expect(layout.recalculateNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    let referenceHeight: CGFloat = 0.8
                    layout.height = referenceHeight
                    expect(layout.height).to(beCloseTo(referenceHeight))
                    expect(layout.recalculateNeeded).to(beTrue())
                }

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
                    let referenceNode = TransformNode()
                    layout.addItem(referenceNode)
                    expect(layout.itemsCount).to(equal(1))
                    expect(layout.container.childNodes.count).to(equal(1))

                    guard let proxyChild = layout.container.childNodes.first,
                        let childChildNode = proxyChild.childNodes.first else { fail("Sth wrong with child nodes structure."); return }
                    expect(childChildNode).to(beIdenticalTo(referenceNode))
                }

                it("should return true if removed item exists as a child node") {
                    let referenceNode = TransformNode()
                    layout.addItem(referenceNode)
                    let result = layout.removeItem(referenceNode)
                    expect(result).to(beTrue())
                    expect(layout.itemsCount).to(equal(0))
                    expect(layout.container.childNodes.count).to(equal(0))
                }

                it("should return false if removed item does not exist as a child node") {
                    let referenceNode1 = TransformNode()
                    let referenceNode2 = TransformNode()
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
                        let referenceNode = TransformNode()
                        layout.addItem(referenceNode)
                        expect(layout.itemsCount).to(equal(i))
                        expect(layout.container.childNodes.count).to(equal(i))
                    }
                }
            }

            context("hitTest") {

                it("should return nil if layout is invalidated") {
                    let referenceSize: CGFloat = 0.1
                    let nodes = self.prepare2x2GridLayout(layout, childSize: referenceSize)
                    layout.invalidate()

                    let ray = Ray(begin: SCNVector3(-0.5 * referenceSize, 0, 0.5 * referenceSize), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = layout.hitTest(ray: ray, node: nodes[0])
                    expect(result).to(beNil())
                }

                it("should return nil if ray does not hit the area of layout") {
                    let referenceSize: CGFloat = 0.1
                    let nodes = self.prepare2x2GridLayout(layout, childSize: referenceSize)

                    let ray = Ray(begin: SCNVector3(-1, 0, 1), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = layout.hitTest(ray: ray, node: nodes[0])
                    expect(result).to(beNil())
                }

                it("should return hit node if ray hits the area of layout") {
                    let referenceSize: CGFloat = 0.1
                    let nodes = self.prepare2x2GridLayout(layout, childSize: referenceSize)
                    (nodes.last as? UiImageNode)?.height = 0.5 * referenceSize

                    let begins: [SCNVector3] = [
                        SCNVector3(-0.5 * referenceSize, 0.5 * referenceSize, 0),
                        SCNVector3( 0.5 * referenceSize, 0.5 * referenceSize, 0),
                        SCNVector3(-0.5 * referenceSize, -0.5 * referenceSize, 0),
                        SCNVector3( 0.5 * referenceSize, -0.5 * referenceSize, 0)
                    ]
                    for i in 0..<4 {
                        let ray = Ray(begin: begins[i], direction: SCNVector3(0, 0, -1), length: 3)
                        let result = layout.hitTest(ray: ray, node: nodes[i])
                        expect(result).to(beIdenticalTo(nodes[i]))
                    }
                }
            }

            context("when asked for size") {
                it("should calculate it according to configuration (default width and height)") {
                    layout.columns = 0
                    layout.rows = 0
                    expect(layout.getSize()).to(beCloseTo(CGSize.zero))

                    var referenceWidth: CGFloat = 0.0
                    var referenceHeight: CGFloat = 0.0

                    for number in 1...4 {
                        let referenceNode = UiButtonNode()
                        referenceNode.name = "button_\(number)"
                        referenceNode.text = "Default text"
                        layout.addItem(referenceNode)
                        referenceWidth = referenceNode.getSize().width
                        referenceHeight = referenceNode.getSize().height
                    }

                    // If neither rows or columns are set, the grid layout will have 1 row and add columns as needed.
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 4 * referenceWidth, height: referenceHeight)))

                    layout.columns = 2
                    layout.rows = 2
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 2 * referenceWidth, height: 2 * referenceHeight)))

                    layout.columns = 3
                    layout.rows = 3
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 3 * referenceWidth, height: 2 * referenceHeight)))

                    // If both 'columns' and 'rows' props are set to be non-zero, the columns will take precedence.
                    layout.columns = 1
                    layout.rows = 1
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: referenceWidth, height: 4 * referenceHeight)))

                    layout.columns = 0
                    layout.rows = 1
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 4 * referenceWidth, height: referenceHeight)))

                    layout.columns = 1
                    layout.rows = 0
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: referenceWidth, height: 4 * referenceHeight)))
                }

                it("should calculate it according to configuration (defined width and height)") {
                    let referenceSize = CGSize(width: 0.5, height: 0.25)
                    layout.width = referenceSize.width
                    layout.height = referenceSize.height
                    layout.columns = 0
                    layout.rows = 0
                    expect(layout.getSize()).to(beCloseTo(CGSize.zero))

                    for number in 1...4 {
                        let referenceNode = UiButtonNode()
                        referenceNode.name = "button_\(number)"
                        referenceNode.text = "Default text"
                        layout.addItem(referenceNode)
                    }

                    // If neither rows or columns are set, the grid layout will have 1 row and add columns as needed.
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(referenceSize))

                    layout.columns = 2
                    layout.rows = 2
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(referenceSize))

                    layout.columns = 3
                    layout.rows = 3
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(referenceSize))

                    // If both 'columns' and 'rows' props are set to be non-zero, the columns will take precedence.
                    layout.columns = 1
                    layout.rows = 1
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(referenceSize))

                    layout.columns = 0
                    layout.rows = 1
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(referenceSize))

                    layout.columns = 1
                    layout.rows = 0
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(referenceSize))
                }

                it("should ignore invisible items in calculation") {
                    layout.columns = 0
                    layout.rows = 1
                    layout.skipInvisibleItems = false
                    expect(layout.getSize()).to(beCloseTo(CGSize.zero))

                    var referenceWidth: CGFloat = 0.0
                    var referenceHeight: CGFloat = 0.0

                    for number in 1...4 {
                        let referenceNode = UiButtonNode()
                        referenceNode.name = "button_\(number)"
                        referenceNode.text = "Default text"
                        referenceNode.visible = (number % 2 == 0)
                        layout.addItem(referenceNode)
                        referenceWidth = referenceNode.getSize().width
                        referenceHeight = referenceNode.getSize().height
                    }

                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 4 * referenceWidth, height: referenceHeight)))

                    layout.skipInvisibleItems = true
                    layout.recalculateIfNeeded()
                    expect(layout.getSize()).to(beCloseTo(CGSize(width: 2 * referenceWidth, height: referenceHeight)))
                }
            }
        }
    }

    @discardableResult
    func prepare2x2GridLayout(_ gridLayout: GridLayout, childSize: CGFloat = 0.1) -> [UiNode] {
        let icons = ["eject", "emoji", "enter", "exit"]
        var nodes: [UiNode] = []
        for icon in icons {
            let node = UiImageNode(props: ["alignment": "center-center", "icon": icon, "height": childSize])
            nodes.append(node)
            gridLayout.addItem(node)
        }
        gridLayout.columns = 2
        gridLayout.recalculateIfNeeded()
        gridLayout.updateLayout()
        return nodes
    }
}
