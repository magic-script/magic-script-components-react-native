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

class SCNNodeClippingPlaneSpec: QuickSpec {
    override func spec() {
        describe("SCNNode+ClippingPlane") {
            let min = SCNVector3(-2, -2, -2)
            let max = SCNVector3(2, 2, 2)
            let planes: [SCNVector4] = [
                SCNVector4( 1, 0, 0,-min.x),
                SCNVector4(-1, 0, 0, max.x),
                SCNVector4(0, 1, 0,-min.y),
                SCNVector4(0,-1, 0, max.y),
                SCNVector4(0, 0, 1,-min.z),
                SCNVector4(0, 0,-1, max.z),
            ]
            var referenceNode: SCNNode!

            beforeEach {
                referenceNode = SCNNode(geometry: SCNSphere(radius: 3.0))
            }

            context("shaders source code") {
                it("should load shaders source code") {
                    let bundle = Bundle.resourcesBundle
                    expect(bundle).notTo(beNil())

                    let geometryURL = bundle!.url(forResource: "ClippingPlane.geometry", withExtension: "txt")!
                    let geometryModifier: String! = try? String(contentsOf: geometryURL)
                    expect(geometryModifier).notTo(beNil())
                    expect(geometryModifier.isEmpty).to(beFalse())

                    let fragmentURL = bundle!.url(forResource: "ClippingPlane.fragment", withExtension: "txt")!
                    let fragmentModifier: String! = try? String(contentsOf: fragmentURL)
                    expect(fragmentModifier).notTo(beNil())
                    expect(fragmentModifier.isEmpty).to(beFalse())
                }
            }

            context("setClippingPlanes") {
                it("should set clipping planes (> 0)") {
                    referenceNode.setClippingPlanes(planes)
                    let modifiers = referenceNode.geometry?.shaderModifiers
                    expect(modifiers).notTo(beNil())
                    expect(modifiers?.count).to(equal(2))
                }

                it("should reset clipping planes (= 0)") {
                    referenceNode.setClippingPlanes(planes)
                    referenceNode.setClippingPlanes([])
                    let modifiers = referenceNode.geometry?.shaderModifiers
                    expect(modifiers).to(beNil())
                }
            }

            context("resetClippingPlanes") {
                it("should reset clipping planes") {
                    referenceNode.setClippingPlanes(planes)
                    referenceNode.resetClippingPlanes()
                    let modifiers = referenceNode.geometry?.shaderModifiers
                    expect(modifiers).to(beNil())
                }
            }
        }
    }
}
