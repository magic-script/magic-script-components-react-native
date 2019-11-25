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
@testable import RNMagicScriptHostApplication

class DatePickerInputViewSpec: QuickSpec {
    override func spec() {
        describe("DatePickerInputView") {
            var inputView: DatePickerInputView!
            var simpleDatePickerDataProviding: SimpleDatePickerDataProviding!

            beforeEach() {
                inputView = DatePickerInputView()
                simpleDatePickerDataProviding = SimpleDatePickerDataProviding()
                inputView.pickerData = simpleDatePickerDataProviding
            }

            context("when user select value") {
                let referenceDate = Date.fromTime(string: "13/06/1983", format: "dd/MM/yyyy")
                var datePicker: UIDatePicker!

                beforeEach {
                    datePicker = UIDatePicker()
                    datePicker.date = referenceDate
                }

                it("should update pickerData") {
                    inputView.valueChanged(datePicker)
                    expect(simpleDatePickerDataProviding.datePickerValue).to(equal(referenceDate))
                }

                it("should notify with callback (dateChanged)") {
                    inputView.valueChanged(datePicker)
                    expect(simpleDatePickerDataProviding.dateChangedTriggered).to(beTrue())
                }
            }

            context("when user confirm selected value") {
                it("should notify with callback (dateConfirmed)") {
                    inputView.doneButtonAction(UIButton())
                    expect(simpleDatePickerDataProviding.dateConfirmedTriggered).to(beTrue())
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

fileprivate class SimpleDatePickerDataProviding: DatePickerDataProviding {
    var datePickerValue: Date = Date.fromTime(string: "13/06/1983", format: "dd/MM/yyyy")

    func dateChanged() { dateChangedTriggered = true }

    func dateConfirmed() { dateConfirmedTriggered = true }

    var dateChangedTriggered = false
    var dateConfirmedTriggered = false
}
