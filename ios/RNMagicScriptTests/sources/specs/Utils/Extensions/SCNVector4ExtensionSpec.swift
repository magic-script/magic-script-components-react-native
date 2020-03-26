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
import SceneKit
@testable import RNMagicScriptHostApplication

class SCNVector4ExtensionSpec: QuickSpec {
    override func spec() {
        describe("SCNVector4+Extension") {
            context("init") {                
                it("should init vector with input of type SCNVector3") {
                    let referenceVector3 = SCNVector3(3.1418, 6.73, -56.48)
                    let result = SCNVector4(referenceVector3)
                    expect(result.x).to(beCloseTo(referenceVector3.x))
                    expect(result.y).to(beCloseTo(referenceVector3.y))
                    expect(result.z).to(beCloseTo(referenceVector3.z))
                    expect(result.w).to(beCloseTo(1.0))
                }
            }
            
            context("array of floats") {
                it("should return vector's coordintes as an array of Float") {
                    let referenceVector = SCNVector4(29.722, -88.51, 7.004, 5.6)
                    let referenceArray: [Float] = [referenceVector.x, referenceVector.y, referenceVector.z, referenceVector.w]
                    let array = referenceVector.toArrayOfFloat
                    expect(array.count).to(equal(referenceArray.count))
                    for i in 0..<array.count {
                        expect(array[i]).to(beCloseTo(referenceArray[i]))
                    }
                }
                
                it("should return vector's coordintes as an array of CGFloat") {
                    let referenceVector = SCNVector4(71.332, 14.11, -3.119, -3.4)
                    let referenceArray: [CGFloat] = [CGFloat(referenceVector.x), CGFloat(referenceVector.y), CGFloat(referenceVector.z), CGFloat(referenceVector.w)]
                    let array = referenceVector.toArrayOfCGFloat
                    expect(array.count).to(equal(referenceArray.count))
                    for i in 0..<array.count {
                        expect(array[i]).to(beCloseTo(referenceArray[i]))
                    }
                }
                
                it("should return vector's coordintes as an array of Double") {
                    let referenceVector = SCNVector4(-0.09, 8.0, 9.9, -0.8)
                    let referenceArray: [Double] = [Double(referenceVector.x), Double(referenceVector.y), Double(referenceVector.z), Double(referenceVector.w)]
                    let array = referenceVector.toArrayOfDouble
                    expect(array.count).to(equal(referenceArray.count))
                    for i in 0..<array.count {
                        expect(array[i]).to(beCloseTo(referenceArray[i]))
                    }
                }
                
                it("should return vector's coordintes as an array of Int") {
                    let referenceVector = SCNVector4(-6.4, 2.72, 3.1415, 4.54)
                    let referenceArray: [Int] = [Int(referenceVector.x), Int(referenceVector.y), Int(referenceVector.z), Int(referenceVector.w)]
                    let array = referenceVector.toArrayOfInt
                    expect(array.count).to(equal(referenceArray.count))
                    for i in 0..<array.count {
                        expect(array[i]).to(equal(referenceArray[i]))
                    }
                }
            }
            
            context("static constants") {
                it("should return zero vector") {
                    expect(SCNVector4.zero).to(beCloseTo(SCNVector4(0, 0, 0, 0)))
                }
            }
        }
    }
}
