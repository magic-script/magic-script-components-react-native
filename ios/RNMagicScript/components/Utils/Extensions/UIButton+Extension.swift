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
        button.backgroundColor = .white
        button.setTitleColor(.black, for: .normal)
        button.tintColor = .black
        button.layer.borderWidth = 1.0
        button.layer.cornerRadius = 10;
        button.clipsToBounds = true
        button.titleEdgeInsets = UIEdgeInsets(top: 5.0, left: 5.0, bottom: 5.0, right: 5.0)
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }

    func update(image: UIImage?) {
        if let buttonImage = image {
            self.setImage(UIImage.image(with: buttonImage, scaledToSize: CGSize(width: 32.0, height: 32.0)), for: UIControl.State.normal)
            self.imageView?.contentMode = .scaleAspectFit
            self.imageEdgeInsets = UIEdgeInsets(top: 5.0, left: 5.0, bottom: 5.0, right: 5.0)
        }
    }
}
