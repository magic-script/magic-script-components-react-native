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

class UiLayoutNodeSpec: QuickSpec {
    override func spec() {
        describe("UiLayoutNode") {
            var node: UiLayoutNode!

            beforeEach {
                node = UiLayoutNode(props: [:])
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.width).to(beCloseTo(0))
                    expect(node.height).to(beCloseTo(0))
                }
            }

            context("update properties") {
                it("should not update 'width' prop") {
                    let referenceWidth = 1.2
                    node.update(["width" : referenceWidth])
                    expect(node.width).to(beCloseTo(referenceWidth))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should not update 'height' prop") {
                    let referenceHeight = 3.4
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }

            context("getSize") {
                it("should return current size of layout") {
                    let referenceSize = CGSize(width: 0.4, height: 0.9)
                    node.update(["width": referenceSize.width, "height" : referenceSize.height])
                    node.layoutIfNeeded()
                    expect(node.getSize()).to(beCloseTo(referenceSize))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }
        }
    }
}
