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

class DialogView: UIView {
    static let titleFontSize: CGFloat = 15.0
    static let messageFontSize: CGFloat = 12.0

    static var width: CGFloat {
        let margin: CGFloat = 16.0
        return UIScreen.width > UIScreen.height ? UIScreen.height - 2 * margin : UIScreen.width - 2 * margin
    }

    static var height: CGFloat {
        return DialogView.width * 0.75
    }

    var dialogData: DialogDataProviding? {
        didSet {
            titleLabel.text = dialogData?.title
            messageTextView.text = dialogData?.message
            confirmButton.setTitle(dialogData?.confirmText, for: UIControl.State.normal)
            confirmButton.update(image: dialogData?.confirmIcon?.image)
            cancelButton.setTitle(dialogData?.cancelText, for: UIControl.State.normal)
            cancelButton.update(image: dialogData?.cancelIcon?.image)
            setExpirationTimer()
        }
    }

    fileprivate var titleLabel: UILabel!
    fileprivate var messageTextView: GrowingTextView!
    fileprivate var confirmButton: UIButton!
    fileprivate var cancelButton: UIButton!

    fileprivate var expirationTimer: Timer!

    init(frame: CGRect, dialogData: DialogDataProviding) {
        super.init(frame: frame)
        setupView()
    }

    required init?(coder: NSCoder) {
        fatalError("please use init(frame: CGRect, dialogData: DialogDataProviding)")
    }

    fileprivate func setupView() {
        backgroundColor = .white

        titleLabel = UILabel()
        titleLabel.text = dialogData?.title
        titleLabel.textColor = .black
        titleLabel.font = UIFont.systemFont(ofSize: DialogView.titleFontSize, weight: .semibold)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(titleLabel)

        messageTextView = GrowingTextView()
        messageTextView.isEditable = false
        messageTextView.isSelectable = false
        messageTextView.maxHeight = 140.0
        messageTextView.text = dialogData?.message
        messageTextView.textColor = .black
        messageTextView.font = UIFont.systemFont(ofSize: DialogView.messageFontSize, weight: .regular)
        messageTextView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(messageTextView)

        confirmButton = UIButton.createDialogButton(text: dialogData?.confirmText,
                                                    image: dialogData?.confirmIcon?.image,
                                                    target: self,
                                                    action: #selector(confirmButtonAction(_:)))

        cancelButton = UIButton.createDialogButton(text: dialogData?.cancelText,
                                                   image: dialogData?.cancelIcon?.image,
                                                   target: self,
                                                   action: #selector(cancelButtonAction(_:)))

        let stackView = UIStackView(arrangedSubviews: [cancelButton, confirmButton])
        stackView.distribution = .fillEqually
        stackView.spacing = 8.0
        stackView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(stackView)

        let margin: CGFloat = 16.0
        NSLayoutConstraint.activate([
            titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: margin),
            titleLabel.leftAnchor.constraint(equalTo: leftAnchor, constant: margin),
            titleLabel.rightAnchor.constraint(equalTo: rightAnchor, constant: -margin),
            messageTextView.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: margin),
            messageTextView.rightAnchor.constraint(equalTo: rightAnchor, constant: -margin),
            messageTextView.leftAnchor.constraint(equalTo: leftAnchor, constant: margin),
            stackView.topAnchor.constraint(equalTo: messageTextView.bottomAnchor, constant: margin),
            stackView.leftAnchor.constraint(equalTo: leftAnchor, constant: margin),
            stackView.rightAnchor.constraint(equalTo: rightAnchor, constant: -margin),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -margin)
        ])

        setExpirationTimer()
    }

    fileprivate func setExpirationTimer() {
        if let expireTime = dialogData?.expireTime, expireTime > 0, let interval = TimeInterval(exactly: expireTime) {
            if expirationTimer != nil { expirationTimer.invalidate() }
            expirationTimer = Timer.scheduledTimer(timeInterval: interval, target: self, selector: #selector(timerExpirationAction(_:)), userInfo: nil, repeats: false)
            expirationTimer.tolerance = 0.5
        }
    }
}

extension DialogView {
    @objc fileprivate func confirmButtonAction(_ sender: UIButton) {
        dialogData?.dialogConfirmed()
    }

    @objc fileprivate func cancelButtonAction(_ sender: UIButton) {
        dialogData?.dialogCanceled()
    }

    @objc fileprivate func timerExpirationAction(_ sender: Timer) {
        dialogData?.dialogTimeExpired()
    }
}
