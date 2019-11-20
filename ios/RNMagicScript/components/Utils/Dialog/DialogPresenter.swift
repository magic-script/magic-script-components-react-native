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

class DialogPresenter: DialogPresenting {

    fileprivate(set) var parentView: UIView
    fileprivate(set) var backgroundView: UIView!
    fileprivate(set) var presentedDialogs: [String : UIView] = [:]

    init(parentView: UIView) {
        self.parentView = parentView
    }

    fileprivate func addBackgroudView() {
        backgroundView = UIView(frame: CGRect(x: 0.0, y: 0.0, width: UIScreen.width, height: UIScreen.height))
        backgroundView.backgroundColor = UIColor.black.withAlphaComponent(0.35)
        parentView.addSubview(backgroundView)
        NSLayoutConstraint.activate([
            backgroundView.leftAnchor.constraint(lessThanOrEqualTo: parentView.leftAnchor),
            backgroundView.rightAnchor.constraint(lessThanOrEqualTo: parentView.rightAnchor),
            backgroundView.topAnchor.constraint(lessThanOrEqualTo: parentView.topAnchor),
            backgroundView.bottomAnchor.constraint(lessThanOrEqualTo: parentView.bottomAnchor)
        ])
    }

    fileprivate func removeBackgroundView() {
        backgroundView.removeFromSuperview()
        backgroundView = nil
    }

    func present(_ dialog: DialogDataProviding) {
        if let dialogView = DialogFactory.createDialog(for: dialog) {
            dialogView.translatesAutoresizingMaskIntoConstraints = false

            if presentedDialogs.count == 0 { addBackgroudView() }

            backgroundView.addSubview(dialogView)

            NSLayoutConstraint.activate([
                dialogView.widthAnchor.constraint(equalToConstant: DialogView.width),
                dialogView.heightAnchor.constraint(lessThanOrEqualToConstant: min(dialogView.frame.height, DialogView.height)),
                dialogView.centerXAnchor.constraint(lessThanOrEqualTo: backgroundView.centerXAnchor),
                dialogView.centerYAnchor.constraint(lessThanOrEqualTo: backgroundView.centerYAnchor)
            ])
            presentedDialogs[dialog.id] = dialogView
        }
    }

    func dismiss(_ dialog: DialogDataProviding) {
        if let dialogView = presentedDialogs[dialog.id] {
            dialogView.removeFromSuperview()
            presentedDialogs.removeValue(forKey: dialog.id)
            if presentedDialogs.count == 0 { removeBackgroundView() }
        }
    }
}


