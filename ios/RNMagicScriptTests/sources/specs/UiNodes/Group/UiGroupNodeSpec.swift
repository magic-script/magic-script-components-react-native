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

class UiGroupNodeSpec: QuickSpec {
    override func spec() {
        describe("UiGroupNode") {
            var node: UiGroupNode!
            
            beforeEach {
                node = UiGroupNode()
            }
            
            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
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
            }

            context("hitTest") {
                it("should return nil if ray does not hit the area of group") {
                    let ray = Ray(begin: SCNVector3(0, 0, -1), direction: SCNVector3(0, 0, 1), length: 3)
                    let result = node.hitTest(ray: ray)
                    expect(result).to(beNil())
                }

                it("should return hit node") {
                    let imageSize: CGFloat = 0.5
                    let images = [
                        UiImageNode(props: ["icon": "cut", "localPosition": [-0.25, 0.25, 0], "width": imageSize, "height": imageSize]),
                        UiImageNode(props: ["icon": "copy", "localPosition": [0.25, 0.25, 0], "width": imageSize, "height": imageSize]),
                        UiImageNode(props: ["icon": "edit", "localPosition": [0.25, -0.25, 0], "width": imageSize, "height": imageSize]),
                        UiImageNode(props: ["icon": "paste", "localPosition": [-0.25, -0.25, 0], "width": imageSize, "height": imageSize])
                    ]
                    for image in images {
                        node.addChild(image)
                    }
                    node.layoutIfNeeded()

                    let beginOffset = SCNVector3(0, 0, 1)
                    let direction = SCNVector3(0, 0, -1)
                    for image in images {
                        let pos = image.localPosition
                        let ray = Ray(begin: pos + beginOffset, direction: direction, length: 3)
                        let result = node.hitTest(ray: ray)
                        expect(result).to(beIdenticalTo(image))
                    }
                }
            }
            
            context("when asked for size") {
                it("should calculate it") {
                    let referenceNode1 = UiButtonNode(props: ["width": 1.0, "height": 1.0])
                    referenceNode1.text = "Text"
                    referenceNode1.layoutIfNeeded()
                    let referenceNode2 = UiButtonNode(props: ["width": 1.0, "height": 1.0])
                    referenceNode2.text = "Text"
                    referenceNode2.position = SCNVector3(0.5, 0.5, 0.0)
                    referenceNode2.layoutIfNeeded()
                    
                    node.addChild(referenceNode1)
                    node.addChild(referenceNode2)
                    node.layoutIfNeeded()
                    
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 1.5, height: 1.5)))
                }
            }
                
            context("when no child nodes") {
                it("should return zero size") {
                    expect(node.getSize()).to(beCloseTo(CGSize.zero))
                }
            }

            context("add/removeChild") {
                it("should add children") {
                    expect(node.itemsCount).to(equal(0))
                    let referenceItemsCount = 5
                    for _ in 0..<referenceItemsCount {
                        node.addChild(TransformNode())
                    }

                    expect(node.itemsCount).to(equal(referenceItemsCount))
                }

                it("should remove children") {
                    expect(node.itemsCount).to(equal(0))
                    let referenceItems = [TransformNode(), TransformNode(), TransformNode()]
                    for item in referenceItems {
                        node.addChild(item)
                    }

                    expect(node.itemsCount).to(equal(referenceItems.count))
                    for item in referenceItems {
                        node.removeChild(item)
                    }

                    expect(node.itemsCount).to(equal(0))
                }
            }

            context("getBounds") {
                it("should return group's bounds") {
                    let imageSize: CGFloat = 0.5
                    let images = [
                        UiImageNode(props: ["icon": "cut", "localPosition": [-0.25, 0.25, 0], "width": imageSize, "height": imageSize]),
                        UiImageNode(props: ["icon": "copy", "localPosition": [0.25, 0.25, 0], "width": imageSize, "height": imageSize]),
                        UiImageNode(props: ["icon": "edit", "localPosition": [0.25, -0.25, 0], "width": imageSize, "height": imageSize]),
                        UiImageNode(props: ["icon": "paste", "localPosition": [-0.25, -0.25, 0], "width": imageSize, "height": imageSize])
                    ]
                    for image in images {
                        node.addChild(image)
                    }
                    let offset = SCNVector3(7, 1, 3)
                    node.localPosition = offset
                    node.layoutIfNeeded()
                    let localBounds = node.getBounds()
                    expect(localBounds).to(beCloseTo(CGRect(x: -imageSize, y: -imageSize, width: 2 * imageSize, height: 2 * imageSize)))

                    let parentBounds = node.getBounds(parentSpace: true)
                    expect(parentBounds).to(beCloseTo(CGRect(x: -imageSize + CGFloat(offset.x), y: -imageSize + CGFloat(offset.y), width: 2 * imageSize, height: 2 * imageSize)))
                }
            }
        }
    }
}
