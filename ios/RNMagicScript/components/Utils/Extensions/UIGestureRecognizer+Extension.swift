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

import Foundation
import UIKit

extension UIGestureRecognizer {
    func ignoreAllTouchesButFirst(_ touches: Set<UITouch>, with event: UIEvent) {
        guard let firstTouch = touches.first else { return }
        touches.filter { $0 != firstTouch }.forEach { ignore($0, for: event) }
    }
}

extension UIGestureRecognizer.State: CustomStringConvertible {
    public var description: String {
        return "UIGestureRecognizer.State.\(rawValue)"
    }
}
