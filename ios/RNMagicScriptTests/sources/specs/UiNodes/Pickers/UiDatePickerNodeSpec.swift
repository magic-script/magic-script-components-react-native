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

class UiDatePickerNodeSpec: QuickSpec {
    override func spec() {
        describe("UiDatePickerNode") {
            var node: UiDatePickerNode!
            let shortReferenceText: String = "Info text"

            beforeEach {
                node = UiDatePickerNode(props: [:])
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.label).to(beNil())
                    expect(node.labelSide).to(equal(.top))
                    let referenceDefaultDate = "MM/DD/YYYY"
                    expect(node.date).to(equal(referenceDefaultDate))
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.color).to(beCloseTo(referenceTextColor))
                    expect(node.dateFormat).to(equal(UiDatePickerNode.defaultInputDateFormat))
                    expect(node.defaultDate).to(equal(Date().toString(format: "MM/dd/YYYY")))
                    expect(node.yearMin).to(equal(-1))
                    expect(node.yearMax).to(equal(-1))
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
                    expect(node.isLayoutNeeded).to(beTrue())

                    expect(node.valueNode.textColor).to(beCloseTo(referenceTextColor))
                }

                context("when correct (in format MM/DD/YYYY) date provided") {
                    it("should update 'date' prop") {
                        let referenceDefaultDate = "06/13/1983"
                        node.update(["date": referenceDefaultDate])
                        expect(node.date).to(equal(referenceDefaultDate))
                    }
                }

                context("when incorrect dateFormat provided") {
                    it("should not update 'date' prop (should return dateFormat)") {
                        let referenceDefaultDate = "15/15/1515"
                        node.update(["date": referenceDefaultDate])
                        expect(node.date).to(equal("MM/DD/YYYY"))
                    }
                }

                context("when correct (from the list: MM/dd/YYYY, dd/MM/YYYY, DD/YYYY, MM/YYYY) dateFormat provided") {
                    it("should update 'dateFormat' prop") {
                        for referenceDateFormat in ["MM/dd/yyyy", "dd/MM/yyyy", "dd/yyyy", "MM/yyyy"] {
                            node.update(["dateFormat": referenceDateFormat])
                            expect(node.dateFormat).to(equal(referenceDateFormat))
                        }
                    }
                }

                context("when incorrect dateFormat provided") {
                    it("should not update 'dateFormat' prop") {
                        node.update(["dateFormat": "YYYY/DD/MM"])
                        expect(node.dateFormat).to(equal(UiDatePickerNode.defaultInputDateFormat))
                    }
                }

                context("when correct (in format MM/DD/YYYY) defaultDate provided") {
                    it("should update 'defaultDate' prop") {
                        let referenceDefaultDate = "06/13/1983"
                        node.update(["defaultDate": referenceDefaultDate])
                        expect(node.defaultDate).to(equal(referenceDefaultDate))
                    }
                }

                context("when incorrect defaultDate provided") {
                    it("should not update 'defaultDate' prop (default date should be today)") {
                        let referenceDefaultDate = Date().toString(format: "MM/dd/YYYY")
                        node.update(["defaultDate": "13/33/3033"])
                        expect(node.defaultDate).to(equal(referenceDefaultDate))
                    }
                }

                it("should update 'yearMin' prop") {
                    let referenceYear = 1300
                    node.update(["yearMin": referenceYear])
                    expect(node.yearMin).to(equal(referenceYear))
                }

                it("should update 'yearMax' prop") {
                    let referenceYear = 2500
                    node.update(["yearMax": referenceYear])
                    expect(node.yearMax).to(equal(referenceYear))
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
