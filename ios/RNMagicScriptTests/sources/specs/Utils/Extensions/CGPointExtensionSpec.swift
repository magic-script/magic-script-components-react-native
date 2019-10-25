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

import Quick
import Nimble
import CoreGraphics
@testable import RNMagicScriptHostApplication

class CGPointExtensionSpec: QuickSpec {
    override func spec() {
        describe("CGPoint+Extension") {
            context("addition") {
                it("should add two CGPoint objects") {
                    let point1 = CGPoint(x: 0.1, y: 0.2)
                    let point2 = CGPoint(x: 30.5, y: 1.7)
                    expect(point1 + point2).to(beCloseTo(CGPoint(x: point1.x + point2.x, y: point1.y + point2.y)))
                    expect(point2 + point1).to(beCloseTo(point1 + point2))
                    let point3 = CGPoint(x: -6.7, y: 9.133)
                    var point4 = point3
                    point4 += point2
                    expect(point4).to(beCloseTo(point3 + point2))
                }
            }

            context("subtraction") {
                it("should subtract two CGPoint objects") {
                    let point1 = CGPoint(x: 0.4, y: 0.24)
                    let point2 = CGPoint(x: -6.77, y: 10.8)
                    expect(point1 - point2).to(beCloseTo(CGPoint(x: point1.x - point2.x, y: point1.y - point2.y)))
                    expect(point2 - point1).to(beCloseTo(CGPoint.zero - (point1 - point2)))
                    let point3 = CGPoint(x: -0.07, y: 3.44)
                    var point4 = point3
                    point4 -= point2
                    expect(point4).to(beCloseTo(point3 - point2))
                }
            }

            context("division") {
                it("should divide two CGPoint objects") {
                    let point1 = CGPoint(x: 0.4, y: 0.24)
                    let point2 = CGPoint(x: 0.5, y: 3.0)
                    expect(point1 / point2).to(beCloseTo(CGPoint(x: 0.8, y: 0.08)))
                    let point3 = CGPoint(x: 0.125, y: 3.33)
                    var point4 = point3
                    point4 /= point2
                    expect(point4).to(beCloseTo(CGPoint(x: 0.25, y: 1.11)))
                }

                it("should divide CGPoint object by scalar") {
                    let point1 = CGPoint(x: 0.9, y: 2.7)
                    let scalar: CGFloat = 4.5
                    expect(point1 / scalar).to(beCloseTo(CGPoint(x: 0.2, y: 0.6)))
                    let point3 = CGPoint(x: 0.125, y: 3.33)
                    var point4 = point3
                    point4 /= scalar
                    expect(point4).to(beCloseTo(CGPoint(x: 0.027778, y: 0.74)))
                }

                it("should divide scalar by CGPoint object") {
                    let scalar: CGFloat = 1.0
                    let point = CGPoint(x: 3.0, y: 7.0)
                    let result: CGPoint = scalar / point
                    expect(result).to(beCloseTo(CGPoint(x: 0.333333, y: 0.14285714)))
                }
            }

            context("multiplication") {
                it("should multiply two CGPoint objects") {
                    let point1 = CGPoint(x: 0.4, y: 0.24)
                    let point2 = CGPoint(x: 0.5, y: 3.0)
                    expect(point1 * point2).to(beCloseTo(CGPoint(x: 0.2, y: 0.72)))
                    let point3 = CGPoint(x: 0.125, y: 3.33)
                    var point4 = point3
                    point4 *= point2
                    expect(point4).to(beCloseTo(CGPoint(x: 0.0625, y: 9.99)))
                }

                it("should multiply CGPoint object by scalar") {
                    let point1 = CGPoint(x: 0.9, y: 2.7)
                    let scalar: CGFloat = 0.5
                    expect(point1 * scalar).to(beCloseTo(CGPoint(x: 0.45, y: 1.35)))
                    expect(scalar * point1).to(beCloseTo(CGPoint(x: 0.45, y: 1.35)))
                    let point3 = CGPoint(x: 0.5, y: 3.33)
                    var point4 = point3
                    point4 *= scalar
                    expect(point4).to(beCloseTo(CGPoint(x: 0.25, y: 1.665)))
                }
            }
        }
    }
}

