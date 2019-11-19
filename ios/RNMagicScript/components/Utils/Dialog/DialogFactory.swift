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

class DialogFactory {
    static func createDialog(for dialogData: DialogDataProviding, onConfirm: (() -> Void)?, onCancel: (() -> Void)?, onExpire: (() -> Void)?) -> UIView? {
        let dialogView = UIView(frame: CGRect(x: 0.0, y: 0.0, width: 350.0, height: 200.0))
        dialogView.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 1)

        let titleLabel = UILabel()
        titleLabel.text = dialogData.title
        titleLabel.textColor = .white
        titleLabel.font = UIFont.systemFont(ofSize: 15.0, weight: .semibold)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        dialogView.addSubview(titleLabel)

        let messageLabel = UILabel()
        messageLabel.text = dialogData.message
        messageLabel.textColor = .white
        messageLabel.font = UIFont.systemFont(ofSize: 12.0, weight: .regular)
        messageLabel.translatesAutoresizingMaskIntoConstraints = false
        dialogView.addSubview(messageLabel)

        let confirmButton: UIButton = UIButton(type: .system)
        confirmButton.setTitle(dialogData.confirmText, for: UIControl.State.normal)
        confirmButton.actionHandler(controlEvents: .touchUpInside) {
            onConfirm?()
            dialogView.removeFromSuperview()
        }
        confirmButton.backgroundColor = .white
        confirmButton.setTitleColor(.black, for: .normal)
        confirmButton.layer.borderWidth = 1.0
        confirmButton.layer.cornerRadius = 10;
        confirmButton.clipsToBounds = true
        confirmButton.translatesAutoresizingMaskIntoConstraints = false
        dialogView.addSubview(confirmButton)

        let cancelButton: UIButton = UIButton(type: .system)
        cancelButton.setTitle(dialogData.cancelText, for: UIControl.State.normal)
        cancelButton.actionHandler(controlEvents: .touchUpInside) {
            onCancel?()
            dialogView.removeFromSuperview()
        }
        cancelButton.backgroundColor = .white
        cancelButton.setTitleColor(.black, for: .normal)
        cancelButton.layer.borderWidth = 1.0
        cancelButton.layer.cornerRadius = 10;
        cancelButton.clipsToBounds = true
        cancelButton.translatesAutoresizingMaskIntoConstraints = false
        dialogView.addSubview(cancelButton)

        let margin: CGFloat = 32.0
        NSLayoutConstraint.activate([
            titleLabel.centerXAnchor.constraint(equalTo: dialogView.centerXAnchor),
            messageLabel.centerXAnchor.constraint(equalTo: dialogView.centerXAnchor),
            titleLabel.topAnchor.constraint(equalTo: dialogView.topAnchor, constant: margin),
            messageLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: margin),

            confirmButton.widthAnchor.constraint(equalToConstant: 120.0),
            cancelButton.widthAnchor.constraint(equalToConstant: 120.0),
            confirmButton.rightAnchor.constraint(equalTo: dialogView.rightAnchor, constant: -margin),
            cancelButton.leftAnchor.constraint(equalTo: dialogView.leftAnchor, constant: margin),

            cancelButton.bottomAnchor.constraint(equalTo: dialogView.bottomAnchor, constant: -margin),
            confirmButton.bottomAnchor.constraint(equalTo: dialogView.bottomAnchor, constant: -margin)
        ])

        return dialogView
    }
}
