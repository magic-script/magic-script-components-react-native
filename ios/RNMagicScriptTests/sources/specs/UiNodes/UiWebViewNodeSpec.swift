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
import SceneKit
import AVKit
@testable import RNMagicScriptHostApplication

class UiWebViewNodeSpec: QuickSpec {
    override func spec() {
        describe("UiWebViewNode") {
            var node: UiWebViewNode!

            beforeEach {
                node = UiWebViewNode()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.url).to(beNil())
                    expect(node.width).to(beCloseTo(0))
                    expect(node.height).to(beCloseTo(0))
                    expect(node.action).to(equal(WebViewAction.reload))
                    expect(node.scrollBy).to(beCloseTo(CGSize.zero))
                }
            }

            context("initialization") {
                it("should throw exception if 'setupNode' has been called more than once") {
                    expect(node.setupNode()).to(throwAssertion())
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

                it("should update 'url' prop") {
                    let referenceUrl = "https://www.magicleap.com"
                    node.update(["url" : referenceUrl])
                    expect(node.url).to(equal(URL(string: referenceUrl)!))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'width' prop") {
                    let referenceWidth = 1.1
                    node.update(["width" : referenceWidth])
                    expect(node.width).to(beCloseTo(referenceWidth))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    let referenceHeight = 0.7
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'action' prop") {
                    let referenceActions: [WebViewAction] = [.back, .forward, .reload]
                    for action in referenceActions {
                        node.update(["action" : action.rawValue])
                        expect(node.action).to(equal(action))
                        expect(node.isLayoutNeeded).to(beFalse())
                    }
                }

                it("should update 'scrollBy' prop") {
                    let referenceScrollBy = CGSize(width: 1.2, height: 2.3)
                    node.update(["scrollBy" : [referenceScrollBy.width, referenceScrollBy.height]])
                    expect(node.scrollBy).to(beCloseTo(referenceScrollBy))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            context("focus") {
                it("should canHaveFocus if url is defined") {
                    expect(node.canHaveFocus).to(beFalse())
                    node.url = URL(string: "https://www.magicleap.com")!
                    expect(node.canHaveFocus).to(beTrue())
                }

                it("should not canHaveFocus if enabled is set to false") {
                    node.url = URL(string: "https://www.magicleap.com")!
                    expect(node.canHaveFocus).to(beTrue())
                    node.enabled = false
                    expect(node.canHaveFocus).to(beFalse())
                }

                it("should present SFSafariViewController when component gets focus") {
                    let rootVC: UIViewController! = UIApplication.shared.keyWindow?.rootViewController!
                    expect(rootVC).notTo(beNil())
                    expect(rootVC?.presentedViewController).to(beNil())
                    node.enterFocus()
                    expect(rootVC?.presentedViewController).to(beNil())

                    node.url = URL(string: "https://www.magicleap.com")!
                    node.enterFocus()
                    expect(rootVC?.presentedViewController).notTo(beNil())
                }
            }
        }
    }
}
