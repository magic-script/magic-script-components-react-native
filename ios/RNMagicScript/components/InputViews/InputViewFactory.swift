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

import UIKit

class InputViewFactory {
    static func createView(for input: DataProviding, onFinishEditing: (() -> Void)?) -> UIView? {
        if let sliderInput = input as? SliderDataProviding {
            return InputViewFactory.createView(for: sliderInput, onFinishEditing: onFinishEditing)
        }

        if let datePicker = input as? DatePickerDataProviding {
            return InputViewFactory.createView(for: datePicker, onFinish: onFinishEditing)
        }

        if let timePicker = input as? TimePickerDataProviding {
            return InputViewFactory.createView(for: timePicker, onFinish: onFinishEditing)
        }

        if let colorPicker = input as? ColorPickerDataProviding {
            return InputViewFactory.createView(for: colorPicker, onFinish: onFinishEditing)
        }

        return nil
    }

    static func createView(for input: SliderDataProviding, onFinishEditing: (() -> Void)?) -> UIView? {
        let rect = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: 160)
        let sliderInputView = SliderInputView(frame: rect)
        sliderInputView.input = input
        sliderInputView.onFinishEditing = onFinishEditing
        return sliderInputView
    }

    static func createView(for input: DatePickerDataProviding, onFinish: (() -> Void)?) -> UIView? {
        let rect = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: 160)
        let dateTimePickerInputView = DatePickerInputView(frame: rect)
        dateTimePickerInputView.onFinish = onFinish
        dateTimePickerInputView.pickerData = input
        return dateTimePickerInputView
    }

    static func createView(for input: TimePickerDataProviding, onFinish: (() -> Void)?) -> UIView? {
        let rect = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: 160)
        let dateTimePickerInputView = TimePickerInputView(frame: rect)
        dateTimePickerInputView.onFinish = onFinish
        dateTimePickerInputView.pickerData = input
        return dateTimePickerInputView
    }

    static func createView(for input: ColorPickerDataProviding, onFinish: (() -> Void)?) -> UIView? {
        let rect = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: 220.0)
        let colorPickerInputView = ColorPickerInputView(frame: rect)
        colorPickerInputView.pickerData = input
        colorPickerInputView.onFinish = onFinish
        return colorPickerInputView
    }
}
