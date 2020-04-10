//
//  Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

typealias HitTestResult = (node: BaseNode, point: SCNVector3)

@objc open class BaseNode: SCNNode {
    static let plane: Plane = Plane(center: SCNVector3.zero, normal: SCNVector3.forward)
    
    @objc func update(_ props: [String: Any]) {

    }

    @objc func addNode(_ node: SCNNode) -> Bool {
        return false
    }

    @objc func removeNode(_ node: SCNNode) {

    }

    func hitTest(ray: Ray) -> HitTestResult? {
        return selfHitTest(ray: ray)
    }

    func selfHitTest(ray: Ray) -> HitTestResult? {
        return nil
    }

    func convertRayToLocal(ray: Ray) -> Ray {
       let localRayBegin = convertPosition(ray.begin, from: nil)
       let localRayDirection = convertVector(ray.direction, from: nil)
       return Ray(begin: localRayBegin, direction: localRayDirection, length: ray.length)
    }
}
