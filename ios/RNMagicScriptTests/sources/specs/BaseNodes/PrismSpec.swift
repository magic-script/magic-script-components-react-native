//
//  Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

class PrismSpec: QuickSpec {
    override func spec() {
        describe("Prism") {
            var prism: Prism!

            beforeEach {
                prism = Prism(props: [:])
            }

            context("when initialized") {
                it("should have set default values") {
                    expect(prism.debug).to(beFalse())
                    expect(prism.size).to(beCloseTo(SCNVector3.zero))
                }

                it("content node should be initialized") {
                    expect(prism.rootNode).toNot(beNil())
                    expect(prism.childNodes.count).to(equal(1))
                    expect(prism.childNodes.first).to(equal(prism.rootNode))
                }
            }

            context("update properties") {
                it("should update 'size' prop") {
                    let referenceSize = SCNVector3(0.5, 0.75, 1.25)
                    prism.update(["size": referenceSize.toArrayOfFloat])
                    expect(prism.size).to(beCloseTo(referenceSize))
                }

                it("should update 'position' prop") {
                    let v1 = SCNVector3(0.4, -0.5, 0.6)
                    let v2 = SCNVector3(0.333, 0.98, 1.43)
                    prism = Prism(props: ["position": v1.toArrayOfFloat])
                    expect(prism.position).to(beCloseTo(v1))
                    prism.update(["position": v2.toArrayOfFloat])
                    expect(prism.position).to(beCloseTo(v2))
                }

                it("should update 'rotation' prop") {
                    let q1 = SCNQuaternion(0.8, 1.7, 0, 1)
                    let q2 = SCNQuaternion(-0.6, 1, 1.333, 1)
                    prism = Prism(props: ["rotation": q1.toArrayOfDouble])
                    expect(prism.rotation).to(beCloseTo(q1))
                    prism.update(["rotation": q2.toArrayOfDouble])
                    expect(prism.rotation).to(beCloseTo(q2))
                }

                it("should update 'scale' prop") {
                    let v1 = SCNVector3(-0.4, -0.5, -0.6)
                    let v2 = SCNVector3(1.2, 1.2, 1.2)
                    prism = Prism(props: ["scale": v1.toArrayOfFloat])
                    expect(prism.scale).to(beCloseTo(v1))
                    prism.update(["scale": v2.toArrayOfCGFloat])
                    expect(prism.scale).to(beCloseTo(v2))
                }

                it("should update 'debug' prop") {
                    prism = Prism(props: ["debug": true])
                    expect(prism.debug).to(beTrue())
                    prism.update(["debug": false])
                    expect(prism.debug).to(beFalse())
                }
            }


            context("when asked to add child node") {
                context("when child node is TransformNode") {
                    it("should add it to content") {
                        expect(prism.rootNode.childNodes.count).to(equal(1))

                        let childNode = TransformNode()
                        let result = prism.addNode(childNode)

                        expect(result).to(beTrue())
                        expect(prism.rootNode.childNodes.count).to(equal(2))
                        expect(prism.rootNode.childNodes[1]).to(equal(childNode))
                    }
                }

                context("when child node is NOT TransformNode") {
                    it("should do nothing") {
                        expect(prism.rootNode.childNodes.count).to(equal(1))

                        let childNode = BaseNode()
                        let result = prism.addNode(childNode)

                        expect(result).to(beFalse())
                        expect(prism.rootNode.childNodes.count).to(equal(1))
                    }
                }

            }

            context("when asked to remove child node") {
                context("when child node is Prism") {
                    it("should remove it from content") {
                        let childNode = TransformNode()
                        let _ = prism.addNode(childNode)
                        expect(prism.rootNode.childNodes.count).to(equal(2))
                        expect(prism.rootNode.childNodes[1]).to(equal(childNode))

                        prism.removeNode(childNode)
                        expect(prism.rootNode.childNodes.count).to(equal(1))
                    }
                }

                context("when child node is NOT Prism/NOT added to Scene") {
                    it("should do nothing") {
                        let childNode = TransformNode()
                        let _ = prism.addNode(childNode)
                        expect(prism.rootNode.childNodes.count).to(equal(2))
                        expect(prism.rootNode.childNodes[1]).to(equal(childNode))

                        let baseNode = BaseNode()
                        prism.removeNode(baseNode)
                        expect(prism.rootNode.childNodes.count).to(equal(2))
                        expect(prism.rootNode.childNodes[1]).to(equal(childNode))                    }
                }
            }

            context("when asked for hitTest") {
                context("when contains TransformNode") {
                    it("should iterate through them") {
                        let childNode1 = StubbedTransformNode()
                        let childNode2 = StubbedTransformNode()
                        let childNode3 = StubbedTransformNode()
                        let hitNode = TransformNode()
                        childNode3.hitNode = hitNode

                        _ = prism.addNode(childNode1)
                        _ = prism.addNode(childNode2)
                        _ = prism.addNode(childNode3)

                        let hitResult = prism.hitTest(ray: Ray(begin: SCNVector3.zero, direction: SCNVector3.zero, length: 0.0))
                        expect(hitResult).to(equal(hitNode))
                        expect(childNode1.wasIteratedByHitTest).to(beTrue())
                        expect(childNode2.wasIteratedByHitTest).to(beTrue())
                        expect(childNode3.wasIteratedByHitTest).to(beTrue())
                    }
                }

                context("when doesn't contain TransformNode") {
                    it("should return itself") {
                        let hitResult = prism.hitTest(ray: Ray(begin: SCNVector3.zero, direction: SCNVector3.zero, length: 0.0))
                        expect(hitResult).to(equal(prism))
                    }
                }
            }
        }
    }
}


private class StubbedTransformNode: TransformNode {
    var wasIteratedByHitTest = false
    var hitNode: TransformNode?

    @objc override func hitTest(ray: Ray) -> BaseNode? {
        wasIteratedByHitTest = true
        return hitNode
    }
}
