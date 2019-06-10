//
//  SCNMatrix4Extension.swift
//  Scene3D
//
//  Created by Pawel Leszkiewicz on 01/04/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit
import CoreGraphics

extension SCNMatrix4 {
    public static func createPerspectiveFieldOfView(_ fieldOfView: Float, aspectRatio: Float, nearPlaneDistance: Float, farPlaneDistance: Float) -> SCNMatrix4 {
        if (fieldOfView <= 0.0 || fieldOfView >= Float.pi) {
            return SCNMatrix4Identity
        }

        if (nearPlaneDistance <= 0.0) {
            return SCNMatrix4Identity
        }


        if (farPlaneDistance <= 0.0) {
            return SCNMatrix4Identity
        }

        if (nearPlaneDistance >= farPlaneDistance) {
            return SCNMatrix4Identity
        }

        let yScale: Float = 1.0 / tan(fieldOfView * 0.5)
        let xScale: Float = yScale / aspectRatio

        let result = SCNMatrix4(
            m11: xScale, m12: 0, m13: 0, m14: 0,
            m21: yScale, m22: 0, m23: 0, m24: 0,
            m31: 0, m32: 0, m33: farPlaneDistance / (nearPlaneDistance - farPlaneDistance), m34: -1,
            m41: 0, m42: 0, m43: 0, m44: nearPlaneDistance * farPlaneDistance / (nearPlaneDistance - farPlaneDistance))

        return result
    }

    var right: SCNVector3 { return SCNVector3(m11, m12, m13) }
    var up: SCNVector3 { return SCNVector3(m21, m22, m23) }
    var forward: SCNVector3 { return SCNVector3(m31, m32, m33) }
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
