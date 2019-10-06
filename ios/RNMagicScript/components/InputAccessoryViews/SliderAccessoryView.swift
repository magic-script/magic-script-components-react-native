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

class SliderAccessoryView: UIView {

    var input: SliderDataProviding? {
        didSet {
            if let input = input {
                updateCurrentValue(input.value)
            }
        }
    }

    func updateCurrentValue(_ value: CGFloat) {
        currentValueLabel.text = String(format: "Current value: %.1f", arguments: [value])
        setNeedsLayout()
    }

    var onFinish: (() -> (Void))?

    fileprivate var currentValueLabel: UILabel!

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
        currentValueLabel.text = String(format: "Current value: %d", arguments: [input?.value ?? "n/a"])
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
            currentValueLabel.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -margin),

            button.trailingAnchor.constraint(equalTo: safeAreaLayoutGuide.trailingAnchor, constant: -margin),
            button.heightAnchor.constraint(equalToConstant: 20),
            button.widthAnchor.constraint(equalToConstant: 60),
            button.centerYAnchor.constraint(equalTo: currentValueLabel.centerYAnchor)
        ])
    }
}

// MARK: - Event handlers
extension SliderAccessoryView {
    @objc fileprivate func doneButtonAction(_ sender: UIButton) {
        onFinish?()
    }
}
