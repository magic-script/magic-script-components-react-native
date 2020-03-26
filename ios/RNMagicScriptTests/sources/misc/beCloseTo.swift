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

import Nimble
import SceneKit
@testable import RNMagicScriptHostApplication

func beCloseTo(_ expected: SCNVector3, epsilon: Float = Float(defaultEpsilon)) -> Predicate<SCNVector3> {
    return Predicate { (actualExpression) throws in
        guard let actual = try actualExpression.evaluate() else {
            return PredicateResult(bool: false, message: ExpectationMessage.fail("The provided value is nil."))
        }

        let dx: Float = abs(actual.x - expected.x)
        let dy: Float = abs(actual.y - expected.y)
        let dz: Float = abs(actual.z - expected.z)
        let areCloseTo = (dx < epsilon) && (dy < epsilon) && (dz < epsilon)
        return PredicateResult(bool: areCloseTo, message: ExpectationMessage.fail("The value \(actual) is significantly different than the value \(expected)."))
    }
}

func beCloseTo(_ expected: SCNVector4, epsilon: Float = Float(defaultEpsilon)) -> Predicate<SCNVector4> {
    return Predicate { (actualExpression) throws in
        guard let actual = try actualExpression.evaluate() else {
            return PredicateResult(bool: false, message: ExpectationMessage.fail("The provided value is nil."))
        }

        let dx: Float = abs(actual.x - expected.x)
        let dy: Float = abs(actual.y - expected.y)
        let dz: Float = abs(actual.z - expected.z)
        let dw: Float = abs(actual.w - expected.w)
        let areCloseTo = (dx < epsilon) && (dy < epsilon) && (dz < epsilon) && (dw < epsilon)
        return PredicateResult(bool: areCloseTo, message: ExpectationMessage.fail("The value \(actual) is significantly different than the value \(expected)."))
    }
}

func beCloseTo(_ expected: SCNMatrix4, epsilon: Float = Float(defaultEpsilon)) -> Predicate<SCNMatrix4> {
    return Predicate { (actualExpression) throws in
        guard let actual = try actualExpression.evaluate() else {
            return PredicateResult(bool: false, message: ExpectationMessage.fail("The provided value is nil."))
        }

        let dm11: Float = abs(actual.m11 - expected.m11)
        let dm12: Float = abs(actual.m12 - expected.m12)
        let dm13: Float = abs(actual.m13 - expected.m13)
        let dm14: Float = abs(actual.m14 - expected.m14)
        let dm21: Float = abs(actual.m21 - expected.m21)
        let dm22: Float = abs(actual.m22 - expected.m22)
        let dm23: Float = abs(actual.m23 - expected.m23)
        let dm24: Float = abs(actual.m24 - expected.m24)
        let dm31: Float = abs(actual.m31 - expected.m31)
        let dm32: Float = abs(actual.m32 - expected.m32)
        let dm33: Float = abs(actual.m33 - expected.m33)
        let dm34: Float = abs(actual.m34 - expected.m34)
        let dm41: Float = abs(actual.m41 - expected.m41)
        let dm42: Float = abs(actual.m42 - expected.m42)
        let dm43: Float = abs(actual.m43 - expected.m43)
        let dm44: Float = abs(actual.m44 - expected.m44)

        let areCloseTo =
            (dm11 < epsilon) && (dm12 < epsilon) && (dm13 < epsilon) && (dm14 < epsilon) &&
                (dm21 < epsilon) && (dm22 < epsilon) && (dm23 < epsilon) && (dm24 < epsilon) &&
                (dm31 < epsilon) && (dm32 < epsilon) && (dm33 < epsilon) && (dm34 < epsilon) &&
                (dm41 < epsilon) && (dm42 < epsilon) && (dm43 < epsilon) && (dm44 < epsilon)
        return PredicateResult(bool: areCloseTo, message: ExpectationMessage.fail("The value \(actual) is significantly different than the value \(expected)."))
    }
}

