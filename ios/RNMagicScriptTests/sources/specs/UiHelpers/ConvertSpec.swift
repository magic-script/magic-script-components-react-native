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
import ARKit
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

            context("toFontStyle") {
                it("should convert FontStyle value") {
                    let normal: FontStyle = .normal
                    let italic: FontStyle = .italic

                    let normalFontStyleString: String = "normal"
                    let italicFontStyleString: String = "italic"

                    expect(Convert.toFontStyle(normalFontStyleString)).to(equal(normal))
                    expect(Convert.toFontStyle(italicFontStyleString)).to(equal(italic))

                    expect(Convert.toFontStyle(normalFontStyleString)).notTo(equal(italic))
                    expect(Convert.toFontStyle(italicFontStyleString)).notTo(equal(normal))

                    expect(Convert.toFontStyle(nil)).to(beNil())
                    expect(Convert.toFontStyle(any_bool)).to(beNil())
                    expect(Convert.toFontStyle(any_int)).to(beNil())
                    expect(Convert.toFontStyle(any_cgFloat)).to(beNil())
                    expect(Convert.toFontStyle(any_float)).to(beNil())
                    expect(Convert.toFontStyle(any_double)).to(beNil())
                    expect(Convert.toFontStyle(any_vec3)).to(beNil())
                    expect(Convert.toFontStyle(any_arrayOfInts)).to(beNil())
                }
            }

            context("toFontWeight") {
                it("should convert FontWeight value") {
                    let extraLight: FontWeight = .extraLight
                    let light: FontWeight = .light
                    let regular: FontWeight = .regular
                    let medium: FontWeight = .medium
                    let bold: FontWeight = .bold
                    let extraBold: FontWeight = .extraBold

                    let extraLightFontWeightString: String = "extra-light"
                    let lightFontWeightString: String = "light"
                    let regularFontWeightString: String = "regular"
                    let mediumFontWeightString: String = "medium"
                    let boldFontWeightString: String = "bold"
                    let extraBoldFontWeightString: String = "extra-bold"

                    expect(Convert.toFontWeight(extraLightFontWeightString)).to(equal(extraLight))
                    expect(Convert.toFontWeight(lightFontWeightString)).to(equal(light))
                    expect(Convert.toFontWeight(regularFontWeightString)).to(equal(regular))
                    expect(Convert.toFontWeight(mediumFontWeightString)).to(equal(medium))
                    expect(Convert.toFontWeight(boldFontWeightString)).to(equal(bold))
                    expect(Convert.toFontWeight(extraBoldFontWeightString)).to(equal(extraBold))

                    expect(Convert.toFontWeight(regularFontWeightString)).notTo(equal(extraLight))
                    expect(Convert.toFontWeight(regularFontWeightString)).notTo(equal(light))
                    expect(Convert.toFontWeight(regularFontWeightString)).notTo(equal(medium))
                    expect(Convert.toFontWeight(regularFontWeightString)).notTo(equal(bold))
                    expect(Convert.toFontWeight(regularFontWeightString)).notTo(equal(extraBold))

                    expect(Convert.toFontWeight(nil)).to(beNil())
                    expect(Convert.toFontWeight(any_bool)).to(beNil())
                    expect(Convert.toFontWeight(any_int)).to(beNil())
                    expect(Convert.toFontWeight(any_cgFloat)).to(beNil())
                    expect(Convert.toFontWeight(any_float)).to(beNil())
                    expect(Convert.toFontWeight(any_double)).to(beNil())
                    expect(Convert.toFontWeight(any_vec3)).to(beNil())
                    expect(Convert.toFontWeight(any_arrayOfInts)).to(beNil())
                }
            }

            context("toScrollBarVisibility") {
                it("should convert ScrollBarVisibility value") {
                    let always: ScrollBarVisibility = .always
                    let auto: ScrollBarVisibility = .auto
                    let off: ScrollBarVisibility = .off

                    let alwaysScrollBarVisibilityString: String = "always"
                    let autoScrollBarVisibilityString: String = "auto"
                    let offScrollBarVisibilityString: String = "off"

                    expect(Convert.toScrollBarVisibility(alwaysScrollBarVisibilityString)).to(equal(always))
                    expect(Convert.toScrollBarVisibility(autoScrollBarVisibilityString)).to(equal(auto))
                    expect(Convert.toScrollBarVisibility(offScrollBarVisibilityString)).to(equal(off))

                    expect(Convert.toScrollBarVisibility(alwaysScrollBarVisibilityString)).notTo(equal(auto))
                    expect(Convert.toScrollBarVisibility(alwaysScrollBarVisibilityString)).notTo(equal(off))

                    expect(Convert.toScrollBarVisibility(nil)).to(beNil())
                    expect(Convert.toScrollBarVisibility(any_bool)).to(beNil())
                    expect(Convert.toScrollBarVisibility(any_int)).to(beNil())
                    expect(Convert.toScrollBarVisibility(any_cgFloat)).to(beNil())
                    expect(Convert.toScrollBarVisibility(any_float)).to(beNil())
                    expect(Convert.toScrollBarVisibility(any_double)).to(beNil())
                    expect(Convert.toScrollBarVisibility(any_vec3)).to(beNil())
                    expect(Convert.toScrollBarVisibility(any_arrayOfInts)).to(beNil())
                }
            }

            context("toScrollDirection") {
                it("should convert ScrollDirection value") {
                    let horizontal: ScrollDirection = .horizontal
                    let vertical: ScrollDirection = .vertical

                    let horizontalScrollDirectionString: String = "horizontal"
                    let verticalScrollDirectionString: String = "vertical"

                    expect(Convert.toScrollDirection(horizontalScrollDirectionString)).to(equal(horizontal))
                    expect(Convert.toScrollDirection(verticalScrollDirectionString)).to(equal(vertical))

                    expect(Convert.toScrollDirection(horizontalScrollDirectionString)).notTo(equal(vertical))
                    expect(Convert.toScrollDirection(verticalScrollDirectionString)).notTo(equal(horizontal))

                    expect(Convert.toScrollDirection(nil)).to(beNil())
                    expect(Convert.toScrollDirection(any_bool)).to(beNil())
                    expect(Convert.toScrollDirection(any_int)).to(beNil())
                    expect(Convert.toScrollDirection(any_cgFloat)).to(beNil())
                    expect(Convert.toScrollDirection(any_float)).to(beNil())
                    expect(Convert.toScrollDirection(any_double)).to(beNil())
                    expect(Convert.toScrollDirection(any_vec3)).to(beNil())
                    expect(Convert.toScrollDirection(any_arrayOfInts)).to(beNil())
                }
            }

            context("toOrientation") {
                it("should convert Orientation value") {
                    let horizontal: Orientation = .horizontal
                    let vertical: Orientation = .vertical

                    let horizontalOrientationString: String = "horizontal"
                    let verticalOrientationString: String = "vertical"

                    expect(Convert.toOrientation(horizontalOrientationString)).to(equal(horizontal))
                    expect(Convert.toOrientation(verticalOrientationString)).to(equal(vertical))

                    expect(Convert.toOrientation(horizontalOrientationString)).notTo(equal(vertical))
                    expect(Convert.toOrientation(verticalOrientationString)).notTo(equal(horizontal))

                    expect(Convert.toOrientation(nil)).to(beNil())
                    expect(Convert.toOrientation(any_bool)).to(beNil())
                    expect(Convert.toOrientation(any_int)).to(beNil())
                    expect(Convert.toOrientation(any_cgFloat)).to(beNil())
                    expect(Convert.toOrientation(any_float)).to(beNil())
                    expect(Convert.toOrientation(any_double)).to(beNil())
                    expect(Convert.toOrientation(any_vec3)).to(beNil())
                    expect(Convert.toOrientation(any_arrayOfInts)).to(beNil())
                }
            }

            context("toTextEntryMode") {
                it("should convert TextEntryMode value") {
                    let email: TextEntryMode = .email
                    let none: TextEntryMode = .none
                    let normal: TextEntryMode = .normal
                    let numeric: TextEntryMode = .numeric
                    let url: TextEntryMode = .url

                    let emailTextEntryModeString: String = "email"
                    let noneTextEntryModeString: String = "none"
                    let normalTextEntryModeString: String = "normal"
                    let numericTextEntryModeString: String = "numeric"
                    let urlTextEntryModeString: String = "url"

                    expect(Convert.toTextEntryMode(emailTextEntryModeString)).to(equal(email))
                    expect(Convert.toTextEntryMode(noneTextEntryModeString)).to(equal(none))
                    expect(Convert.toTextEntryMode(normalTextEntryModeString)).to(equal(normal))
                    expect(Convert.toTextEntryMode(numericTextEntryModeString)).to(equal(numeric))
                    expect(Convert.toTextEntryMode(urlTextEntryModeString)).to(equal(url))

                    expect(Convert.toTextEntryMode(normalTextEntryModeString)).notTo(equal(email))
                    expect(Convert.toTextEntryMode(normalTextEntryModeString)).notTo(equal(none))
                    expect(Convert.toTextEntryMode(normalTextEntryModeString)).notTo(equal(numeric))
                    expect(Convert.toTextEntryMode(normalTextEntryModeString)).notTo(equal(url))

                    expect(Convert.toTextEntryMode(nil)).to(beNil())
                    expect(Convert.toTextEntryMode(any_bool)).to(beNil())
                    expect(Convert.toTextEntryMode(any_int)).to(beNil())
                    expect(Convert.toTextEntryMode(any_cgFloat)).to(beNil())
                    expect(Convert.toTextEntryMode(any_float)).to(beNil())
                    expect(Convert.toTextEntryMode(any_double)).to(beNil())
                    expect(Convert.toTextEntryMode(any_vec3)).to(beNil())
                    expect(Convert.toTextEntryMode(any_arrayOfInts)).to(beNil())
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

            context("toSystemIcon") {
                it("should convert to SystemIcon value") {
                    let referenceName: String = "address-book"
                    let referenceImageName: String = "AddressBook"
                    let icon: SystemIcon = Convert.toSystemIcon(referenceName)!
                    expect(icon.getImage()).notTo(beNil())
                    expect(icon.getImage(forceDefaultImage: false)).notTo(beNil())
                    expect(icon.getImage(forceDefaultImage: true)).notTo(beNil())
                    expect(icon.name).to(equal(referenceName))
                    expect(icon.imageName).to(equal(referenceImageName))

                    expect(Convert.toSystemIcon(nil)).to(beNil())
                    expect(Convert.toSystemIcon(any_bool)).to(beNil())
                    expect(Convert.toSystemIcon(any_int)).to(beNil())
                    expect(Convert.toSystemIcon(any_cgFloat)).to(beNil())
                    expect(Convert.toSystemIcon(any_float)).to(beNil())
                    expect(Convert.toSystemIcon(any_double)).to(beNil())
                    expect(Convert.toSystemIcon(any_vec3)).to(beNil())
                    expect(Convert.toSystemIcon(any_arrayOfInts)).to(beNil())
                }
            }

            context("toAudioAction") {
                it("should convert to AudioAction value") {
                    let start: AudioAction = .start
                    let stop: AudioAction = .stop
                    let pause: AudioAction = .pause
                    let resume: AudioAction = .resume

                    let startAudioActionString: String = "start"
                    let stopAudioActionString: String = "stop"
                    let pauseAudioActionString: String = "pause"
                    let resumeAudioActionString: String = "resume"

                    expect(Convert.toAudioAction(startAudioActionString)).to(equal(start))
                    expect(Convert.toAudioAction(stopAudioActionString)).to(equal(stop))
                    expect(Convert.toAudioAction(pauseAudioActionString)).to(equal(pause))
                    expect(Convert.toAudioAction(resumeAudioActionString)).to(equal(resume))

                    expect(Convert.toAudioAction(startAudioActionString)).notTo(equal(stop))
                    expect(Convert.toAudioAction(startAudioActionString)).notTo(equal(pause))
                    expect(Convert.toAudioAction(startAudioActionString)).notTo(equal(resume))

                    expect(Convert.toAudioAction(nil)).to(beNil())
                    expect(Convert.toAudioAction(any_bool)).to(beNil())
                    expect(Convert.toAudioAction(any_int)).to(beNil())
                    expect(Convert.toAudioAction(any_cgFloat)).to(beNil())
                    expect(Convert.toAudioAction(any_float)).to(beNil())
                    expect(Convert.toAudioAction(any_double)).to(beNil())
                    expect(Convert.toAudioAction(any_vec3)).to(beNil())
                    expect(Convert.toAudioAction(any_arrayOfInts)).to(beNil())
                }
            }

            context("toVideoAction") {
                it("should convert to VideoAction value") {
                    let start: VideoAction = .start
                    let pause: VideoAction = .pause
                    let stop: VideoAction = .stop

                    let startVideoActionString: String = "start"
                    let pauseVideoActionString: String = "pause"
                    let stopVideoActionString: String = "stop"

                    expect(Convert.toVideoAction(startVideoActionString)).to(equal(start))
                    expect(Convert.toVideoAction(stopVideoActionString)).to(equal(stop))
                    expect(Convert.toVideoAction(pauseVideoActionString)).to(equal(pause))

                    expect(Convert.toVideoAction(startVideoActionString)).notTo(equal(stop))
                    expect(Convert.toVideoAction(startVideoActionString)).notTo(equal(pause))

                    expect(Convert.toVideoAction(nil)).to(beNil())
                    expect(Convert.toVideoAction(any_bool)).to(beNil())
                    expect(Convert.toVideoAction(any_int)).to(beNil())
                    expect(Convert.toVideoAction(any_cgFloat)).to(beNil())
                    expect(Convert.toVideoAction(any_float)).to(beNil())
                    expect(Convert.toVideoAction(any_double)).to(beNil())
                    expect(Convert.toVideoAction(any_vec3)).to(beNil())
                    expect(Convert.toVideoAction(any_arrayOfInts)).to(beNil())
                }
            }

            context("toVideoViewMode") {
                it("should convert to VideoViewMode value") {
                    let fullArea: VideoViewMode = .fullArea
                    let leftRight: VideoViewMode = .leftRight

                    let fullAreaVideoViewModeString: String = "full-area"
                    let leftRightVideoViewModeString: String = "left-right"

                    expect(Convert.toVideoViewMode(fullAreaVideoViewModeString)).to(equal(fullArea))
                    expect(Convert.toVideoViewMode(leftRightVideoViewModeString)).to(equal(leftRight))

                    expect(Convert.toVideoViewMode(fullAreaVideoViewModeString)).notTo(equal(leftRight))
                    expect(Convert.toVideoViewMode(leftRightVideoViewModeString)).notTo(equal(fullArea))

                    expect(Convert.toVideoViewMode(nil)).to(beNil())
                    expect(Convert.toVideoViewMode(any_bool)).to(beNil())
                    expect(Convert.toVideoViewMode(any_int)).to(beNil())
                    expect(Convert.toVideoViewMode(any_cgFloat)).to(beNil())
                    expect(Convert.toVideoViewMode(any_float)).to(beNil())
                    expect(Convert.toVideoViewMode(any_double)).to(beNil())
                    expect(Convert.toVideoViewMode(any_vec3)).to(beNil())
                    expect(Convert.toVideoViewMode(any_arrayOfInts)).to(beNil())
                }
            }

            context("toSide") {
                it("should convert to Side value") {
                    let top: Side = .top
                    let right: Side = .right
                    let bottom: Side = .bottom
                    let left: Side = .left

                    let topSideString: String = "top"
                    let rightSideString: String = "right"
                    let bottomSideString: String = "bottom"
                    let leftSideString: String = "left"

                    expect(Convert.toSide(topSideString)).to(equal(top))
                    expect(Convert.toSide(rightSideString)).to(equal(right))
                    expect(Convert.toSide(bottomSideString)).to(equal(bottom))
                    expect(Convert.toSide(leftSideString)).to(equal(left))

                    expect(Convert.toSide(topSideString)).notTo(equal(right))
                    expect(Convert.toSide(topSideString)).notTo(equal(bottom))
                    expect(Convert.toSide(topSideString)).notTo(equal(left))

                    expect(Convert.toSide(nil)).to(beNil())
                    expect(Convert.toSide(any_bool)).to(beNil())
                    expect(Convert.toSide(any_int)).to(beNil())
                    expect(Convert.toSide(any_cgFloat)).to(beNil())
                    expect(Convert.toSide(any_float)).to(beNil())
                    expect(Convert.toSide(any_double)).to(beNil())
                    expect(Convert.toSide(any_vec3)).to(beNil())
                    expect(Convert.toSide(any_arrayOfInts)).to(beNil())
                }
            }

            context("toToggleType") {
                it("should convert to ToggleType value") {
                    let `default`: ToggleType = .default
                    let radio: ToggleType = .radio
                    let checkbox: ToggleType = .checkbox

                    let defaultToggleTypeString: String = "default"
                    let radioToggleTypeString: String = "radio"
                    let checkboxToggleTypeString: String = "checkbox"

                    expect(Convert.toToggleType(defaultToggleTypeString)).to(equal(`default`))
                    expect(Convert.toToggleType(radioToggleTypeString)).to(equal(radio))
                    expect(Convert.toToggleType(checkboxToggleTypeString)).to(equal(checkbox))

                    expect(Convert.toToggleType(defaultToggleTypeString)).notTo(equal(radio))
                    expect(Convert.toToggleType(defaultToggleTypeString)).notTo(equal(checkbox))
                    expect(Convert.toToggleType(radioToggleTypeString)).notTo(equal(checkbox))

                    expect(Convert.toToggleType(nil)).to(beNil())
                    expect(Convert.toToggleType(any_bool)).to(beNil())
                    expect(Convert.toToggleType(any_int)).to(beNil())
                    expect(Convert.toToggleType(any_cgFloat)).to(beNil())
                    expect(Convert.toToggleType(any_float)).to(beNil())
                    expect(Convert.toToggleType(any_double)).to(beNil())
                    expect(Convert.toToggleType(any_vec3)).to(beNil())
                    expect(Convert.toToggleType(any_arrayOfInts)).to(beNil())
                }
            }
            
            context("toPlaneDetection") {
                it("should convert to PlaneDetection option set") {
                    let horizontal: ARWorldTrackingConfiguration.PlaneDetection = .horizontal
                    let vertical: ARWorldTrackingConfiguration.PlaneDetection = .vertical
                    let bothValues: ARWorldTrackingConfiguration.PlaneDetection = [.horizontal, .vertical]
                    
                    let horizontalInput: [String] = ["horizontal"]
                    let verticalInput: [String] = ["vertical"]
                    let bothValuesInput: [String] = ["horizontal", "vertical"]
                    
                    expect(Convert.toPlaneDetection(horizontalInput)).to(equal(horizontal))
                    expect(Convert.toPlaneDetection(verticalInput)).to(equal(vertical))
                    expect(Convert.toPlaneDetection(bothValuesInput)).to(equal(bothValues))
                    
                    expect(Convert.toPlaneDetection(horizontalInput)).notTo(equal(vertical))
                    expect(Convert.toPlaneDetection(verticalInput)).notTo(equal(horizontal))
                    expect(Convert.toPlaneDetection(verticalInput)).notTo(equal(bothValues))
                }
            }
            
            context("toButtonType") {
                it("should convert to ButtonType value") {
                    let simple: ButtonType = .simple
                    let icon: ButtonType = .icon
                    let iconWithLabel: ButtonType = .iconWithLabel
                    let text: ButtonType = .text
                    let textWithIcon: ButtonType = .textWithIcon

                    let simpleButtonTypeString: String = "simple"
                    let iconButtonTypeString: String = "icon"
                    let iconWithLabelButtonTypeString: String = "icon-with-label"
                    let textButtonTypeString: String = "text"
                    let textWithIconButtonTypeString: String = "text-with-icon"
                    
                    expect(Convert.toButtonType(simpleButtonTypeString)).to(equal(simple))
                    expect(Convert.toButtonType(iconButtonTypeString)).to(equal(icon))
                    expect(Convert.toButtonType(iconWithLabelButtonTypeString)).to(equal(iconWithLabel))
                    expect(Convert.toButtonType(textButtonTypeString)).to(equal(text))
                    expect(Convert.toButtonType(textWithIconButtonTypeString)).to(equal(textWithIcon))
                    
                    expect(Convert.toButtonType(iconButtonTypeString)).notTo(equal(simple))
                    expect(Convert.toButtonType(iconButtonTypeString)).notTo(equal(iconWithLabel))
                    expect(Convert.toButtonType(iconButtonTypeString)).notTo(equal(text))
                    expect(Convert.toButtonType(iconButtonTypeString)).notTo(equal(textWithIcon))
                    
                    expect(Convert.toButtonType(nil)).to(beNil())
                    expect(Convert.toButtonType(any_bool)).to(beNil())
                    expect(Convert.toButtonType(any_int)).to(beNil())
                    expect(Convert.toButtonType(any_cgFloat)).to(beNil())
                    expect(Convert.toButtonType(any_float)).to(beNil())
                    expect(Convert.toButtonType(any_double)).to(beNil())
                    expect(Convert.toButtonType(any_vec3)).to(beNil())
                    expect(Convert.toButtonType(any_arrayOfInts)).to(beNil())
                }
            }
            
            context("toItemAlignment") {
                it("should convert to an array of Alignment values") {
                    let validInput: [[String: Any]] = [
                        ["index": 0, "alignment": "center-left"],
                        ["index": 3, "alignment": "bottom-left"],
                        ["index": 6, "alignment": "top-right"],
                        ["index": 9, "alignment": "top-center"],
                        ["index": 12, "align": "top-left"],
                        ["index": 15, "alignment": "left-bottom"],
                    ]
                    let validOutput = Convert.toItemAlignment(validInput)
                    expect(validOutput?.count).to(equal(validInput.count - 2))
                    expect(validOutput![0]).to(equal(Alignment.centerLeft))
                    expect(validOutput![3]).to(equal(Alignment.bottomLeft))
                    expect(validOutput![6]).to(equal(Alignment.topRight))
                    expect(validOutput![9]).to(equal(Alignment.topCenter))
                    expect(validOutput![12]).to(beNil())
                    expect(validOutput![15]).to(beNil())
                        
                    expect(Convert.toItemAlignment(nil)).to(beNil())
                    expect(Convert.toItemAlignment(any_bool)).to(beNil())
                    expect(Convert.toItemAlignment(any_int)).to(beNil())
                    expect(Convert.toItemAlignment(any_cgFloat)).to(beNil())
                    expect(Convert.toItemAlignment(any_float)).to(beNil())
                    expect(Convert.toItemAlignment(any_double)).to(beNil())
                    expect(Convert.toItemAlignment(any_vec3)).to(beNil())
                    expect(Convert.toItemAlignment(any_arrayOfInts)).to(beNil())
                }
            }
            
            context("toItemPadding") {
                it("should convert to array of UIEdgeInsets values") {
                    let getPadding: (_ value: CGFloat) -> ([CGFloat]) = { [$0, $0, $0, $0] }
                    let getUIEdgeInsets: (_ value: CGFloat) -> (UIEdgeInsets) = { UIEdgeInsets(top: $0, left: $0, bottom: $0, right: $0) }
                    let validInput: [[String: Any]] = [
                        ["index": 0, "padding": getPadding(0.1)],
                        ["index": 3, "padding": getPadding(0.2)],
                        ["index": 6, "padding": getPadding(0.3)],
                        ["index": 9, "padding": getPadding(0.4)],
                        ["index": 12, "padd": getPadding(0.5)],
                        ["index": 15, "padding": [0, 0]],
                    ]
                    let validOutput = Convert.toItemPadding(validInput)
                    expect(validOutput?.count).to(equal(validInput.count - 2))
                    expect(validOutput![0]).to(beCloseTo(getUIEdgeInsets(0.1)))
                    expect(validOutput![3]).to(beCloseTo(getUIEdgeInsets(0.2)))
                    expect(validOutput![6]).to(beCloseTo(getUIEdgeInsets(0.3)))
                    expect(validOutput![9]).to(beCloseTo(getUIEdgeInsets(0.4)))
                    expect(validOutput![12]).to(beNil())
                    expect(validOutput![15]).to(beNil())
                        
                    expect(Convert.toItemPadding(nil)).to(beNil())
                    expect(Convert.toItemPadding(any_bool)).to(beNil())
                    expect(Convert.toItemPadding(any_int)).to(beNil())
                    expect(Convert.toItemPadding(any_cgFloat)).to(beNil())
                    expect(Convert.toItemPadding(any_float)).to(beNil())
                    expect(Convert.toItemPadding(any_double)).to(beNil())
                    expect(Convert.toItemPadding(any_vec3)).to(beNil())
                    expect(Convert.toItemPadding(any_arrayOfInts)).to(beNil())
                }
            }
            
            context("toItemAlignmentColumnRow") {
                it("should convert to array of Alignment values") {
                    let validInput: [[String: Any]] = [
                        ["column": 0, "row": 0, "alignment": "center-left"],
                        ["column": 0, "row": 1, "alignment": "bottom-left"],
                        ["column": 1, "row": 2, "alignment": "top-right"],
                        ["column": 2, "row": 3, "alignment": "top-center"],
                        ["column": 3, "row": 4, "align": "top-left"],
                        ["column": 4, "row": 5, "alignment": "left-bottom"],
                        ["col":    5, "row": 6, "alignment": "top-left"],
                        ["column": 6, "raw": 7, "alignment": "top-left"],
                    ]
                    let validOutput = Convert.toItemAlignmentColumnRow(validInput)
                    expect(validOutput?.count).to(equal(validInput.count - 4))
                    expect(validOutput![0].column).to(equal(0))
                    expect(validOutput![0].row).to(equal(0))
                    expect(validOutput![0].alignment).to(equal(Alignment.centerLeft))
                    expect(validOutput![1].column).to(equal(0))
                    expect(validOutput![1].row).to(equal(1))
                    expect(validOutput![1].alignment).to(equal(Alignment.bottomLeft))
                    expect(validOutput![2].column).to(equal(1))
                    expect(validOutput![2].row).to(equal(2))
                    expect(validOutput![2].alignment).to(equal(Alignment.topRight))
                    expect(validOutput![3].column).to(equal(2))
                    expect(validOutput![3].row).to(equal(3))
                    expect(validOutput![3].alignment).to(equal(Alignment.topCenter))
                        
                    expect(Convert.toItemAlignmentColumnRow(nil)).to(beNil())
                    expect(Convert.toItemAlignmentColumnRow(any_bool)).to(beNil())
                    expect(Convert.toItemAlignmentColumnRow(any_int)).to(beNil())
                    expect(Convert.toItemAlignmentColumnRow(any_cgFloat)).to(beNil())
                    expect(Convert.toItemAlignmentColumnRow(any_float)).to(beNil())
                    expect(Convert.toItemAlignmentColumnRow(any_double)).to(beNil())
                    expect(Convert.toItemAlignmentColumnRow(any_vec3)).to(beNil())
                    expect(Convert.toItemAlignmentColumnRow(any_arrayOfInts)).to(beNil())
                }
            }
            
            context("toItemPaddingColumnRow") {
                it("should convert to array of UIEdgeInsets values") {
                    let getPadding: (_ value: CGFloat) -> ([CGFloat]) = { [$0, $0, $0, $0] }
                    let getUIEdgeInsets: (_ value: CGFloat) -> (UIEdgeInsets) = { UIEdgeInsets(top: $0, left: $0, bottom: $0, right: $0) }
                    let validInput: [[String: Any]] = [
                        ["column": 0, "row": 0, "padding": getPadding(0.1)],
                        ["column": 0, "row": 1, "padding": getPadding(0.2)],
                        ["column": 1, "row": 2, "padding": getPadding(0.3)],
                        ["column": 2, "row": 3, "padding": getPadding(0.4)],
                        ["column": 3, "row": 4, "padd": getPadding(0.5)],
                        ["column": 4, "row": 5, "padding": [0.6, 0.6]],
                        ["col":    5, "row": 6, "padding": getPadding(0.7)],
                        ["column": 6, "raw": 7, "padding": getPadding(0.8)],
                    ]
                    let validOutput = Convert.toItemPaddingColumnRow(validInput)
                    expect(validOutput?.count).to(equal(validInput.count - 4))
                    expect(validOutput![0].column).to(equal(0))
                    expect(validOutput![0].row).to(equal(0))
                    expect(validOutput![0].padding).to(beCloseTo(getUIEdgeInsets(0.1)))
                    expect(validOutput![1].column).to(equal(0))
                    expect(validOutput![1].row).to(equal(1))
                    expect(validOutput![1].padding).to(beCloseTo(getUIEdgeInsets(0.2)))
                    expect(validOutput![2].column).to(equal(1))
                    expect(validOutput![2].row).to(equal(2))
                    expect(validOutput![2].padding).to(beCloseTo(getUIEdgeInsets(0.3)))
                    expect(validOutput![3].column).to(equal(2))
                    expect(validOutput![3].row).to(equal(3))
                    expect(validOutput![3].padding).to(beCloseTo(getUIEdgeInsets(0.4)))
                        
                    expect(Convert.toItemPaddingColumnRow(nil)).to(beNil())
                    expect(Convert.toItemPaddingColumnRow(any_bool)).to(beNil())
                    expect(Convert.toItemPaddingColumnRow(any_int)).to(beNil())
                    expect(Convert.toItemPaddingColumnRow(any_cgFloat)).to(beNil())
                    expect(Convert.toItemPaddingColumnRow(any_float)).to(beNil())
                    expect(Convert.toItemPaddingColumnRow(any_double)).to(beNil())
                    expect(Convert.toItemPaddingColumnRow(any_vec3)).to(beNil())
                    expect(Convert.toItemPaddingColumnRow(any_arrayOfInts)).to(beNil())
                }
            }
        }
    }
}
