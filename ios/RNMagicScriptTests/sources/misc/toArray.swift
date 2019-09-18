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

extension SCNVector3 {
    var toArrayOfFloat: [Float] {
        return [x, y, z]
    }
    var toArrayOfCGFloat: [CGFloat] {
        return [CGFloat(x), CGFloat(y), CGFloat(z)]
    }
    var toArrayOfDouble: [Double] {
        return [Double(x), Double(y), Double(z)]
    }
    var toArrayOfInt: [Int] {
        return [Int(x), Int(y), Int(z)]
    }
}

extension SCNVector4 {
    var toArrayOfFloat: [Float] {
        return [x, y, z, w]
    }
    var toArrayOfCGFloat: [CGFloat] {
        return [CGFloat(x), CGFloat(y), CGFloat(z), CGFloat(w)]
    }
    var toArrayOfDouble: [Double] {
        return [Double(x), Double(y), Double(z), Double(w)]
    }
    var toArrayOfInt: [Int] {
        return [Int(x), Int(y), Int(z), Int(w)]
    }
}

extension SCNMatrix4 {
    var toArrayOfFloat: [Float] {
        return [m11, m12, m13, m14, m21, m22, m23, m24, m31, m32, m33, m34, m41, m42, m43, m44]
    }
    var toArrayOfCGFloat: [CGFloat] {
        return [CGFloat(m11), CGFloat(m12), CGFloat(m13), CGFloat(m14), CGFloat(m21), CGFloat(m22), CGFloat(m23), CGFloat(m24), CGFloat(m31), CGFloat(m32), CGFloat(m33), CGFloat(m34), CGFloat(m41), CGFloat(m42), CGFloat(m43), CGFloat(m44)]
    }
    var toArrayOfDouble: [Double] {
        return [Double(m11), Double(m12), Double(m13), Double(m14), Double(m21), Double(m22), Double(m23), Double(m24), Double(m31), Double(m32), Double(m33), Double(m34), Double(m41), Double(m42), Double(m43), Double(m44)]
    }
    var toArrayOfInt: [Int] {
        return [Int(m11), Int(m12), Int(m13), Int(m14), Int(m21), Int(m22), Int(m23), Int(m24), Int(m31), Int(m32), Int(m33), Int(m34), Int(m41), Int(m42), Int(m43), Int(m44)]
    }
}

extension UIColor {
    var toArrayOfCGFloat: [CGFloat] {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        return [red, green, blue, alpha]
    }
    var toArrayOfFloat: [Float] {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        return [Float(red), Float(green), Float(blue), Float(alpha)]
    }
    var toArrayOfDouble: [Double] {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        return [Double(red), Double(green), Double(blue), Double(alpha)]
    }
    var toArrayOfInt: [Int] {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        return [Int(red), Int(green), Int(blue), Int(alpha)]
    }
}

extension CGSize {
    var toArrayOfCGFloat: [CGFloat] {
        return [self.width, self.height]
    }
    var toArrayOfFloat: [Float] {
        return [Float(self.width), Float(self.height)]
    }
}

extension UIEdgeInsets {
    var toArrayOfCGFloat: [CGFloat] {
        return [self.top, self.right, self.bottom, self.left]
    }
    var toArrayOfFloat: [Float] {
        return [Float(self.top), Float(self.right), Float(self.bottom), Float(self.left)]
    }
    var toArrayOfDouble: [Double] {
        return [Double(self.top), Double(self.right), Double(self.bottom), Double(self.left)]
    }
    var toArrayOfInt: [Int] {
        return [Int(self.top), Int(self.right), Int(self.bottom), Int(self.left)]
    }
}
