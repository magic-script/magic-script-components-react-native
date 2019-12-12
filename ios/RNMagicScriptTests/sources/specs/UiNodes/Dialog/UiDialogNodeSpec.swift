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

class UiDialogNodeSpec: QuickSpec {
    override func spec() {
        describe("UiDialogNode") {
            var node: UiDialogNode!
            let shortReferenceText: String = "Info text"
            let systemIconReferenceText: String = "chevron-down"
            let systemIconReference: SystemIcon = SystemIcon(systemIconReferenceText)

            beforeEach {
                node = UiDialogNode()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.title).to(beNil())
                    expect(node.message).to(beNil())
                    expect(node.confirmText).to(beNil())
                    expect(node.confirmIcon).to(beNil())
                    expect(node.cancelText).to(beNil())
                    expect(node.cancelIcon).to(beNil())
                    expect(node.scrolling).to(beFalse())
                    expect(node.expireTime).to(beCloseTo(0.0))
                }
            }

            context("update properties") {
                it("should update 'text' prop") {
                    node.update(["title" : shortReferenceText])
                    expect(node.title).to(equal(shortReferenceText))
                }

                it("should update 'message' prop") {
                    node.update(["message" : shortReferenceText])
                    expect(node.message).to(equal(shortReferenceText))
                }

                it("should update 'confirmText' prop") {
                    node.update(["confirmText" : shortReferenceText])
                    expect(node.confirmText).to(equal(shortReferenceText))
                }

                it("should update 'confirmIcon' prop") {
                    node.update(["confirmIcon" : systemIconReferenceText])
                    expect(node.confirmIcon).to(equal(systemIconReference))
                }

                it("should update 'cancelText' prop") {
                    node.update(["cancelText" : shortReferenceText])
                    expect(node.cancelText).to(equal(shortReferenceText))
                }

                it("should update 'cancelIcon' prop") {
                    node.update(["cancelIcon" : systemIconReferenceText])
                    expect(node.cancelIcon).to(equal(systemIconReference))
                }

                it("should update 'scrolling' prop") {
                    node.update(["scrolling" : true])
                    expect(node.scrolling).to(beTrue())
                }

                it("should update 'expireTime' prop") {
                    let referenceExpireTime = 7.0
                    node.update(["expireTime" : referenceExpireTime])
                    expect(node.expireTime).to(beCloseTo(referenceExpireTime))
                }
            }

            context("when dialog confirmed") {
                it("should trigger onDialogConfirmed event") {
                    var result = false
                    node.onDialogConfirmed = { dialogNode in
                        expect(dialogNode).to(beIdenticalTo(node))
                        result = true
                    }
                    node.dialogConfirmed()
                    expect(result).toEventually(beTrue())
                }
            }

            context("when dialog canceled") {
                it("should trigger onDialogCanceled event") {
                    var result = false
                    node.onDialogCanceled = { dialogNode in
                        expect(dialogNode).to(beIdenticalTo(node))
                        result = true
                    }
                    node.dialogCanceled()
                    expect(result).toEventually(beTrue())
                }
            }

            context("when time expired for dialog") {
                it("should trigger onDialogTimeExpired event") {
                    var result = false
                    node.onDialogTimeExpired = { dialogNode in
                        expect(dialogNode).to(beIdenticalTo(node))
                        result = true
                    }
                    node.dialogTimeExpired()
                    expect(result).toEventually(beTrue())
                }
            }
        }
    }
}
