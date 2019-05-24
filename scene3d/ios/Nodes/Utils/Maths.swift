//
//  Maths.swift
//  Scene3D
//
//  Created by Pawel Leszkiewicz on 13.09.2017.
//  Copyright © 2017 Nomtek. All rights reserved.
//

import Foundation
import CoreGraphics

///
/// Converts degrees into radians.
///
/// - Parameters:
///     - angle:    The angle value in degrees.
///
/// - Returns:  Value of angle in radians.
///
@inline(__always) public func deg2rad<T: FloatingPoint>(_ angle: T) -> T {
    return angle * (T.pi / T(180))
}

///
/// Converts radians into degrees.
///
/// - Parameters:
///     - angle:    The angle value in radians.
///
/// - Returns:  Value of angle in degrees.
///
@inline(__always) public func rad2deg<T: FloatingPoint>(_ angle: T) -> T {
    return angle * (T(180) / T.pi)
}

///
/// Linear interpolation between two values.
///
@inline(__always) public func lerp<T>(_ left: T, _ right: T, _ p: T) -> T where T: FloatingPoint{
    return (1 - p) * left + p * right
}

///
/// Returns a [0, 1] value which says where the value 'value' is situated within the [left, right] range.
///
/// Function returns:
/// - 0: value <= left
/// - 0-1: left < value && value < right
/// - 1: right <= value
///
@inline(__always) public func inverseLerp<T>(_ left: T, _ right: T, _ value: T) -> T where T: FloatingPoint {
    return (value - left) / (right - left)
}

///
/// Constraints a value to an interval [a, b]
///
@inline(__always) public func clamp<T>(_ value: T, _ a: T, _ b: T) -> T where T: Comparable {
    return max(a, min(value, b))
}

extension CGFloat
{
    public var toRadians: CGFloat { return deg2rad(self) }
    public var toDegrees: CGFloat { return rad2deg(self) }
    public func lerp(_ left: CGFloat, _ right: CGFloat) -> CGFloat {
        return SceneKitComponents.lerp(left, right, self)
    }
    public func inverseLerp(_ left: CGFloat, _ right: CGFloat) -> CGFloat {
        return SceneKitComponents.inverseLerp(left, right, self)
    }
    public func clamped(_ minimum: CGFloat, _ maximum: CGFloat) -> CGFloat {
        return clamp(self, minimum, maximum)
    }
}

extension Float
{
    public var toRadians: Float { return deg2rad(self) }
    public var toDegrees: Float { return rad2deg(self) }
    public func lerp(_ left: Float, _ right: Float) -> Float {
        return SceneKitComponents.lerp(left, right, self)
    }
    public func inverseLerp(_ left: Float, _ right: Float) -> Float {
        return SceneKitComponents.inverseLerp(left, right, self)
    }
    public func clamped(_ minimum: Float, _ maximum: Float) -> Float {
        return clamp(self, minimum, maximum)
    }
}

extension Double
{
    public var toRadians: Double { return deg2rad(self) }
    public var toDegrees: Double { return rad2deg(self) }
    public func lerp(_ left: Double, _ right: Double) -> Double {
        return SceneKitComponents.lerp(left, right, self)
    }
    public func inverseLerp(_ left: Double, _ right: Double) -> Double {
        return SceneKitComponents.inverseLerp(left, right, self)
    }
    public func clamped(_ minimum: Double, _ maximum: Double) -> Double {
        return clamp(self, minimum, maximum)
    }
}

extension Int
{
    public func clamped(_ minimum: Int, _ maximum: Int) -> Int {
        return clamp(self, minimum, maximum)
    }
}
