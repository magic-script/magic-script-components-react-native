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

class SCNCircleSpec: QuickSpec {
    override func spec() {
        describe("SCNCircleSpec") {
            context("init") {
                it("should init circle geometry with radius and thickness") {
                    let radius: CGFloat = 0.2
                    let circle1 = SCNCircle(radius: radius, thickness: 0)
                    let circle2 = SCNCircle(radius: radius, thickness: 0.01)
                    let circle3 = SCNCircle(radius: radius, thickness: 0.05)
                    let circle4 = SCNCircle(radius: radius, thickness: 0.2)
                    expect(circle1.sources.first!.vectorCount).to(equal(146))
                    expect(circle2.sources.first!.vectorCount).to(equal(146))
                    expect(circle3.sources.first!.vectorCount).to(equal(146))
                    expect(circle4.sources.first!.vectorCount).to(equal(146))
                }

                it("should init circle geometry with size and thickness") {
                    let size = CGSize(width: 0.2, height: 0.1)
                    let circle1 = SCNCircle(size: size, thickness: 0)
                    let circle2 = SCNCircle(size: size, thickness: 0.01)
                    let circle3 = SCNCircle(size: size, thickness: 0.05)
                    let circle4 = SCNCircle(size: size, thickness: 0.2)
                    expect(circle1.sources.first!.vectorCount).to(equal(146))
                    expect(circle2.sources.first!.vectorCount).to(equal(146))
                    expect(circle3.sources.first!.vectorCount).to(equal(146))
                    expect(circle4.sources.first!.vectorCount).to(equal(146))
                }
            }

            context("barImage") {
                it("should set bar image") {
                    let referenceImage = UIImage.image(from: [UIColor.red], size: 1)
                    let circle = SCNCircle(radius: 0.6, thickness: 0.08)
                    circle.barImage = referenceImage
                    expect(circle.barImage).to(beIdenticalTo(referenceImage))
                    expect(circle.materials[0].diffuse.contents).to(beIdenticalTo(referenceImage))
                }
            }

            context("progress") {
                it("should set progress value") {
                    let referenceProgress: Float = 0.7
                    let circle = SCNCircle(radius: 0.5, thickness: 0.05)
                    circle.progress = referenceProgress
                    expect(circle.progress).to(beCloseTo(referenceProgress))

                    let tx: Float = 1.0 - referenceProgress
                    expect(circle.materials[0].diffuse.contentsTransform.m41).to(beCloseTo(tx))
                }
            }
        }
    }
}
