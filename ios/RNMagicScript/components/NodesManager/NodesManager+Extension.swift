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

extension NodesManager: RCTARViewObserving {
    @objc internal func renderer(_ renderer: SCNSceneRenderer, didAdd node: SCNNode, for anchor: ARAnchor) {
        if let anchorId = anchor.name {
            registerAnchorNode(node, anchorId: anchorId)
        }

        if let name = anchor.name, let prism = findPrismWithAnchorUuid(name) {
            prism.applyTransform(from: node)
        }
    }

    @objc internal func renderer(_ renderer: SCNSceneRenderer, didUpdate node: SCNNode, for anchor: ARAnchor) {
        if let name = anchor.name, let prism = findPrismWithAnchorUuid(name) {
            prism.applyTransform(from: node)
        }
    }

    @objc internal func renderer(_ renderer: SCNSceneRenderer, didRemove node: SCNNode, for anchor: ARAnchor) {
        if let anchorId = anchor.name {
            unregisterAnchorNode(anchorId: anchorId)
        }
    }
}

extension Prism {
    func applyTransform(from anchorNode: SCNNode) {
        // This prism may not yet be attached to its parent
        // so set the world transform directly
        setWorldTransform(anchorNode.worldTransform)
    }
}
