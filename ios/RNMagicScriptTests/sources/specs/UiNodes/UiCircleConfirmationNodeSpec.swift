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
@testable import RNMagicScriptHostApplication

import SceneKit

class UiCircleConfirmationNodeSpec: QuickSpec {
    override func spec() {
        describe("UiCircleConfirmationNode") {
            var node: UiCircleConfirmationNode!

            beforeEach {
                node = UiCircleConfirmationNode(props: [:])
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.height).to(beCloseTo(0.0))
                    expect(node.canBeLongPressed).to(beTrue())
                }

                it("should have set default size") {
                    expect(node.getSize()).to(beCloseTo(CGSize(width: UiCircleConfirmationNode.defaultSize, height: UiCircleConfirmationNode.defaultSize)))
                }
            }

            context("update properties") {
                it("should not update 'alignment' prop") {
                    let referenceAlignment = Alignment.bottomRight
                    node.update(["alignment" : referenceAlignment.rawValue])
                    expect(node.alignment).notTo(equal(referenceAlignment))
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'height' prop") {
                    let referenceHeight = 0.6
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.getSize()).to(beCloseTo(CGSize(width: referenceHeight, height: referenceHeight)))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should not update 'canBeLongPressed' prop") {
                    node.update(["canBeLongPressed" : false])
                    expect(node.canBeLongPressed).toNot(beFalse())
                }
            }

            context("when long press gesture") {
                context("started") {
                    it("should trigger event (value updated)") {
                        var result = false
                        var reportedNode: UiCircleConfirmationNode?
                        node.onConfirmationUpdated = { circleConfirmationNode, value in
                            result = true
                            reportedNode = circleConfirmationNode
                        }
                        node.longPressStarted()
                        expect(result).toEventually(beTrue())
                        expect(reportedNode).toEventually(beIdenticalTo(node))
                    }
                }

                context("when max value reached") {
                    it("should trigger event (completed)") {
                        var result = false
                        var reportedNode: UiCircleConfirmationNode?
                        node.onConfirmationCompleted = { circleConfirmationNode in
                            result = true
                            reportedNode = circleConfirmationNode
                        }
                        node.longPressStarted()
                        for _ in 0...20 { node.expirationTimer?.fire() }
                        expect(result).toEventually(beTrue())
                        expect(reportedNode).toEventually(beIdenticalTo(node))
                    }
                }

                context("stopped") {
                    it("should trigger event (canceled)") {
                        var result = false
                        var reportedNode: UiCircleConfirmationNode?
                        node.onConfirmationCanceled = { circleConfirmationNode in
                            result = true
                            reportedNode = circleConfirmationNode
                        }
                        node.longPressEnded()
                        expect(result).toEventually(beTrue())
                        expect(reportedNode).toEventually(beIdenticalTo(node))
                    }
                }
            }
        }
    }
}
