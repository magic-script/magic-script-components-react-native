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

import SceneKit

@objc open class UiDialogNode: UiNode {
//    DialogType dialogType;
//    DialogLayout dialogLayout;

    @objc var title: String?

    @objc var message: String?

    @objc var confirmText: String?
    @objc var confirmIcon: SystemIcon?

    @objc var cancelText: String?
    @objc var cancelIcon: SystemIcon?

//    EclipseButtonType buttonType;

    @objc var scrolling: Bool = false

    @objc var expireTime: CGFloat = 0.0

    @objc public var onDialogConfirmed: ((_ sender: UiDialogNode) -> (Void))?
    @objc public var onDialogCanceled: ((_ sender: UiDialogNode) -> (Void))?
    @objc public var onDialogTimeExpired: ((_ sender: UiDialogNode) -> (Void))?

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let title = Convert.toString(props["title"]) {
            self.title = title
        }

        if let message = Convert.toString(props["message"]) {
            self.message = message
        }

        if let confirmText = Convert.toString(props["confirmText"]) {
            self.confirmText = confirmText
        }

        if let confirmIcon = Convert.toSystemIcon(props["confirmIcon"]) {
            self.confirmIcon = confirmIcon
        }

        if let cancelText = Convert.toString(props["cancelText"]) {
            self.cancelText = cancelText
        }

        if let cancelIcon = Convert.toSystemIcon(props["cancelIcon"]) {
            self.cancelIcon = cancelIcon
        }

        if let scrolling = Convert.toBool(props["scrolling"]) {
            self.scrolling = scrolling
        }

        if let expireTime = Convert.toCGFloat(props["expireTime"]) {
            self.expireTime = expireTime
        }
    }
}

extension UiDialogNode: DialogDataProviding {
    var id: String {
        return self.name ?? "UnidentifiedObject"
    }

    func dialogConfirmed() {
        onDialogConfirmed?(self)
    }

    func dialogCanceled() {
        onDialogCanceled?(self)
    }

    func dialogTimeExpired() {
        onDialogTimeExpired?(self)
    }
}
