//
//  SCNQuaternionExtension.swift
//  Scene3D
//
//  Created by Pawel Leszkiewicz on 03/04/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import Foundation
import SceneKit

extension simd_quatf {
    init(_ quat: SCNQuaternion) {
        self.init(ix: quat.x, iy: quat.y, iz: quat.z, r: quat.w)
    }
}

extension SCNQuaternion {
    public static func fromAxis(_ axis: SCNVector3, andAngle angle: Float) -> SCNQuaternion {
        let s: Float = sin(angle / 2)
        return SCNQuaternion(axis.x * s, axis.y * s, axis.z * s, cos(angle / 2))
    }

    public static func fromSimdQuat(_ simdQuat: simd_quatf) -> SCNQuaternion {
        return SCNVector4Make(simdQuat.vector.x, simdQuat.vector.y, simdQuat.vector.z, simdQuat.vector.w)
    }
}

func * (q1: SCNQuaternion, q2: SCNQuaternion) -> SCNQuaternion {
    return SCNQuaternion.fromSimdQuat(simd_quatf(q1) * simd_quatf(q2))
}
