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

import ChromaColorPicker

class ColorPickerInputView: UIView {
    var pickerData: ColorPickerDataProviding? {
        didSet {
            if let selectedColor = pickerData?.colorPickerValue {
                neatColorPicker.adjustToColor(selectedColor)
            }
        }
    }

    fileprivate var neatColorPicker: ChromaColorPicker!

    var onFinish: (() -> (Void))?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView()
    }

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
            doneButton.topAnchor.constraint(equalTo: safeAreaLayoutGuide.topAnchor, constant: margin),
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
            cancelButton.topAnchor.constraint(equalTo: safeAreaLayoutGuide.topAnchor, constant: margin),
        ])

        let colorPickerSize = CGSize(width: frame.height - 40.0, height: frame.height - 40.0)
        neatColorPicker = ChromaColorPicker(frame: CGRect(x: 0, y: 0, width: colorPickerSize.width, height: colorPickerSize.height))
        neatColorPicker.delegate = self //ChromaColorPickerDelegate
        neatColorPicker.padding = 1
        neatColorPicker.stroke = 10
        neatColorPicker.hexLabel.textColor = UIColor.black
        neatColorPicker.translatesAutoresizingMaskIntoConstraints = false
        addSubview(neatColorPicker)

        NSLayoutConstraint.activate([
            neatColorPicker.heightAnchor.constraint(equalToConstant: colorPickerSize.height),
            neatColorPicker.widthAnchor.constraint(equalToConstant: colorPickerSize.width),
            neatColorPicker.centerXAnchor.constraint(equalTo: centerXAnchor),
            neatColorPicker.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor, constant: margin)
        ])
    }
}

extension ColorPickerInputView: ChromaColorPickerDelegate {
    func colorPickerDidChooseColor(_ colorPicker: ChromaColorPicker, color: UIColor) {
        pickerData?.colorPickerValue = color
    }

    @objc fileprivate func doneButtonAction(_ sender: UIButton) {
        pickerData?.colorConfirmed()
        onFinish?()
    }

    @objc fileprivate func cancelButtonAction(_ sender: UIButton) {
        pickerData?.colorCanceled()
        onFinish?()
    }
}
