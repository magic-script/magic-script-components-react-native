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
import SceneKit

@objc class DragGestureRecognizer: UIGestureRecognizer {
    fileprivate let cameraNode: SCNNode
    fileprivate let nodeSelector: UiNodeSelector
    fileprivate var trackedTouch: UITouch?
    fileprivate var initialRay: Ray?
    fileprivate(set) var dragNode: Dragging?
    fileprivate var beginPoint: SCNVector3 = SCNVector3Zero
    fileprivate(set) var beginDragValue: CGFloat = 0
    fileprivate(set) var dragDelta: CGFloat = 0

    init(cameraNode: SCNNode, nodeSelector: UiNodeSelector, target: Any?, action: Selector?) {
        self.cameraNode = cameraNode
        self.nodeSelector = nodeSelector
        super.init(target: target, action: action)
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent) {
        if touches.count != 1 {
          state = .failed
        }

        // Capture the first touch and store some information about it.
        if trackedTouch == nil {
            if let firstTouch = touches.first,
               let ray = Ray(gesture: self, cameraNode: cameraNode),
               let node = nodeSelector.draggingHitTest(ray: ray),
               let dragAxis = node.dragAxis,
               let point = dragAxis.getClosestPointTo(ray: ray) {
                trackedTouch = firstTouch
                initialRay = ray
                dragNode = node
                beginPoint = point
                beginDragValue = node.dragValue
                dragDelta = 0
                state = .began
            } else {
                state = .failed
            }
        } else {
            // Ignore all but the first touch.
            for touch in touches {
                if touch != trackedTouch {
                    ignore(touch, for: event)
                }
            }
        }
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let ray = Ray(gesture: self, cameraNode: cameraNode),
            let dragRange = dragNode?.dragRange, dragRange > 0 {
            let delta = calculateDelta(for: ray)
            dragDelta = delta / dragRange
        }
        state = .changed
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        state = .ended
    }

    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        state = .cancelled
    }

    override func reset() {
        trackedTouch = nil
        initialRay = nil
        dragNode = nil
        beginPoint = SCNVector3Zero
        beginDragValue = 0
        dragDelta = 0
    }

    fileprivate func calculateDelta(for ray: Ray) -> CGFloat {
        guard let dragAxis = dragNode?.dragAxis,
            let point = dragAxis.getClosestPointTo(ray: ray) else { return 0 }

        let dir = (point - beginPoint).normalized()
        let sign: CGFloat = (dir.dot(dragAxis.direction) >= 0) ? 1 : -1
        return CGFloat(point.distance(beginPoint)) * sign
    }
}
