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

class InputViewFactorySpec: QuickSpec {
    override func spec() {
        describe("InputViewFactory") {
            context("when parent view is kind of DatePickerDataProviding") {
                it("should return correct input view") {
                    let datePickerDataProvider: DataProviding = SimpleDatePickerDataProvider()
                    let view = InputViewFactory.createView(for: datePickerDataProvider, onFinishEditing: nil)
                    expect(view).toNot(beNil())
                }
            }

            context("when parent view is kind of DatePickerDataProviding") {
                it("should return correct input view") {
                    let timePickerDataProvider: DataProviding = SimpleTimePickerDataProvider()
                    let view = InputViewFactory.createView(for: timePickerDataProvider, onFinishEditing: nil)
                    expect(view).toNot(beNil())
                }
            }

            context("when parent view is kind of ColorPickerDataProviding") {
                it("should return correct input view") {
                    let colorPickerDataProvider: DataProviding = SimpleColorPickerDataProvider()
                    let view = InputViewFactory.createView(for: colorPickerDataProvider, onFinishEditing: nil)
                    expect(view).toNot(beNil())
                }
            }

            context("when parent view is kind of SliderDataProviding") {
                it("should return correct input view") {
                    let sliderDataProvider: DataProviding = SimpleSliderDataProvider()
                    let view = InputViewFactory.createView(for: sliderDataProvider, onFinishEditing: nil)
                    expect(view).toNot(beNil())
                }
            }

            context("when parent view isn't kind of SliderDataProviding") {
                it("should return nil") {
                    let dataProvider = SimpleDataProvider()
                    let view = InputViewFactory.createView(for: dataProvider, onFinishEditing: nil)
                    expect(view).to(beNil())
                }
            }
        }
    }
}

fileprivate struct SimpleDataProvider: DataProviding { }
fileprivate struct SimpleSliderDataProvider: SliderDataProviding {
    var sliderValue: CGFloat = 0.0
    var min: CGFloat = 0.0
    var max: CGFloat = 1.0
}
fileprivate struct SimpleDatePickerDataProvider: DatePickerDataProviding {
    var datePickerValue: Date = Date()
    func dateChanged() { }
    func dateConfirmed() { }
}
fileprivate struct SimpleTimePickerDataProvider: TimePickerDataProviding {
    var amPmFormat: Bool = true
    var timePickerValue: Date = Date()
    func timeChanged() { }
    func timeConfirmed() { }
}
fileprivate struct SimpleColorPickerDataProvider: ColorPickerDataProviding {
    var colorPickerValue: UIColor = UIColor.white
    func colorChanged() { }
    func colorConfirmed() { }
    func colorCanceled() { }
}
