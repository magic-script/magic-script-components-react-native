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

import Foundation
import SceneKit
import ARKit

//sourcery: AutoMockable
protocol NodesGestureHandling {
    var onInputFocused: ((_ input: DataProviding) -> (Void))? { get set }
    var onInputUnfocused: (() -> (Void))? { get set }

    func handleNodeTap(_ node: BaseNode?)
    func handleNodeLongPress(_ node: BaseNode?, _ state: UIGestureRecognizer.State)
}

class NodesGestureHandler: NodesGestureHandling {
    public static let instance = NodesGestureHandler()
    private(set) var focusedNode: UiNode?
    private(set) var longPressedNode: UiNode?

    var onInputFocused: ((_ input: DataProviding) -> (Void))?
    var onInputUnfocused: (() -> (Void))?

    func handleNodeTap(_ node: BaseNode?) {
        let uiNode = node as? UiNode
        #if targetEnvironment(simulator)
        let nodeType: String = (uiNode != nil) ? "\(uiNode!.classForCoder)" : "nil"
        print("handleNodeTap: \(nodeType)")
        #endif

        if focusedNode?.leaveFocus(onBehalfOf: uiNode) ?? true {
            if focusedNode != nil {
                onInputUnfocused?()
            }

            focusedNode = uiNode
        }

        if uiNode?.enabled ?? false {
            uiNode?.activate()
            uiNode?.enterFocus()
        }

        if let input = node as? DataProviding {
            onInputFocused?(input)
        }
    }

    func handleNodeLongPress(_ node: BaseNode?, _ state: UIGestureRecognizer.State) {
        switch state {
        case .began:
            longPressedNode = node as? UiNode
            longPressedNode?.longPressStarted()
        case .changed:
            break
        case .ended, .cancelled:
            longPressedNode?.longPressEnded()
            longPressedNode = nil
        default:
            print("LongPressGesture unsupported state.")
        }
    }
}
