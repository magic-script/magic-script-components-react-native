//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
        return min(UIScreen.height, UIScreen.width) - 2 * margin
    }

    static var height: CGFloat {
        let margin: CGFloat = 16.0
        return max(UIScreen.height, UIScreen.width) - 2 * margin
    }

    var dialogData: DialogDataProviding? {
        didSet {
            titleLabel.text = dialogData?.title
            messageTextView.text = dialogData?.message
            messageTextView.isScrollEnabled = dialogData?.scrolling ?? false
            confirmButton.setTitle(dialogData?.confirmText, for: UIControl.State.normal)
            confirmButton.sizeToFit()
            confirmButton.update(image: dialogData?.confirmIcon?.image)
            cancelButton.setTitle(dialogData?.cancelText, for: UIControl.State.normal)
            cancelButton.sizeToFit()
            cancelButton.update(image: dialogData?.cancelIcon?.image)
            toggleButtonsVisibility()
            setExpirationTimer()
        }
    }

    fileprivate var titleLabel: UILabel!
    fileprivate var messageTextView: GrowingTextView!
    fileprivate var confirmButton: UIButton!
    fileprivate var cancelButton: UIButton!

    fileprivate var expirationTimer: Timer?

    init(frame: CGRect, dialogData: DialogDataProviding) {
        super.init(frame: frame)
        setupView()
    }

    required init?(coder: NSCoder) {
        fatalError("please use init(frame: CGRect, dialogData: DialogDataProviding)")
    }

    deinit {
        expirationTimer?.invalidate()
    }

    fileprivate func setupView() {
        let backgroundImage = UIImageView(frame: frame)
        backgroundImage.image = ImageAsset.dialogBackground.image
        backgroundImage.contentMode = .scaleToFill
        backgroundImage.translatesAutoresizingMaskIntoConstraints = false
        insertSubview(backgroundImage, at: 0)

        let imageMargin: CGFloat = 100
        NSLayoutConstraint.activate([
            backgroundImage.topAnchor.constraint(equalTo: topAnchor, constant: -imageMargin),
            backgroundImage.bottomAnchor.constraint(equalTo: bottomAnchor, constant: imageMargin),
            backgroundImage.leftAnchor.constraint(equalTo: leftAnchor, constant: -imageMargin),
            backgroundImage.rightAnchor.constraint(equalTo: rightAnchor, constant: imageMargin),
        ])

        titleLabel = UILabel()
        titleLabel.text = dialogData?.title
        titleLabel.textColor = .white
        titleLabel.textAlignment = .center
        titleLabel.font = UIFont.systemFont(ofSize: DialogView.titleFontSize, weight: .semibold)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(titleLabel)

        messageTextView = GrowingTextView()
        messageTextView.isEditable = false
        messageTextView.isSelectable = false
        messageTextView.isScrollEnabled = dialogData?.scrolling ?? false
        messageTextView.indicatorStyle = .white
        messageTextView.maxHeight = 140.0
        messageTextView.text = dialogData?.message
        messageTextView.textColor = .white
        messageTextView.textAlignment = .center
        messageTextView.backgroundColor = .clear
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

        let margin: CGFloat = 16.0
        
        let buttonsContainer: UIStackView = UIStackView()
        buttonsContainer.axis = .horizontal
        buttonsContainer.alignment = .center
        buttonsContainer.spacing = margin
        buttonsContainer.translatesAutoresizingMaskIntoConstraints = false
        buttonsContainer.addArrangedSubview(cancelButton)
        buttonsContainer.addArrangedSubview(confirmButton)

        NSLayoutConstraint.activate([
            cancelButton.topAnchor.constraint(equalTo: buttonsContainer.topAnchor, constant: 0),
            cancelButton.bottomAnchor.constraint(equalTo: buttonsContainer.bottomAnchor, constant: 0),
            confirmButton.topAnchor.constraint(equalTo: buttonsContainer.topAnchor, constant: 0),
            confirmButton.bottomAnchor.constraint(equalTo: buttonsContainer.bottomAnchor, constant: 0),
        ])

        addSubview(buttonsContainer)

        NSLayoutConstraint.activate([
            titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: margin),
            titleLabel.leftAnchor.constraint(equalTo: leftAnchor, constant: margin),
            titleLabel.rightAnchor.constraint(equalTo: rightAnchor, constant: -margin),
            titleLabel.heightAnchor.constraint(equalToConstant: 16.0),
            messageTextView.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: margin),
            messageTextView.rightAnchor.constraint(equalTo: rightAnchor, constant: -margin),
            messageTextView.leftAnchor.constraint(equalTo: leftAnchor, constant: margin),
            buttonsContainer.topAnchor.constraint(equalTo: messageTextView.bottomAnchor, constant: margin),
            buttonsContainer.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -margin),
            buttonsContainer.centerXAnchor.constraint(equalTo: centerXAnchor)
        ])
        
        // if vertical content is too big, make sure the action buttons are compacted
        messageTextView.setContentHuggingPriority(.defaultLow, for: .vertical)
        cancelButton.setContentHuggingPriority(.defaultHigh, for: .vertical)
        confirmButton.setContentHuggingPriority(.defaultHigh, for: .vertical)

        setExpirationTimer()
    }

    fileprivate func setExpirationTimer() {
        if let expireTime = dialogData?.expireTime, expireTime > 0, let interval = TimeInterval(exactly: expireTime) {
            expirationTimer?.invalidate()
            expirationTimer = Timer.scheduledTimer(withTimeInterval: interval, repeats: false, block: { [weak self] timer in
                self?.dialogData?.dialogTimeExpired()
            })
            expirationTimer?.tolerance = 0.5
        }
    }

    fileprivate func toggleButtonsVisibility() {
        if dialogData?.confirmIcon == nil && dialogData?.confirmText == nil {
            confirmButton.isHidden = true
        } else {
            confirmButton.isHidden = false
        }

        if dialogData?.cancelIcon == nil && dialogData?.cancelText == nil {
            cancelButton.isHidden = true
        } else {
            cancelButton.isHidden = false
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        switch dialogData?.buttonType {
        case .textWithIcon:
            confirmButton.contentEdgeInsets = UIEdgeInsets(top: 8.0, left: 16.0, bottom: 8.0, right: 24.0)
            confirmButton.titleEdgeInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: -8.0)
            cancelButton.contentEdgeInsets = UIEdgeInsets(top: 8.0, left: 16.0, bottom: 8.0, right: 24.0)
            cancelButton.titleEdgeInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: -8.0)
            confirmButton.layer.cornerRadius = confirmButton.bounds.height / 2
            confirmButton.layer.borderWidth = 2
            cancelButton.layer.cornerRadius = cancelButton.bounds.height / 2
            cancelButton.layer.borderWidth = 2
        case .text:
            confirmButton.contentEdgeInsets = UIEdgeInsets(top: 4.0, left: 16.0, bottom: 4.0, right: 16.0)
            cancelButton.contentEdgeInsets = UIEdgeInsets(top: 4.0, left: 16.0, bottom: 4.0, right: 16.0)
            confirmButton.layer.cornerRadius = confirmButton.bounds.height / 2
            confirmButton.layer.borderWidth = 2
            cancelButton.layer.cornerRadius = cancelButton.bounds.height / 2
            cancelButton.layer.borderWidth = 2
            // reset images
            cancelButton.setImage(nil, for: .normal)
            confirmButton.setImage(nil, for: .normal)
        case .iconWithLabel:
            cancelButton.layer.borderWidth = 0
            confirmButton.layer.borderWidth = 0
            confirmButton.centerVertically()
            cancelButton.centerVertically()
        case .icon:
            cancelButton.layer.borderWidth = 0
            confirmButton.layer.borderWidth = 0
            confirmButton.centerVertically()
            cancelButton.centerVertically()
            // reset labels
            cancelButton.setTitle(nil, for: .normal)
            confirmButton.setTitle(nil, for: .normal)
        default:
            print("Default: \(#function)")
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
}
