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

class SCNNodeEnumerateSpec: QuickSpec {
    override func spec() {
        describe("SCNNode+Enumerate") {

            var rootNode: SCNNode!
            var kNode: SCNNode!

            beforeEach {
                rootNode = SCNNode(); rootNode.name = "R"
                let nodes = self.createTreeHierarchy(rootNode)
                kNode = nodes[10]
            }

            context("enumerateTransformNodes") {
                it("should call block closure for each TransformNode") {
                    let expectedResult = "gajkldmb"
                    var result: String = ""
                    rootNode.enumerateTransformNodes { node in
                        result += node.name ?? "?"
                    }
                    expect(result).to(equal(expectedResult))
                }
            }

            context("enumerateTransformNodesParents") {
                it("should call block closure for each TransformNode") {
                    let expectedResult = "kdb"
                    var result: String = ""
                    kNode.enumerateTransformNodesParents { node in
                        result += node.name ?? "?"
                    }
                    expect(result).to(equal(expectedResult))
                }
            }
        }
    }

    fileprivate func createTreeHierarchy(_ rootNode: SCNNode) -> [SCNNode] {
        // level 1 of the tree
        let node10 = TransformNode(); node10.name = "a"
        let node11 = TransformNode(); node11.name = "b"
        rootNode.addChildNode(node10)
        rootNode.addChildNode(node11)

        // level 2 of the tree
        let node20 = SCNNode(); node20.name = "c"
        let node21 = TransformNode(); node21.name = "d"
        let node22 = SCNNode(); node22.name = "e"
        let node23 = SCNNode(); node23.name = "f"
        node10.addChildNode(node20)
        node11.addChildNode(node21)
        node11.addChildNode(node22)
        node11.addChildNode(node23)

        // level 3 of the tree
        let node30 = TransformNode(); node30.name = "g"
        let node31 = SCNNode(); node31.name = "h"
        let node32 = SCNNode(); node32.name = "i"
        node20.addChildNode(node30)
        node21.addChildNode(node31)
        node22.addChildNode(node32)

        // level 4 of the tree
        let node40 = TransformNode(); node40.name = "j"
        let node41 = TransformNode(); node41.name = "k"
        let node42 = TransformNode(); node42.name = "l"
        let node43 = TransformNode(); node43.name = "m"
        node31.addChildNode(node40)
        node31.addChildNode(node41)
        node31.addChildNode(node42)
        node32.addChildNode(node43)

        return [node10, node11, node20, node21, node22, node23, node30, node31, node32, node40, node41, node42, node43]
    }

    /*
     ( ) - SCNNode
     [ ] - TransformNode
     R - root node

                        (R)
                       /   \___
                      /        \
                    [a]      __[b]__
                     |      /   \   \
                    (c)   [d]   (e) (f)
                     |     |     |
                    [g]   (h)   (i)
                         / | \   |
                       [j][k][l][m]
     */
}
