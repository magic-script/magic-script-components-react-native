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

class TimePickerInputViewSpec: QuickSpec {
    override func spec() {
        describe("TimePickerInputView") {
            var inputView: TimePickerInputView!
            var simpleTimePickerDataProviding: SimpleTimePickerDataProviding!

            beforeEach() {
                inputView = TimePickerInputView()
                simpleTimePickerDataProviding = SimpleTimePickerDataProviding()
            }

            context("when 12 hours format set") {
                it("should set Locale to en_US") {
                    simpleTimePickerDataProviding.amPmFormat = true
                    inputView.pickerData = simpleTimePickerDataProviding
                    expect(inputView.dateTimePicker.locale).to(equal(Locale(identifier: "en_US")))
                }
            }

            context("when 24 hours format set") {
                it("should set Locale to en_GB") {
                    simpleTimePickerDataProviding.amPmFormat = false
                    inputView.pickerData = simpleTimePickerDataProviding
                    expect(inputView.dateTimePicker.locale).to(equal(Locale(identifier: "en_GB")))
                }
            }

            context("when user select value") {
                let referenceTime = Date.fromTime(string: "13:13:13", format: "HH:mm:ss")
                var datePicker: UIDatePicker!

                beforeEach {
                    inputView.pickerData = simpleTimePickerDataProviding
                    datePicker = UIDatePicker()
                    datePicker.date = referenceTime
                }

                it("should update pickerData") {
                    inputView.valueChanged(datePicker)
                    expect(simpleTimePickerDataProviding.timePickerValue).to(equal(referenceTime))
                }

                it("should notify with callback (timeChanged)") {
                    inputView.valueChanged(datePicker)
                    expect(simpleTimePickerDataProviding.timeChangedTriggered).to(beTrue())
                }
            }

            context("when user confirm selected value") {
                it("should notify with callback (timeConfirmed)") {
                    inputView.pickerData = simpleTimePickerDataProviding
                    inputView.doneButtonAction(UIButton())
                    expect(simpleTimePickerDataProviding.timeConfirmedTriggered).to(beTrue())
                }
            }

            context("when user cancel selecting") {
                it("should notify with callback (onFinish)") {
                    var result = false
                    inputView.onFinish = {
                        result = true
                    }
                    inputView.cancelButtonAction(UIButton())
                    expect(result).to(beTrue())
                }
            }
        }
    }
}

fileprivate class SimpleTimePickerDataProviding: TimePickerDataProviding {
    var amPmFormat: Bool = false
    var timePickerValue: Date = Date.fromTime(string: "13:13:13", format: "HH:mm:ss")

    func timeChanged() { timeChangedTriggered = true }
    func timeConfirmed() { timeConfirmedTriggered = true }

    var timeChangedTriggered = false
    var timeConfirmedTriggered = false
}
