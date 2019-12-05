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

class SliderInputView: UIView {
    var input: SliderDataProviding? {
        didSet {
            if let input = input {
                minLabel.text = input.min.uiPrecision()
                maxLabel.text = input.max.uiPrecision()
                slider.minimumValue = Float(input.min)
                slider.maximumValue = Float(input.max)
                slider.value = Float(input.sliderValue)
                updateCurrentValue(input.sliderValue)
            }
        }
    }

    func updateCurrentValue(_ value: CGFloat?) {
        guard let value = value else {
            currentValueLabel.text = "Current value: n/a"
            return
        }

        let stringValue = value.uiPrecision() ?? String(format: "%.2f", value)
        currentValueLabel.text = "Current value: \(stringValue)"
    }

    var onFinishEditing: (() -> (Void))?
 
    fileprivate var currentValueLabel: UILabel!
    fileprivate var slider: UISlider!
    fileprivate var minLabel: UILabel!
    fileprivate var maxLabel: UILabel!

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView()
    }

    fileprivate func setupView() {
        backgroundColor = UIColor.white

        currentValueLabel = UILabel(frame: CGRect(x: 0, y: 0, width: 100, height: 30))
        currentValueLabel.font = UIFont.systemFont(ofSize: 15)
        currentValueLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(currentValueLabel)

        let button: UIButton = UIButton(type: .system)
        button.setTitle("Done", for: UIControl.State.normal)
        button.addTarget(self, action: #selector(doneButtonAction(_:)), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        addSubview(button)

        let margin: CGFloat = 8.0
        NSLayoutConstraint.activate([
            currentValueLabel.leftAnchor.constraint(equalTo: safeAreaLayoutGuide.leftAnchor, constant: margin),
            currentValueLabel.topAnchor.constraint(equalTo: topAnchor, constant: margin),

            button.trailingAnchor.constraint(equalTo: safeAreaLayoutGuide.trailingAnchor, constant: -margin),
            button.centerYAnchor.constraint(equalTo: currentValueLabel.centerYAnchor)
        ])

        minLabel = UILabel(frame: CGRect(x: 0.0, y: 0.0, width: 30.0, height: 22.0))
        minLabel.font = UIFont.systemFont(ofSize: 15)

        maxLabel = UILabel(frame: CGRect(x: 0.0, y: 0.0, width: 30.0, height: 22.0))
        maxLabel.font = UIFont.systemFont(ofSize: 15)

        slider = UISlider(frame: CGRect(x: 0.0, y: 0.0, width: 250, height: 30))
        slider.addTarget(self, action: #selector(onValueChangedAction(_:)), for: .valueChanged)

        let stackView = UIStackView(arrangedSubviews: [minLabel, slider, maxLabel])
        stackView.spacing = 8.0
        addSubview(stackView)
        stackView.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            stackView.leftAnchor.constraint(equalTo: safeAreaLayoutGuide.leftAnchor, constant: margin),
            stackView.rightAnchor.constraint(equalTo: safeAreaLayoutGuide.rightAnchor, constant: -margin),
            stackView.topAnchor.constraint(equalTo: topAnchor, constant: frame.height/3)
        ])

        updateCurrentValue(input?.sliderValue)
    }
}

// MARK: - Event handlers
extension SliderInputView {
    @objc fileprivate func onValueChangedAction(_ sender: UISlider) {
        input?.sliderValue = CGFloat(slider.value)
        updateCurrentValue(input?.sliderValue)
    }

    @objc fileprivate func doneButtonAction(_ sender: UIButton) {
        onFinishEditing?()
    }
}
