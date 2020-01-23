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

class TimePickerInputView: UIView {
    var pickerData: TimePickerDataProviding? {
        didSet {
            if let pickerData = pickerData {
                dateTimePicker.locale = pickerData.amPmFormat ? Locale(identifier: "en_US") : Locale(identifier: "en_GB")
            } else {
                dateTimePicker.locale = Locale(identifier: "en_GB")
            }
        }
    }

    var onFinish: (() -> (Void))?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView()
    }

    fileprivate(set) var dateTimePicker: UIDatePicker!

    fileprivate func setupView() {
        backgroundColor = .white

        let doneButton: UIButton = UIButton(type: .system)
        doneButton.setTitle("Done", for: UIControl.State.normal)
        doneButton.addTarget(self, action: #selector(doneButtonAction(_:)), for: .touchUpInside)
        doneButton.translatesAutoresizingMaskIntoConstraints = false
        addSubview(doneButton)

        let margin: CGFloat = 8
        NSLayoutConstraint.activate([
            doneButton.rightAnchor.constraint(equalTo: safeAreaLayoutGuide.rightAnchor, constant: -margin),
            doneButton.heightAnchor.constraint(equalToConstant: 20),
            doneButton.widthAnchor.constraint(equalToConstant: 100),
            doneButton.topAnchor.constraint(equalTo: topAnchor, constant: margin),
        ])


        let cancelButton: UIButton = UIButton(type: .system)
        cancelButton.setTitle("Cancel", for: UIControl.State.normal)
        cancelButton.addTarget(self, action: #selector(cancelButtonAction(_:)), for: .touchUpInside)
        cancelButton.translatesAutoresizingMaskIntoConstraints = false
        addSubview(cancelButton)

        NSLayoutConstraint.activate([
            cancelButton.leftAnchor.constraint(equalTo: safeAreaLayoutGuide.leftAnchor, constant: margin),
            cancelButton.heightAnchor.constraint(equalToConstant: 20),
            cancelButton.widthAnchor.constraint(equalToConstant: 100),
            cancelButton.topAnchor.constraint(equalTo: topAnchor, constant: margin),
        ])

        dateTimePicker = UIDatePicker()
        dateTimePicker.backgroundColor = .white
        dateTimePicker.datePickerMode = .time
        dateTimePicker.timeZone = TimeZone(abbreviation: "UTC") // to avoid issue with different timezones
        dateTimePicker.translatesAutoresizingMaskIntoConstraints = false
        addSubview(dateTimePicker)

        NSLayoutConstraint.activate([
            dateTimePicker.leftAnchor.constraint(equalTo: safeAreaLayoutGuide.leftAnchor),
            dateTimePicker.topAnchor.constraint(equalTo: doneButton.bottomAnchor, constant: margin),
            dateTimePicker.rightAnchor.constraint(equalTo: safeAreaLayoutGuide.rightAnchor),
            dateTimePicker.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor)
        ])

        dateTimePicker.addTarget(self, action: #selector(valueChanged(_:)), for: .valueChanged)
    }
}

// MARK: - Event handlers
extension TimePickerInputView {
    @objc func valueChanged(_ sender: UIDatePicker) {
        pickerData?.timePickerValue = sender.date
        pickerData?.timeChanged()
    }

    @objc func doneButtonAction(_ sender: UIButton) {
        pickerData?.timeConfirmed()
        onFinish?()
    }

    @objc func cancelButtonAction(_ sender: UIButton) {
        onFinish?()
    }
}
