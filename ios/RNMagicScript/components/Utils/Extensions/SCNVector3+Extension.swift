/*
 * Copyright (c) 2013-2014 Kim Pedersen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import Foundation
import SceneKit

extension SCNVector3
{
    /**
     * Negates the vector described by SCNVector3 and returns
     * the result as a new SCNVector3.
     */
    func negated() -> SCNVector3 {
        return self * -1
    }

    /**
     * Negates the vector described by SCNVector3
     */
    @discardableResult
    mutating func negate() -> SCNVector3 {
        self = negated()
        return self
    }

    /**
     * Returns the length (magnitude) of the vector described by the SCNVector3
     */
    func length() -> Float {
        return sqrtf(x*x + y*y + z*z)
    }

    func lengthSq() -> Float {
        return x*x + y*y + z*z
    }

    /**
     * Normalizes the vector described by the SCNVector3 to length 1.0 and returns
     * the result as a new SCNVector3.
     */
    func normalized() -> SCNVector3 {
        return self / length()
    }

    /**
     * Normalizes the vector described by the SCNVector3 to length 1.0.
     */
    @discardableResult
    mutating func normalize() -> SCNVector3 {
        self = normalized()
        return self
    }

    /**
     * Calculates the distance between two SCNVector3. Pythagoras!
     */
    func distance(_ vector: SCNVector3) -> Float {
        return (self - vector).length()
    }

    /**
     * Calculates the dot product between two SCNVector3.
     */
    func dot(_ vector: SCNVector3) -> Float {
        return x * vector.x + y * vector.y + z * vector.z
    }

    /**
     * Calculates the cross product between two SCNVector3.
     */
    func cross(_ vector: SCNVector3) -> SCNVector3 {
        return SCNVector3Make(y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x)
    }

    func angleToVector(_ vector: SCNVector3) -> Float {
        //cos(angle) = (A.B)/(|A||B|)
        let cosineAngle = dot(vector) / (length() * vector.length())
        return acos(cosineAngle)
    }

    func lerp(_ vector: SCNVector3, _ s: Float) -> SCNVector3 {
        return self + s * (vector - self)
    }
}

/**
 * Adds two SCNVector3 vectors and returns the result as a new SCNVector3.
 */
func + (left: SCNVector3, right: SCNVector3) -> SCNVector3 {
    return SCNVector3Make(left.x + right.x, left.y + right.y, left.z + right.z)
}

/**
 * Increments a SCNVector3 with the value of another.
 */
func += ( left: inout SCNVector3, right: SCNVector3) {
    left = left + right
}

/**
 * Subtracts two SCNVector3 vectors and returns the result as a new SCNVector3.
 */
func - (left: SCNVector3, right: SCNVector3) -> SCNVector3 {
    return SCNVector3Make(left.x - right.x, left.y - right.y, left.z - right.z)
}

/**
 * Decrements a SCNVector3 with the value of another.
 */
func -= ( left: inout SCNVector3, right: SCNVector3) {
    left = left - right
}

/**
 * Multiplies two SCNVector3 vectors and returns the result as a new SCNVector3.
 */
func * (left: SCNVector3, right: SCNVector3) -> SCNVector3 {
    return SCNVector3Make(left.x * right.x, left.y * right.y, left.z * right.z)
}

/**
 * Multiplies a SCNVector3 with another.
 */
func *= ( left: inout SCNVector3, right: SCNVector3) {
    left = left * right
}

/**
 * Multiplies the x, y and z fields of a SCNVector3 with the same scalar value and
 * returns the result as a new SCNVector3.
 */
func * (vector: SCNVector3, scalar: Float) -> SCNVector3 {
    return SCNVector3Make(vector.x * scalar, vector.y * scalar, vector.z * scalar)
}

func * (scalar: Float, vector: SCNVector3) -> SCNVector3 {
    return SCNVector3Make(vector.x * scalar, vector.y * scalar, vector.z * scalar)
}

func * (vector: SCNVector3, scalar: CGFloat) -> SCNVector3 {
    return vector * Float(scalar)
}

func * (scalar: CGFloat, vector: SCNVector3) -> SCNVector3 {
    return Float(scalar) * vector
}

func * (vector: SCNVector3, scalar: Double) -> SCNVector3 {
    return vector * Float(scalar)
}

func * (scalar: Double, vector: SCNVector3) -> SCNVector3 {
    return Float(scalar) * vector
}

func * (vector: SCNVector3, scalar: Int) -> SCNVector3 {
    return vector * Float(scalar)
}

func * (scalar: Int, vector: SCNVector3) -> SCNVector3 {
    return Float(scalar) * vector
}

/**
 * Multiplies the x and y fields of a SCNVector3 with the same scalar value.
 */
func *= ( vector: inout SCNVector3, scalar: Float) {
    vector = vector * scalar
}

/**
 * Divides two SCNVector3 vectors abd returns the result as a new SCNVector3
 */
func / (left: SCNVector3, right: SCNVector3) -> SCNVector3 {
    return SCNVector3Make(left.x / right.x, left.y / right.y, left.z / right.z)
}

/**
 * Divides a SCNVector3 by another.
 */
func /= ( left: inout SCNVector3, right: SCNVector3) {
    left = left / right
}

/**
 * Divides the x, y and z fields of a SCNVector3 by the same scalar value and
 * returns the result as a new SCNVector3.
 */
func / (vector: SCNVector3, scalar: Float) -> SCNVector3 {
    return SCNVector3Make(vector.x / scalar, vector.y / scalar, vector.z / scalar)
}

/**
 * Divides the scalar value by the x, y and z fields of a SCNVector3 and
 * returns the result as a new SCNVector3.
 */
func / (scalar: Float, vector: SCNVector3) -> SCNVector3 {
    return SCNVector3Make(scalar / vector.x, scalar / vector.y, scalar / vector.z)
}

/**
 * Divides the x, y and z of a SCNVector3 by the same scalar value.
 */
func /= (vector: inout SCNVector3, scalar: Float) {
    vector = vector / scalar
}

extension SCNVector3 {
    init(_ v: SIMD4<Float>) {
        self.init(x: v.x / v.w, y: v.y / v.w, z: v.z / v.w)
    }
}

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

extension SCNVector3 {
    static var zero: SCNVector3 = SCNVector3Zero
    static var right: SCNVector3 = SCNVector3(1, 0, 0)
    static var up: SCNVector3 = SCNVector3(0, 1, 0)
    static var forward: SCNVector3 = SCNVector3(0, 0, 1)
}

public func SCNVector3NOTEqualToVector3(_ a: SCNVector3, _ b: SCNVector3) -> Bool {
    return !SCNVector3EqualToVector3(a, b)
}
