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
import CoreGraphics
import UIKit
@testable import RNMagicScriptHostApplication

class RaySpec: QuickSpec {
    override func spec() {
        describe("Ray") {
            context("init with begin, direction and length") {
                it("should init Ray object") {
                    let referenceBegin: SCNVector3 = SCNVector3(1, 2, 3)
                    let referenceDirection: SCNVector3 = SCNVector3(0, 1, 0)
                    let referenceLength: CGFloat = 2.3
                    let referenceEnd: SCNVector3 = referenceBegin + referenceDirection * referenceLength
                    let ray = Ray(begin: referenceBegin, direction: referenceDirection, length: referenceLength)
                    expect(ray.begin).to(beCloseTo(referenceBegin))
                    expect(ray.direction).to(beCloseTo(referenceDirection))
                    expect(ray.length).to(beCloseTo(referenceLength))
                    expect(ray.end).to(beCloseTo(referenceEnd))
                }
            }

            context("init with gesture and cameraNode") {
                it("should not init Ray object due to gesture not attached to any view") {
                    let tapGesture = UITapGestureRecognizer()
                    let cameraNode = SCNNode()
                    let ray = Ray(gesture: tapGesture, cameraNode: cameraNode)
                    expect(ray).to(beNil())
                }

                it("should not init Ray object due to no camera is attached to the node") {
                    let view = UIView(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
                    let tapGesture = UITapGestureRecognizer()
                    view.addGestureRecognizer(tapGesture)
                    let cameraNode = SCNNode()
                    let ray = Ray(gesture: tapGesture, cameraNode: cameraNode)
                    expect(ray).to(beNil())
                }

                it("should init Ray object") {
                    let view = UIView(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
                    let tapGesture = UITapGestureRecognizer()
                    view.addGestureRecognizer(tapGesture)

                    let camera = SCNCamera()
                    let cameraNode = SCNNode()
                    cameraNode.position = SCNVector3(0, 0, 1.5)
                    cameraNode.camera = camera
                    let ray = Ray(gesture: tapGesture, cameraNode: cameraNode)
                    expect(ray).notTo(beNil())
                    expect(ray!.begin).to(beCloseTo(SCNVector3(-0.5773503, 0.5773503, 0.5)))
                    expect(ray!.direction).to(beCloseTo(SCNVector3(-0.44721362, 0.44721362, -0.77459663)))
                    expect(ray!.length).to(beCloseTo(CGFloat(camera.zFar - camera.zNear)))
                }
            }
        }
    }
}
