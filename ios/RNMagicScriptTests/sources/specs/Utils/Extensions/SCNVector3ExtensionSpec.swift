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

class SCNVector3ExtensionSpec: QuickSpec {
    override func spec() {
        describe("SCNVector3+Extension") {
            context("class functions") {
                it("should return negated vector") {
                    let referenceVector = SCNVector3(0.1, 0.2, 0.3)
                    let referenceNegatedVector = SCNVector3(-0.1, -0.2, -0.3)
                    expect(referenceVector.negated()).to(beCloseTo(referenceNegatedVector))

                    var vector = referenceVector
                    vector.negate()
                    expect(vector).to(beCloseTo(referenceNegatedVector))
                }

                it("should return length of a vector") {
                    let referenceVector = SCNVector3(0.1, 0.2, 0.3)
                    let referenceNegatedVector = SCNVector3(-0.1, -0.2, -0.3)
                    expect(referenceVector.length()).to(beCloseTo(0.3741657387))
                    expect(referenceNegatedVector.length()).to(beCloseTo(0.3741657387))
                }

                it("should return square length of a vector") {
                    let referenceVector = SCNVector3(0.3, 0.2, 0.1)
                    let referenceNegatedVector = SCNVector3(-0.3, -0.2, -0.1)
                    expect(referenceVector.lengthSq()).to(beCloseTo(0.14))
                    expect(referenceNegatedVector.lengthSq()).to(beCloseTo(0.14))
                }

                it("should return normalized vector") {
                    let referenceVector = SCNVector3(0.1, 0.2, 0.3)
                    expect(referenceVector.normalized()).to(beCloseTo(SCNVector3(0.2672612419, 0.5345224838, 0.8017837257)))

                    let right = SCNVector3(1, 0, 0)
                    expect(right.normalized()).to(beCloseTo(right))

                    var up = SCNVector3(0, 33, 0)
                    up.normalize()
                    expect(up).to(beCloseTo(SCNVector3(0, 1, 0)))
                }

                it("should return distance between two vectors") {
                    let referenceVector1 = SCNVector3Zero
                    let referenceVector2 = SCNVector3(0.1, 0.2, 0.3)
                    expect(referenceVector2.distance(referenceVector1)).to(beCloseTo(referenceVector2.length()))

                    let referenceVector3 = SCNVector3(2.3, 4.5, -8)
                    let distanceBetweenVector2_3 = 9.6031244915
                    expect(referenceVector2.distance(referenceVector3)).to(beCloseTo(distanceBetweenVector2_3))
                }

                it("should return dot product of two vectors") {
                    let referenceVector1 = SCNVector3Zero
                    let referenceVector2 = SCNVector3(0.1, 0.2, 0.3)
                    expect(referenceVector2.dot(referenceVector1)).to(beCloseTo(0))

                    let referenceVector3 = SCNVector3(2.3, 4.5, -8)
                    let dotProductOfVector2_3 = 0.23 + 0.9 - 2.4
                    expect(referenceVector2.dot(referenceVector3)).to(beCloseTo(dotProductOfVector2_3))
                }

                it("should return cross product of two vectors") {
                    let referenceVector1 = SCNVector3Zero
                    let referenceVector2 = SCNVector3(0.1, 0.2, 0.3)
                    expect(referenceVector2.cross(referenceVector1)).to(beCloseTo(SCNVector3Zero))

                    let referenceVector3 = SCNVector3(2.3, 4.5, -8)
                    let crossProductOfVector2_3 = SCNVector3(-2.95, 1.49, -0.01)
                    expect(referenceVector2.cross(referenceVector3)).to(beCloseTo(crossProductOfVector2_3))

                    let referenceVector4 = SCNVector3(1, 0, 0)
                    let referenceVector5 = SCNVector3(0, 1, 0)
                    let crossProductOfVector4_5 = SCNVector3(0, 0, 1)
                    expect(referenceVector4.cross(referenceVector5)).to(beCloseTo(crossProductOfVector4_5))
                }

                it("should return angle between two vectors") {
                    let referenceVector1 = SCNVector3(1, 0, 0)
                    let referenceVector2 = SCNVector3(0, 1, 0)
                    let angleBetweenVector1_2 = 0.5 * Float.pi
                    expect(referenceVector1.angleToVector(referenceVector2)).to(beCloseTo(angleBetweenVector1_2))

                    let referenceVector3 = SCNVector3(1, 1, 0).normalized()
                    let angleBetweenVector2_3 = 0.25 * Float.pi
                    expect(referenceVector2.angleToVector(referenceVector3)).to(beCloseTo(angleBetweenVector2_3))
                }
            }

            context("global operators") {
                it("should add two vectors") {
                    let v1 = SCNVector3(0.1, 0.2, 0.3)
                    let v2 = SCNVector3(0.4, 0.5, 0.6)
                    expect(v1 + v2).to(beCloseTo(SCNVector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z)))

                    var v3 = v1
                    v3 += v2
                    expect(v3).to(beCloseTo(SCNVector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z)))
                }

                it("should subtract two vectors") {
                    let v1 = SCNVector3(0.1, 0.2, 0.3)
                    let v2 = SCNVector3(0.4, 0.5, 0.6)
                    expect(v1 - v2).to(beCloseTo(SCNVector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z)))

                    var v3 = v1
                    v3 -= v2
                    expect(v3).to(beCloseTo(SCNVector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z)))
                }

                it("should multiply two vectors") {
                    let v1 = SCNVector3(0.1, 0.2, 0.3)
                    let v2 = SCNVector3(0.4, 0.5, 0.6)
                    expect(v1 * v2).to(beCloseTo(SCNVector3(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z)))

                    var v3 = v1
                    v3 *= v2
                    expect(v3).to(beCloseTo(SCNVector3(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z)))
                }

                it("should multiply vector by scalar") {
                    let vector = SCNVector3(0.1, 0.2, 0.3)
                    let scalarFloat: Float = 3.5
                    expect(vector * scalarFloat).to(beCloseTo(SCNVector3(vector.x * scalarFloat, vector.y * scalarFloat, vector.z * scalarFloat)))
                    expect(scalarFloat * vector).to(beCloseTo(SCNVector3(scalarFloat * vector.x, scalarFloat * vector.y, scalarFloat * vector.z)))

                    let scalarCGFloat: CGFloat = 4.6
                    expect(vector * scalarCGFloat).to(beCloseTo(SCNVector3(vector.x * Float(scalarCGFloat), vector.y * Float(scalarCGFloat), vector.z * Float(scalarCGFloat))))
                    expect(scalarCGFloat * vector).to(beCloseTo(SCNVector3(Float(scalarCGFloat) * vector.x, Float(scalarCGFloat) * vector.y, Float(scalarCGFloat) * vector.z)))

                    let scalarInt: Int = 7
                    expect(vector * scalarInt).to(beCloseTo(SCNVector3(vector.x * Float(scalarInt), vector.y * Float(scalarInt), vector.z * Float(scalarInt))))
                    expect(scalarInt * vector).to(beCloseTo(SCNVector3(Float(scalarInt) * vector.x, Float(scalarInt) * vector.y, Float(scalarInt) * vector.z)))

                    var vector2 = vector
                    vector2 *= scalarFloat
                    expect(vector2).to(beCloseTo(SCNVector3(vector.x * scalarFloat, vector.y * scalarFloat, vector.z * scalarFloat)))
                }

                it("should divide two vectors") {
                    let v1 = SCNVector3(0.1, 0.2, 0.3)
                    let v2 = SCNVector3(0.4, 0.5, 0.6)
                    expect(v1 / v2).to(beCloseTo(SCNVector3(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z)))

                    var v3 = v1
                    v3 /= v2
                    expect(v3).to(beCloseTo(SCNVector3(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z)))
                }

                it("should divide vector by scalar") {
                    let vector = SCNVector3(0.1, 0.2, 0.3)
                    let scalar: Float = 7.5
                    expect(vector / scalar).to(beCloseTo(SCNVector3(vector.x / scalar, vector.y / scalar, vector.z / scalar)))

                    var vector2 = vector
                    vector2 /= scalar
                    expect(vector2).to(beCloseTo(SCNVector3(vector.x / scalar, vector.y / scalar, vector.z / scalar)))
                }
            }
            
            context("init with SIMD4<Float>") {
                it("should init vector with input of type SIMD4<Float>") {
                    let referenceSIMD4 = SIMD4<Float>(2.0, 0.7, 0.46, 0.2)
                    let result = SCNVector3(referenceSIMD4)
                    expect(result.x).to(beCloseTo(referenceSIMD4.x / referenceSIMD4.w))
                    expect(result.y).to(beCloseTo(referenceSIMD4.y / referenceSIMD4.w))
                    expect(result.z).to(beCloseTo(referenceSIMD4.z / referenceSIMD4.w))
                }
            }
            
            context("array of floats") {
                it("should return vector's coordintes as an array of Float") {
                    let referenceVector = SCNVector3(29.722, -88.51, 7.004)
                    let referenceArray: [Float] = [referenceVector.x, referenceVector.y, referenceVector.z]
                    let array = referenceVector.toArrayOfFloat
                    expect(array.count).to(equal(referenceArray.count))
                    for i in 0..<array.count {
                        expect(array[i]).to(beCloseTo(referenceArray[i]))
                    }
                }
                
                it("should return vector's coordintes as an array of CGFloat") {
                    let referenceVector = SCNVector3(71.332, 14.11, -3.119)
                    let referenceArray: [CGFloat] = [CGFloat(referenceVector.x), CGFloat(referenceVector.y), CGFloat(referenceVector.z)]
                    let array = referenceVector.toArrayOfCGFloat
                    expect(array.count).to(equal(referenceArray.count))
                    for i in 0..<array.count {
                        expect(array[i]).to(beCloseTo(referenceArray[i]))
                    }
                }
                
                it("should return vector's coordintes as an array of Double") {
                    let referenceVector = SCNVector3(-0.09, 8.0, 9.9)
                    let referenceArray: [Double] = [Double(referenceVector.x), Double(referenceVector.y), Double(referenceVector.z)]
                    let array = referenceVector.toArrayOfDouble
                    expect(array.count).to(equal(referenceArray.count))
                    for i in 0..<array.count {
                        expect(array[i]).to(beCloseTo(referenceArray[i]))
                    }
                }
                
                it("should return vector's coordintes as an array of Int") {
                    let referenceVector = SCNVector3(-6.4, 2.72, 3.1415)
                    let referenceArray: [Int] = [Int(referenceVector.x), Int(referenceVector.y), Int(referenceVector.z)]
                    let array = referenceVector.toArrayOfInt
                    expect(array.count).to(equal(referenceArray.count))
                    for i in 0..<array.count {
                        expect(array[i]).to(equal(referenceArray[i]))
                    }
                }
            }
            
            context("static constants") {
                it("should return zero vector") {
                    expect(SCNVector3.zero).to(beCloseTo(SCNVector3(0, 0, 0)))
                }
                
                it("should return directional vectors") {
                    expect(SCNVector3.right).to(beCloseTo(SCNVector3(1, 0, 0)))
                    expect(SCNVector3.up).to(beCloseTo(SCNVector3(0, 1, 0)))
                    expect(SCNVector3.forward).to(beCloseTo(SCNVector3(0, 0, 1)))
                }
            }
        }
    }
}
