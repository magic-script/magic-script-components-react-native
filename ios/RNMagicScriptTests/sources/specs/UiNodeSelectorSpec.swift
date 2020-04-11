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
                        expect(result!.node).to(beIdenticalTo(node))
                        node.skipRaycast = true
                    }

                    let result = nodeSelector.hitTest(ray: ray)
                    expect(result!.node).to(beIdenticalTo(node3))
                }
                
                it("should return the closest node (rotation)") {
                    rootNode.childNodes.forEach { $0.removeFromParentNode() }
                    let node1 = UiImageNode(props: ["localPosition": [0, 0, -0.1], "height": 0.1, "width": 0.3, "color": [1, 1, 0, 1]])
                    node1.name = "node1"
                    let node2 = UiImageNode(props: ["localPosition": [0, 0,  0.1], "height": 0.1, "width": 0.1, "color": [1, 0, 1, 1]])
                    node2.name = "node2"
                    rootNode.addChildNode(node1)
                    rootNode.addChildNode(node2)
                    
                    let input: [(angle: Double, hitNode: BaseNode?)] = [
                        (angle: 0.0.toRadians, hitNode: node2),
                        (angle: 45.0.toRadians, hitNode: node1),
                        (angle: 90.0.toRadians, hitNode: nil),
                        (angle: 135.0.toRadians, hitNode: nil),
                        (angle: 180.0.toRadians, hitNode: nil),
                        (angle: 225.0.toRadians, hitNode: nil),
                        (angle: 270.0.toRadians, hitNode: nil),
                        (angle: 315.0.toRadians, hitNode: node1),
                    ]
                    
                    input.forEach {
                        let radius: Double = 0.35355
                        let x: Double = radius * sin($0.angle)
                        let z: Double = radius * cos($0.angle)
                        let position = SCNVector3(x, 0, z)
                        let direction = (SCNVector3.zero - position).normalized()
                        let ray = Ray(begin: position, direction: direction, length: 1.0)
                        let result = nodeSelector.hitTest(ray: ray)
                        if let hitNode = $0.hitNode {
                            expect(result).notTo(beNil())
                            expect(result?.node).to(beIdenticalTo(hitNode))
                        } else {
                            expect(result).to(beNil())
                        }
                    }
                }
                
                it("should return the closest node based on hitPoint (not node's center)") {
                    rootNode.childNodes.forEach { $0.removeFromParentNode() }
                    let node1 = UiImageNode(props: ["localPosition": [0, 0, 0], "height": 0.1, "width": 0.8, "color": [1, 1, 0, 1]])
                    let node2 = UiImageNode(props: ["localPosition": [0.2, 0, 0.05], "height": 0.1, "width": 0.1, "color": [1, 0, 1, 1]])
                    rootNode.addChildNode(node1)
                    rootNode.addChildNode(node2)
                    
                    // A ray that points at node1.center
                    let ray1 = Ray(begin: SCNVector3(0, 0, 0.15), direction: SCNVector3(0, 0, -1), length: 1.0)
                    let result1 = nodeSelector.hitTest(ray: ray1)
                    expect(result1).notTo(beNil())
                    expect(result1!.node).to(beIdenticalTo(node1))
                    expect(result1!.point).to(beCloseTo(SCNVector3(0, 0, 0)))
                    
                    // A ray that points at node2.center
                    let ray2 = Ray(begin: SCNVector3(0, 0, 0.15), direction: SCNVector3(0.3, 0, -0.15).normalized(), length: 1.0)
                    
                    // Make sure the node1.position is closer to the ray2 than the node2.position
                    let rayToNode1Dist = node1.position.distance(ray2.begin)
                    let rayToNode2Dist = node2.position.distance(ray2.begin)
                    expect(rayToNode1Dist < rayToNode2Dist).to(beTrue())
                    
                    let result2 = nodeSelector.hitTest(ray: ray2)
                    expect(result2).notTo(beNil())
                    expect(result2!.node).to(beIdenticalTo(node2))
                    expect(result2!.point).to(beCloseTo(SCNVector3(0, 0, 0)))
                }
            }
        }
    }
}
