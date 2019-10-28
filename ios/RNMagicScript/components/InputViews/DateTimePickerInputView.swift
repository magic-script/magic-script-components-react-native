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

class DateTimePickerInputView: UIView {
    var pickerData: DatePickerDataProviding? {
        didSet {
            if let pickerData = pickerData {
                dateTimePicker.date = pickerData.datePickerValue
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

    fileprivate var dateTimePicker: UIDatePicker!

    fileprivate func setupView() {
        backgroundColor = .white

        let button: UIButton = UIButton(type: .system)
        button.setTitle("Done", for: UIControl.State.normal)
        button.addTarget(self, action: #selector(doneButtonAction(_:)), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        addSubview(button)

        let margin: CGFloat = 8
        NSLayoutConstraint.activate([
            button.rightAnchor.constraint(equalTo: safeAreaLayoutGuide.rightAnchor, constant: -margin),
            button.heightAnchor.constraint(equalToConstant: 20),
            button.widthAnchor.constraint(equalToConstant: 100),
            button.topAnchor.constraint(equalTo: topAnchor, constant: margin),
        ])

        dateTimePicker = UIDatePicker()
        dateTimePicker.backgroundColor = .white
        dateTimePicker.datePickerMode = .date
        dateTimePicker.translatesAutoresizingMaskIntoConstraints = false
        addSubview(dateTimePicker)

        NSLayoutConstraint.activate([
            dateTimePicker.leftAnchor.constraint(equalTo: safeAreaLayoutGuide.leftAnchor),
            dateTimePicker.topAnchor.constraint(equalTo: button.bottomAnchor, constant: margin),
            dateTimePicker.rightAnchor.constraint(equalTo: safeAreaLayoutGuide.rightAnchor),
            dateTimePicker.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor)
        ])

        dateTimePicker.addTarget(self, action: #selector(valueChanged(_:)), for: .valueChanged)
    }
}


// MARK: - Event handlers
extension DateTimePickerInputView {
    @objc fileprivate func valueChanged(_ sender: UIDatePicker) {
        pickerData?.datePickerValue = sender.date
    }

    @objc fileprivate func doneButtonAction(_ sender: UIButton) {
        onFinish?()
    }
}
