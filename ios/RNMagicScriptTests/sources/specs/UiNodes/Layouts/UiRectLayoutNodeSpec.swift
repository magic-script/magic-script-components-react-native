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

class UiRectLayoutNodeSpec: QuickSpec {
    override func spec() {
        describe("UiRectLayoutNode") {
            var node: UiRectLayoutNode!

            context("initial properties") {
                it("should have set default values") {
                    node = UiRectLayoutNode()
                    expect(node.width).to(equal(0))
                    expect(node.height).to(equal(0))
                    expect(node.contentAlignment).to(equal(Alignment.topLeft))
                    expect(node.padding).to(beCloseTo(UIEdgeInsets.zero))
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
                
                it("should update 'contentAlignment' prop") {
                    node = UiRectLayoutNode(props: ["contentAlignment": "top-right"])
                    expect(node.contentAlignment).to(equal(Alignment.topRight))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    node.update(["contentAlignment" : "bottom-left"])
                    expect(node.contentAlignment).to(equal(Alignment.bottomLeft))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'padding' prop") {
                    let referencePadding = UIEdgeInsets(top: 0.1, left: 0.2, bottom: 0.3, right: 0.4)
                    node = UiRectLayoutNode(props: ["padding": referencePadding.toArrayOfFloat])
                    expect(node.padding).to(beCloseTo(referencePadding))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("add/remove child nodes") {
                it("should add child node correctly") {
                    node = UiRectLayoutNode()
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

                it("should not allow adding multiple child nodes") {
                    node = UiRectLayoutNode()
                    let containerNode = node.contentNode.childNodes.first!
                    expect(containerNode.childNodes.count).to(equal(0))

                    let referenceNode1 = TransformNode()
                    let referenceNode2 = TransformNode()
                    let referenceNode3 = TransformNode()
                    node.addChild(referenceNode1)
                    node.addChild(referenceNode2)
                    node.addChild(referenceNode3)

                    expect(containerNode.childNodes.count).to(equal(1))
                }

                it("should remove child node correctly") {
                    node = UiRectLayoutNode()

                    let referenceNode = TransformNode()
                    node.addChild(referenceNode)
                    node.removeChild(referenceNode)

                    let containerNode = node.contentNode.childNodes.first!
                    let proxyNodes = containerNode.childNodes
                    expect(proxyNodes.count).to(equal(0))
                }
            }

            context("hitTest") {
                it("should return nil if ray does not hit the area of node") {
                    node = UiRectLayoutNode(props: ["alignment": "center-center"])

                    let referenceSize: CGFloat = 0.08
                    let referenceNode = UiImageNode(props: ["icon": "address-book", "height": referenceSize])
                    node.addChild(referenceNode)
                    node.layoutIfNeeded()

                    let ray = Ray(begin: SCNVector3(-1, 0, 1), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = node.hitTest(ray: ray)
                    expect(result).to(beNil())
                }

                it("should return hit node if ray hits the area of node") {
                    node = UiRectLayoutNode(props: ["alignment": "center-center"])

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
                    node = UiRectLayoutNode()
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
                    expect(node.getSize()).to(beCloseTo(CGSize(width: referenceWidth, height: referenceHeight)))
                }
            }
        }
    }
}
