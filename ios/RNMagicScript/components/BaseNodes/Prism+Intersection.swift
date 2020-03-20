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

import Foundation
import SceneKit

// MARK: - Intersection
extension Prism {
    func intersect(with ray: Ray) -> Bool {
        // Fast test: ray against outer sphere
        let sqDistanceToSpehere = ray.getSqDistanceToPoint(position)
        let maxSize = 0.5 * max(size.x, max(size.y, size.z))
        let maxRadius = maxSize * 1.73205
        if sqDistanceToSpehere > maxRadius * maxRadius {
            return false
        }
        
        // Fast test: ray against inner sphere
        let minRadius = 0.5 * min(size.x, min(size.y, size.z))
        if sqDistanceToSpehere < minRadius * minRadius {
            return true
        }
    
        // Fast test: ray inside prism
        let beginPoint = ray.begin
        let endPoint = ray.end
        if isPointInside(beginPoint) || isPointInside(endPoint) {
            return true
        }
        
        // Exhaustive test: ray against all planes
        let worldSpacePlanes = getClippingPlanes()
        let intersectionPoint: SCNVector3? = intersectPlanes(ray: ray, inputPlanes: worldSpacePlanes, allPlanes: worldSpacePlanes)
        return intersectionPoint != nil
    }
    
    func clipRay(_ ray: Ray) -> Ray {
        let worldSpacePlanes = getClippingPlanes()
        
        let beginDir = ray.direction
        let backPlanes: [Plane] = worldSpacePlanes.filter { $0.normal.dot(beginDir) > 0 }
        let frontPlanes: [Plane] = worldSpacePlanes.filter { $0.normal.dot(beginDir) < 0 }
        
        // begin of clipped ray
        let clippedBegin: SCNVector3? = intersectPlanes(ray: ray, inputPlanes: backPlanes, allPlanes: worldSpacePlanes)
        let clippedEnd: SCNVector3? = intersectPlanes(ray: ray, inputPlanes: frontPlanes, allPlanes: worldSpacePlanes)
        
        if clippedBegin == nil && clippedEnd == nil {
            return ray
        }
        
        let newBegin: SCNVector3 = clippedBegin ?? ray.begin
        let newEnd: SCNVector3 = clippedEnd ?? ray.end
        let direction = newEnd - newBegin
        return Ray(begin: newBegin, direction: direction.normalized(), length: CGFloat(direction.length()))
    }
    
    fileprivate func intersectPlanes(ray: Ray, inputPlanes: [Plane], allPlanes: [Plane]) -> SCNVector3? {
        for plane1 in inputPlanes {
            guard let intersection = plane1.intersectRay(ray) else { continue }
            
            var edgeCounter: Int = 0
            for j in 0..<allPlanes.count {
                let plane2: Plane = allPlanes[j]
                guard abs(plane1.normal.dot(plane2.normal)) < 0.95 else { continue }
                guard plane2.isPointInFront(intersection) else {
                    break
                }
                edgeCounter += 1
            }
            
            assert(edgeCounter <= 4, "Logic error: point must not be bounded by more than 4 planes!")
            if edgeCounter == 4 {
                return intersection
            }
        }
        
        return nil
    }
    
    func isPointInside(_ point: SCNVector3) -> Bool {
        let worldSpacePlanes = getClippingPlanes()
        for plane in worldSpacePlanes {
            if !plane.isPointInFront(point) {
                return false
            }
        }
        
        return true
    }
}
