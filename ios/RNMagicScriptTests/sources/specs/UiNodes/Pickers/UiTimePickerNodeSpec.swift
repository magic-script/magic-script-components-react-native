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

class UiTimePickerNodeSpec: QuickSpec {
    override func spec() {
        describe("UiTimePickerNode") {
            var node: UiTimePickerNode!
            let shortReferenceText: String = "Info text"

            beforeEach {
                node = UiTimePickerNode(props: [:])
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.label).to(beNil())
                    expect(node.labelSide).to(equal(.top))
                    let referenceDefaultTime = "HH:MM:SS"
                    expect(node.time).to(equal(referenceDefaultTime))
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.color).to(beCloseTo(referenceTextColor))
                    expect(node.timeFormat).to(equal(UiTimePickerNode.defaultInputTimeFormat))
                    expect(node.defaultTime).to(equal(Date().toString(format: "HH:mm:ss")))
                }
            }

            context("update properties") {
                it("should update 'label' prop") {
                    node.update(["label": shortReferenceText])
                    expect(node.label).to(equal(shortReferenceText))
                }

                it("should update 'labelSide' prop") {
                    node.update(["labelSide": "right"])
                    expect(node.labelSide).to(equal(Side.right))
                }

                it("should update 'color' prop") {
                    let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["color" : referenceTextColor.toArrayOfFloat])
                    expect(node.color).to(beCloseTo(referenceTextColor))
                    expect(node.isLayoutNeeded).to(beFalse())

                    expect(node.valueNode.textColor).to(beCloseTo(referenceTextColor))
                }

                context("when correct (in format HH:mm:ss) time provided") {
                    it("should update 'time' prop") {
                        let referenceDefaultDate = "12:12:12"
                        node.update(["time": referenceDefaultDate])
                        expect(node.time).to(equal(referenceDefaultDate))
                    }
                }

                context("when incorrect time provided (wrong format)") {
                    it("should not update 'time' prop (should return timeFormat)") {
                        let referenceDefaultDate = "35:75:95"
                        node.update(["time": referenceDefaultDate])
                        expect(node.time).to(equal("HH:MM:SS"))
                    }
                }

                context("when correct (from the list: HH:mm:ss p, HH:mm p, HH:ss p, HH:mm:ss, HH:mm, HH:ss, mm:ss) timeFormat provided") {
                    it("should update 'timeFormat' prop") {
                        for referenceTimeFormat in ["HH:mm:ss p", "HH:mm p", "HH:ss p", "HH:mm:ss", "HH:mm", "HH:ss", "mm:ss"] {
                            node.update(["timeFormat": referenceTimeFormat])
                            expect(node.timeFormat).to(equal(referenceTimeFormat))
                        }
                    }
                }

                context("when incorrect timeFormat provided") {
                    it("should not update 'timeFormat' prop") {
                        node.update(["timeFormat": "ss:mm:HH"])
                        expect(node.timeFormat).to(equal(UiTimePickerNode.defaultInputTimeFormat))
                    }
                }

                context("when correct (in format HH:mm:ss) defaultTime provided") {
                    it("should update 'defaultTime' prop") {
                        let referenceDefaultTime = "12:14:15"
                        node.update(["defaultTime": referenceDefaultTime])
                        expect(node.defaultTime).to(equal(referenceDefaultTime))
                    }
                }

                context("when incorrect defaultTime provided") {
                    it("should not update 'defaultTime' prop (default time should be now)") {
                        let referenceDefaultTime = Date().toString(format: "HH:mm:ss")
                        node.update(["defaultTime": "24:25:26"])
                        expect(node.defaultTime).to(equal(referenceDefaultTime))
                    }
                }
            }

            it("should reload node when active state changed") {
                expect(node.isLayoutNeeded).to(beFalse())
                node.enterFocus()
                expect(node.isLayoutNeeded).to(beFalse())
                node.leaveFocus()
                expect(node.isLayoutNeeded).to(beFalse())
            }
        }
    }
}
