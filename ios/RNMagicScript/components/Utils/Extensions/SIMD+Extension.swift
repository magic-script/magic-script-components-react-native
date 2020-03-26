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

extension SIMD4 where Scalar == Float {
    init(_ vector: SCNVector4) {
        self.init(vector.x, vector.y, vector.z, vector.w)
    }

    init(_ vector: SCNVector3) {
        self.init(vector.x, vector.y, vector.z, 1)
    }
}
