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
    fileprivate let nodeSelector: UiNodeSelector
    var longPressedNode: TransformNode?
    var getCameraNode: (() -> SCNNode?)?
    
    var minimumPressDuration: TimeInterval = 0.5
    fileprivate var trackedTouch: (touch: UITouch, node: TransformNode?)?
    fileprivate var longpressTimer: Timer?
    
    init(nodeSelector: UiNodeSelector, target: Any?, action: Selector?) {
        self.nodeSelector = nodeSelector
        super.init(target: target, action: action)
        delaysTouchesBegan = true
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent) {
        if let cameraNode = getCameraNode?(),
            let ray = Ray(gesture: self, cameraNode: cameraNode),
            state == .possible,
            let touch = touches.first {
            print("BUKA \(self.classForCoder) \(#function)")
            let hitNode = nodeSelector.hitTest(ray: ray)
            trackedTouch = (touch, hitNode)
            longpressTimer = Timer.scheduledTimer(withTimeInterval: minimumPressDuration, repeats: false) { [weak self] _ in
                if self?.trackedTouch != nil, self?.trackedTouch?.touch == touch, self?.trackedTouch?.node == hitNode {
                    self?.longpressTimer = nil
                    self?.longPressedNode = hitNode
                    self?.state = .began
                }
            }
        }
    }
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent) {
        longpressTimer?.invalidate()
        longPressedNode = nil
        trackedTouch = nil
        state = .ended
    }
    
    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent) {
        longpressTimer?.invalidate()
        longPressedNode = nil
        trackedTouch = nil
        state = .cancelled
    }
}
