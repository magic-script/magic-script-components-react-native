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

    static func toInt(_ value: Any?) -> Int? {
        return value as? Int
    }

    static func toCGFloat(_ value: Any?) -> CGFloat? {
        guard let value = value else { return nil }
        let floatValue: Float? = NumberFormatter().number(from: "\(value)")?.floatValue
        return (floatValue != nil) ? CGFloat(floatValue!) : nil
    }

    static func toCGSize(_ value: Any?) -> CGSize? {
        guard let vec = value as? [Any] else { return nil }
        guard vec.count == 2 else { return nil }
        guard let v0 = Convert.toCGFloat(vec[0]),
            let v1 = Convert.toCGFloat(vec[1]) else { return nil }
        return CGSize(width: v0, height: v1)
    }

    static func toVector3(_ value: Any?) -> SCNVector3? {
        guard let vec = value as? [Any] else { return nil }
        guard vec.count == 3 else { return nil }
        guard let v0 = Convert.toCGFloat(vec[0]),
            let v1 = Convert.toCGFloat(vec[1]),
            let v2 = Convert.toCGFloat(vec[2]) else { return nil }
        return SCNVector3(v0, v1, v2)
    }

    static func toVector4(_ value: Any?) -> SCNVector4? {
        guard let vec = value as? [Any] else { return nil }
        guard vec.count == 4 else { return nil }
        guard let v0 = Convert.toCGFloat(vec[0]),
            let v1 = Convert.toCGFloat(vec[1]),
            let v2 = Convert.toCGFloat(vec[2]),
            let v3 = Convert.toCGFloat(vec[3]) else { return nil }
        return SCNVector4(v0, v1, v2, v3)
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
        guard let value = value else { return nil }
        guard let rgba = value as? [Any], rgba.count >= 3 else {
            return RCTConvert.uiColor(value)
        }
        guard let red = Convert.toCGFloat(rgba[0]),
            let green = Convert.toCGFloat(rgba[1]),
            let blue = Convert.toCGFloat(rgba[2]) else { return nil }
        let alpha: CGFloat? = (rgba.count >= 4) ? Convert.toCGFloat(rgba[3]) : nil
        return UIColor(red: red, green: green, blue: blue, alpha: alpha ?? 1.0)
    }

    static func toFont(_ value: Any?) -> UIFont? {
        return nil
    }

    static func toHorizontalTextAlignment(_ value: Any?) -> HorizontalTextAlignment? {
        guard let alignment = value as? String else { return nil }
        return HorizontalTextAlignment(rawValue: alignment)
    }

    static func toAlignment(_ value: Any?) -> Alignment? {
        guard let alignment = value as? String else { return nil }
        return Alignment(rawValue: alignment)
    }

    static func toFileURL(_ value: Any?) -> URL? {
        guard let filePath = value as? String else { return nil }

        if filePath.starts(with: "http") {
            return URL(string: filePath)
        }

        let fileURL = URL(fileURLWithPath: filePath)
        if FileManager.default.fileExists(atPath: fileURL.path) {
            return fileURL
        }

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
