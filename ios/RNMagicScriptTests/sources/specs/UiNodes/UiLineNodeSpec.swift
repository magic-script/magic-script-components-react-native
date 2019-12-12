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

class UiLineNodeSpec: QuickSpec {
    override func spec() {
        describe("UiLineNode") {
            var node: UiLineNode!

            beforeEach {
                node = UiLineNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.points.count).to(equal(0))
                }
            }

            context("update properties") {
                it("should update 'points' prop") {
                    let referencePoints = [[1.0, 1.0, 1.0], [2.0, 2.0, 2.0], [3.0, 3.0, 3.0]]
                    node.update(["points" : referencePoints])
                    expect(node.points.count).to(equal(3))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should clear 'points' if wrong data") {
                    let referencePoints = [["a", "b", "c"], ["d", "e", "f"], ["g", "h", "i"]]
                    node.update(["points" : referencePoints])
                    expect(node.points.count).to(equal(0))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.layoutIfNeeded()
                    let lineNode = node.contentNode.childNodes[0]
                    expect(lineNode.geometry).to(beNil())
                }
            }

            context("when asked for size") {
                it("should calculate it") {
                    node = UiLineNode(props: ["points": [[0.0, 0.0, 2.0], [1.0, 1.0, 3.0]]])
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 1.0, height: 1.0)))
                    let referencePoints = [[0.0, 0.0, 1.0], [1.0, 1.0, 2.0], [-0.5, -0.5, 3.0]]
                    node.update(["points" : referencePoints])
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 1.5, height: 1.5)))
                }

                context("when no points set") {
                    it("should return zero") {
                        expect(node.getSize()).to(beCloseTo(CGSize.zero))
                    }
                }
            }

            it("updateLayout should set color") {
                node = UiLineNode(props: ["points": [[1.0, 1.0, 1.0], [2.0, 2.0, 2.0], [3.0, 3.0, 3.0]]])
                expect(node.contentNode.childNodes.count).to(equal(1))
                node.layoutIfNeeded()
                let linesNode = node.contentNode.childNodes.first!
                expect(linesNode.geometry?.sources.count).to(equal(1))
                expect(linesNode.geometry?.elements.count).to(equal(1))
            }
        }
    }
}
