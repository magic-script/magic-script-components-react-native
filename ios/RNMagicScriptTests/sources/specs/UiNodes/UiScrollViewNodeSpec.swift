//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
import SwiftyMocky
@testable import RNMagicScriptHostApplication

class UiScrollViewNodeSpec: QuickSpec {
    override func spec() {
        describe("UiScrollViewNode") {
            var node: UiScrollViewNode!

            beforeEach {
                node = UiScrollViewNode()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(.centerCenter))
                    expect(node.scrollDirection).to(equal(ScrollDirection.horizontal))
                    expect(node.scrollSpeed).to(beCloseTo(0.1))
                    expect(node.scrollOffset).to(beCloseTo(SCNVector3Zero))
                    expect(node.scrollValue).to(beCloseTo(0))
                    expect(node.scrollBounds).to(beNil())
                    expect(node.scrollBarVisibility).to(equal(ScrollBarVisibility.auto))
                }
            }

            context("initialization") {
                it("should add proxy node to the hierarchy") {
                    expect(node.contentNode.childNodes.count).to(equal(1))
                }
            }

            context("update properties") {
                it("should not update alignment value") {
                    node.update(["alignment" : Alignment.bottomRight])
                    expect(node.alignment).to(equal(.centerCenter))
                    node.alignment = Alignment.centerLeft
                    expect(node.alignment).to(equal(.centerCenter))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
                it("should update scrollDirection value") {
                    let referenceValue: ScrollDirection = .vertical
                    node.update(["scrollDirection" : referenceValue.rawValue])
                    expect(node.scrollDirection).to(equal(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update scrollSpeed value") {
                    let referenceValue: CGFloat = 0.7
                    node.update(["scrollSpeed" : referenceValue])
                    expect(node.scrollSpeed).to(beCloseTo(referenceValue))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
                it("should update scrollOffset value") {
                    let referenceValue: SCNVector3 = SCNVector3(1.05, 2.06, 3.07)
                    node.update(["scrollOffset" : [referenceValue.x, referenceValue.y, referenceValue.z]])
                    expect(node.scrollOffset).to(beCloseTo(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update scrollValue value") {
                    node.scrollValue = 1.4
                    expect(node.scrollValue).to(beCloseTo(1))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())

                    node.scrollValue = 1.1
                    expect(node.scrollValue).to(beCloseTo(1))
                    expect(node.isLayoutNeeded).to(beFalse())

                    let referenceValue: CGFloat = 0.4
                    node.update(["scrollValue" : referenceValue])
                    expect(node.scrollValue).to(beCloseTo(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.scrollValue = -0.6
                    expect(node.scrollValue).to(beCloseTo(0))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())

                    node.scrollValue = -1.8
                    expect(node.scrollValue).to(beCloseTo(0))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
                it("should update scrollBounds value") {
                    let referenceMin = SCNVector3(-0.3, -0.4, -0.5)
                    let referenceMax = SCNVector3(0.4, 0.4, 0.5)
                    node.update([
                        "scrollBounds" : [
                            "min": [referenceMin.x, referenceMin.y, referenceMin.z],
                            "max": [referenceMax.x, referenceMax.y, referenceMax.z]
                        ]
                    ])
                    expect(node.scrollBounds?.min).to(beCloseTo(referenceMin))
                    expect(node.scrollBounds?.max).to(beCloseTo(referenceMax))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                it("should update scrollBarVisibility value") {
                    let referenceValues: [ScrollBarVisibility] = [.always, .auto, .off]
                    for value in referenceValues {
                        node.update(["scrollBarVisibility" : value.rawValue])
                        expect(node.scrollBarVisibility).to(equal(value))
                    }
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            context("Dragging") {
                it("should get dragAxis") {
                    expect(node.dragAxis).to(beNil())

                    node.scrollBounds = (min: SCNVector3(-1,-1,-0.1), max: SCNVector3(1,1,0.1))

                    node.scrollDirection = .vertical
                    expect(node.dragAxis?.direction).to(beCloseTo(SCNVector3(0, 1, 0)))

                    node.scrollDirection = .horizontal
                    expect(node.dragAxis?.direction).to(beCloseTo(SCNVector3(-1, 0, 0)))
                }

                it("should get dragRange") {
                    expect(node.dragRange).to(beCloseTo(0))

                    let contentSize: CGFloat = 1.0
                    let contentNode = UiImageNode(props: ["icon": "address-book", "height": contentSize])
                    node.addChild(contentNode)

                    let boundsSize: CGFloat = 0.2
                    node.scrollBounds = (min: SCNVector3(-boundsSize, -boundsSize, -0.1), max: SCNVector3(boundsSize, boundsSize, 0.1))
                    node.layoutIfNeeded()

                    node.scrollDirection = .vertical
                    expect(node.dragRange).to(beCloseTo(contentSize - 2 * boundsSize))

                    node.scrollDirection = .horizontal
                    expect(node.dragRange).to(beCloseTo(contentSize - 2 * boundsSize))
                }

                it("should get/set dragValue") {
                    let scrollBar = UiScrollBarNode()
                    node.addChild(scrollBar)

                    let referenceScrollValue1: CGFloat = 0.3
                    node.scrollValue = referenceScrollValue1
                    expect(node.dragValue).to(beCloseTo(referenceScrollValue1))

                    let referenceScrollValue2: CGFloat = 0.84
                    node.dragValue = referenceScrollValue2
                    expect(node.scrollValue).to(beCloseTo(referenceScrollValue2))

                }
            }

            context("add/remove child") {
                it("should add/remove srcollBar child") {
                    let internalNodesCount: Int = 1
                    let scrollBar1 = UiScrollBarNode()
                    node.addChild(scrollBar1)
                    expect(node.contentNode.childNodes.count).to(equal(internalNodesCount + 1))
                    expect(node.contentNode.childNodes.last).to(beIdenticalTo(scrollBar1))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let scrollBar2 = UiScrollBarNode()
                    node.addChild(scrollBar2)
                    expect(node.contentNode.childNodes.count).to(equal(internalNodesCount + 1))
                    expect(node.contentNode.childNodes.last).to(beIdenticalTo(scrollBar1))

                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())

                    node.removeChild(scrollBar2)
                    expect(node.contentNode.childNodes.count).to(equal(internalNodesCount + 1))
                    expect(node.isLayoutNeeded).to(beFalse())

                    node.removeChild(scrollBar1)
                    expect(node.contentNode.childNodes.count).to(equal(internalNodesCount + 0))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should add srcollContent child") {
                    let proxyNode: SCNNode = node.contentNode.childNodes.first!
                    let contentNode1 = UiImageNode(props: ["icon": "calendar", "height": 0.5])
                    node.addChild(contentNode1)
                    expect(proxyNode.childNodes.count).to(equal(1))
                    expect(proxyNode.childNodes.first).to(beIdenticalTo(contentNode1))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let contentNode2 = UiImageNode(props: ["icon": "camera", "height": 0.5])
                    node.addChild(contentNode2)
                    expect(proxyNode.childNodes.count).to(equal(1))
                    expect(proxyNode.childNodes.first).to(beIdenticalTo(contentNode1))

                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())

                    node.removeChild(contentNode2)
                    expect(proxyNode.childNodes.count).to(equal(1))
                    expect(node.isLayoutNeeded).to(beFalse())

                    node.removeChild(contentNode1)
                    expect(proxyNode.childNodes.count).to(equal(0))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("updateLayout") {
                it("should update layout") {
                    expect(node.isLayoutNeeded).to(beFalse())
                    node.scrollBounds = (min: SCNVector3(-1, -1, -0.1), max: SCNVector3(1, 1, 0.1))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            context("hitTest") {
                it("should return nil") {
                    node.scrollBounds = (min: SCNVector3(-1, -1, -0.1), max: SCNVector3(1, 1, 0.1))

                    let ray = Ray(begin: SCNVector3(0,0,1), direction: SCNVector3(1,0,0), length: 2)
                    expect(node.hitTest(ray: ray)).to(beNil())
                }

                it("should return self") {
                    node.scrollBounds = (min: SCNVector3(-1, -1, -0.1), max: SCNVector3(1, 1, 0.1))

                    let ray = Ray(begin: SCNVector3(0,0,1), direction: SCNVector3(0,0,-1), length: 2)
                    expect(node.hitTest(ray: ray)).to(beIdenticalTo(node))
                }

                it("should return content node") {
                    node.scrollBounds = (min: SCNVector3(-1, -1, -0.1), max: SCNVector3(1, 1, 0.1))

                    let contentNode = UiImageNode(props: ["icon": "address-book", "height": 0.5])
                    node.addChild(contentNode)
                    
                    let ray = Ray(begin: SCNVector3(0,0,1), direction: SCNVector3(0,0,-1), length: 2)
                    expect(node.hitTest(ray: ray)).to(beIdenticalTo(contentNode))
                }
            }
        }
    }
}

