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

import SceneKit

class PrismContextMenuSpec: QuickSpec {
    override func spec() {
        describe("PrismContextMenu") {
            var prismContextMenu: PrismContextMenu!
            var prismInteractorMock: PrismInteractingMock!

            beforeEach {
                prismContextMenu = PrismContextMenu()
                prismInteractorMock = PrismInteractingMock()
            }

            context("when initialized") {
                it("should have set default values") {
                    expect(prismContextMenu.linearLayout).toNot(beNil())
                    expect(prismContextMenu.titleNode).toNot(beNil())
                    expect(prismContextMenu.actionButton).toNot(beNil())
                }
            }

            context("when text set") {
                it("should set text for title node") {
                    let referenceText = "referenceText"
                    prismContextMenu.text = referenceText
                    expect(prismContextMenu.titleNode.text).to(equal(referenceText))
                }
            }

            context("when asked for size") {
                it("should return linear layout size") {
                    expect(prismContextMenu.getSize()).to(beCloseTo(prismContextMenu.linearLayout.getSize()))
                }
            }

            context("when prism interactor attached") {
                it("should set action for action button") {
                    expect(prismContextMenu.actionButton.onActivate).to(beNil())
                    prismContextMenu.prismInteractor = prismInteractorMock
                    expect(prismContextMenu.actionButton.onActivate).toNot(beNil())
                }

                context("when trigger action") {
                    it("should use related prism") {
                        let relatedPrism = SimplePrims()
                        prismContextMenu.prismInteractor = prismInteractorMock
                        prismContextMenu.prism = relatedPrism
                        prismContextMenu.actionButton.onActivate?(prismContextMenu.actionButton)
                        prismInteractorMock.verify(.toggleInteractions(for: .value(relatedPrism)))
                    }
                }

            }

            context("when debug mode set") {
                it("should set for all child nodes") {
                    prismContextMenu.setDebugMode(true)
                    expect(prismContextMenu.childNodes.count).to(equal(2))
                    prismContextMenu.setDebugMode(false)
                    expect(prismContextMenu.childNodes.count).to(equal(1))

                }
            }
        }
    }
}

private class SimplePrims: Prism { }
