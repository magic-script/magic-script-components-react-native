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

class UiNodeSpec: QuickSpec {
    override func spec() {
        describe("UiNode") {
            var node: UiNode!

            beforeEach {
                node = UiNode()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.topLeft))
                    expect(node.enabled).to(beTrue())
                    expect(node.canHaveFocus).to(beFalse())
                }
            }

            context("update properties") {
                it("should update 'alignment' prop") {
                    node = UiNode(props: ["alignment" : "bottom-right"])
                    expect(node.alignment).to(equal(Alignment.bottomRight))
                    node.update(["alignment" : "center-center"])
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                }

                it("should update 'enabled' prop") {
                    node = UiNode(props: ["enabled" : ""]) // enabled set to default value == true
                    expect(node.enabled).to(beTrue())
                    node.update(["enabled" : false])
                    expect(node.enabled).to(beFalse())
                }

                it("shouldn't update 'canHaveFocus' prop" /* it's hardcoded */) {
                    node = UiNode(props: ["canHaveFocus" : true])
                    expect(node.canHaveFocus).to(beFalse())
                    node.update(["canHaveFocus" : true])
                    expect(node.canHaveFocus).to(beFalse())
                }
            }

            context("when initialized") {
                context("for parent space") {
                    it("should calculate bounds") {
                        /* correctness of calculation should be checked in spec for derived classes */
                        expect(node.getBounds(parentSpace: true)).to(beCloseTo(CGRect.zero))
                    }
                }

                context("for own space") {
                    it("should calculate bounds") {
                        /* correctness of calculation should be checked in spec for derived classes */
                        expect(node.getBounds()).to(beCloseTo(CGRect.zero))
                    }
                }
            }

            context("when has focus") {
                context("when enters focus") {
                    /* call to onFocusChanged should be checked in spec for derived classes */
                }

                context("when leaves focus") {
                    /* call to onFocusChanged should be checked in spec for derived classes */
                }
            }
        }
    }
}
