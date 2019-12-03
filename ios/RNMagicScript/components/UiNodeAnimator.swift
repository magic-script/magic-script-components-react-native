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
import SceneKit

//sourcery: AutoMockable
protocol NodeAnimating {
    func startAnimation(duration: TimeInterval, update: @escaping (_ node: SCNNode, _ timeElapsed: CGFloat) -> Void)
    func stopAnimation()
}

class UiNodeAnimator: NodeAnimating {
    fileprivate let node: SCNNode
    fileprivate let actionKey: String = String.random(length: 8)

    deinit {
        stopAnimation()
    }

    init(_ node: SCNNode) {
        self.node = node
    }

    func startAnimation(duration: TimeInterval, update: @escaping (_ node: SCNNode, _ timeElapsed: CGFloat) -> Void) {
        stopAnimation()
        
        let action = SCNAction.customAction(duration: duration, action: update)
        node.runAction(action, forKey: actionKey)
    }

    func stopAnimation() {
        node.removeAction(forKey: actionKey)
    }
}