func beCloseTo(_ expected: UIColor, epsilon: CGFloat = CGFloat(defaultEpsilon)) -> Predicate<UIColor> {
    return Predicate { (actualExpression) throws in
        guard let actual = try actualExpression.evaluate() else {
            return PredicateResult(bool: false, message: ExpectationMessage.fail("The provided value is nil."))
        }

        var r1: CGFloat = 0
        var g1: CGFloat = 0
        var b1: CGFloat = 0
        var a1: CGFloat = 0
        actual.getRed(&r1, green: &g1, blue: &b1, alpha: &a1)

        var r2: CGFloat = 0
        var g2: CGFloat = 0
        var b2: CGFloat = 0
        var a2: CGFloat = 0
        expected.getRed(&r2, green: &g2, blue: &b2, alpha: &a2)

        let dr: CGFloat = abs(r2 - r1)
        let dg: CGFloat = abs(g2 - g1)
        let db: CGFloat = abs(b2 - b1)
        let da: CGFloat = abs(a2 - a1)
        let areCloseTo = (dr < epsilon) && (dg < epsilon) && (db < epsilon) && (da < epsilon)
        return PredicateResult(bool: areCloseTo, message: ExpectationMessage.fail("The value \(actual) is significantly different than the value \(expected)."))
    }
}

func beCloseTo(_ expected: CGPoint, epsilon: Float = Float(defaultEpsilon)) -> Predicate<CGPoint> {
    return Predicate { (actualExpression) throws in
        guard let actual = try actualExpression.evaluate() else {
            return PredicateResult(bool: false, message: ExpectationMessage.fail("The provided value is nil."))
        }

        let dWidth: Float = Float(abs(actual.x - expected.x))
        let dHeight: Float = Float(abs(actual.y - expected.y))
        let areCloseTo = (dWidth < epsilon) && (dHeight < epsilon)
        return PredicateResult(bool: areCloseTo, message: ExpectationMessage.fail("The value \(actual) is significantly different than the value \(expected)."))
    }
}

func beCloseTo(_ expected: CGSize, epsilon: Float = Float(defaultEpsilon)) -> Predicate<CGSize> {
    return Predicate { (actualExpression) throws in
        guard let actual = try actualExpression.evaluate() else {
            return PredicateResult(bool: false, message: ExpectationMessage.fail("The provided value is nil."))
        }

        let dWidth: Float = Float(abs(actual.width - expected.width))
        let dHeight: Float = Float(abs(actual.height - expected.height))
        let areCloseTo = (dWidth < epsilon) && (dHeight < epsilon)
        return PredicateResult(bool: areCloseTo, message: ExpectationMessage.fail("The value \(actual) is significantly different than the value \(expected)."))
    }
}

func beCloseTo(_ expected: CGRect, epsilon: Float = Float(defaultEpsilon)) -> Predicate<CGRect> {
    return Predicate { (actualExpression) throws in
        guard let actual = try actualExpression.evaluate() else {
            return PredicateResult(bool: false, message: ExpectationMessage.fail("The provided value is nil."))
        }

        let dWidth: Float = Float(abs(actual.size.width - expected.size.width))
        let dHeight: Float = Float(abs(actual.size.height - expected.size.height))
        let dx: Float = Float(abs(actual.origin.x - expected.origin.x))
        let dy: Float = Float(abs(actual.origin.y - expected.origin.y))
        let areCloseTo = (dWidth < epsilon) && (dHeight < epsilon) && (dx < epsilon) && (dy < epsilon)
        return PredicateResult(bool: areCloseTo, message: ExpectationMessage.fail("The value \(actual) is significantly different than the value \(expected)."))
    }
}

func beCloseTo(_ expected: UIEdgeInsets, epsilon: Float = Float(defaultEpsilon)) -> Predicate<UIEdgeInsets> {
    return Predicate { (actualExpression) throws in
        guard let actual = try actualExpression.evaluate() else {
            return PredicateResult(bool: false, message: ExpectationMessage.fail("The provided value is nil."))
        }

        let dTop: Float = Float(abs(actual.top - expected.top))
        let dBottom: Float = Float(abs(actual.bottom - expected.bottom))
        let dLeft: Float = Float(abs(actual.left - expected.left))
        let dRight: Float = Float(abs(actual.right - expected.right))
        let areCloseTo = (dTop < epsilon) && (dBottom < epsilon) && (dLeft < epsilon) && (dRight < epsilon)
        return PredicateResult(bool: areCloseTo, message: ExpectationMessage.fail("The value \(actual) is significantly different than the value \(expected)."))
    }
}
