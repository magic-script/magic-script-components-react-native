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

import SceneKit

extension SCNNode {
    public func orientUpVectorAlong(_ vector: SCNVector3) {
        let up = SCNVector3(0, 1, 0)
        let rotationAxis: SCNVector3 = vector.cross(up).normalized()
        let angle: Float = -vector.angleToVector(up)
        orientation = SCNQuaternion.fromAxis(rotationAxis, andAngle: angle)
    }
    
    public func angleToWorldFront() -> Float {
        let globalFront = SCNVector3.forward
        var front = worldFront
        front.y = 0.0
        front.normalize()
        let angle = acos(front.dot(globalFront))
        
        var right = self.worldRight
        right.y = 0.0
        right.normalize()
        
        return right.dot(globalFront) >= 0.0 ? angle : (2 * Float.pi - angle)
    }
}

