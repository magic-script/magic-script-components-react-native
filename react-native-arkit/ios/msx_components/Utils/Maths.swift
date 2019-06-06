//
//  Maths.swift
//  Scene3D
//
//  Created by Pawel Leszkiewicz on 13.09.2017.
//  Copyright © 2017 Nomtek. All rights reserved.
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

    @inline(__always) static public func inverseLerp<T>(_ left: T, _ right: T, _ value: T) -> T where T: FloatingPoint {
        return (value - left) / (right - left)
    }

    @inline(__always) static public func clamp<T>(_ value: T, _ a: T, _ b: T) -> T where T: Comparable {
        return max(a, min(value, b))
    }
}

extension CGFloat
{
    public var toRadians: CGFloat { return Math.deg2rad(self) }
    public var toDegrees: CGFloat { return Math.rad2deg(self) }
    public func lerp(_ left: CGFloat, _ right: CGFloat) -> CGFloat {
        return Math.lerp(left, right, self)
    }
    public func inverseLerp(_ left: CGFloat, _ right: CGFloat) -> CGFloat {
        return Math.inverseLerp(left, right, self)
    }
    public func clamped(_ minimum: CGFloat, _ maximum: CGFloat) -> CGFloat {
        return Math.clamp(self, minimum, maximum)
    }
}

extension Float
{
    public var toRadians: Float { return Math.deg2rad(self) }
    public var toDegrees: Float { return Math.rad2deg(self) }
    public func lerp(_ left: Float, _ right: Float) -> Float {
        return Math.lerp(left, right, self)
    }
    public func inverseLerp(_ left: Float, _ right: Float) -> Float {
        return Math.inverseLerp(left, right, self)
    }
    public func clamped(_ minimum: Float, _ maximum: Float) -> Float {
        return Math.clamp(self, minimum, maximum)
    }
}

extension Double
{
    public var toRadians: Double { return Math.deg2rad(self) }
    public var toDegrees: Double { return Math.rad2deg(self) }
    public func lerp(_ left: Double, _ right: Double) -> Double {
        return Math.lerp(left, right, self)
    }
    public func inverseLerp(_ left: Double, _ right: Double) -> Double {
        return Math.inverseLerp(left, right, self)
    }
    public func clamped(_ minimum: Double, _ maximum: Double) -> Double {
        return Math.clamp(self, minimum, maximum)
    }
}

extension Int
{
    public func clamped(_ minimum: Int, _ maximum: Int) -> Int {
        return Math.clamp(self, minimum, maximum)
    }
}
