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

@objc open class Plane: NSObject {
    let center: SCNVector3
    let normal: SCNVector3

    init(center: SCNVector3, normal: SCNVector3) {
        self.center = center
        self.normal = normal
    }

    func distanceToPoint(_ point: SCNVector3) -> Float {
        // https://stackoverflow.com/questions/3860206/signed-distance-between-plane-and-point
        return normal.dot(point - center)
    }

    func isPointInFront(_ point: SCNVector3) -> Bool {
        return distanceToPoint(point) > 0
    }

    func intersectRay(_ ray: Ray) -> SCNVector3? {
        // http://geomalgorithms.com/a05-_intersect-1.html
        let u: SCNVector3 = ray.end - ray.begin
        let w: SCNVector3 = ray.begin - center

        let D: Float = normal.dot(u)
        let N: Float = -normal.dot(w)

        guard abs(D) > 0.0001 else {
            // ray is parallel to the plane
            // if N == 0 => ray lies in the plane
            return nil
        }

        let sI: Float = N / D
        guard sI >= 0 && sI <= 1.0 else { return nil }

        return ray.begin + sI * u
    }
}
