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

class UiNodeSelectorSpec: QuickSpec {
    override func spec() {
        describe("UiNodeSelector") {
            var nodeSelector: UiNodeSelector!
            var rootNode: SCNNode!
            let size: CGFloat = 0.1
            let node1 = UiImageNode(props: ["icon": "alphabetical", "height": size, "alignment": "center-center"])
            let node2 = UiImageNode(props: ["icon": "analytics", "height": size, "alignment": "center-center"])
            let node3 = UiImageNode(props: ["icon": "keyboard", "height": size, "alignment": "center-center"])
            let node4 = UiImageNode(props: ["icon": "mesh", "height": size, "alignment": "center-center"])
            let node5 = UiImageNode(props: ["icon": "object-recognition", "height": size, "alignment": "center-center"])
            let node6 = UiImageNode(props: ["icon": "tag", "height": size, "alignment": "center-center"])
            let referenceNodes: [TransformNode] = [node1, node2, node3, node4, node5, node6]

            beforeEach() {
                rootNode = SCNNode()
                nodeSelector = UiNodeSelector(rootNode)

                for (index, node) in referenceNodes.enumerated() {
                    node.position = SCNVector3(0, 0, -0.5 * CGFloat(index))
                    rootNode.addChildNode(node)
                }
            }

            context("hitTest") {
                it("should return nil if ray does not hit any node") {
                    let ray = Ray(begin: SCNVector3(-2, 0, 1), direction: SCNVector3(0, 0, -1), length: 2)
                    let result = nodeSelector.hitTest(ray: ray)
                    expect(result).to(beNil())
                }

                it("should return the closest node") {
                    let rayLength: CGFloat = 2
                    let ray = Ray(begin: SCNVector3(0, 0, 1), direction: SCNVector3(0, 0, -1), length: rayLength)
                    let rayMinZ = min(ray.begin.z, ray.end.z) + 0.1
                    let rayMaxZ = max(ray.begin.z, ray.end.z) - 0.1

                    let nodesInRayRange: [TransformNode] = referenceNodes.filter { (rayMinZ <= $0.position.z) && ($0.position.z <= rayMaxZ) }
                    for node in nodesInRayRange {
                        let result = nodeSelector.hitTest(ray: ray)
                        expect(result).to(beIdenticalTo(node))
                        node.skipRaycast = true
                    }

                    let result = nodeSelector.hitTest(ray: ray)
                    expect(result).to(beNil())
                }
            }
        }
    }
}
