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
@testable import RNMagicScriptHostApplication

class AlignmentSpec: QuickSpec {
    override func spec() {
        describe("Alignment") {
            let objectByRawValue: [String: Alignment] = [
                "top-left" : Alignment.topLeft,
                "top-center" : Alignment.topCenter,
                "top-right" : Alignment.topRight,
                "center-left" : Alignment.centerLeft,
                "center-center" : Alignment.centerCenter,
                "center-right" : Alignment.centerRight,
                "bottom-left" : Alignment.bottomLeft,
                "bottom-center" : Alignment.bottomCenter,
                "bottom-right" : Alignment.bottomRight
            ]

            context("raw value") {
                it("should create proper object from raw value") {
                    objectByRawValue.forEach({ (arg0) in
                        let (key, value) = arg0
                        expect(Alignment(rawValue: key)).to(equal(value))
                    })
                }

                it("should return proper raw value") {
                    objectByRawValue.forEach({ (arg0) in
                        let (key, value) = arg0
                        expect(value.rawValue).to(equal(key))
                    })
                }

                it("should return nil for unknown alignment") {
                    expect(Alignment(rawValue: "left-top")).to(beNil())
                    expect(Alignment(rawValue: "left-center")).to(beNil())
                    expect(Alignment(rawValue: "left-bottom")).to(beNil())
                    expect(Alignment(rawValue: "center-top")).to(beNil())
                    expect(Alignment(rawValue: "center-bottom")).to(beNil())
                    expect(Alignment(rawValue: "right-top")).to(beNil())
                    expect(Alignment(rawValue: "right-center")).to(beNil())
                    expect(Alignment(rawValue: "right-bottom")).to(beNil())
                }
            }

            context("shiftDirection") {
                it("should return proper direction") {
                    expect(Alignment.topLeft.shiftDirection).to(beCloseTo(CGPoint(x: 0.5, y: -0.5)))
                    expect(Alignment.topCenter.shiftDirection).to(beCloseTo(CGPoint(x: 0, y: -0.5)))
                    expect(Alignment.topRight.shiftDirection).to(beCloseTo(CGPoint(x: -0.5, y: -0.5)))
                    expect(Alignment.centerLeft.shiftDirection).to(beCloseTo(CGPoint(x: 0.5, y: 0)))
                    expect(Alignment.centerCenter.shiftDirection).to(beCloseTo(CGPoint(x: 0, y: 0)))
                    expect(Alignment.centerRight.shiftDirection).to(beCloseTo(CGPoint(x: -0.5, y: 0)))
                    expect(Alignment.bottomLeft.shiftDirection).to(beCloseTo(CGPoint(x: 0.5, y: 0.5)))
                    expect(Alignment.bottomCenter.shiftDirection).to(beCloseTo(CGPoint(x: 0, y: 0.5)))
                    expect(Alignment.bottomRight.shiftDirection).to(beCloseTo(CGPoint(x: -0.5, y: 0.5)))
                }
            }

            context("boundsOffset") {
                it("should return proper offset") {
                    expect(Alignment.topLeft.boundsOffset).to(beCloseTo(CGPoint(x: 0, y: -1)))
                    expect(Alignment.topCenter.boundsOffset).to(beCloseTo(CGPoint(x: -0.5, y: -1)))
                    expect(Alignment.topRight.boundsOffset).to(beCloseTo(CGPoint(x: -1, y: -1)))
                    expect(Alignment.centerLeft.boundsOffset).to(beCloseTo(CGPoint(x: 0, y: -0.5)))
                    expect(Alignment.centerCenter.boundsOffset).to(beCloseTo(CGPoint(x: -0.5, y: -0.5)))
                    expect(Alignment.centerRight.boundsOffset).to(beCloseTo(CGPoint(x: -1, y: -0.5)))
                    expect(Alignment.bottomLeft.boundsOffset).to(beCloseTo(CGPoint(x: 0, y: 0)))
                    expect(Alignment.bottomCenter.boundsOffset).to(beCloseTo(CGPoint(x: -0.5, y: 0)))
                    expect(Alignment.bottomRight.boundsOffset).to(beCloseTo(CGPoint(x: -1, y: 0)))
                }
            }
        }
    }
}
