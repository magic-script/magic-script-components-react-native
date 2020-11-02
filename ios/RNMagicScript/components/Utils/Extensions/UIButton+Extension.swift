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

extension UIButton {
    static func createDialogButton(text: String?, image: UIImage?, target: Any?, action: Selector) -> UIButton {
        let button: UIButton = UIButton(type: .system)
        button.setTitle(text, for: UIControl.State.normal)
        button.update(image: image)
        button.addTarget(target, action: action, for: .touchUpInside)
        button.backgroundColor = .clear
        button.setTitleColor(.white, for: .normal)
        button.tintColor = .white
        button.layer.borderColor = UIColor.white.cgColor
        button.clipsToBounds = true
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setNeedsLayout()
        return button
    }

    func update(image: UIImage?) {
        if let buttonImage = image {
            self.setImage(UIImage.image(with: buttonImage, scaledToSize: CGSize(width: 18.0, height: 18.0)), for: UIControl.State.normal)
            self.imageView?.contentMode = .scaleAspectFit
            self.setNeedsLayout()
        }
    }


    func centerVertically() {
        guard let imageViewSize = imageView?.frame.size, let titleLabelSize = titleLabel?.frame.size else { return }

        let padding: CGFloat = 22.0
        let totalHeight = imageViewSize.height + titleLabelSize.height + padding
        imageEdgeInsets = UIEdgeInsets(top: max(0, -(totalHeight - imageViewSize.height)), left: 0.0, bottom: 0.0, right: -titleLabelSize.width)
        titleEdgeInsets = UIEdgeInsets(top: 0.0, left: -imageViewSize.width, bottom: -(totalHeight - titleLabelSize.height), right: 0.0)
        contentEdgeInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: titleLabelSize.height, right: 0.0)
    }
}
