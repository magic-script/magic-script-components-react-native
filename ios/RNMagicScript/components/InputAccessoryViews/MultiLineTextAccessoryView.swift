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
import GrowingTextView

class MultiLineTextAccessoryView: UIView {

    var input: InputDataProviding? {
        didSet { prepareAccessoryView() }
    }

    var onFinishEditing: (() -> (Void))?

    fileprivate var textView: GrowingTextView!
    fileprivate weak var heightConstraint: NSLayoutConstraint?

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
        translatesAutoresizingMaskIntoConstraints = false

        textView = GrowingTextView()
        textView.backgroundColor = backgroundColor
        textView.delegate = self
        textView.font = UIFont.systemFont(ofSize: 15)
        textView.maxHeight = 100
        textView.placeholderColor = UIColor(white: 0.8, alpha: 1.0)
        textView.translatesAutoresizingMaskIntoConstraints = false
        textView.trimWhiteSpaceWhenEndEditing = true
        addSubview(textView)

        let button: UIButton = UIButton(type: .system)
        button.setTitle("Done", for: UIControl.State.normal)
        button.addTarget(self, action: #selector(doneButtonAction(_:)), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        addSubview(button)

        let margin: CGFloat = 8
        let topConstraint = textView.topAnchor.constraint(equalTo: topAnchor, constant: margin)
        topConstraint.priority = UILayoutPriority(999)
        NSLayoutConstraint.activate([
            button.trailingAnchor.constraint(equalTo: trailingAnchor, constant: margin),
            button.heightAnchor.constraint(equalToConstant: 20),
            button.widthAnchor.constraint(equalToConstant: 100),
            button.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -12),

            textView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: margin),
            textView.trailingAnchor.constraint(equalTo: button.leadingAnchor, constant: margin),
            topConstraint,
            textView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -margin)
        ])
    }

    fileprivate func prepareAccessoryView() {
        guard let input = input else { return }

        if let autocapitalizationType = input.autocapitalizationType {
            textView.autocapitalizationType = autocapitalizationType
        }
        textView.autocorrectionType = .no
        textView.isSecureTextEntry = input.password
        if let keyboardType = input.keyboardType {
            textView.keyboardType = keyboardType
        }
        textView.maxLength = input.charLimit
        textView.placeholder = input.placeholder
        textView.returnKeyType = .default
    }

    fileprivate func disableHeightConstraint() {
        guard let parent = superview, heightConstraint == nil else { return }

        for constraint in parent.constraints {
            if (constraint.firstAttribute == .height &&
                constraint.relation == .equal &&
                constraint.firstItem === self) {
                heightConstraint = constraint
                heightConstraint?.isActive = false
                break
            }
        }
    }

    override open func layoutSubviews() {
        super.layoutSubviews()
        disableHeightConstraint()
    }
}

// MARK: - UIResponder
extension MultiLineTextAccessoryView {
    override var canBecomeFirstResponder: Bool {
        return textView.canBecomeFirstResponder
    }

    override func becomeFirstResponder() -> Bool {
        return textView.becomeFirstResponder()
    }

    override var canResignFirstResponder: Bool {
        return textView.canResignFirstResponder
    }

    override func resignFirstResponder() -> Bool {
        return textView.resignFirstResponder()
    }

    override var isFirstResponder: Bool {
        return textView.isFirstResponder
    }
}

// MARK: - Event handlers
extension MultiLineTextAccessoryView {
    @objc fileprivate func doneButtonAction(_ sender: UIButton) {
        onFinishEditing?()
    }
}

// MARK: - GrowingTextViewDelegate
extension MultiLineTextAccessoryView: GrowingTextViewDelegate {
    public func textViewDidBeginEditing(_ textView: UITextView) {
        textView.text = input?.value as? String
    }

    public func textViewDidChange(_ textView: UITextView) {
        input?.value = textView.text
    }
}
