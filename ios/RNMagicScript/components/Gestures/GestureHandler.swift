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

//sourcery: AutoMockable
//sourcery: ObjcProtocol
@objc protocol GestureHandling {
    func handleTapGesture(_ sender: TapGestureRecognizing)
    func handleDragGesture(_ sender: DragGestureRecognizing)
    func handleLongPressGesture(_ sender: LongPressGestureRecognizing)
}

class GestureHandler: GestureHandling {
    fileprivate(set) var nodesGestureHandler: NodesGestureHandling

    init(nodesGestureHandler: NodesGestureHandling) {
        self.nodesGestureHandler = nodesGestureHandler
    }

    @objc func handleTapGesture(_ sender: TapGestureRecognizing) {
        if sender.state == .ended {
            nodesGestureHandler.handleNodeTap(sender.tappedNode)
        }
    }

    @objc func handleDragGesture(_ sender: DragGestureRecognizing) {
        if sender.state == UIGestureRecognizer.State.changed {
            sender.dragNode?.dragValue = sender.beginDragValue + sender.dragDelta
        }
    }

    @objc func handleLongPressGesture(_ sender: LongPressGestureRecognizing) {
        nodesGestureHandler.handleNodeLongPress(sender.longPressedNode, sender.state)
    }
}
