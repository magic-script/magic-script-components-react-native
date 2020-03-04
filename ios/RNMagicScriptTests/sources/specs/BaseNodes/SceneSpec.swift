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

class SceneSpec: QuickSpec {
    override func spec() {
        describe("Scene") {
            var scene: Scene!

            beforeEach {
                scene = Scene()
            }

            context("when initialized") {
                context("when called with params") {
                    it("should initialize object according to params") {
                        scene = Scene(props: [:])
                        expect(scene.prisms).toNot(beNil())
                        expect(scene.prisms.count).to(equal(0))
                    }
                }

                it("prisms container should be empty") {
                    expect(scene.prisms).toNot(beNil())
                    expect(scene.prisms.count).to(equal(0))
                }

                it("content node should be initialized") {
                    expect(scene.rootNode).toNot(beNil())
                    expect(scene.childNodes.count).to(equal(1))
                    expect(scene.childNodes.first).to(equal(scene.rootNode))
                }

            }

            context("when asked to add child node") {
                context("when child node is Prism") {
                    it("should add it to content") {
                        let prismNode = Prism()
                        let result = scene.addNode(prismNode)
                        expect(result).to(beTrue())
                        expect(scene.prisms.count).to(equal(1))
                        expect(scene.childNodes.count).to(equal(1))
                        let rootNode = scene.childNodes.first
                        expect(rootNode!.childNodes.count).to(equal(1))
                        expect(rootNode!.childNodes.first).to(equal(prismNode))
                        expect(scene.prisms.first).to(equal(prismNode))
                        expect(prismNode.parent).to(equal(scene.rootNode))
                    }
                }

                context("when child node is NOT Prism") {
                    it("should do nothing") {
                        let genericSCNNode = SCNNode()
                        let result = scene.addNode(genericSCNNode)
                        expect(result).to(beFalse())
                        expect(scene.prisms.count).to(equal(0))
                        expect(scene.childNodes.count).to(equal(1))
                        let rootNode = scene.childNodes.first
                        expect(rootNode!.childNodes.count).to(equal(0))
                    }
                }

            }

            context("when asked to remove child node") {
                context("when child node is Prism") {
                    it("should add it to content") {
                        let prismNode = Prism()

                        // prepare
                        let result = scene.addNode(prismNode)
                        expect(result).to(beTrue())
                        expect(scene.prisms.count).to(equal(1))
                        expect(scene.childNodes.count).to(equal(1))
                        let rootNode = scene.childNodes.first
                        expect(rootNode!.childNodes.count).to(equal(1))
                        expect(rootNode!.childNodes.first).to(equal(prismNode))

                        // remove
                        scene.removeNode(prismNode)
                        expect(scene.prisms.count).to(equal(0))
                        expect(prismNode.parent).to(beNil())
                    }
                }

                context("when child node is NOT Prism/NOT added to Scene") {
                    it("should do nothing") {
                        let genericSCNNode = SCNNode()
                        scene.removeNode(genericSCNNode)
                        expect(scene.prisms.count).to(equal(0))
                        expect(scene.childNodes.count).to(equal(1))
                        let rootNode = scene.childNodes.first
                        expect(rootNode!.childNodes.count).to(equal(0))
                    }
                }
            }

            context("when asked for hitTest") {
                context("when contains Prisms") {
                    it("should iterate through them") {
                        let prism1 = StubbedPrism()
                        let prism2 = StubbedPrism()
                        let prism3 = StubbedPrism()
                        let hitNode = TransformNode()
                        prism3.hitNode = hitNode

                        _ = scene.addNode(prism1)
                        _ = scene.addNode(prism2)
                        _ = scene.addNode(prism3)

                        let hitResult = scene.hitTest(ray: Ray(begin: SCNVector3.zero, direction: SCNVector3.zero, length: 0.0))
                        expect(hitResult).to(equal(hitNode))
                        expect(prism1.wasIteratedByHitTest).to(beTrue())
                        expect(prism2.wasIteratedByHitTest).to(beTrue())
                        expect(prism3.wasIteratedByHitTest).to(beTrue())
                    }
                }

                context("when doesn't contain Prisms") {
                    it("should return itself") {
                        let hitResult = scene.hitTest(ray: Ray(begin: SCNVector3.zero, direction: SCNVector3.zero, length: 0.0))
                        expect(hitResult).to(equal(scene))
                    }
                }
            }
        }
    }
}

private class StubbedPrism: Prism {
    var wasIteratedByHitTest = false
    var hitNode: TransformNode?

    @objc override func hitTest(ray: Ray) -> BaseNode? {
        wasIteratedByHitTest = true
        return hitNode
    }
}
