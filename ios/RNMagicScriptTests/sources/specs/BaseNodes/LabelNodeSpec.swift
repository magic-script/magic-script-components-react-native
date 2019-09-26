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

class LabelNodeSpec: QuickSpec {
    override func spec() {
        describe("LabelNode") {
            var node: LabelNode!

            beforeEach {
                node = LabelNode()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.text).to(beNil())
                    expect(node.textAlignment).to(equal(HorizontalTextAlignment.left))
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.textSize).to(beCloseTo(0.0))
                    expect(node.defaultTextSize).to(beCloseTo(0.015)) // defined as defaultTextSizeInMeters: CGFloat = 0.015
                    expect(node.boundsSize).to(beCloseTo(CGSize.zero))
                    expect(node.multiline).to(beFalse())
                }
            }

            context("when initialized") {
                it("should contain empty text") {
                    let childsNode = node.childNodes
                    expect(childsNode.count).to(equal(1))
                }
            }

            context("when asked for size") {
                context("when text empty") {
                    it("should return zero") {
                        expect(node.getSize()).to(beCloseTo(CGSize.zero))
                    }
                }

                context("when text provided") {
                    it("should calculate it") {
                        node.text = "Label text"
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 0.051, height: 0.0144)))
                    }
                }

                context("when boundsSize set") {
                    it("should return boundsSize") {
                        node.boundsSize = CGSize(width: 3.14, height: 3.14)
                        expect(node.getSize()).to(beCloseTo(CGSize(width: 3.14, height: 3.14)))
                    }
                }
            }

            context("when reloads") {
                it("should recreate childe node") {
                    node.text = "Label text"
                    node.boundsSize = CGSize(width: 0.514, height: 0.14)
                    node.reload()
                    let childNodeBeforeUpdate = node.childNodes.first!
                    node.text = "New Label text"
                    node.reload()
                    let childNodeAfterUpdate = node.childNodes.first!
                    expect(childNodeAfterUpdate).toNot(equal(childNodeBeforeUpdate))
                    expect(childNodeAfterUpdate.scale).to(beCloseTo(childNodeBeforeUpdate.scale))
                    expect(childNodeAfterUpdate.position).to(beCloseTo(childNodeBeforeUpdate.position))
                }
            }
        }
    }
}
