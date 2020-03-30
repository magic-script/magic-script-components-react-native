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
@testable import RNMagicScriptHostApplication

import SceneKit

class UiPageViewNodeSpec: QuickSpec {
    override func spec() {
        describe("UiPageViewNode") {
            var node: UiPageViewNode!

            beforeEach {
                node = UiPageViewNode()
                node.layoutIfNeeded()
            }
            
            context("initial properties") {
                it("should have set default values") {
                    expect(node.width).to(beCloseTo(0))
                    expect(node.height).to(beCloseTo(0))
                    expect(node.defaultPageAlignment).to(equal(Alignment.topLeft))
                    expect(node.defaultPagePadding).to(beCloseTo(UIEdgeInsets.zero))
                    expect(node.pageAlignment).to(equal([:]))
                    expect(node.pagePadding).to(equal([:]))
                    expect(node.visiblePage).to(equal(-1))
                }
            }

            context("update properties") {
                it("should update 'width' prop") {
                    let referenceWidth: CGFloat = 0.7
                    node.update(["width": referenceWidth])
                    expect(node.width).to(beCloseTo(referenceWidth))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    let referenceHeight: CGFloat = 0.8
                    node.update(["height": referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'defaultPageAlignment' prop") {
                    let referenceContentAlignment1 = Alignment.topRight
                    let referenceContentAlignment2 = Alignment.bottomLeft
                    node.update(["defaultPageAlignment": referenceContentAlignment1.rawValue])
                    expect(node.defaultPageAlignment).to(equal(referenceContentAlignment1))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())
                    node.update(["defaultPageAlignment" : referenceContentAlignment2.rawValue])
                    expect(node.defaultPageAlignment).to(equal(referenceContentAlignment2))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'defaultPagePadding' prop") {
                    let referencePadding = UIEdgeInsets(top: 0.1, left: 0.2, bottom: 0.3, right: 0.4)
                    node.update(["defaultPagePadding": referencePadding.toArrayOfFloat])
                    expect(node.defaultPagePadding).to(beCloseTo(referencePadding))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                
                it("should update 'pageAlignment' prop") {
                    let referenceIndex: Int = 2
                    let referenceAlignment = Alignment.topRight
                    let pageAlignment = [["index": referenceIndex, "alignment": referenceAlignment.rawValue]]
                    node.update(["pageAlignment": pageAlignment])
                    expect(node.pageAlignment.count).to(equal(1))
                    expect(node.pageAlignment[referenceIndex]).to(equal(referenceAlignment))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                
                it("should update 'pagePadding' prop") {
                    let referenceIndex: Int = 1
                    let referencePadding = UIEdgeInsets(top: 0.1, left: 0.2, bottom: 0.3, right: 0.4)
                    let pagePadding = [["index": referenceIndex, "padding": referencePadding.toArrayOfFloat]]
                    node.update(["pagePadding": pagePadding])
                    expect(node.pagePadding.count).to(equal(1))
                    expect(node.pagePadding[referenceIndex]).to(beCloseTo(referencePadding))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'visiblePage' prop") {
                    let referenceVisiblePage = 3
                    node.update(["visiblePage": referenceVisiblePage])
                    expect(node.visiblePage).to(equal(referenceVisiblePage))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("add/remove child nodes") {
                it("should add child node correctly") {
                    expect(node.pagesCount).to(equal(0))

                    let referenceNode1 = TransformNode()
                    node.addChild(referenceNode1)
                    expect(node.pagesCount).to(equal(1))
                    expect(node.getPage(at: 0)).to(beIdenticalTo(referenceNode1))

                    let referenceNode2 = TransformNode()
                    node.addChild(referenceNode2)
                    expect(node.pagesCount).to(equal(2))
                    expect(node.getPage(at: 1)).to(beIdenticalTo(referenceNode2))
                }

                it("should remove child node correctly") {
                    let referenceNode = TransformNode()
                    node.addChild(referenceNode)
                    expect(node.pagesCount).to(equal(1))
                    node.removeChild(referenceNode)
                    expect(node.pagesCount).to(equal(0))
                }
            }

            context("hitTest") {
                it("should return nil if ray does not hit the area of node") {
                    node.alignment = .centerCenter

                    let referenceSize: CGFloat = 0.5
                    let referenceNode = UiImageNode(props: ["icon": "local-area-server", "height": referenceSize])
                    node.addChild(referenceNode)
                    node.layoutIfNeeded()

                    let ray = Ray(begin: SCNVector3(-1, 0, 1), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = node.hitTest(ray: ray)
                    expect(result).to(beNil())
                }

                it("should return hit node if ray hits the area of node") {
                    node.alignment = .centerCenter

                    let referenceSize: CGFloat = 0.5
                    let referenceNode = UiImageNode(props: ["icon": "marquee-selection", "height": referenceSize, "alignment": "center-center"])
                    node.addChild(referenceNode)
                    node.visiblePage = 0
                    node.layoutIfNeeded()

                    let ray = Ray(begin: SCNVector3(0, 0, 1), direction: SCNVector3(0, 0, -1), length: 3)
                    let result = node.hitTest(ray: ray)
                    expect(result).to(beIdenticalTo(referenceNode))
                }
            }

            context("visiblePage") {
                it("should return nil page if index is out of range") {
                    node.addChild(TransformNode())
                    expect(node.getPage(at: 1)).to(beNil())
                }

                it("should show proper page if visiblePage is valid") {
                    let referenceNode1 = TransformNode()
                    let referenceNode2 = TransformNode()
                    node.addChild(referenceNode1)
                    node.addChild(referenceNode2)

                    node.visiblePage = 0
                    node.layoutIfNeeded()
                    expect(node.getVisiblePage()).to(beIdenticalTo(referenceNode1))

                    node.visiblePage = 1
                    node.layoutIfNeeded()
                    expect(node.getVisiblePage()).to(beIdenticalTo(referenceNode2))

                    node.visiblePage = 1
                    node.layoutIfNeeded()
                    expect(node.getVisiblePage()).to(beIdenticalTo(referenceNode2))
                }

                it("should update content if a middle page is removed") {
                    let referenceNode1 = TransformNode()
                    let referenceNode2 = TransformNode()
                    node.addChild(referenceNode1)
                    node.addChild(referenceNode2)

                    node.visiblePage = 0
                    node.layoutIfNeeded()
                    expect(node.getVisiblePage()).to(beIdenticalTo(referenceNode1))

                    node.removeChild(referenceNode1)
                    node.layoutIfNeeded()
                    expect(node.getVisiblePage()).to(beIdenticalTo(referenceNode2))
                }

                it("should hide all pages if visiblePage is out of range") {
                    let referenceNode = TransformNode()
                    node.addChild(referenceNode)

                    node.visiblePage = 1
                    node.layoutIfNeeded()
                    expect(node.getVisiblePage()).to(beNil())
                }
            }
            
            context("enumerateTransformNodes") {
                it("should iterate through all pages even though they are not in the nodes hierarchy") {
                    let referenceName = "__ref_name_of_page_view_node__"
                    node.name = referenceName
                    let referenceNodesCount: Int = 10
                    for _ in 0..<referenceNodesCount {
                        let referenceNode = TransformNode()
                        referenceNode.name = referenceName
                        node.addChild(referenceNode)
                    }
                    
                    var iterationCount: Int = 0
                    node.enumerateTransformNodes {
                        expect($0.name).to(equal(referenceName))
                        iterationCount += 1
                    }
                    
                    expect(iterationCount - 1).to(equal(referenceNodesCount))
                }
            }
            
            context("TransformNodeContainer") {
                it("should return itemsCount which equals to pagesCount") {
                    expect(node.itemsCount).to(equal(0))

                    for i in 0..<10 {
                        let referenceNode = TransformNode()
                        node.addChild(referenceNode)
                        expect(node.itemsCount).to(equal(node.pagesCount))
                        expect(node.itemsCount).to(equal(i + 1))
                    }
                }
            }
        }
    }
}
