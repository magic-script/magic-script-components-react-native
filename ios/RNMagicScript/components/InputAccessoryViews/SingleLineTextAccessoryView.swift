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

class SingleLineTextAccessoryView: UIView {

    var input: InputDataProviding? {
        didSet { prepareAccessoryView() }
    }

    var onFinishEditing: (() -> (Void))?

    fileprivate var textField: UITextField!

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

        textField = UITextField(frame: CGRect(x: 0, y: 0, width: 100, height: 30))
        textField.backgroundColor = backgroundColor
        textField.delegate = self
        textField.font = UIFont.systemFont(ofSize: 15)
        textField.translatesAutoresizingMaskIntoConstraints = false
        textField.addTarget(self, action: #selector(textFieldValueChanged), for: .editingChanged)
        addSubview(textField)

        let button: UIButton = UIButton(type: .system)
        button.setTitle("Done", for: UIControl.State.normal)
        button.addTarget(self, action: #selector(doneButtonAction(_:)), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        addSubview(button)

        let margin: CGFloat = 8
        NSLayoutConstraint.activate([
            button.trailingAnchor.constraint(equalTo: trailingAnchor, constant: margin),
            button.heightAnchor.constraint(equalToConstant: 20),
            button.widthAnchor.constraint(equalToConstant: 100),
            button.centerYAnchor.constraint(equalTo: textField.centerYAnchor, constant: 0),

            textField.leadingAnchor.constraint(equalTo: leadingAnchor, constant: margin),
            textField.trailingAnchor.constraint(equalTo: button.leadingAnchor, constant: margin),
            textField.topAnchor.constraint(equalTo: topAnchor, constant: margin),
            textField.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -margin)
        ])
    }

    fileprivate func prepareAccessoryView() {
        guard let input = input else { return }

        if let autocapitalizationType = input.autocapitalizationType {
            textField.autocapitalizationType = autocapitalizationType
        }
        textField.autocorrectionType = .no
        textField.isSecureTextEntry = input.password
        if let keyboardType = input.keyboardType {
            textField.keyboardType = keyboardType
        }
        textField.placeholder = input.placeholder
        textField.returnKeyType = .default
    }
}

// MARK: - UIResponder
extension SingleLineTextAccessoryView {
    override var canBecomeFirstResponder: Bool {
        return textField.canBecomeFirstResponder
    }

    override func becomeFirstResponder() -> Bool {
        return textField.becomeFirstResponder()
    }

    override var canResignFirstResponder: Bool {
        return textField.canResignFirstResponder
    }

    override func resignFirstResponder() -> Bool {
        return textField.resignFirstResponder()
    }

    override var isFirstResponder: Bool {
        return textField.isFirstResponder
    }
}

// MARK: - Event handlers
extension SingleLineTextAccessoryView {
    @objc fileprivate func doneButtonAction(_ sender: UIButton) {
        onFinishEditing?()
    }

    @objc fileprivate func textFieldValueChanged(_ sender: UITextField) {
        if let text = sender.text,
           let maxLength = input?.charLimit, maxLength > 0, text.count > maxLength {
            let currentLength = (input?.value as? String)?.count ?? 0
            sender.text = (currentLength == maxLength) ? input?.value as? String : String(text.prefix(maxLength))
        }

        input?.value = sender.text
    }
}

// MARK: - UITextFieldDelegate
extension SingleLineTextAccessoryView: UITextFieldDelegate {
    public func textFieldDidBeginEditing(_ textField: UITextField) {
        textField.text = input?.value as? String
    }
}
