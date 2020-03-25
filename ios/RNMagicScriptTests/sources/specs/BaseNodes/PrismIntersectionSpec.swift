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
    override func spec() {
        describe("PrismIntersection") {
            var prism: Prism!

            beforeEach {
                prism = Prism()
                prism.size = SCNVector3(1, 1, 1)
            }
            
            context("prism intersection by ray") {
                it("should not intersect prism") {
                    let directions: [SCNVector3] = [SCNVector3.right, SCNVector3.up, SCNVector3.forward]
                    directions.forEach { direction in
                        let rays = self.getSampleRays(direction: direction)
                        rays.forEach {
                            expect(prism.intersect(with: $0, clippedRay: nil)).to(beFalse())
                        }
                    }
                    
//                    var clippedRay: Ray?
//                    prism.intersect(with: ray, clippedRay: &clippedRay)
//                    prism.getIntersectionPoints(ray: ray)
//                    prism.getClippedRay(ray: ray)
//                    prism.isPointInside(SCNVector3.zero)
//                    prism.isPointInside(SCNVector3(10, 10, 10))
//                    func intersect(with ray: Ray, clippedRay: UnsafeMutablePointer<Ray?>?) -> Bool
//                    func getIntersectionPoints(ray: Ray) -> (begin: SCNVector3?, end: SCNVector3?)?
//                    func getClippedRay(ray: Ray) -> Ray?
//                    func isPointInside(_ point: SCNVector3) -> Bool
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
    
    fileprivate func getSampleRays(direction: SCNVector3, length: CGFloat = 3.0, distance: CGFloat = 1.0, includeLookingAtCenter: Bool = false) -> [Ray] {
        let points = getSamplePoints(distance: distance, includeCenter: includeLookingAtCenter)
        var rays: [Ray] = []
        points.forEach {
            guard includeLookingAtCenter || $0.dot(direction) > -0.99 else { return }
            rays.append(Ray(begin: $0, direction: direction, length: length))
        }
        return rays
    }
}
