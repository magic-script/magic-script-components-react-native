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

@objc open class BaseNode: SCNNode {
    @objc func update(_ props: [String: Any]) {

    }

    @objc func addNode(_ node: SCNNode) -> Bool {
        return false
    }

    @objc func removeNode(_ node: SCNNode) {

    }

    @objc func hitTest(ray: Ray) -> BaseNode? {
        return selfHitTest(ray: ray)
    }

    func selfHitTest(ray: Ray) -> BaseNode? {
        return nil
    }

    func convertRayToLocal(ray: Ray) -> Ray {
       let localRayBegin = convertPosition(ray.begin, from: nil)
       let localRayDirection = convertVector(ray.direction, from: nil)
       return Ray(begin: localRayBegin, direction: localRayDirection, length: ray.length)
    }

    func getPlane() -> Plane {
        return Plane(center: position, normal: transform.forward)
    }
}
