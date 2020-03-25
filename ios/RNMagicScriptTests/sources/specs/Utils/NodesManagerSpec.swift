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

class NodesManagerSpec: QuickSpec {
    override func spec() {
        describe("NodesManager") {
            var nodesManager: NodesManager!
            let referenceSceneNode = Scene()
            let referenceSceneNodeId = "referenceSceneNodeId"
            let referenceNode = TransformNode()
            let referenceNodeId = "referenceNodeId"
            let referencePrismId = "referencePrismId"
            let referenceAnchorUUID = "referenceAnchorUUID"
            let referencePrism = Prism()

            context("always") {
                it("should register AR view when requested") {
                    let nodesManagerRootNode = NodesManager.instance.rootNode
                    let arView = RCTARView()
                    let sceneRootNodes: [SCNNode] = arView.scene.rootNode.childNodes.compactMap { $0 as? BaseNode }
                    expect(sceneRootNodes.count).to(equal(1))
                    expect(sceneRootNodes[0]).to(beIdenticalTo(nodesManagerRootNode))
                }

                it("should handle tap event") {
                    let focusedNode = UiButtonNode()
                    focusedNode.enterFocus()

                    let localReferenceNode = UiButtonNode()
                    let localReferenceNodeId = "referenceNodeId"
                    nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [localReferenceNodeId: localReferenceNode], prismsByAnchorUuid: [:])
                    expect(focusedNode.hasFocus).to(beFalse())
                }
            }

            context("when asked for node by ID") {
                it("should return node when exists") {
                    nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [referenceNodeId: referenceNode], prismsByAnchorUuid: [:])

                    let result = nodesManager.findNodeWithId(referenceNodeId)
                    expect(result).to(beIdenticalTo(referenceNode))
                }

                it("should return nil when node doesnt exist") {
                    nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [:], prismsByAnchorUuid: [:])

                    let result = nodesManager.findNodeWithId(referenceNodeId)
                    expect(result).to(beNil())
                }
            }

            context("when asked for prism by Anchor UUID") {
                it("should return prism when exists") {
                    nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [:], prismsByAnchorUuid: [referenceAnchorUUID: referencePrism])

                    let result = nodesManager.findPrismWithAnchorUuid(referenceAnchorUUID)
                    expect(result).to(beIdenticalTo(referencePrism))
                }

                it("should return nil when prism doesnt exist") {
                    nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [:], prismsByAnchorUuid: [:])

                    let result = nodesManager.findPrismWithAnchorUuid(referenceAnchorUUID)
                    expect(result).to(beNil())
                }
            }

            context("when registering node") {
                it("node should be stored (by ID)") {
                    nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [:], prismsByAnchorUuid: [:])

                    nodesManager.registerNode(referenceNode, nodeId: referenceNodeId)

                    let result = nodesManager.findNodeWithId(referenceNodeId)
                    expect(result).to(beIdenticalTo(referenceNode))
                    expect(result?.name).to(equal(referenceNodeId))
                }

                context("when anchor UUID set (not empty)") {
                    it("prism should be stored (by UUID)") {
                        referencePrism.anchorUuid = referenceAnchorUUID
                        nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [:], prismsByAnchorUuid: [:])

                        nodesManager.registerPrism(referencePrism, prismId: referencePrismId)
                        let result = nodesManager.findPrismWithAnchorUuid(referenceAnchorUUID)
                        expect(result).to(beIdenticalTo(referencePrism))
                    }
                }
            }

            context("when asked to unregister node") {
                context("when node exists in store (by ID)") {
                    it("should be removed from storage") {
                        nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [referenceNodeId: referenceNode], prismsByAnchorUuid: [:])
                        nodesManager.unregisterNode(referenceNodeId)

                        let result = nodesManager.findNodeWithId(referenceNodeId)
                        expect(result).to(beNil())
                    }

                    it("should be removed from parent") {
                        nodesManager = NodesManager(rootNode: TransformNode(), nodesById: [referenceNodeId: referenceNode], prismsByAnchorUuid: [:])
                        let parentNode = SCNNode(geometry: SCNGeometry(sources: [], elements: nil))
                        parentNode.addChildNode(referenceNode)

                        nodesManager.unregisterNode(referenceNodeId)

                        let result = nodesManager.findNodeWithId(referenceNodeId)
                        expect(result).to(beNil())
                        expect(parentNode.childNodes.count).to(equal(0))
                    }
                }
            }

            context("when child and parent node exist in storage (by ID)") {
                it("should add node to parent (when requested)") {
                    let parentReferenceNode = TransformNode()
                    let parentRreferenceNodeId = "parentRreferenceNodeId"

                    nodesManager = NodesManager(rootNode: TransformNode(),
                                                nodesById: [referenceNodeId: referenceNode, parentRreferenceNodeId: parentReferenceNode],
                                                prismsByAnchorUuid: [:])
                    nodesManager.addNode(referenceNodeId, toParent: parentRreferenceNodeId)
                    expect(parentReferenceNode.contentNode.childNodes).to(contain(referenceNode))
                }

                it("should remove node from parent (when requested)") {
                    let parentReferenceNode = TransformNode()
                    let parentRreferenceNodeId = "parentRreferenceNodeId"
                    parentReferenceNode.contentNode.addChildNode(referenceNode)

                    nodesManager = NodesManager(rootNode: TransformNode(),
                                                nodesById: [referenceNodeId: referenceNode, parentRreferenceNodeId: parentReferenceNode],
                                                prismsByAnchorUuid: [:])
                    nodesManager.removeNode(referenceNodeId, fromParent: parentRreferenceNodeId)
                    expect(parentReferenceNode.contentNode.childNodes).toNot(contain(referenceNode))
                }
            }

            context("when child node exists in storage (by ID)") {
                it("should add node to root node (when requested)") {
                    let rootRefereneceNode = BaseNode()
                    nodesManager = NodesManager(rootNode: rootRefereneceNode,
                                                nodesById: [:],
                                                prismsByAnchorUuid: [:])

                    nodesManager.registerScene(referenceSceneNode, sceneId: referenceSceneNodeId)
                    nodesManager.addNodeToRoot(referenceSceneNodeId)
                    expect(rootRefereneceNode.childNodes).to(contain(referenceSceneNode))
                }

                it("should remove node from root node (when requested)") {
                    let rootRefereneceNode = BaseNode()
                    nodesManager = NodesManager(rootNode: rootRefereneceNode,
                                                nodesById: [:],
                                                prismsByAnchorUuid: [:])

                    nodesManager.registerScene(referenceSceneNode, sceneId: referenceSceneNodeId)
                    nodesManager.addNodeToRoot(referenceSceneNodeId)

                    nodesManager.removeNodeFromRoot(referenceNodeId)
                    expect(rootRefereneceNode.childNodes).toNot(contain(referenceNode))
                }
            }

            context("when asked to clear") {
                it("should remove all nodes from parents and storage") {
                    let rootRefereneceNode = TransformNode()
                    rootRefereneceNode.addChildNode(referenceNode)
                    nodesManager = NodesManager(rootNode: rootRefereneceNode,
                                                nodesById: [referenceNodeId: referenceNode],
                                                prismsByAnchorUuid: [:])

                    nodesManager.clear()
                    expect(rootRefereneceNode.childNodes).toNot(contain(referenceNode))
                    let result = nodesManager.findNodeWithId(referenceNodeId)
                    expect(result).to(beNil())
                }
            }

            context("update properties") {
                it("should be delegated update to correct node") {

                }

                it("whould do nothing when node doesn't exist in storage") {
                    nodesManager = NodesManager(rootNode: TransformNode(),
                                                nodesById: [referenceNodeId: referenceNode],
                                                prismsByAnchorUuid: [:])

                    let result = nodesManager.updateNode(referenceNodeId, properties: ["visible" : true])
                    expect(result).to(beTrue())
                    expect(referenceNode.visible).to(beTrue())
                }
            }
        }
    }
}
