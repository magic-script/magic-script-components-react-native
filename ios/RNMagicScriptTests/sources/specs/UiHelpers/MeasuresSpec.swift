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

import Quick
import Nimble
@testable import RNMagicScriptHostApplication

class MeasuresSpec: QuickSpec {
    override func spec() {
        describe("MeasuresSpec") {
            let ppi: CGFloat = 0.5 * 326.0
            let inchesInMeters: CGFloat = 39.3700787
            let ppm: CGFloat = ppi * inchesInMeters

            it("should calculate correct pixels from width/height in meters") {
                let result = Measures.pixels(from: 1.5)
                expect(result).to(beCloseTo(ppm*1.5))
            }

            it("should calculate correct pixels from size in meters") {
                let result = Measures.pixels(from: CGSize(width: 1.0, height: 1.0))
                expect(result).to(beCloseTo(CGSize(width: ppm, height: ppm)))
            }

            it("should calculate correct meters from width/height in pixels") {
                let result = Measures.meters(from: 1024.0)
                expect(result).to(beCloseTo(1024.0/ppm))
            }

            it("should calculate correct meters from size in pixels") {
                let result = Measures.meters(from: CGSize(width: 1024.0, height: 1024.0))
                expect(result).to(beCloseTo(CGSize(width: 1024.0/ppm, height: 1024.0/ppm)))
            }
        }
    }
}
