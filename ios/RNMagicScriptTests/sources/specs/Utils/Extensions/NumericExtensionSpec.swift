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

class NumericExtensionSpec: QuickSpec {
    override func spec() {
        describe("Numeric extension") {
            context("uiPrecision") {
                it("should convert numeric value to string with precision") {
                    expect((-1).uiPrecision()).to(equal("-1.0"))
                    expect(0.uiPrecision()).to(equal("0.0"))
                    expect(36.uiPrecision()).to(equal("36.0"))

                    expect(3.14.uiPrecision()).to(equal("3.14"))
                    expect(1.355.uiPrecision()).to(equal("1.36"))
                    expect(2.451.uiPrecision()).to(equal("2.45"))
                    expect(18.997.uiPrecision()).to(equal("19.0"))
                    expect((-33.000013).uiPrecision()).to(equal("-33.0"))
                }

                it("should return infinity") {
                    expect((2.0 / 0.0).uiPrecision()).to(equal("+∞"))
                    expect((-3.0 / 0.0).uiPrecision()).to(equal("-∞"))
                }

                it("should return NaN") {
                    expect((powf(-1, 0.5)).uiPrecision()).to(equal("NaN"))
                }
            }
        }
    }
}
