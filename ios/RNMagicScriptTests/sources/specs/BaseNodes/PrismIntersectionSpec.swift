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

class PrismIntersectionSpec: QuickSpec {
    // func intersect(with ray: Ray, clippedRay: UnsafeMutablePointer<Ray?>?) -> Bool
    // func getIntersectionPoints(ray: Ray) -> (begin: SCNVector3?, end: SCNVector3?)?
    // func getClippedRay(ray: Ray) -> Ray?
    // func isPointInside(_ point: SCNVector3) -> Bool
    override func spec() {
        describe("PrismIntersection") {
            var prism: Prism!

            beforeEach {
                prism = Prism()
                prism.size = SCNVector3(1, 1, 1)
            }
            
            context("ray does not intersect prism") {
                let validateRays: (_ rays: [Ray]) -> () = { rays in
                    rays.forEach {
                        var outRay: Ray?
                        expect(prism.intersect(with: $0, clippedRay: &outRay)).to(beFalse())
                        expect(outRay).to(beNil())
                    }
                }
                
                it("should not intersect prism (axis aligned rays)") {
                    let directions: [SCNVector3] = [SCNVector3.right, SCNVector3.up, SCNVector3.forward]
                    directions.forEach { direction in
                        let rays = self.getAxisAlignedRays(direction: direction)
                        validateRays(rays)
                    }
                }
                
                it("should not intersect prism (sphere of negated rays)") {
                    let rays = self.getSphereOfRays(center: SCNVector3.zero, radius: 0.87, length: 1.0, negated: true)
                    validateRays(rays)
                }
                
                it("should not intersect prism (sphere of positive but short rays)") {
                    let rays = self.getSphereOfRays(center: SCNVector3.zero, radius: 1.0, length: 0.1, negated: false)
                    validateRays(rays)
                }
                
                it("should not intersect prism (sphere of rays moved away from prism)") {
                    let positiveRays = self.getSphereOfRays(center: SCNVector3(3, 3, 3), radius: 1, length: 0.5, negated: false)
                    validateRays(positiveRays)
                    
                    let negatedRays = self.getSphereOfRays(center: SCNVector3(3, 3, 3), radius: 1, length: 0.5, negated: true)
                    validateRays(negatedRays)
                }
            }
                
            context("ray does intersect prism") {
                let validateRays: (_ rays: [Ray]) -> () = { rays in
                    rays.forEach {
                        var outRay: Ray?
                        expect(prism.intersect(with: $0, clippedRay: &outRay)).to(beTrue())

                        if outRay == nil {
                            print("outRay must not be nil")
                            prism.intersect(with: $0, clippedRay: &outRay)
                        }
                        expect(outRay).notTo(beNil())
                    }
               }
                
                it("should intersect prism (sphere of positive rays)") {
                    let rays = self.getSphereOfRays(center: SCNVector3.zero, radius: 1.0, length: 2.0, negated: false)
                    validateRays(rays)
                }
                
                it("should intersect prism (sphere of internal short rays)") {
                    let rays = self.getSphereOfRays(center: SCNVector3.zero, radius: 0.4, length: 0.1, negated: false)
                    validateRays(rays)
                }
                
                it("should intersect prism (sphere of internal short rays with offset)") {
                    let rays = self.getSphereOfRays(center: SCNVector3(0.35, 0.35, 0.35), radius: 0.1, length: 0.01, negated: false)
                    validateRays(rays)
                }
                
                it("should intersect prism (edge cases)") {
                    let edgeCaseRays: [Ray] = [
                        Ray(begin: SCNVector3(-0.49, -0.49, -1.0), direction: SCNVector3.forward, length: 2.0),
                        Ray(begin: SCNVector3( 0.49,  0.49, -1.0), direction: SCNVector3.forward, length: 0.6),
                        Ray(begin: SCNVector3(-0.49, -0.49,  1.0), direction: SCNVector3.forward.negated(), length: 2.0),
                        Ray(begin: SCNVector3( 0.49,  0.49,  1.0), direction: SCNVector3.forward.negated(), length: 0.6),
                    ]
                    validateRays(edgeCaseRays)
                }
            }
            
            context("isPointInside") {
                it("should be inside prism") {
                    let points = self.getSamplePoints(distance: 0.49)
                    points.forEach { expect(prism.isPointInside($0)).to(beTrue()) }
                }
                
                it("should be outside prism") {
                    let points = self.getSamplePoints(includeCenter: false)
                    points.forEach { expect(prism.isPointInside($0)).to(beFalse()) }
                }
            }
        }
    }
    
    fileprivate func getSphereOfRays(center: SCNVector3 = SCNVector3.zero, radius: CGFloat = 2.0, length: CGFloat = 5.0, negated: Bool = false) -> [Ray] {
        var circleOfPoints: [SCNVector3] = []
        let circlePointsCount: Int = 20
        let deltaAlpha: CGFloat = 2.0 * CGFloat.pi / CGFloat(circlePointsCount)
        for alpha in stride(from: -CGFloat.pi, to: CGFloat.pi, by: deltaAlpha) {
            let x = radius * cos(alpha)
            let y = radius * sin(alpha)
            circleOfPoints.append(SCNVector3(x, y, 0))
        }
        
        var sphereOfPoints: [SCNVector3] = []
        let deltaBeta: CGFloat = CGFloat.pi / CGFloat(circlePointsCount)
        for beta in stride(from: 0, to: CGFloat.pi, by: deltaBeta) {
            let quat = SCNQuaternion.fromAxis(SCNVector3.right, andAngle: Float(beta))
            let matrix: SCNMatrix4 = SCNMatrix4.fromQuaternion(quat: quat)
            circleOfPoints.forEach { sphereOfPoints.append(matrix * $0) }
        }
        
        if negated {
            return sphereOfPoints.map { Ray(begin: center + $0, direction: $0.normalized(), length: length) }
        } else {
            return sphereOfPoints.map { Ray(begin: center + $0, direction: $0.normalized().negated(), length: length) }
        }
    }
    
    fileprivate func getSamplePoints(distance: CGFloat = 1.0, includeCenter: Bool = false) -> [SCNVector3] {
        var points: [SCNVector3] = []
        for x in -1...1 {
            for y in -1...1 {
                for z in -1...1 {
                    points.append(distance * SCNVector3(x, y, z))
                }
            }
        }
        
        if !includeCenter {
            points.remove(at: 13)
        }
        
        return points
    }
    
    fileprivate func getAxisAlignedRays(direction: SCNVector3, length: CGFloat = 3.0, distance: CGFloat = 1.0, includeLookingAtCenter: Bool = false) -> [Ray] {
        let points = getSamplePoints(distance: distance, includeCenter: includeLookingAtCenter)
        var rays: [Ray] = []
        points.forEach {
            guard includeLookingAtCenter || $0.dot(direction) > -0.99 else { return }
            rays.append(Ray(begin: $0, direction: direction, length: length))
        }
        return rays
    }
}
