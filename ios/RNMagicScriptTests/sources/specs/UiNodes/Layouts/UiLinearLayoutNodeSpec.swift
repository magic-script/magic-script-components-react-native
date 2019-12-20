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

class UiLinearLayoutNodeSpec: QuickSpec {
    override func spec() {
        describe("UiLinearLayoutNode") {
            var node: UiLinearLayoutNode!

            context("initial properties") {
                it("should have set default values") {
                    node = UiLinearLayoutNode()
                    expect(node.width).to(equal(0))
                    expect(node.height).to(equal(0))
                    expect(node.layoutOrientation).to(equal(Orientation.vertical))
                    expect(node.defaultItemAlignment).to(equal(Alignment.topLeft))
                    expect(node.defaultItemPadding).to(beCloseTo(UIEdgeInsets.zero))
                    expect(node.skipInvisibleItems).to(beFalse())
                    expect(node.itemsCount).to(equal(0))
                }
            }

            context("update properties") {
                it("should update 'width' prop") {
                    let referenceWidth: CGFloat = 0.7
                    node.width = referenceWidth
                    expect(node.width).to(beCloseTo(referenceWidth))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    let referenceHeight: CGFloat = 0.8
                    node.height = referenceHeight
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                
                it("should update 'orientation' prop") {
                    node = UiLinearLayoutNode(props: ["orientation": "horizontal"])
                    expect(node.layoutOrientation).to(equal(Orientation.horizontal))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'defaultItemAlignment' prop") {
                    node = UiLinearLayoutNode(props: ["defaultItemAlignment": "top-right"])
                    expect(node.defaultItemAlignment).to(equal(Alignment.topRight))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    node.update(["defaultItemAlignment" : "bottom-left"])
                    expect(node.defaultItemAlignment).to(equal(Alignment.bottomLeft))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'defaultItemPadding' prop") {
                    let referencePadding = UIEdgeInsets(top: 0.1, left: 0.2, bottom: 0.3, right: 0.4)
                    node = UiLinearLayoutNode(props: ["defaultItemPadding": referencePadding.toArrayOfFloat])
                    expect(node.defaultItemPadding).to(beCloseTo(referencePadding))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'skipInvisibleItems' prop") {
                    node = UiLinearLayoutNode(props: ["skipInvisibleItems": true])
                    expect(node.skipInvisibleItems).to(beTrue())
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    node.update(["skipInvisibleItems" : false])
                    expect(node.skipInvisibleItems).to(beFalse())
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("always") {
                it("should add child node correctly") {
                    node = UiLinearLayoutNode()
                    let childNodes = node.contentNode.childNodes
                    expect(childNodes.count).to(equal(1))

                    let referenceNode = TransformNode()
                    node.addChild(referenceNode)
                    expect(childNodes.count).to(equal(1))

                    guard let containerNode = childNodes.first,
                        let proxyChild = containerNode.childNodes.first,
                        let itemNode = proxyChild.childNodes.first else { fail("Sth wrong with child nodes structure."); return }
                    expect(itemNode).to(beIdenticalTo(referenceNode))
                }

                it("should remove child node correctly") {
                    node = UiLinearLayoutNode()

                    let referenceNode = TransformNode()
                    node.addChild(referenceNode)
                    node.removeChild(referenceNode)

                    let childNodes = node.contentNode.childNodes
                    expect(childNodes.count).to(equal(1))

                    guard let containerNode = childNodes.first else { fail("Sth wrong with child nodes structure."); return }
                    expect(containerNode.childNodes.count).to(equal(0))
                }
            }

            context("hitTest") {
                it("should return nil if ray does not hit the area of node") {
                    node = UiLinearLayoutNode(props: ["alignment": "center-center"])

                    let referenceSize: CGFloat = 0.08
                    let referenceNode = UiImageNode(props: ["icon": "address-book", "height": referenceSize])
                    node.addChild(referenceNode)
                    node.layoutIfNeeded()

                    let ray = Ray(begin: SCNVector3(-1, 0, 1), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = node.hitTest(ray: ray)
                    expect(result).to(beNil())
                }

                it("should return hit node if ray hits the area of node") {
                    node = UiLinearLayoutNode(props: ["alignment": "center-center"])

                    let referenceSize: CGFloat = 0.08
                    let referenceNode = UiImageNode(props: ["icon": "address-book", "height": referenceSize])
                    node.addChild(referenceNode)
                    node.layoutIfNeeded()

                    let ray = Ray(begin: SCNVector3(0, 0, 1), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = node.hitTest(ray: ray)
                    expect(result).to(beIdenticalTo(referenceNode))
                }
            }

            context("when asked for size") {
                it("should calculate it according to configuration") {
                    node = UiLinearLayoutNode(props: ["orientation": "horizontal"])
                    expect(node.getSize()).to(beCloseTo(CGSize.zero))

                    var referenceWidth: CGFloat = 0.0
                    var referenceHeight: CGFloat = 0.0

                    for number in 1...4 {
                        let referenceNode = UiButtonNode()
                        referenceNode.name = "button_\(number)"
                        referenceNode.text = "Default text"
                        node.addChild(referenceNode)
                        referenceWidth = referenceNode.getSize().width
                        referenceHeight = referenceNode.getSize().height
                    }

                    node.updateLayout()
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 4 * referenceWidth, height: referenceHeight)))

                    node.update(["orientation": "vertical"])
                    node.updateLayout()
                    expect(node.getSize()).to(beCloseTo(CGSize(width: referenceWidth, height: 4 * referenceHeight)))
                }

                it("should ignore invisible items in calculation") {
                    node = UiLinearLayoutNode(props: ["orientation": "horizontal", "skipInvisibleItems": false])
                    expect(node.getSize()).to(beCloseTo(CGSize.zero))

                    var referenceWidth: CGFloat = 0.0
                    var referenceHeight: CGFloat = 0.0

                    for number in 1...4 {
                        let referenceNode = UiButtonNode()
                        referenceNode.name = "button_\(number)"
                        referenceNode.text = "Default text"
                        referenceNode.visible = (number % 2 == 0)
                        node.addChild(referenceNode)
                        referenceWidth = referenceNode.getSize().width
                        referenceHeight = referenceNode.getSize().height
                    }

                    node.layoutIfNeeded()
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 4 * referenceWidth, height: referenceHeight)))

                    node.skipInvisibleItems = true
                    node.layoutIfNeeded()
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 2 * referenceWidth, height: referenceHeight)))
                }
            }
        }
    }
}
