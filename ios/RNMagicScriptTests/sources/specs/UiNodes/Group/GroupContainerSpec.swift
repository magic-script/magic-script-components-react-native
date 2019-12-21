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

class GroupContainerSpec: QuickSpec {
    override func spec() {
        describe("GroupContainer") {
            var group: GroupContainer!

            beforeEach {
                group = GroupContainer()
            }

            context("initialization") {
                it("should be empty just after initialization") {
                    expect(group.container.childNodes.count).to(equal(0))
                    expect(group.getSize()).to(beCloseTo(CGSize.zero))
                    expect(group.getBounds()).to(beCloseTo(CGRect.zero))
                }

                it("hitTest should return nil for invalid and recalculated group") {
                    let rays = [
                        Ray(begin: SCNVector3(0, 0, -1), direction: SCNVector3(0, 0, 1), length: 3),
                        Ray(begin: SCNVector3(0, 0, 1), direction: SCNVector3(0, 0, -1), length: 3),
                        Ray(begin: SCNVector3(0.1, 0.1, -2), direction: SCNVector3(0, 0, 1), length: 3),
                        Ray(begin: SCNVector3(-0.1, 0.1, -2), direction: SCNVector3(0, 0, 1), length: 3),
                        Ray(begin: SCNVector3(0.1, -0.1, -2), direction: SCNVector3(0, 0, 1), length: 3),
                        Ray(begin: SCNVector3(-0.1, -0.1, -2), direction: SCNVector3(0, 0, 1), length: 3),
                    ]
                    for ray in rays {
                        expect(group.hitTest(ray: ray)).to(beNil())
                    }

                    group.recalculateIfNeeded()
                    for ray in rays {
                        expect(group.hitTest(ray: ray)).to(beNil())
                    }
                }
            }
        }
    }
}
