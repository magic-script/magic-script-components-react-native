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

class BaseNodeSpec: QuickSpec {
    override func spec() {
        describe("BaseNode") {
            var node: BaseNode!

            beforeEach {
                node = BaseNode()
            }

            context("when asked to add child node") {
                it("should return false") {
                    let childNode = SCNNode()
                    expect(node.childNodes.count).to(equal(0))
                    let result = node.addNode(childNode)
                    expect(result).to(beFalse())
                    expect(node.childNodes.count).to(equal(0))
                }
            }

            context("when asked to remove child node") {
                it("should do nothing") {
                    let childNode = SCNNode()
                    expect(node.childNodes.count).to(equal(0))
                    node.removeNode(childNode)
                    expect(node.childNodes.count).to(equal(0))
                }
            }

            context("when hitTest requested for") {
                it("should return nil") {
                    let result = node.hitTest(ray: Ray(begin: SCNVector3(), direction: SCNVector3(), length: 0.0))
                    expect(result).to(beNil())
                }
            }

            context("when asked for local plane") {
                it("should always return the same plane") {
                    expect(BaseNode.plane.center).to(beCloseTo(SCNVector3.zero))
                    expect(BaseNode.plane.normal).to(beCloseTo(SCNVector3.forward))
                }
            }
        }
    }
}
