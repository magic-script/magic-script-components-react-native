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

class SCNRectangleSpec: QuickSpec {
    override func spec() {
        describe("SCNRectangle") {
            context("init") {
                it("should init rectangle geometry with no radius") {
                    let size = CGSize(width: 0.2, height: 0.1)
                    let rect1 = SCNRectangle(size: size, thickness: 0)
                    let rect2 = SCNRectangle(size: size, thickness: 0.01)
                    let rect3 = SCNRectangle(size: size, thickness: 0.05)
                    let rect4 = SCNRectangle(size: size, thickness: 0.2)
                    expect(rect1.sources.first!.vectorCount).to(equal(4))
                    expect(rect2.sources.first!.vectorCount).to(equal(8))
                    expect(rect3.sources.first!.vectorCount).to(equal(8))
                    expect(rect4.sources.first!.vectorCount).to(equal(8))
                }

                it("should init rectangle geometry with some radius") {
                    let size = CGSize(width: 0.2, height: 0.1)
                    let rect1 = SCNRectangle(size: size, thickness: 0, radius: 0.01)
                    let rect2 = SCNRectangle(size: size, thickness: 0, radius: 0.05)
                    let rect3 = SCNRectangle(size: size, thickness: 0, radius: 0.1)
                    let rect4 = SCNRectangle(size: size, thickness: 0, radius: 0.33)
                    expect(rect1.sources.first!.vectorCount).to(equal(44))
                    expect(rect2.sources.first!.vectorCount).to(equal(44))
                    expect(rect3.sources.first!.vectorCount).to(equal(44))
                    expect(rect4.sources.first!.vectorCount).to(equal(44))

                    let rect5 = SCNRectangle(size: size, thickness: 0.01, radius: 0.01)
                    let rect6 = SCNRectangle(size: size, thickness: 0.01, radius: 0.05)
                    let rect7 = SCNRectangle(size: size, thickness: 0.01, radius: 0.1)
                    let rect8 = SCNRectangle(size: size, thickness: 0.01, radius: 0.33)
                    expect(rect5.sources.first!.vectorCount).to(equal(88))
                    expect(rect6.sources.first!.vectorCount).to(equal(88))
                    expect(rect7.sources.first!.vectorCount).to(equal(88))
                    expect(rect8.sources.first!.vectorCount).to(equal(88))

                    let rect9 = SCNRectangle(size: size, thickness: 0.075, radius: 0.01)
                    let rect10 = SCNRectangle(size: size, thickness: 0.075, radius: 0.05)
                    let rect11 = SCNRectangle(size: size, thickness: 0.075, radius: 0.1)
                    let rect12 = SCNRectangle(size: size, thickness: 0.075, radius: 0.33)
                    expect(rect9.sources.first!.vectorCount).to(equal(88))
                    expect(rect10.sources.first!.vectorCount).to(equal(88))
                    expect(rect11.sources.first!.vectorCount).to(equal(88))
                    expect(rect12.sources.first!.vectorCount).to(equal(88))

                    let rect13 = SCNRectangle(size: size, thickness: 0.3, radius: 0.01)
                    let rect14 = SCNRectangle(size: size, thickness: 0.3, radius: 0.05)
                    let rect15 = SCNRectangle(size: size, thickness: 0.3, radius: 0.1)
                    let rect16 = SCNRectangle(size: size, thickness: 0.3, radius: 0.33)
                    expect(rect13.sources.first!.vectorCount).to(equal(88))
                    expect(rect14.sources.first!.vectorCount).to(equal(88))
                    expect(rect15.sources.first!.vectorCount).to(equal(88))
                    expect(rect16.sources.first!.vectorCount).to(equal(88))
                }
            }
        }
    }
}
