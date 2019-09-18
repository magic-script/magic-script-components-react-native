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

class SCNQuaternionExtensionSpec: QuickSpec {
    override func spec() {
        describe("SCNQuaternion+Extension") {
            context("identity quaternion") {
                it("should initialize an identity quaternion") {
                    let quat1 = SCNQuaternionIdentity
                    let quat2 = SCNQuaternion.identity
                    expect(quat1).to(beCloseTo(quat2))
                    expect(quat1).to(beCloseTo(SCNQuaternion(0, 0, 0, 1)))
                }
            }

            context("fromAxisAndAngle") {
                it("should create quaternion from axis and angle") {
                    let angle: Float = 1.23
                    let axis = SCNVector3(0.577, 0.577, 0.577)
                    let quat1 = SCNQuaternion.fromAxis(axis, andAngle: angle)
                    expect(quat1).to(beCloseTo(SCNQuaternion(0.3329, 0.3329, 0.3329, 0.81677)))
                }
            }
        }
    }
}
