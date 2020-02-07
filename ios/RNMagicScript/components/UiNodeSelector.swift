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
protocol NodeSelecting {
    func hitTest(ray: Ray) -> TransformNode?
    func draggingHitTest(ray: Ray) -> Dragging?
}

class UiNodeSelector: NodeSelecting  {
    let rootNode: SCNNode

    init(_ rootNode: SCNNode) {
        self.rootNode = rootNode
    }

    func hitTest(ray: Ray) -> TransformNode? {
        let topNodes: [TransformNode] = rootNode.childNodes.filter { $0 is TransformNode }.map { $0 as! TransformNode }
        var hitNodes: [TransformNode] = []

        for node in topNodes {
            if let hitNode = node.hitTest(ray: ray) {
                hitNodes.append(hitNode)
            }
        }

        hitNodes.sort { (node1, node2) -> Bool in
            let worldPosition1 = node1.convertPosition(node1.position, to: nil)
            let worldPosition2 = node2.convertPosition(node2.position, to: nil)
            let dist1 = (worldPosition1 - ray.begin).lengthSq()
            let dist2 = (worldPosition2 - ray.begin).lengthSq()
            return dist1 < dist2
        }

        return hitNodes.first
    }

    func draggingHitTest(ray: Ray) -> Dragging? {
        guard let hitNode = hitTest(ray: ray) else { return nil }

        var node: SCNNode? = hitNode
        while node != nil {
            if node is Dragging {
                break
            }

            node = node?.parent
        }

        return node as? Dragging
    }
}
