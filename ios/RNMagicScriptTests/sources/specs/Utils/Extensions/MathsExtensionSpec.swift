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
import Foundation
@testable import RNMagicScriptHostApplication

class MathsExtensionSpec: QuickSpec {
    override func spec() {
        describe("Math functions") {
            context("angle conversions") {
                it("should convert degrees to radians") {
                    expect(Math.deg2rad(Float(-60.0))).to(beCloseTo(-0.3333333 * Float.pi))
                    expect(Math.deg2rad(Float(-30.0))).to(beCloseTo(-0.1666666 * Float.pi))
                    expect(Math.deg2rad(Float(0.0))).to(beCloseTo(0))
                    expect(Math.deg2rad(Float(30.0))).to(beCloseTo(0.16666666 * Float.pi))
                    expect(Math.deg2rad(Float(60.0))).to(beCloseTo(0.33333333 * Float.pi))
                    expect(Math.deg2rad(Float(90.0))).to(beCloseTo(0.5 * Float.pi))
                    expect(Math.deg2rad(Float(120.0))).to(beCloseTo(0.66666666 * Float.pi))
                    expect(Math.deg2rad(Float(150.0))).to(beCloseTo(0.83333333 * Float.pi))
                    expect(Math.deg2rad(Float(180.0))).to(beCloseTo(Float.pi))
                    expect(Math.deg2rad(Float(270.0))).to(beCloseTo(1.5 * Float.pi))
                    expect(Math.deg2rad(Float(360.0))).to(beCloseTo(2 * Float.pi))

                    expect(Float(90).toRadians).to(beCloseTo(0.5 * Float.pi))
                    expect(CGFloat(90).toRadians).to(beCloseTo(0.5 * CGFloat.pi))
                    expect(Double(90).toRadians).to(beCloseTo(0.5 * Double.pi))
                }

                it("should convert radians to degrees") {
                    expect(Math.rad2deg(Float(-0.3333333 * Float.pi))).to(beCloseTo(-60.0))
                    expect(Math.rad2deg(Float(-0.1666666 * Float.pi))).to(beCloseTo(-30.0))
                    expect(Math.rad2deg(Float(0))).to(beCloseTo(0.0))
                    expect(Math.rad2deg(Float(0.16666666 * Float.pi))).to(beCloseTo(30.0))
                    expect(Math.rad2deg(Float(0.33333333 * Float.pi))).to(beCloseTo(60.0))
                    expect(Math.rad2deg(Float(0.5 * Float.pi))).to(beCloseTo(90.0))
                    expect(Math.rad2deg(Float(0.66666666 * Float.pi))).to(beCloseTo(120.0))
                    expect(Math.rad2deg(Float(0.83333333 * Float.pi))).to(beCloseTo(150.0))
                    expect(Math.rad2deg(Float(Float.pi))).to(beCloseTo(180.0))
                    expect(Math.rad2deg(Float(1.5 * Float.pi))).to(beCloseTo(270.0))
                    expect(Math.rad2deg(Float(2 * Float.pi))).to(beCloseTo(360.0))

                    expect(Float.pi.toDegrees).to(beCloseTo(180.0))
                    expect(CGFloat.pi.toDegrees).to(beCloseTo(180.0))
                    expect(Double.pi.toDegrees).to(beCloseTo(180.0))
                }
            }

            context("linear interpolation") {
                it("should return a lerp of two floating point values") {
                    expect(Math.lerp(0.0, 1.0, 0.3)).to(beCloseTo(0.3))
                    expect(Math.lerp(-10.0, 10.0, 0.75)).to(beCloseTo(5))
                    expect(Math.lerp(3.0, 13.0, 0)).to(beCloseTo(3))
                    expect(Math.lerp(-5.0, -15.0, 1)).to(beCloseTo(-15))
                }
            }

            context("clamp") {
                it("should return a value clamped to given range") {
                    expect(Math.clamp(0.0, 0.0, 1.0)).to(beCloseTo(0.0))
                    expect(Math.clamp(0.5, 0.0, 1.0)).to(beCloseTo(0.5))
                    expect(Math.clamp(1.0, 0.0, 1.0)).to(beCloseTo(1.0))
                    expect(Math.clamp(-1.0, 0.0, 1.0)).to(beCloseTo(0.0))
                    expect(Math.clamp(4.4, -2.0, 2.0)).to(beCloseTo(2.0))
                    expect(Math.clamp(-1.4, -2.0, 2.0)).to(beCloseTo(-1.4))

                    expect(Float(-0.3).clamped(0.0, 1.0)).to(beCloseTo(0.0))
                    expect(CGFloat(1.3).clamped(0.6, 0.7)).to(beCloseTo(0.7))
                    expect(Double(13.3).clamped(0.0, 100.0)).to(beCloseTo(13.3))
                    expect(Int(5).clamped(2, 4)).to(equal(4))
                }
            }
        }
    }
}
