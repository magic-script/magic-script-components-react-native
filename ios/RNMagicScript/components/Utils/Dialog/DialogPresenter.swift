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

    init(parentView: UIView) {
        self.parentView = parentView
    }

    func present(_ dialog: DialogDataProviding) {
        if let dialogView = DialogFactory.createDialog(for: dialog, onConfirm: dialog.dialogConfirmed, onCancel: dialog.dialogCanceled, onExpire: dialog.dialogTimeExpired) {
            dialogView.translatesAutoresizingMaskIntoConstraints = false
            parentView.addSubview(dialogView)
            NSLayoutConstraint.activate([
                dialogView.widthAnchor.constraint(equalToConstant: 350.0),
                dialogView.heightAnchor.constraint(equalToConstant: 200.0),
                dialogView.centerXAnchor.constraint(lessThanOrEqualTo: parentView.centerXAnchor),
                dialogView.centerYAnchor.constraint(lessThanOrEqualTo: parentView.centerYAnchor)
            ])
        }
    }

    func dismiss(_ dialog: DialogDataProviding) {

    }
}


