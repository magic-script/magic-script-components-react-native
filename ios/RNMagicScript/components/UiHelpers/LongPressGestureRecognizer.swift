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

@objc class LongPressGestureRecognizer: UIGestureRecognizer {
    fileprivate let nodeSelector: NodeSelecting
    fileprivate(set) var longPressedNode: TransformNode?
    fileprivate(set) var initialTouchLocation: CGPoint?
    fileprivate var rayBuilder: RayBuilding
    fileprivate var longpressTimer: Timer?

    var getCameraNode: (() -> SCNNode?)?
    var minimumPressDuration: TimeInterval = 0.5

    init(nodeSelector: NodeSelecting, rayBuilder: RayBuilding, target: Any?, action: Selector?) {
        self.nodeSelector = nodeSelector
        self.rayBuilder = rayBuilder
        super.init(target: target, action: action)
    }

    deinit {
        longpressTimer?.invalidate()
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent) {
        if touches.count != 1 {
            state = .failed
        }

        if state == .possible,
            let cameraNode = getCameraNode?(),
            let firstTouch = touches.first,
            let ray = rayBuilder.build(gesture: self, cameraNode: cameraNode) {
            longPressedNode = nodeSelector.hitTest(ray: ray)
            initialTouchLocation = firstTouch.location(in: firstTouch.view)

            longpressTimer?.invalidate()
            longpressTimer = Timer.scheduledTimer(withTimeInterval: minimumPressDuration, repeats: false) { [weak self] _ in
                self?.longpressTimer = nil
                self?.state = .began
            }
        } else {
            state = .failed
        }

        if longPressedNode != nil {
            ignoreAllTouchesButFirst(touches, with: event)
        }

        super.touchesBegan(touches, with: event)
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent) {
        if let firstTouch = touches.first,
            let initialTouchLocation = initialTouchLocation {
            let currentLocation = firstTouch.location(in: firstTouch.view)
            let delta = (currentLocation - initialTouchLocation)
            let distanceSq = delta.x * delta.x + delta.y + delta.y
            if distanceSq > 400 {
                state = .failed
            }
        } else {
            state = .failed
        }

        super.touchesMoved(touches, with: event)
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent) {
        longpressTimer?.invalidate()
        longpressTimer = nil
        longPressedNode = nil
        initialTouchLocation = nil
        state = (state == .began) ? .ended : .failed
        super.touchesEnded(touches, with: event)
    }

    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent) {
        longpressTimer?.invalidate()
        longpressTimer = nil
        state = .cancelled
        super.touchesCancelled(touches, with: event)
    }

    override func reset() {
        longpressTimer?.invalidate()
        longpressTimer = nil
        longPressedNode = nil
        initialTouchLocation = nil
        super.reset()
    }
}


