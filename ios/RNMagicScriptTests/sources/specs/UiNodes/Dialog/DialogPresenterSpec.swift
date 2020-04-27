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

class DialogPresenterSpec: QuickSpec {
    override func spec() {
        describe("DialogPresenterSpec") {
            var node: DialogPresenter!
            var parentNode: UIView!

            beforeEach {
                parentNode = UIView()
                node = DialogPresenter(parentView: parentNode)
            }

            context("when new dialog requested to be presented") {
                it("should manage background view presentation") {
                    let simpleDialogDataProvider = SimpleDialogDataProvider()
                    node.present(simpleDialogDataProvider)
                    expect(node.backgroundView).toNot(beNil())
                }

                it("should present dialog on top of background") {
                    let simpleDialogDataProvider = SimpleDialogDataProvider()
                    node.present(simpleDialogDataProvider)
                    expect(node.backgroundView.subviews.count).to(equal(1))
                    let dialogView = node.backgroundView.subviews.first as? DialogView
                    expect(dialogView).toNot(beNil())
                }

                it("should store dialog reference internally") {
                    let simpleDialogDataProvider = SimpleDialogDataProvider()
                    node.present(simpleDialogDataProvider)
                    expect(node.presentedDialogs.count).to(equal(1))
                    let dialogView = node.presentedDialogs["fakeID"] as? DialogView
                    expect(dialogView).toNot(beNil())
                }
            }

            context("when dialog requested to be dismissed") {
                it("should manage background view presentation (remove if it was last dialog)") {
                    let simpleDialogDataProvider = SimpleDialogDataProvider()
                    node.present(simpleDialogDataProvider)
                    expect(node.backgroundView).toNot(beNil())

                    node.dismiss(simpleDialogDataProvider)
                    expect(node.backgroundView).to(beNil())
                }

                it("should remove dialog internally") {
                    let simpleDialogDataProvider = SimpleDialogDataProvider()
                    node.present(simpleDialogDataProvider)
                    expect(node.presentedDialogs.count).to(equal(1))

                    node.dismiss(simpleDialogDataProvider)
                    expect(node.presentedDialogs.count).to(equal(0))
                }
            }
        }
    }
}

fileprivate class SimpleDialogDataProvider: DialogDataProviding {
    var id: String {
        return "fakeID"
    }
    var dialogType: DialogType = .dualAction
    var buttonType: ButtonType = .textWithIcon
    var title: String?
    var message: String?
    var confirmText: String?
    var confirmIcon: SystemIcon?
    var cancelText: String?
    var cancelIcon: SystemIcon?
    var scrolling: Bool = false
    var expireTime: CGFloat = 0.0
    func dialogConfirmed() { }
    func dialogCanceled() { }
    func dialogTimeExpired() { }
}
