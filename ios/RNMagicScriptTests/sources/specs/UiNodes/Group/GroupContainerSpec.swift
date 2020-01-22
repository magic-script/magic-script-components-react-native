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

class GroupContainerSpec: QuickSpec {
    override func spec() {
        describe("GroupContainer") {
            var group: GroupContainer!

            beforeEach {
                group = GroupContainer()
            }

            context("initialization") {
                it("should be empty just after initialization") {
                    expect(group.container.childNodes.count).to(equal(0))
                    expect(group.getSize()).to(beCloseTo(CGSize.zero))
                    expect(group.getBounds()).to(beCloseTo(CGRect.zero))
                }

                it("hitTest should always return nil") {
                    let rays = [
                        Ray(begin: SCNVector3(0, 0, -1), direction: SCNVector3(0, 0, 1), length: 3),
                        Ray(begin: SCNVector3(0, 0, 1), direction: SCNVector3(0, 0, -1), length: 3),
                        Ray(begin: SCNVector3(0.1, 0.1, -2), direction: SCNVector3(0, 0, 1), length: 3),
                        Ray(begin: SCNVector3(-0.1, 0.1, -2), direction: SCNVector3(0, 0, 1), length: 3),
                        Ray(begin: SCNVector3(0.1, -0.1, -2), direction: SCNVector3(0, 0, 1), length: 3),
                        Ray(begin: SCNVector3(-0.1, -0.1, -2), direction: SCNVector3(0, 0, 1), length: 3),
                    ]
                    for ray in rays {
                        expect(group.hitTest(ray: ray)).to(beNil())
                    }

                    group.recalculateIfNeeded()
                    for ray in rays {
                        expect(group.hitTest(ray: ray)).to(beNil())
                    }
                }
            }

            context("items") {
                it("should add/remove items") {
                    let items = self.prepareSampleTransformNodes()
                    items.forEach { (node) in
                        group.addItem(node)
                    }
                    expect(group.container.childNodes.count).to(equal(items.count))

                    items.forEach { (node) in
                        group.removeItem(node)
                    }
                    expect(group.container.childNodes.count).to(equal(0))
                }

                it("should add new items only once") {
                    let items = self.prepareSampleTransformNodes()
                    items.forEach { (node) in
                        group.addItem(node)
                        group.addItem(node)
                    }
                    expect(group.container.childNodes.count).to(equal(items.count))
                }
            }

            context("recalculation") {
                it("should need recalculation") {
                    expect(group.isRecalculationNeeded).to(beTrue())
                    group.recalculateIfNeeded()
                    expect(group.isRecalculationNeeded).to(beFalse())
                }

                it("should recalculation be needed after invalidate") {
                    group.recalculateIfNeeded()
                    expect(group.isRecalculationNeeded).to(beFalse())
                    group.invalidate()
                    expect(group.isRecalculationNeeded).to(beTrue())
                }

                it("should recalculation be needed after add item") {
                    group.recalculateIfNeeded()
                    expect(group.isRecalculationNeeded).to(beFalse())
                    group.addItem(TransformNode())
                    expect(group.isRecalculationNeeded).to(beTrue())
                }

                it("should recalculation be needed after remove item") {
                    let node = TransformNode()
                    group.addItem(node)
                    group.recalculateIfNeeded()
                    expect(group.isRecalculationNeeded).to(beFalse())

                    group.removeItem(node)
                    expect(group.isRecalculationNeeded).to(beTrue())
                }
            }

            context("getBounds") {
                it("should return bounds") {
                    let items = self.prepareSampleTransformNodes()
                    items.forEach { group.addItem($0) }
                    let targetBounds = CGRect(x: 0.3, y: -0.5, width: 2.63, height: 1.45)
                    expect(group.getBounds()).to(beCloseTo(targetBounds))
                }

                it("should return size") {
                    let items = [
                        TransformNode(props: ["localPosition": [1.3, -0.9, 0]]),
                        TransformNode(props: ["localPosition": [-9.8, 2.3, 0]]),
                    ]
                    items.forEach { group.addItem($0) }
                    let targetSize = CGSize(width: 11.1, height: 3.2)
                    expect(group.getSize()).to(beCloseTo(targetSize))
                }
            }

            context("hitTest") {
                it("should return child node") {
                    let items = self.prepareSampleTransformNodes()
                    items.forEach { group.addItem($0) }
                    group.recalculateIfNeeded()

                    let direction = SCNVector3(0, 0, 1)
                    let result1 = group.hitTest(ray: Ray(begin: SCNVector3(1.45, 0.8, -1), direction: direction, length: 3))
                    expect(result1).to(beIdenticalTo(items[1]))

                    let result2 = group.hitTest(ray: Ray(begin: SCNVector3(2.55, 0.05, -1), direction: direction, length: 3))
                    expect(result2).to(beIdenticalTo(items[2]))
                }

                it("should return nil if no child node is hit") {
                    let items = self.prepareSampleTransformNodes()
                    items.forEach { group.addItem($0) }

                    let direction = SCNVector3(0, 0, 1)
                    let rayBegins = [
                        SCNVector3(0.5, 0.5, -1),
                        SCNVector3(2, -0.4, -1),
                        SCNVector3(2.6, 0.11, -1),
                    ]
                    for begin in rayBegins {
                        let result = group.hitTest(ray: Ray(begin: begin, direction: direction, length: 3))
                        expect(result).to(beNil())
                    }
                }
            }
        }
    }

    fileprivate func prepareSampleTransformNodes() -> [TransformNode] {
        let node1 = TransformNode()
        node1.localPosition = SCNVector3(0.3, -0.5, 0)
        node1.layoutIfNeeded()

        let node2 = UiImageNode()
        node2.alignment = .centerCenter
        node2.localPosition = SCNVector3(1.3, 0.7, -0.2)
        node2.height = 0.5
        node2.width = 0.4
        node2.color = UIColor.red
        node2.layoutIfNeeded()

        let node3 = UiToggleNode()
        node3.localPosition = SCNVector3(2.6, 0, 0)
        node3.text = "Radio"
        node3.type = .radio
        node3.height = 0.2
        node3.layoutIfNeeded()
        return [node1, node2, node3]
    }
}
