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
import SceneKit
@testable import RNMagicScriptHostApplication

class ConvertSpec: QuickSpec {
    override func spec() {
        let any_bool: Bool = false
        let any_int: Int = 2
        let any_cgFloat: CGFloat = 3.1415
        let any_float: Float = Float.pi
        let any_double: Double = Double.pi
        let any_arrayOfInts: [Int] = [2, 5]
        let any_vec3: SCNVector3 = SCNVector3(1, 2, 3)

        describe("Convert") {
            context("toString") {
                it("should convert string value") {
                    let value: String = "some text"
                    expect(Convert.toString(value)).to(equal(value))
                    expect(Convert.toString(nil)).to(beNil())
                    expect(Convert.toString(any_bool)).to(beNil())
                    expect(Convert.toString(any_int)).to(beNil())
                    expect(Convert.toString(any_cgFloat)).to(beNil())
                    expect(Convert.toString(any_float)).to(beNil())
                    expect(Convert.toString(any_double)).to(beNil())
                    expect(Convert.toString(any_arrayOfInts)).to(beNil())
                }
            }

            context("toBool") {
                it("should convert bool value") {
                    let value: Bool = true
                    expect(Convert.toBool(value)).to(equal(value))
                    expect(Convert.toBool(nil)).to(beNil())
                    expect(Convert.toBool(any_bool)).notTo(beNil())
                    expect(Convert.toBool(any_int)).to(beNil())
                    expect(Convert.toBool(any_cgFloat)).to(beNil())
                    expect(Convert.toBool(any_float)).to(beNil())
                    expect(Convert.toBool(any_double)).to(beNil())
                    expect(Convert.toBool(any_vec3)).to(beNil())
                    expect(Convert.toBool(any_arrayOfInts)).to(beNil())
                }
            }

            context("toInt") {
                it("should convert int value") {
                    let value: Int = 123
                    expect(Convert.toInt(value)).to(equal(value))
                    expect(Convert.toInt(nil)).to(beNil())
                    expect(Convert.toInt(any_bool)).to(beNil())
                    expect(Convert.toInt(any_int)).notTo(beNil())
                    expect(Convert.toInt(any_cgFloat)).to(beNil())
                    expect(Convert.toInt(any_float)).to(beNil())
                    expect(Convert.toInt(any_double)).to(beNil())
                    expect(Convert.toInt(any_vec3)).to(beNil())
                    expect(Convert.toInt(any_arrayOfInts)).to(beNil())
                }
            }

            context("toCGFloat") {
                it("should convert CGFloat value") {
                    let value: CGFloat = 2.17
                    expect(Convert.toCGFloat(value)).to(beCloseTo(value))
                    expect(Convert.toCGFloat(nil)).to(beNil())
                    expect(Convert.toCGFloat(any_bool)).to(beNil())
                    expect(Convert.toCGFloat(any_int)).notTo(beNil())
                    expect(Convert.toCGFloat(any_cgFloat)).notTo(beNil())
                    expect(Convert.toCGFloat(any_float)).notTo(beNil())
                    expect(Convert.toCGFloat(any_double)).notTo(beNil())
                    expect(Convert.toCGFloat(any_vec3)).to(beNil())
                    expect(Convert.toCGFloat(any_arrayOfInts)).to(beNil())
                }
            }

            context("toCGSize") {
                it("should convert Size value") {
                    let cgFloats: [CGFloat] = [3.88, -0.91]
                    expect(Convert.toCGSize(cgFloats)).to(beCloseTo(CGSize(width: cgFloats[0], height: cgFloats[1])))
                    let floats: [Float] = [54.0, 34.8]
                    expect(Convert.toCGSize(floats)).to(beCloseTo(CGSize(width: CGFloat(floats[0]), height: CGFloat(floats[1]))))
                    let doubles: [Double] = [-11.7, 21.4]
                    expect(Convert.toCGSize(doubles)).to(beCloseTo(CGSize(width: doubles[0], height: doubles[1])))
                    let ints: [Int] = [2, 4]
                    expect(Convert.toCGSize(ints)).to(beCloseTo(CGSize(width: ints[0], height: ints[1])))

                    expect(Convert.toCGSize(CGSize(width: 0.1, height: 0.2))).to(beNil())
                    let intArray: [Int] = [1, 2, 3]
                    expect(Convert.toCGSize(intArray)).to(beNil())
                    let doubleArray: [Double] = [3.7,-2.5,3.1,1.0]
                    expect(Convert.toCGSize(doubleArray)).to(beNil())
                    let anyArray: [Any] = [2, "some"]
                    expect(Convert.toCGSize(anyArray)).to(beNil())
                    expect(Convert.toCGSize(nil)).to(beNil())
                    expect(Convert.toCGSize(any_bool)).to(beNil())
                    expect(Convert.toCGSize(any_int)).to(beNil())
                    expect(Convert.toCGSize(any_cgFloat)).to(beNil())
                    expect(Convert.toCGSize(any_float)).to(beNil())
                    expect(Convert.toCGSize(any_double)).to(beNil())
                    expect(Convert.toCGSize(any_vec3)).to(beNil())
                    expect(Convert.toCGSize(any_arrayOfInts)).notTo(beNil())
                }
            }

            context("toVector3") {
                it("should convert Vector3 value") {
                    let vector1: SCNVector3 = SCNVector3(x: 0.000044, y: -0.000013, z: 0.00004)
                    expect(Convert.toVector3(vector1.toArrayOfCGFloat)).to(beCloseTo(vector1))
                    let vector2: SCNVector3 = SCNVector3(x: 3000.88, y: -12340.91, z: 1999.33)
                    expect(Convert.toVector3(vector2.toArrayOfFloat)).to(beCloseTo(vector2))
                    let vector3: SCNVector3 = SCNVector3(x: 54.0, y: 43.8, z: -32.9)
                    expect(Convert.toVector3(vector3.toArrayOfDouble)).to(beCloseTo(vector3))
                    let vector4: SCNVector3 = SCNVector3(x: -11.7, y: 21.4, z: 44.17)
                    expect(Convert.toVector3(vector4.toArrayOfCGFloat)).to(beCloseTo(vector4))
                    let vector5: SCNVector3 = SCNVector3(x: 2, y: 4, z: -8)
                    expect(Convert.toVector3(vector5.toArrayOfInt)).to(beCloseTo(vector5))

                    let anyArray: [Any] = [2, true, "some"]
                    expect(Convert.toVector3(anyArray)).to(beNil())
                    expect(Convert.toVector3(nil)).to(beNil())
                    expect(Convert.toVector3(any_bool)).to(beNil())
                    expect(Convert.toVector3(any_int)).to(beNil())
                    expect(Convert.toVector3(any_cgFloat)).to(beNil())
                    expect(Convert.toVector3(any_float)).to(beNil())
                    expect(Convert.toVector3(any_double)).to(beNil())
                    expect(Convert.toVector3(any_vec3)).to(beNil())
                    expect(Convert.toVector3(any_arrayOfInts)).to(beNil())
                }
            }

            context("toVector4") {
                it("should convert Vector4 value") {
                    let vector1 = SCNVector4(x: 0.000044, y: -0.000013, z: 0.00004, w: 1.000001)
                    expect(Convert.toVector4(vector1.toArrayOfCGFloat)).to(beCloseTo(vector1))
                    let vector2 = SCNVector4(x: 3000.88, y: -12340.91, z: 1999.33, w: 200.94)
                    expect(Convert.toVector4(vector2.toArrayOfFloat)).to(beCloseTo(vector2))
                    let vector3 = SCNVector4(x: 54.0, y: 43.8, z: -32.9, w: 12.3)
                    expect(Convert.toVector4(vector3.toArrayOfDouble)).to(beCloseTo(vector3))
                    let vector4 = SCNVector4(x: -11.7, y: 21.4, z: 44.17, w: 22.2)
                    expect(Convert.toVector4(vector4.toArrayOfCGFloat)).to(beCloseTo(vector4))
                    let vector5 = SCNVector4(x: 2, y: 4, z: -8, w: 3)
                    expect(Convert.toVector4(vector5.toArrayOfInt)).to(beCloseTo(vector5))

                    let anyArray: [Any] = [2, true, "some", 5.4]
                    expect(Convert.toVector4(anyArray)).to(beNil())
                    expect(Convert.toVector4(nil)).to(beNil())
                    expect(Convert.toVector4(any_bool)).to(beNil())
                    expect(Convert.toVector4(any_int)).to(beNil())
                    expect(Convert.toVector4(any_cgFloat)).to(beNil())
                    expect(Convert.toVector4(any_float)).to(beNil())
                    expect(Convert.toVector4(any_double)).to(beNil())
                    expect(Convert.toVector4(any_vec3)).to(beNil())
                    expect(Convert.toVector4(any_arrayOfInts)).to(beNil())
                }
            }

            context("toQuaternion") {
                it("should convert Quaternion value") {
                    let quat: SCNQuaternion = SCNQuaternion(x: 1, y: 0.707, z: 0.707, w: 1)
                    expect(Convert.toQuaternion(quat.toArrayOfCGFloat)).to(beCloseTo(quat))
                    expect(Convert.toQuaternion(quat.toArrayOfFloat)).to(beCloseTo(quat))
                    expect(Convert.toQuaternion(quat.toArrayOfDouble)).to(beCloseTo(quat))

                    let anyArray: [Any] = [2, true, "some", 1]
                    expect(Convert.toQuaternion(anyArray)).to(beNil())
                    expect(Convert.toQuaternion(nil)).to(beNil())
                    expect(Convert.toQuaternion(any_bool)).to(beNil())
                    expect(Convert.toQuaternion(any_int)).to(beNil())
                    expect(Convert.toQuaternion(any_cgFloat)).to(beNil())
                    expect(Convert.toQuaternion(any_float)).to(beNil())
                    expect(Convert.toQuaternion(any_double)).to(beNil())
                    expect(Convert.toQuaternion(any_vec3)).to(beNil())
                    expect(Convert.toQuaternion(any_arrayOfInts)).to(beNil())
                }
            }

            context("toMatrix4") {
                it("should convert Matrix4 value") {
                    let value: SCNMatrix4 = SCNMatrix4MakeRotation(1.2300001, 1, 0, 1)
                    expect(Convert.toMatrix4(value.toArrayOfCGFloat)).to(beCloseTo(value))
                    expect(Convert.toMatrix4(value.toArrayOfFloat)).to(beCloseTo(value))
                    expect(Convert.toMatrix4(value.toArrayOfDouble)).to(beCloseTo(value))

                    let anyArray: [Any] = [1, 0, 0, 0, "0", "1", "0", "0", 0, 0, 1, 0, false, false, false, true]
                    expect(Convert.toMatrix4(anyArray)).to(beNil())
                    expect(Convert.toMatrix4(nil)).to(beNil())
                    expect(Convert.toMatrix4(any_bool)).to(beNil())
                    expect(Convert.toMatrix4(any_int)).to(beNil())
                    expect(Convert.toMatrix4(any_cgFloat)).to(beNil())
                    expect(Convert.toMatrix4(any_float)).to(beNil())
                    expect(Convert.toMatrix4(any_double)).to(beNil())
                    expect(Convert.toMatrix4(any_vec3)).to(beNil())
                    expect(Convert.toMatrix4(any_arrayOfInts)).to(beNil())
                }
            }

            context("toPadding") {
                it("should convert Padding value") {
                    let padding1 = UIEdgeInsets(top: 0.000044, left: -0.000013, bottom: 0.00004, right: 1.000001)
                    expect(Convert.toPadding(padding1.toArrayOfCGFloat)).to(beCloseTo(padding1))
                    let padding2 = UIEdgeInsets(top: 3000.88, left: -12340.91, bottom: 1999.33, right: 200.94)
                    expect(Convert.toPadding(padding2.toArrayOfFloat)).to(beCloseTo(padding2, epsilon: 0.001))
                    let padding3 = UIEdgeInsets(top: 54.0, left: 43.8, bottom: -32.9, right: 12.3)
                    expect(Convert.toPadding(padding3.toArrayOfDouble)).to(beCloseTo(padding3))
                    let padding4 = UIEdgeInsets(top: -11.7, left: 21.4, bottom: 44.17, right: 22.2)
                    expect(Convert.toPadding(padding4.toArrayOfCGFloat)).to(beCloseTo(padding4))
                    let padding5 = UIEdgeInsets(top: 2, left: 4, bottom: -8, right: 3)
                    expect(Convert.toPadding(padding5.toArrayOfInt)).to(beCloseTo(padding5))

                    let anyArray: [Any] = [2, true, "some", 5.4]
                    expect(Convert.toPadding(anyArray)).to(beNil())
                    expect(Convert.toPadding(nil)).to(beNil())
                    expect(Convert.toPadding(any_bool)).to(beNil())
                    expect(Convert.toPadding(any_int)).to(beNil())
                    expect(Convert.toPadding(any_cgFloat)).to(beNil())
                    expect(Convert.toPadding(any_float)).to(beNil())
                    expect(Convert.toPadding(any_double)).to(beNil())
                    expect(Convert.toPadding(any_vec3)).to(beNil())
                    expect(Convert.toPadding(any_arrayOfInts)).to(beNil())
                }
            }

            context("toColor") {
                it("should convert Color value") {
                    let color1: UIColor = UIColor(red: 0.10001, green: 0.20002, blue: 0.30003, alpha: 0.80008)
                    let color2: UIColor = UIColor(red: 0.72, green: 0.74, blue: 0.76, alpha: 0.97)
                    let rgb: [CGFloat] = [0.1, 0.2, 0.3]
                    let color3: UIColor = UIColor(red: rgb[0], green: rgb[1], blue: rgb[2], alpha: 1.0)
                    expect(Convert.toColor(color1.toArrayOfCGFloat)).to(beCloseTo(color1))
                    expect(Convert.toColor(color1.toArrayOfFloat)).to(beCloseTo(color1))
                    expect(Convert.toColor(color1.toArrayOfDouble)).to(beCloseTo(color1))
                    expect(Convert.toColor(color2.toArrayOfCGFloat)).notTo(beCloseTo(color1))
                    expect(Convert.toColor(color2.toArrayOfFloat)).notTo(beCloseTo(color1))
                    expect(Convert.toColor(color2.toArrayOfDouble)).notTo(beCloseTo(color1))
                    expect(Convert.toColor(rgb)).to(beCloseTo(color3))

                    let anyArray: [Any] = [2, true, "some", 5.4]
                    expect(Convert.toColor(anyArray)).to(beNil())
                    expect(Convert.toColor(any_bool)).to(beNil())
                    expect(Convert.toColor(any_int)).to(beNil())
                    expect(Convert.toColor(any_cgFloat)).to(beNil())
                    expect(Convert.toColor(any_float)).to(beNil())
                    expect(Convert.toColor(any_double)).to(beNil())
                    expect(Convert.toColor(any_vec3)).to(beNil())
                    expect(Convert.toColor(any_arrayOfInts)).to(beNil())
                }
            }

            context("toHorizontalTextAlignment") {
                it("should convert HorizontalTextAlignment value") {
                    let center: HorizontalTextAlignment = .center
                    let justify: HorizontalTextAlignment = .justify
                    let left: HorizontalTextAlignment = .left
                    let right: HorizontalTextAlignment = .right

                    let centerAlignmentString: String = "center"
                    let justifyAlignmentString: String = "justify"
                    let leftAlignmentString: String = "left"
                    let rightAlignmentString: String = "right"


                    expect(Convert.toHorizontalTextAlignment(centerAlignmentString)).to(equal(center))
                    expect(Convert.toHorizontalTextAlignment(justifyAlignmentString)).to(equal(justify))
                    expect(Convert.toHorizontalTextAlignment(leftAlignmentString)).to(equal(left))
                    expect(Convert.toHorizontalTextAlignment(rightAlignmentString)).to(equal(right))

                    expect(Convert.toHorizontalTextAlignment(rightAlignmentString)).notTo(equal(left))
                    expect(Convert.toHorizontalTextAlignment(rightAlignmentString)).notTo(equal(center))
                    expect(Convert.toHorizontalTextAlignment(rightAlignmentString)).notTo(equal(justify))

                    expect(Convert.toHorizontalTextAlignment(nil)).to(beNil())
                    expect(Convert.toHorizontalTextAlignment(any_bool)).to(beNil())
                    expect(Convert.toHorizontalTextAlignment(any_int)).to(beNil())
                    expect(Convert.toHorizontalTextAlignment(any_cgFloat)).to(beNil())
                    expect(Convert.toHorizontalTextAlignment(any_float)).to(beNil())
                    expect(Convert.toHorizontalTextAlignment(any_double)).to(beNil())
                    expect(Convert.toHorizontalTextAlignment(any_vec3)).to(beNil())
                    expect(Convert.toHorizontalTextAlignment(any_arrayOfInts)).to(beNil())
                }
            }

            context("toAlignment") {
                it("should convert Alignment value") {
                    let topLeft: Alignment = .topLeft
                    let topCenter: Alignment = .topCenter
                    let topRight: Alignment = .topRight
                    let centerLeft: Alignment = .centerLeft
                    let centerCenter: Alignment = .centerCenter
                    let centerRight: Alignment = .centerRight
                    let bottomLeft: Alignment = .bottomLeft
                    let bottomCenter: Alignment = .bottomCenter
                    let bottomRight: Alignment = .bottomRight

                    let topLeftAlignmentString: String = "top-left"
                    let topCenterAlignmentString: String = "top-center"
                    let topRightAlignmentString: String = "top-right"
                    let centerLeftAlignmentString: String = "center-left"
                    let centerCenterAlignmentString: String = "center-center"
                    let centerRightAlignmentString: String = "center-right"
                    let bottomLeftAlignmentString: String = "bottom-left"
                    let bottomCenterAlignmentString: String = "bottom-center"
                    let bottomRightAlignmentString: String = "bottom-right"

                    expect(Convert.toAlignment(topLeftAlignmentString)).to(equal(topLeft))
                    expect(Convert.toAlignment(topCenterAlignmentString)).to(equal(topCenter))
                    expect(Convert.toAlignment(topRightAlignmentString)).to(equal(topRight))
                    expect(Convert.toAlignment(centerLeftAlignmentString)).to(equal(centerLeft))
                    expect(Convert.toAlignment(centerCenterAlignmentString)).to(equal(centerCenter))
                    expect(Convert.toAlignment(centerRightAlignmentString)).to(equal(centerRight))
                    expect(Convert.toAlignment(bottomLeftAlignmentString)).to(equal(bottomLeft))
                    expect(Convert.toAlignment(bottomCenterAlignmentString)).to(equal(bottomCenter))
                    expect(Convert.toAlignment(bottomRightAlignmentString)).to(equal(bottomRight))

                    expect(Convert.toAlignment(bottomRightAlignmentString)).notTo(equal(topLeft))
                    expect(Convert.toAlignment(bottomRightAlignmentString)).notTo(equal(topCenter))
                    expect(Convert.toAlignment(bottomRightAlignmentString)).notTo(equal(topRight))
                    expect(Convert.toAlignment(bottomRightAlignmentString)).notTo(equal(centerLeft))
                    expect(Convert.toAlignment(bottomRightAlignmentString)).notTo(equal(centerCenter))
                    expect(Convert.toAlignment(bottomRightAlignmentString)).notTo(equal(centerRight))
                    expect(Convert.toAlignment(bottomRightAlignmentString)).notTo(equal(bottomLeft))
                    expect(Convert.toAlignment(bottomRightAlignmentString)).notTo(equal(bottomCenter))

                    expect(Convert.toAlignment(nil)).to(beNil())
                    expect(Convert.toAlignment(any_bool)).to(beNil())
                    expect(Convert.toAlignment(any_int)).to(beNil())
                    expect(Convert.toAlignment(any_cgFloat)).to(beNil())
                    expect(Convert.toAlignment(any_float)).to(beNil())
                    expect(Convert.toAlignment(any_double)).to(beNil())
                    expect(Convert.toAlignment(any_vec3)).to(beNil())
                    expect(Convert.toAlignment(any_arrayOfInts)).to(beNil())
                }
            }

            context("toFileURL") {
                it("should convert file path value") {
                    let pathURL1 = URL(string: "http://www.sample.com/image.png")!
                    expect(Convert.toFileURL(pathURL1.absoluteString)).to(equal(pathURL1))

                    let pathURL2 = URL(string: "http://www.example.com/picture.jpg")!
                    let dict2 = ["uri" : pathURL2.absoluteString]
                    expect(Convert.toFileURL(dict2)).to(equal(pathURL2))

                    let dict3 = ["uri" : "foo"]
                    expect(Convert.toFileURL(dict3)).to(beNil())

                    expect(Convert.toFileURL(nil)).to(beNil())
                    expect(Convert.toFileURL(any_bool)).to(beNil())
                    expect(Convert.toFileURL(any_int)).to(beNil())
                    expect(Convert.toFileURL(any_cgFloat)).to(beNil())
                    expect(Convert.toFileURL(any_float)).to(beNil())
                    expect(Convert.toFileURL(any_double)).to(beNil())
                    expect(Convert.toFileURL(any_vec3)).to(beNil())
                    expect(Convert.toFileURL(any_arrayOfInts)).to(beNil())
                }
            }
        }
    }
}
