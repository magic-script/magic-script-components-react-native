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

extension SCNNode {
    /// Enumerates SCNNode hierarchy in post order and invokes the block closure for each TransformNode.
    @objc func enumerateTransformNodes(_ block: (TransformNode) -> Void) {
        childNodes.forEach { child in
            child.enumerateTransformNodes(block)
        }

        if let transformNode = self as? TransformNode {
            block(transformNode)
        }
    }

    /// Enumerates SCNNode hierarchy from given node (self) to the root.
    /// Invokes the block closure for each TransformNode.
    @objc func enumerateTransformNodesParents(_ block: (TransformNode) -> Void) {
        var node: SCNNode! = self
        while node != nil {
            if let transformNode = node as? TransformNode {
                block(transformNode)
            }
            node = node.parent
        }
    }

    @objc func enumerateBaseNodes(_ block: (BaseNode) -> Void) {
        childNodes.forEach { child in
            child.enumerateBaseNodes(block)
        }

        if let baseNode = self as? BaseNode {
            block(baseNode)
        }
    }

    /// Enumerates SCNNode hierarchy from given node (self) to the root.
    /// Invokes the block closure for each BaseNode.
    @objc func enumerateBaseNodesParents(_ block: (BaseNode) -> Void) {
        var node: SCNNode! = self
        while node != nil {
            if let baseNode = node as? BaseNode {
                block(baseNode)
            }
            node = node.parent
        }
    }
}
