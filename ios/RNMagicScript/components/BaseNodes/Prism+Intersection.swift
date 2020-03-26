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

import Foundation
import SceneKit

// MARK: - Intersection
extension Prism {
    func intersect(with ray: Ray, clippedRay: UnsafeMutablePointer<Ray?>?) -> Bool {
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
            if clippedRay != nil {
                clippedRay?.pointee = getClippedRay(ray: ray)
            }
            return true
        }
    
        // Fast test: ray inside prism
        let beginPoint = ray.begin
        let endPoint = ray.end
        if isPointInside(beginPoint) || isPointInside(endPoint) {
            if clippedRay != nil {
                clippedRay?.pointee = getClippedRay(ray: ray)
            }
            return true
        }
        
        // Exhaustive test: ray against all planes
        let result = getClippedRay(ray: ray)
        if clippedRay != nil {
            clippedRay?.pointee = result
        }
        
        return result != nil
    }
    
    func getIntersectionPoints(ray: Ray) -> (begin: SCNVector3?, end: SCNVector3?)? {
        let worldSpacePlanes = getClippingPlanes()
        
        let beginDir = ray.direction
        let backPlanes: [Plane] = worldSpacePlanes.filter { $0.normal.dot(beginDir) > 0 }
        let frontPlanes: [Plane] = worldSpacePlanes.filter { $0.normal.dot(beginDir) < 0 }
        
        // begin of clipped ray
        let clippedBegin: SCNVector3? = intersectPlanes(ray: ray, inputPlanes: backPlanes, allPlanes: worldSpacePlanes)
        let clippedEnd: SCNVector3? = intersectPlanes(ray: ray, inputPlanes: frontPlanes, allPlanes: worldSpacePlanes)
        
        if clippedBegin == nil && clippedEnd == nil {
            return nil
        }
        
        return (begin: clippedBegin, end: clippedEnd)
    }
    
    func getClippedRay(ray: Ray) -> Ray? {
        if isPointInside(ray.begin) && isPointInside(ray.end) {
            return ray
        }
        
        guard let points = getIntersectionPoints(ray: ray) else {
            return nil
        }
        
        let newBegin: SCNVector3 = points.begin ?? ray.begin
        let newEnd: SCNVector3 = points.end ?? ray.end
        let direction = newEnd - newBegin
        return Ray(begin: newBegin, direction: direction.normalized(), length: CGFloat(direction.length()))
    }
    
    func isPointInside(_ point: SCNVector3) -> Bool {
        let worldSpacePlanes = getClippingPlanes()
        for plane in worldSpacePlanes {
            if !plane.isPointInFrontOrOn(point) {
                return false
            }
        }
        
        return true
    }
    
    fileprivate func intersectPlanes(ray: Ray, inputPlanes: [Plane], allPlanes: [Plane]) -> SCNVector3? {
        for plane1 in inputPlanes {
            guard let intersection = plane1.intersectRay(ray) else { continue }
            
            var edgeCounter: Int = 0
            for j in 0..<allPlanes.count {
                let plane2: Plane = allPlanes[j]
                guard abs(plane1.normal.dot(plane2.normal)) < 0.95 else { continue }
                guard plane2.isPointInFrontOrOn(intersection) else {
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
}
