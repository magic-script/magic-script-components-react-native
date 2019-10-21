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

    init(begin: SCNVector3, direction: SCNVector3, length: CGFloat = 0) {
        self.begin = begin
        self.direction = direction
        self.length = length
    }
}

// MARK: UITapGestureRecognizer
extension Ray {
    convenience init?(gesture: UITapGestureRecognizer, cameraNode: SCNNode) {
        guard let view = gesture.view else { return nil }
        guard let camera = cameraNode.camera else { return nil }

        print("fieldOfView: \(camera.fieldOfView)")

        let tapPoint: CGPoint = gesture.location(in: view)
        let tanFOV: CGFloat = CGFloat(tanf(Float(0.5 * camera.fieldOfView.toRadians)))
        let aspect = view.frame.width / view.frame.height
        let halfWidth = view.frame.width * 0.5
        let halfHeight = view.frame.height * 0.5

        let screenPoint = CGPoint(
            x: tanFOV * (tapPoint.x / halfWidth - 1.0) * aspect,
            y: tanFOV * (1.0 - tapPoint.y / halfHeight)
        )

        let cameraPosition = cameraNode.position
        let cameraRight = cameraNode.transform.right
        let cameraUp = cameraNode.transform.up
        let cameraForward = cameraNode.transform.forward.negated()
        print("cameraPosition: \(cameraPosition)")
        print("cameraRight: \(cameraRight)")
        print("cameraUp: \(cameraUp)")
        print("cameraForward: \(cameraForward)")

        let distance = camera.zNear
        let nearPlaneOrigin: SCNVector3 = cameraPosition + cameraForward * distance
        let shiftRight = cameraRight * screenPoint.x * distance
        let shiftUp = cameraUp * screenPoint.y * distance
        let tapPointOnNearPlane: SCNVector3 = nearPlaneOrigin + shiftRight + shiftUp
        print("nearPlaneOrigin: \(nearPlaneOrigin)")
        print("shiftRight: \(shiftRight)")
        print("shiftUp: \(shiftUp)")

        let rayCastFrom: SCNVector3 = tapPointOnNearPlane
        let rayCastDir: SCNVector3 = (tapPointOnNearPlane - cameraPosition).normalized()
        let rayLength: CGFloat = CGFloat(camera.zFar - camera.zNear)
        print("\nrayCastFrom: \(rayCastFrom)\nrayCastDir: \(rayCastDir)\nrayLength: \(rayLength)")
        self.init(begin: rayCastFrom, direction: rayCastDir, length: rayLength)
    }
}
