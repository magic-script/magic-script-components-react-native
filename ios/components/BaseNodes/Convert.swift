//
//  Converter.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 14/06/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

import SceneKit

class Convert {
    static func toString(_ value: Any?) -> String? {
        return value as? String
    }

    static func toBool(_ value: Any?) -> Bool? {
        return value as? Bool
    }

    static func toCGFloat(_ value: Any?) -> CGFloat? {
        return value as? CGFloat
    }

    static func toCGSize(_ value: Any?) -> CGSize? {
        guard let vec = value as? [CGFloat] else { return nil }
        guard vec.count == 2 else { return nil }
        return CGSize(width: vec[0], height: vec[1])
    }

    static func toVector3(_ value: Any?) -> SCNVector3? {
        guard let vec = value as? [CGFloat] else { return nil }
        guard vec.count == 3 else { return nil }
        return SCNVector3(vec[0], vec[1], vec[2])
    }

    static func toVector4(_ value: Any?) -> SCNVector4? {
        guard let vec = value as? [CGFloat] else { return nil }
        guard vec.count == 4 else { return nil }
        return SCNVector4(vec[0], vec[1], vec[2], vec[3])
    }

    static func toQuaternion(_ value: Any?) -> SCNQuaternion? {
        return Convert.toVector4(value)
    }

    static func toMatrix4(_ value: Any?) -> SCNMatrix4? {
        guard let mat = value as? [Float] else { return nil }
        guard mat.count == 16 else { return nil }
        return SCNMatrix4(
            m11: mat[0], m12: mat[1], m13: mat[2], m14: mat[3],
            m21: mat[4], m22: mat[5], m23: mat[6], m24: mat[7],
            m31: mat[8], m32: mat[9], m33: mat[10], m34: mat[11],
            m41: mat[12], m42: mat[13], m43: mat[14], m44: mat[15]
        )
    }

    static func toColor(_ value: Any?) -> UIColor? {
        guard value != nil else { return nil }
        return RCTConvert.uiColor(value!)
    }

    static func toFont(_ value: Any?) -> UIFont? {
        return nil
    }

    static func toFileURL(_ value: Any?) -> URL? {
        guard let filePath = value as? String else { return nil }

        let targetURL: URL = Bundle.main.bundleURL.appendingPathComponent("assets").appendingPathComponent(filePath)
        if FileManager.default.fileExists(atPath: targetURL.path) {
            return targetURL
        }

        #if targetEnvironment(simulator)
        let assetsURL = URL(string: "http://localhost:8081/assets/")
        #else
        let assetsURL = URL(string: "http://192.168.0.94:8081/assets/")
        #endif
        return assetsURL?.appendingPathComponent(filePath)
    }
}
