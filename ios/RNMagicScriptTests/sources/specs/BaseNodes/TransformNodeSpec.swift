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
import SceneKit
@testable import RNMagicScriptHostApplication

class TransformNodeSpec: QuickSpec {
    override func spec() {
        describe("TransformNode") {

            context("initial properties") {
                it("should have set default values") {
                    let node = TransformNode()

                    expect(node.name).to(beNil())
                    //expect(node.parentedBoneName).to(beEmpty())
                    //expect(node.skipRaycast).to(beFalse())
                    //expect(node.triggerable).to(beTrue())
                    expect(node.visible).to(beTrue())
                    //expect(node.visibilityInherited).to(beTrue())
                    expect(node.anchorPosition).to(beCloseTo(SCNVector3(0,0,0)))
                    expect(node.localPosition).to(beCloseTo(SCNVector3(0,0,0)))
                    expect(node.localRotation).to(beCloseTo(SCNQuaternionIdentity))
                    expect(node.localScale).to(beCloseTo(SCNVector3(1,1,1)))
                    expect(node.localTransform).to(beCloseTo(SCNMatrix4Identity))
                    //expect(node.cursorHoverState).to()
                    //expect(node.offset).to()
                }
            }

            context("update properties") {
                it("should update 'visible' prop") {
                    let node = TransformNode(props: ["visible" : false])
                    expect(node.visible).to(beFalse())
                    node.update(["visible" : true])
                    expect(node.visible).to(beTrue())
                }

                it("should update 'anchorPosition' prop") {
                    let v1 = SCNVector3(0.1, 0.2, 0.3)
                    let v2 = SCNVector3(0.765, 0.82, 1.3)
                    let node = TransformNode(props: ["anchorPosition" : v1.toArrayOfCGFloat])
                    expect(node.anchorPosition).to(beCloseTo(v1))
                    node.update(["anchorPosition" : v2.toArrayOfCGFloat])
                    expect(node.anchorPosition).to(beCloseTo(v2))
                }

                it("should update 'localPosition' prop") {
                    let v1 = SCNVector3(0.4, -0.5, 0.6)
                    let v2 = SCNVector3(0.333, 0.98, 1.43)
                    let node = TransformNode(props: ["localPosition" : v1.toArrayOfFloat])
                    expect(node.localPosition).to(beCloseTo(v1))
                    node.update(["localPosition" : v2.toArrayOfFloat])
                    expect(node.localPosition).to(beCloseTo(v2))
                }

                it("should update 'localRotation' prop") {
                    let q1 = SCNQuaternion(0.8, 1.7, 0, 1)
                    let q2 = SCNQuaternion(-0.6, 1, 1.333, 1)
                    let node = TransformNode(props: ["localRotation" : q1.toArrayOfDouble])
                    expect(node.localRotation).to(beCloseTo(q1))
                    node.update(["localRotation" : q2.toArrayOfDouble])
                    expect(node.localRotation).to(beCloseTo(q2))
                }

                it("should update 'localScale' prop") {
                    let v1 = SCNVector3(-0.4, -0.5, -0.6)
                    let v2 = SCNVector3(1.2, 1.2, 1.2)
                    let node = TransformNode(props: ["localScale" : v1.toArrayOfFloat])
                    expect(node.localScale).to(beCloseTo(v1))
                    node.update(["localScale" : v2.toArrayOfCGFloat])
                    expect(node.localScale).to(beCloseTo(v2))
                }

                it("should update 'localTransform' prop") {
                    let m1 = SCNMatrix4.fromQuaternion(quat: SCNQuaternion(0.8, 1.7, 0, 1))
                    let m2 = SCNMatrix4.fromQuaternion(quat: SCNQuaternion(-0.6, 1, 1.333, 1))
                    let node = TransformNode(props: ["localTransform" : m1.toArrayOfCGFloat])
                    expect(node.localTransform).to(beCloseTo(m1))
                    node.update(["localTransform" : m2.toArrayOfFloat])
                    expect(node.localTransform).to(beCloseTo(m2))
                }
            }

            context("when asked for size") {
                it("should calculate it") {
                    let node = TransformNode()
                    /* correctness of calculation should be checked in spec for derived classes */
                    expect(node.getSize()).to(beCloseTo(CGSize.zero))
                }
            }

            context("when initialized") {
                context("for parent space") {
                    it("should calculate bounds") {
                        let node = TransformNode()
                        /* correctness of calculation should be checked in spec for derived classes */
                        expect(node.getBounds(parentSpace: true)).to(beCloseTo(CGRect.zero))
                    }
                }

                context("for own space") {
                    it("should calculate bounds") {
                        let node = TransformNode()
                        /* correctness of calculation should be checked in spec for derived classes */
                        expect(node.getBounds()).to(beCloseTo(CGRect.zero))
                    }
                }
            }

            context("when asked for size edge insets") {
                it("should calculate it") {
                    let node = TransformNode()
                    /* correctness of calculation should be checked in spec for derived classes */
                    expect(node.getEdgeInsets()).to(beCloseTo(UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: 0.0)))
                }
            }
        }
    }
}
