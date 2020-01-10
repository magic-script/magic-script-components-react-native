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

import SceneKit
import UIKit

@objc open class Ray: NSObject {
    let begin: SCNVector3
    let direction: SCNVector3
    let length: CGFloat
    var end: SCNVector3 {
        return begin + direction * length
    }
    
    init(begin: SCNVector3, direction: SCNVector3, length: CGFloat) {
        self.begin = begin
        self.direction = direction
        self.length = length
    }
    
    // MARK: Intersection
    func getClosestPointTo(ray: Ray) -> SCNVector3? {
        // https://en.wikipedia.org/wiki/Skew_lines#Distance
        let p1 = begin
        let d1 = direction
        let p2 = ray.begin
        let d2 = ray.direction
        let n = d1.cross(d2)
        let n2 = d2.cross(n)
        
        let s2 = d1.dot(n2)
        guard abs(s2) > 0.0001 else { return nil }
        let s1 = (p2 - p1).dot(n2)
        let c1 = p1 + (s1 / s2) * d1
        return c1
    }
}

// MARK: UITapGestureRecognizer
extension Ray {
    convenience init?(gesture: UIGestureRecognizer, cameraNode: SCNNode) {
        let tapPoint: CGPoint = gesture.location(in: gesture.view)
        self.init(view: gesture.view, tapPoint: tapPoint, cameraNode: cameraNode)
    }
    
    convenience init?(view: UIView?, tapPoint: CGPoint, cameraNode: SCNNode) {
        guard let view = view else { return nil }
        guard let camera = cameraNode.camera else { return nil }
        
        // Screen space computations
        let tanFOV: CGFloat = CGFloat(tanf(Float(0.5 * camera.fieldOfView.toRadians)))
        let aspect = view.frame.width / view.frame.height
        let halfWidth = view.frame.width * 0.5
        let halfHeight = view.frame.height * 0.5
        
        let screenPoint = CGPoint(
            x: tanFOV * (tapPoint.x / halfWidth - 1.0) * aspect,
            y: tanFOV * (1.0 - tapPoint.y / halfHeight)
        )
        
        // World space computation
        let cameraPosition = cameraNode.position
        let cameraRight = cameraNode.transform.right
        let cameraUp = cameraNode.transform.up
        let cameraForward = cameraNode.transform.forward.negated()
        
        let distance = camera.zNear
        let nearPlaneOrigin: SCNVector3 = cameraPosition + cameraForward * distance
        let shiftRight = cameraRight * screenPoint.x * distance
        let shiftUp = cameraUp * screenPoint.y * distance
        let tapPointOnNearPlane: SCNVector3 = nearPlaneOrigin + shiftRight + shiftUp
        
        // Ray computations
        let rayCastFrom: SCNVector3 = tapPointOnNearPlane
        let rayCastDir: SCNVector3 = (tapPointOnNearPlane - cameraPosition).normalized()
        let rayLength: CGFloat = CGFloat(camera.zFar - camera.zNear)
        
        self.init(begin: rayCastFrom, direction: rayCastDir, length: rayLength)
    }
}
