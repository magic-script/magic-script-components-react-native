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

class PlaneSpec: QuickSpec {
    override func spec() {
        describe("Plane") {
            context("init") {
                it("should init Plane from center and normal") {
                    let referenceCenter = SCNVector3(0.3, 0.22, 0.45)
                    let referenceNormal = SCNVector3(1, 1, 0).normalized()
                    let plane = Plane(center: referenceCenter, normal: referenceNormal)
                    expect(plane.center).to(beCloseTo(referenceCenter))
                    expect(plane.normal).to(beCloseTo(referenceNormal))
                }

                it("should init Plane from vec4 (x > 0)") {
                    let referenceVector = SCNVector4(1, 0, 0, 2.0)
                    let plane = Plane(vector: referenceVector)
                    expect(plane.toVector4()).to(beCloseTo(referenceVector))
                    expect(plane.center).to(beCloseTo(SCNVector3(-referenceVector.w, 0, 0)))
                    expect(plane.normal).to(beCloseTo(SCNVector3(referenceVector.x, referenceVector.y, referenceVector.z)))
                }

                it("should init Plane from vec4 (y > 0)") {
                    let referenceVector = SCNVector4(0, 1, 0, 2.0)
                    let plane = Plane(vector: referenceVector)
                    expect(plane.toVector4()).to(beCloseTo(referenceVector))
                    expect(plane.center).to(beCloseTo(SCNVector3(0, -referenceVector.w, 0)))
                    expect(plane.normal).to(beCloseTo(SCNVector3(referenceVector.x, referenceVector.y, referenceVector.z)))
                }

                it("should init Plane from vec4 (z > 0)") {
                    let referenceVector = SCNVector4(0, 0, 1, 2.0)
                    let plane = Plane(vector: referenceVector)
                    expect(plane.toVector4()).to(beCloseTo(referenceVector))
                    expect(plane.center).to(beCloseTo(SCNVector3(0, 0, -referenceVector.w)))
                    expect(plane.normal).to(beCloseTo(SCNVector3(referenceVector.x, referenceVector.y, referenceVector.z)))
                }

                it("should invoke assert for wrong vec4") {
                    let referenceVector = SCNVector4(0, 0, 0, 2.0)
                    expect { _ = Plane(vector: referenceVector) }.to(throwAssertion())
                }
            }

            context("distanceToPoint") {
                it("should return distance to point") {
                    let referenceCenter = SCNVector3(0.5, -0.5, 0.5)
                    let referenceNormal = SCNVector3(0, 1, 0)
                    let referencePoint = SCNVector3(-1, 2, 3)
                    let referenceDistance: Float = 2.5
                    let plane = Plane(center: referenceCenter, normal: referenceNormal)
                    expect(plane.distanceToPoint(referencePoint)).to(beCloseTo(referenceDistance))
                }
            }

            context("isPointInFront") {
                it("should return true if point is in front of plane") {
                    let referenceNormal = SCNVector3(0, 0, 1)
                    let plane1 = Plane(center: SCNVector3(0, 0, 0), normal: referenceNormal)
                    let referencePoint1 = SCNVector3(-1, 2, 3)
                    let referencePoint2 = SCNVector3(-1, 2, -3)
                    expect(plane1.isPointInFront(referencePoint1)).to(beTrue())
                    expect(plane1.isPointInFront(referencePoint2)).to(beFalse())

                    let plane2 = Plane(center: SCNVector3(0, 0, 0), normal: referenceNormal.negated())
                    expect(plane2.isPointInFront(referencePoint1)).to(beFalse())
                    expect(plane2.isPointInFront(referencePoint2)).to(beTrue())
                }

                it("should return false if point lies on plane") {
                    let referenceCenter = SCNVector3(0, 1, 0)
                    let plane = Plane(center: referenceCenter, normal: SCNVector3(0, 1, 0))
                    expect(plane.isPointInFront(referenceCenter)).to(beFalse())
                }
            }
            
            context("isPointInFrontOrOn") {
                it("should return true if point is in front of plane") {
                    let referenceNormal = SCNVector3(0, 0, 1)
                    let plane1 = Plane(center: SCNVector3(0, 0, 0), normal: referenceNormal)
                    let referencePoint1 = SCNVector3(-1, 2, 3)
                    let referencePoint2 = SCNVector3(-1, 2, -3)
                    expect(plane1.isPointInFrontOrOn(referencePoint1)).to(beTrue())
                    expect(plane1.isPointInFrontOrOn(referencePoint2)).to(beFalse())

                    let plane2 = Plane(center: SCNVector3(0, 0, 0), normal: referenceNormal.negated())
                    expect(plane2.isPointInFrontOrOn(referencePoint1)).to(beFalse())
                    expect(plane2.isPointInFrontOrOn(referencePoint2)).to(beTrue())
                }

                it("should return true if point lies on plane") {
                    let referenceCenter = SCNVector3(0, 1, 0)
                    let plane = Plane(center: referenceCenter, normal: SCNVector3(0, 1, 0))
                    expect(plane.isPointInFrontOrOn(referenceCenter)).to(beTrue())
                }
            }
            
            context("isPointBehind") {
                it("should return true if point is behind plane") {
                    let referenceNormal = SCNVector3(0, 0, 1)
                    let plane1 = Plane(center: SCNVector3(0, 0, 0), normal: referenceNormal)
                    let referencePoint1 = SCNVector3(-1, 2, 3)
                    let referencePoint2 = SCNVector3(-1, 2, -3)
                    expect(plane1.isPointBehind(referencePoint1)).to(beFalse())
                    expect(plane1.isPointBehind(referencePoint2)).to(beTrue())

                    let plane2 = Plane(center: SCNVector3(0, 0, 0), normal: referenceNormal.negated())
                    expect(plane2.isPointBehind(referencePoint1)).to(beTrue())
                    expect(plane2.isPointBehind(referencePoint2)).to(beFalse())
                }

                it("should return false if point lies in plane") {
                    let referenceCenter = SCNVector3(0, 1, 0)
                    let plane = Plane(center: referenceCenter, normal: SCNVector3(0, 1, 0))
                    expect(plane.isPointBehind(referenceCenter)).to(beFalse())
                }
            }

            context("intersectRay") {
                it("should return nil for parallel ray") {
                    let referenceRay = Ray(begin: SCNVector3(0, 3, 0), direction: SCNVector3(1, 0, 0), length: 5)
                    let plane = Plane(center: SCNVector3(0, 2, 0), normal: SCNVector3(0, 1, 0))
                    expect(plane.intersectRay(referenceRay)).to(beNil())
                }

                it("should return intersection point for given ray") {
                    let referenceRay = Ray(begin: SCNVector3(1, 2, 3), direction: SCNVector3(0, -1, 0), length: 10)
                    let plane = Plane(center: SCNVector3(0, 0, 0), normal: SCNVector3(0, 1, 0))
                    expect(plane.intersectRay(referenceRay)).to(beCloseTo(SCNVector3(1, 0, 3)))
                }
            }
        }

        describe("SCNNode") {
            context("convertPlane:to") {
                it("should convert plane to node space") {
                    let rootNode = SCNNode()
                    rootNode.position = SCNVector3(3.4, -5.6, 7.8)
                    let childNode = SCNNode()
                    childNode.position = SCNVector3(1, 2, 3)
                    let originalPlane = Plane(center: SCNVector3Zero, normal: SCNVector3(0, 1, 0))
                    let convertedPlane = rootNode.convertPlane(originalPlane, to: childNode)
                    expect(convertedPlane.normal).to(beCloseTo(originalPlane.normal))
                    expect(convertedPlane.center).to(beCloseTo(rootNode.position - childNode.position))
                }
            }

            context("convertPlane:from node:") {
                it("should convert plane from node space") {
                    let angle: Float = 0.5 * Float.pi
                    let originalNormal = SCNVector3(0, 0, 1)
                    let convertedNormal = SCNVector3(-1, 0, 0)
                    let rootNode = SCNNode()
                    rootNode.orientation = SCNQuaternion.fromAxis(SCNVector3(0, 1, 0), andAngle: angle)
                    let childNode = SCNNode()
                    childNode.orientation = SCNQuaternionIdentity
                    let originalPlane = Plane(center: SCNVector3Zero, normal: originalNormal)
                    let convertedPlane = rootNode.convertPlane(originalPlane, from: childNode)
                    expect(convertedPlane.center).to(beCloseTo(SCNVector3Zero))
                    expect(convertedPlane.normal).to(beCloseTo(convertedNormal))
                }
            }
        }
    }
}
