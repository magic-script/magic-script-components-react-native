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

class PlaneSpec: QuickSpec {
    override func spec() {
        describe("Plane") {
            context("init") {
                it("should init Plane object") {
                    let referenceCenter = SCNVector3(0.3, 0.22, 0.45)
                    let referenceNormal = SCNVector3(1, 1, 0).normalized()
                    let plane = Plane(center: referenceCenter, normal: referenceNormal)
                    expect(plane.center).to(beCloseTo(referenceCenter))
                    expect(plane.normal).to(beCloseTo(referenceNormal))
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

                it("should return false if point lies in plane") {
                    let referenceCenter = SCNVector3(0, 1, 0)
                    let plane = Plane(center: referenceCenter, normal: SCNVector3(0, 1, 0))
                    expect(plane.isPointInFront(referenceCenter)).to(beFalse())
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
    }
}



//@objc open class Plane: NSObject {
//    let center: SCNVector3
//    let normal: SCNVector3
//
//
//    func isPointInFront(_ point: SCNVector3) -> Bool {
//        return distanceToPoint(point) > 0
//    }
//
//    func intersectRay(_ ray: Ray) -> SCNVector3? {
//        // http://geomalgorithms.com/a05-_intersect-1.html
//        let u: SCNVector3 = ray.end - ray.begin
//        let w: SCNVector3 = ray.begin - center
//
//        let D: Float = normal.dot(u)
//        let N: Float = -normal.dot(w)
//
//        guard abs(D) > 0.0001 else {
//            // ray is parallel to the plane
//            // if N == 0 => ray lies in the plane
//            return nil
//        }
//
//        let sI: Float = N / D
//        guard sI >= 0 && sI <= 1.0 else { return nil }
//
//        return ray.begin + sI * u
//    }
//}
