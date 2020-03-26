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
import GLKit

extension SCNMatrix4 {
    var right: SCNVector3 {
        get { return SCNVector3(m11, m12, m13) }
        set { m11 = newValue.x; m12 = newValue.y; m13 = newValue.z; }
    }
    var up: SCNVector3 {
        get { return SCNVector3(m21, m22, m23) }
        set { m21 = newValue.x; m22 = newValue.y; m23 = newValue.z; }
    }
    var forward: SCNVector3 {
        get { return SCNVector3(m31, m32, m33) }
        set { m31 = newValue.x; m32 = newValue.y; m33 = newValue.z; }
    }
    var position: SCNVector3 {
        get { return SCNVector3(m41, m42, m43) }
        set { m41 = newValue.x; m42 = newValue.y; m43 = newValue.z; }
    }

    public static func fromQuaternion(quat: SCNQuaternion) -> SCNMatrix4 {
        let q: GLKQuaternion = GLKQuaternionMake(Float(quat.x), Float(quat.y), Float(quat.z), Float(quat.w))
        let matrix = GLKMatrix4MakeWithQuaternion(q)
        return SCNMatrix4FromGLKMatrix4(matrix)
    }

    func toQuaternion() -> SCNQuaternion {
        let matrix: GLKMatrix4 = SCNMatrix4ToGLKMatrix4(self)
        let q: GLKQuaternion = GLKQuaternionMakeWithMatrix4(matrix)
        return SCNQuaternion(x: q.x, y: q.y, z: q.z, w: q.w)
    }
}

func * (left: SCNMatrix4, right: SCNVector3) -> SCNVector3 {
    let matrix = float4x4(left)
    let vector = SIMD4<Float>(SCNVector4(right))
    let result = matrix * vector

    return SCNVector3(result)
}

extension float4x4 {
    init(_ matrix: SCNMatrix4) {
        self.init([
            SIMD4<Float>(matrix.m11, matrix.m12, matrix.m13, matrix.m14),
            SIMD4<Float>(matrix.m21, matrix.m22, matrix.m23, matrix.m24),
            SIMD4<Float>(matrix.m31, matrix.m32, matrix.m33, matrix.m34),
            SIMD4<Float>(matrix.m41, matrix.m42, matrix.m43, matrix.m44)
        ])
    }
}
