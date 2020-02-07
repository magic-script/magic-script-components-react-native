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
import ARKit

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

    static let numberFormatter: NumberFormatter = {
        let numberFormatter = NumberFormatter()
        numberFormatter.decimalSeparator = "."
        return numberFormatter
    }()

    static func toFloat(_ value: Any?) -> Float? {
        guard let value = value else { return nil }
        return Convert.numberFormatter.number(from: "\(value)")?.floatValue
    }

    static func toCGFloat(_ value: Any?) -> CGFloat? {
        let floatValue: Float? = Convert.toFloat(value)
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
        guard let v0 = Convert.toFloat(vec[0]),
            let v1 = Convert.toFloat(vec[1]),
            let v2 = Convert.toFloat(vec[2]) else { return nil }
        return SCNVector3(v0, v1, v2)
    }

    static func toVector4(_ value: Any?) -> SCNVector4? {
        guard let vec = value as? [Any] else { return nil }
        guard vec.count == 4 else { return nil }
        guard let v0 = Convert.toFloat(vec[0]),
            let v1 = Convert.toFloat(vec[1]),
            let v2 = Convert.toFloat(vec[2]),
            let v3 = Convert.toFloat(vec[3]) else { return nil }
        return SCNVector4(v0, v1, v2, v3)
    }

    static func toQuaternion(_ value: Any?) -> SCNQuaternion? {
        return Convert.toVector4(value)
    }

    static func toMatrix4(_ value: Any?) -> SCNMatrix4? {
        guard let mat = value as? [Any] else { return nil }
        guard mat.count == 16 else { return nil }
        guard let m11 = Convert.toFloat(mat[0]),
            let m12 = Convert.toFloat(mat[1]),
            let m13 = Convert.toFloat(mat[2]),
            let m14 = Convert.toFloat(mat[3]),
            let m21 = Convert.toFloat(mat[4]),
            let m22 = Convert.toFloat(mat[5]),
            let m23 = Convert.toFloat(mat[6]),
            let m24 = Convert.toFloat(mat[7]),
            let m31 = Convert.toFloat(mat[8]),
            let m32 = Convert.toFloat(mat[9]),
            let m33 = Convert.toFloat(mat[10]),
            let m34 = Convert.toFloat(mat[11]),
            let m41 = Convert.toFloat(mat[12]),
            let m42 = Convert.toFloat(mat[13]),
            let m43 = Convert.toFloat(mat[14]),
            let m44 = Convert.toFloat(mat[15]) else { return nil }
        return SCNMatrix4(
            m11: m11, m12: m12, m13: m13, m14: m14,
            m21: m21, m22: m22, m23: m23, m24: m24,
            m31: m31, m32: m32, m33: m33, m34: m34,
            m41: m41, m42: m42, m43: m43, m44: m44
        )
    }

    // The padding order is: top, right, bottom, left.
    static func toPadding(_ value: Any?) -> UIEdgeInsets? {
        guard let padding = value as? [Any] else { return nil }
        guard padding.count == 4 else { return nil }
        guard let top = Convert.toFloat(padding[0]),
            let right = Convert.toFloat(padding[1]),
            let bottom = Convert.toFloat(padding[2]),
            let left = Convert.toFloat(padding[3]) else { return nil }
        return UIEdgeInsets(top: CGFloat(top), left: CGFloat(left), bottom: CGFloat(bottom), right: CGFloat(right))
    }

    static func toColor(_ value: Any?) -> UIColor? {
        guard let rgba = value as? [Any], rgba.count >= 3 else { return nil }
        guard let red = Convert.toCGFloat(rgba[0]),
            let green = Convert.toCGFloat(rgba[1]),
            let blue = Convert.toCGFloat(rgba[2]) else { return nil }
        let alpha: CGFloat? = (rgba.count >= 4) ? Convert.toCGFloat(rgba[3]) : nil
        return UIColor(red: red, green: green, blue: blue, alpha: alpha ?? 1.0)
    }

    static func toHorizontalTextAlignment(_ value: Any?) -> HorizontalTextAlignment? {
        guard let alignment = value as? String else { return nil }
        return HorizontalTextAlignment(rawValue: alignment)
    }

    static func toAlignment(_ value: Any?) -> Alignment? {
        guard let alignment = value as? String else { return nil }
        return Alignment(rawValue: alignment)
    }

    static func toFontStyle(_ value: Any?) -> FontStyle? {
        guard let fontStyle = value as? String else { return nil }
        return FontStyle(rawValue: fontStyle)
    }

    static func toFontWeight(_ value: Any?) -> FontWeight? {
        guard let fontWeight = value as? String else { return nil }
        return FontWeight(rawValue: fontWeight)
    }

    static func toScrollBarVisibility(_ value: Any?) -> ScrollBarVisibility? {
        guard let scrollBarVisibility = value as? String else { return nil }
        return ScrollBarVisibility(rawValue: scrollBarVisibility)
    }

    static func toScrollDirection(_ value: Any?) -> ScrollDirection? {
        guard let scrollDirection = value as? String else { return nil }
        return ScrollDirection(rawValue: scrollDirection)
    }

    static func toOrientation(_ value: Any?) -> Orientation? {
        guard let orientation = value as? String else { return nil }
        return Orientation(rawValue: orientation)
    }

    static func toTextEntryMode(_ value: Any?) -> TextEntryMode? {
        guard let textEntryMode = value as? String else { return nil }
        return TextEntryMode(rawValue: textEntryMode)
    }

    static func toFileURL(_ value: Any?) -> URL? {
        let path: String?
        if let dict = value as? [String: Any] {
            path = dict["uri"] as? String
        } else {
            path = value as? String
        }

        guard let filePath = path else { return nil }
        if filePath.starts(with: "http") || filePath.starts(with: "file") {
            return URL(string: filePath)
        }

        return nil
    }

    static func toSystemIcon(_ value: Any?) -> SystemIcon? {
        guard let icon = value as? String else { return nil }
        return SystemIcon(icon)
    }

    static func toAudioAction(_ value: Any?) -> AudioAction? {
        guard let action = value as? String else { return nil }
        return AudioAction(rawValue: action)
    }

    static func toVideoAction(_ value: Any?) -> VideoAction? {
        guard let action = value as? String else { return nil }
        return VideoAction(rawValue: action)
    }

    static func toVideoViewMode(_ value: Any?) -> VideoViewMode? {
        guard let viewMode = value as? String else { return nil }
        return VideoViewMode(rawValue: viewMode)
    }

    static func toWebViewAction(_ value: Any?) -> WebViewAction? {
        guard let viewMode = value as? String else { return nil }
        return WebViewAction(rawValue: viewMode)
    }

    static func toSide(_ value: Any?) -> Side? {
        guard let side = value as? String else { return nil }
        return Side(rawValue: side)
    }

    static func toToggleType(_ value: Any?) -> ToggleType? {
        guard let toggleType = value as? String else { return nil }
        return ToggleType(rawValue: toggleType)
    }

    static func toPlaneDetection(_ value: Any?) -> ARWorldTrackingConfiguration.PlaneDetection {
        guard let planeDetection = value as? [Any] else { return [] }
        var result: ARWorldTrackingConfiguration.PlaneDetection = []
        for index in 0..<planeDetection.count {
            if let stringValue = planeDetection[index] as? String {
                if stringValue == "vertical" { result.insert(.vertical) }
                if stringValue == "horizontal" { result.insert(.horizontal) }
            }
        }

        return result
    }

}
