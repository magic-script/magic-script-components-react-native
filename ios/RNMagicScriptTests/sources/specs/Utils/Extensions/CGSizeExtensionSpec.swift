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

class CGSizeExtensionSpec: QuickSpec {
    override func spec() {
        describe("CGSize+Extension") {
            context("addition") {
                it("should add two CGSize objects") {
                    let size1 = CGSize(width: 0.1, height: 0.2)
                    let size2 = CGSize(width: 30.5, height: 1.7)
                    expect(size1 + size2).to(beCloseTo(CGSize(width: size1.width + size2.width, height: size1.height + size2.height)))
                    expect(size2 + size1).to(beCloseTo(size1 + size2))
                    let size3 = CGSize(width: -6.7, height: 9.133)
                    var size4 = size3
                    size4 += size2
                    expect(size4).to(beCloseTo(size3 + size2))
                }
            }

            context("subtraction") {
                it("should subtract two CGSize objects") {
                    let size1 = CGSize(width: 0.4, height: 0.24)
                    let size2 = CGSize(width: -6.77, height: 10.8)
                    expect(size1 - size2).to(beCloseTo(CGSize(width: size1.width - size2.width, height: size1.height - size2.height)))
                    expect(size2 - size1).to(beCloseTo(CGSize.zero - (size1 - size2)))
                    let size3 = CGSize(width: -0.07, height: 3.44)
                    var size4 = size3
                    size4 -= size2
                    expect(size4).to(beCloseTo(size3 - size2))
                }
            }

            context("division") {
                it("should divide two CGSize objects") {
                    let size1 = CGSize(width: 0.4, height: 0.24)
                    let size2 = CGSize(width: 0.5, height: 3.0)
                    expect(size1 / size2).to(beCloseTo(CGSize(width: 0.8, height: 0.08)))
                    let size3 = CGSize(width: 0.125, height: 3.33)
                    var size4 = size3
                    size4 /= size2
                    expect(size4).to(beCloseTo(CGSize(width: 0.25, height: 1.11)))
                }

                it("should divide CGSize object by scalar") {
                    let size1 = CGSize(width: 0.9, height: 2.7)
                    let scalar: CGFloat = 4.5
                    expect(size1 / scalar).to(beCloseTo(CGSize(width: 0.2, height: 0.6)))
                    let size3 = CGSize(width: 0.125, height: 3.33)
                    var size4 = size3
                    size4 /= scalar
                    expect(size4).to(beCloseTo(CGSize(width: 0.027778, height: 0.74)))
                }

                it("should divide scalar by CGSize object") {
                    let scalar: CGFloat = 1.0
                    let size = CGSize(width: 3.0, height: 7.0)
                    let result: CGSize = scalar / size
                    expect(result).to(beCloseTo(CGSize(width: 0.333333, height: 0.14285714)))
                }
            }

            context("multiplication") {
                it("should multiply two CGSize objects") {
                    let size1 = CGSize(width: 0.4, height: 0.24)
                    let size2 = CGSize(width: 0.5, height: 3.0)
                    expect(size1 * size2).to(beCloseTo(CGSize(width: 0.2, height: 0.72)))
                    let size3 = CGSize(width: 0.125, height: 3.33)
                    var size4 = size3
                    size4 *= size2
                    expect(size4).to(beCloseTo(CGSize(width: 0.0625, height: 9.99)))
                }

                it("should multiply CGSize object by scalar") {
                    let size1 = CGSize(width: 0.9, height: 2.7)
                    let scalar: CGFloat = 0.5
                    expect(size1 * scalar).to(beCloseTo(CGSize(width: 0.45, height: 1.35)))
                    expect(scalar * size1).to(beCloseTo(CGSize(width: 0.45, height: 1.35)))
                    let size3 = CGSize(width: 0.5, height: 3.33)
                    var size4 = size3
                    size4 *= scalar
                    expect(size4).to(beCloseTo(CGSize(width: 0.25, height: 1.665)))
                }
            }
        }
    }
}
