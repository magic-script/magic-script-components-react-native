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
import SwiftyMocky
@testable import RNMagicScriptHostApplication

import SceneKit

class UiCircleConfirmationNodeSpec: QuickSpec {
    override func spec() {
        describe("UiCircleConfirmationNode") {
            var node: UiCircleConfirmationNode!
            var nodeAnimatorMock: NodeAnimatingMock!

            beforeEach {
                node = UiCircleConfirmationNode()
                nodeAnimatorMock = NodeAnimatingMock()
                node.nodeAnimator = nodeAnimatorMock
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.radius).to(beCloseTo(0.0))
                    expect(node.canBeLongPressed).to(beTrue())
                }

                it("should have set default size") {
                    let defaultSize = 2 * UiCircleConfirmationNode.defaultRadius
                    expect(node.getSize()).to(beCloseTo(CGSize(width: defaultSize, height: defaultSize)))
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

                it("should update 'radius' prop") {
                    let referenceRadius = 0.6
                    node.update(["radius" : referenceRadius])
                    expect(node.radius).to(beCloseTo(referenceRadius))
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 2 * referenceRadius, height: 2 * referenceRadius)))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should not update 'canBeLongPressed' prop") {
                    node.update(["canBeLongPressed" : false])
                    expect(node.canBeLongPressed).toNot(beFalse())
                }
            }

            context("updateLayout") {
                it("should update size when 'radius' prop has changed") {
                    let referenceRadius: CGFloat = 0.6
                    node.radius = referenceRadius
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()
                    expect(node.isLayoutNeeded).to(beFalse())

                    let circleNode = node.contentNode.childNodes.first!
                    expect(circleNode.scale).to(beCloseTo(SCNVector3(2 * referenceRadius, 2 * referenceRadius, 1)))
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
                        Perform(nodeAnimatorMock, .startAnimation(duration: .value(2.0), update: .any, perform: { (interval, callback) in
                            callback(node, 2.0)
                        }))
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
                        Perform(nodeAnimatorMock, .startAnimation(duration: .value(2.0), update: .any, perform: { (interval, callback) in
                            callback(node, 2.0)
                        }))
                        node.longPressStarted()
                        expect(result).toEventually(beTrue())
                        expect(reportedNode).toEventually(beIdenticalTo(node))
                    }

                    it("should not trigger event (canceled)") {
                        var result = false
                        var reportedNode: UiCircleConfirmationNode?
                        node.onConfirmationCanceled = { circleConfirmationNode in
                            result = true
                            reportedNode = circleConfirmationNode
                        }
                        node.longPressStarted()
                        node.longPressEnded()
                        expect(result).toEventually(beFalse())
                        expect(reportedNode).toEventually(beNil())
                    }
                }

                context("stopped") {
                    it("should trigger event (canceled) - duration 0.0") {
                        var result = false
                        var reportedNode: UiCircleConfirmationNode?
                        node.onConfirmationCanceled = { circleConfirmationNode in
                            result = true
                            reportedNode = circleConfirmationNode
                        }
                        Perform(nodeAnimatorMock, .startAnimation(duration: .value(0.0), update: .any, perform: { (interval, callback) in
                            callback(node, 0.0)
                        }))
                        node.longPressEnded()
                        expect(result).toEventually(beTrue())
                        expect(reportedNode).toEventually(beIdenticalTo(node))
                    }

                    it("should trigger event (canceled) - duration > 0.0") {
                        var result = false
                        var reportedNode: UiCircleConfirmationNode?
                        node.onConfirmationCanceled = { circleConfirmationNode in
                            result = true
                            reportedNode = circleConfirmationNode
                        }
                        Perform(nodeAnimatorMock, .startAnimation(duration: .value(2.0), update: .any, perform: { (interval, callback) in
                            callback(node, 0.5)
                        }))
                        Perform(nodeAnimatorMock, .startAnimation(duration: .value(0.125), update: .any, perform: { (interval, callback) in
                            callback(node, 0.5)
                        }))
                        node.longPressStarted()
                        node.longPressEnded()
                        expect(result).toEventually(beTrue())
                        expect(reportedNode).toEventually(beIdenticalTo(node))
                    }
                }
            }
        }
    }
}
