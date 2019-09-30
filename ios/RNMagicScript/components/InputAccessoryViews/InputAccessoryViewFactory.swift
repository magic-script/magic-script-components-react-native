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

class InputAccessoryViewFactory {
    static func createView(for input: InputDataProviding, onFinishEditing: (() -> Void)?) -> UIView {
        let rect = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: 44)
        if (input.multiline) {
            let multiLineView = MultiLineTextAccessoryView(frame: rect)
            multiLineView.input = input
            multiLineView.onFinishEditing = onFinishEditing
            return multiLineView
        } else {
            let singleLineView = SingleLineTextAccessoryView(frame: rect)
            singleLineView.input = input
            singleLineView.onFinishEditing = onFinishEditing
            return singleLineView
        }
    }
}
