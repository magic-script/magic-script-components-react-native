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

class UiColorPickerNodeSpec: QuickSpec {
    override func spec() {
        describe("UiColorPickerNode") {
            var node: UiColorPickerNode!

            beforeEach {
                node = UiColorPickerNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.startingColor).to(beCloseTo(UIColor.white))
                    expect(node.color).to(beCloseTo(UIColor.white))
                    expect(node.height).to(beCloseTo(0.0))
                }
            }

            context("initialization") {
                it("should throw exception if 'setupNode' has been called more than once") {
                    expect(node.setupNode()).to(throwAssertion())
                }
            }

            context("update properties") {
                it("should update 'startingColor' prop") {
                    let referenceColor = UIColor.red
                    node.update(["startingColor" : referenceColor.toArrayOfFloat])
                    expect(node.startingColor).to(beCloseTo(referenceColor))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'color' prop") {
                    let referenceColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["color" : referenceColor.toArrayOfFloat])
                    expect(node.color).to(beCloseTo(referenceColor))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    let referenceHeight = 0.3
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            context("focus") {
                it("should reload node when active state changed") {
                    expect(node.isLayoutNeeded).to(beFalse())
                    node.enterFocus()
                    expect(node.isLayoutNeeded).to(beFalse())
                    node.leaveFocus()
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            context("debug mode") {
                it("should set debug mode") {
                    let labelNode: LabelNode! = node.contentNode.childNodes.first as? LabelNode
                    expect(labelNode).notTo(beNil())
                    let referenceLabelNodeChildnodesCount: Int = labelNode.childNodes.count
                    node.setDebugMode(true)
                    expect(referenceLabelNodeChildnodesCount + 2).to(equal(labelNode.childNodes.count))
                }
            }

            context("ColorPickerDataProviding") {
                it("should get/set colorPickerValue") {
                    let referenceColor = UIColor.yellow
                    node.colorPickerValue = referenceColor
                    expect(node.color).to(beCloseTo(referenceColor))
                    expect(node.colorPickerValue).to(beCloseTo(referenceColor))
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should fire onColorChanged event") {
                    let referenceColor = UIColor.brown
                    node.color = referenceColor
                    waitUntil { done in
                        node.onColorChanged = { sender, selected in
                            let color = UIColor(red: selected[0], green: selected[1], blue: selected[2], alpha: selected[3])
                            expect(sender).to(beIdenticalTo(node))
                            expect(color).to(beCloseTo(referenceColor))
                            done()
                        }
                        node.colorChanged()
                    }
                }

                it("should fire onColorConfirmed event") {
                    let referenceColor = UIColor.orange
                    node.color = referenceColor
                    waitUntil { done in
                        node.onColorConfirmed = { sender, confirmed in
                            let color = UIColor(red: confirmed[0], green: confirmed[1], blue: confirmed[2], alpha: confirmed[3])
                            expect(sender).to(beIdenticalTo(node))
                            expect(color).to(beCloseTo(referenceColor))
                            done()
                        }
                        node.colorConfirmed()
                    }
                }

                it("should fire onColorCanceled event") {
                    let referenceColor = UIColor.magenta
                    node.color = referenceColor
                    waitUntil { done in
                        node.onColorCanceled = { sender, confirmed in
                            let color = UIColor(red: confirmed[0], green: confirmed[1], blue: confirmed[2], alpha: confirmed[3])
                            expect(sender).to(beIdenticalTo(node))
                            expect(color).to(beCloseTo(referenceColor))
                            done()
                        }
                        node.colorCanceled()
                    }
                }
            }
        }
    }
}
