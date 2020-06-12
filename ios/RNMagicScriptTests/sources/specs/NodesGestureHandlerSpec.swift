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
@testable import RNMagicScriptHostApplication
import SwiftyMocky

class NodesGestureHandlerSpec: QuickSpec {
    override func spec() {
        describe("NodesGestureHandler") {
            var sut: NodesGestureHandler!

            beforeEach {
                sut = NodesGestureHandler()
            }

            context("when handleNodeTap triggered") {
                context("when focused other node") {
                    it("should call leaveFocuse & onInputUnfocused for it") {
                        // prepare inner state
                        let prevUiNode = SimpleUiNode()
                        sut.handleNodeTap(prevUiNode)
                        expect(sut.focusedNode).to(equal(prevUiNode))

                        let uiNode = SimpleUiNode()
                        var result = false
                        sut.onInputUnfocused = {
                            result = true
                        }
                        sut.handleNodeTap(uiNode)
                        expect(result).toEventually(beTrue())
                        expect(sut.focusedNode).to(equal(uiNode))
                    }
                }

                context("when triggered for DataProviding node") {
                    it("should call provided onInputFocused") {
                        let dataProvider = SimpleDataProvider()
                        var result = false
                        sut.onInputFocused = { input in
                            expect(input as! SimpleDataProvider).to(equal(dataProvider))
                            result = true
                        }
                        sut.handleNodeTap(dataProvider)
                        expect(result).toEventually(beTrue())
                    }
                }

                context("when triggered for UiNode node") {
                    it("should call activate & enterFocus") {
                        let uiNode = SimpleUiNode()
                        sut.handleNodeTap(uiNode)
                        expect(uiNode.activateCalled).to(beTrue())
                        expect(uiNode.enterFocusCalled).to(beTrue())
                    }
                }
            }

            context("when handleNodeLongPress triggered") {
                context("when long press began") {
                    it("should call longPressStarted") {
                        let uiNode = SimpleLongPressedNode()

                        sut.handleNodeLongPress(uiNode, UIGestureRecognizer.State.began)
                        expect(sut.longPressedNode).to(equal(uiNode))
                        expect(uiNode.longPressStartedCalled).to(beTrue())
                    }
                }

                context("when long press ended or cancelled") {
                    it("should call longPressEnded") {
                        let uiNode = SimpleLongPressedNode()

                        // prepare inner state
                        sut.handleNodeLongPress(uiNode, UIGestureRecognizer.State.began)

                        sut.handleNodeLongPress(uiNode, UIGestureRecognizer.State.ended)
                        expect(sut.longPressedNode).to(beNil())
                        expect(uiNode.longPressEndedCalled).to(beTrue())

                        // prepare inner state
                        sut.handleNodeLongPress(uiNode, UIGestureRecognizer.State.began)

                        sut.handleNodeLongPress(uiNode, UIGestureRecognizer.State.cancelled)
                        expect(sut.longPressedNode).to(beNil())
                        expect(uiNode.longPressEndedCalled).to(beTrue())
                    }
                }

                context("when long press changed") {
                    it("should call/change nothing") {
                        let uiNode = SimpleLongPressedNode()

                        sut.handleNodeLongPress(uiNode, UIGestureRecognizer.State.changed)
                        expect(sut.longPressedNode).to(beNil())
                        expect(uiNode.longPressEndedCalled).to(beFalse())
                        expect(uiNode.longPressEndedCalled).to(beFalse())

                        sut.handleNodeLongPress(uiNode, UIGestureRecognizer.State.failed)
                        expect(sut.longPressedNode).to(beNil())
                        expect(uiNode.longPressEndedCalled).to(beFalse())
                        expect(uiNode.longPressEndedCalled).to(beFalse())
                    }
                }
            }
        }
    }
}

private class SimpleDataProvider: BaseNode, DataProviding { }
private class SimpleUiNode: UiNode {
    var activateCalled: Bool = false
    override func activate() {
        activateCalled = true
    }

    var enterFocusCalled: Bool = false
    override func enterFocus() {
        enterFocusCalled = true
    }
}
private class SimpleLongPressedNode: UiNode {
    var longPressStartedCalled: Bool = false
    override func longPressStarted() {
        longPressStartedCalled = true
    }
    var longPressEndedCalled: Bool = false
    override func longPressEnded() {
        longPressEndedCalled = true
    }
}

