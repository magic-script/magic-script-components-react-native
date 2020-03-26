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
import CoreGraphics

class Math {

    @inline(__always) static public func deg2rad<T: FloatingPoint>(_ angle: T) -> T {
        return angle * (T.pi / T(180))
    }

    @inline(__always) static public func rad2deg<T: FloatingPoint>(_ angle: T) -> T {
        return angle * (T(180) / T.pi)
    }

    @inline(__always) static public func lerp<T>(_ left: T, _ right: T, _ p: T) -> T where T: FloatingPoint{
        return (1 - p) * left + p * right
    }

    @inline(__always) static public func clamp<T>(_ value: T, _ a: T, _ b: T) -> T where T: Comparable {
        return max(a, min(value, b))
    }
}

extension CGFloat {
    public var toRadians: CGFloat { return Math.deg2rad(self) }
    public var toDegrees: CGFloat { return Math.rad2deg(self) }
    public func clamped(_ minimum: CGFloat, _ maximum: CGFloat) -> CGFloat {
        return Math.clamp(self, minimum, maximum)
    }
}

extension Float {
    public var toRadians: Float { return Math.deg2rad(self) }
    public var toDegrees: Float { return Math.rad2deg(self) }
    public func clamped(_ minimum: Float, _ maximum: Float) -> Float {
        return Math.clamp(self, minimum, maximum)
    }
}

extension Double {
    public var toRadians: Double { return Math.deg2rad(self) }
    public var toDegrees: Double { return Math.rad2deg(self) }
    public func clamped(_ minimum: Double, _ maximum: Double) -> Double {
        return Math.clamp(self, minimum, maximum)
    }
}

extension Int {
    public func clamped(_ minimum: Int, _ maximum: Int) -> Int {
        return Math.clamp(self, minimum, maximum)
    }
}
