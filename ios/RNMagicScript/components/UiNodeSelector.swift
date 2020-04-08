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

//sourcery: AutoMockable
protocol NodeSelecting {
    func hitTest(ray: Ray) -> (node: BaseNode?, point: SCNVector3?)?
    func draggingHitTest(ray: Ray) -> Dragging?
}

class UiNodeSelector: NodeSelecting  {
    let rootNode: SCNNode

    init(_ rootNode: SCNNode) {
        self.rootNode = rootNode
    }

    func hitTest(ray: Ray) -> (node: BaseNode?, point: SCNVector3?)? {
        let topNodes: [BaseNode] = rootNode.childNodes.filter { $0 is BaseNode }.map { $0 as! BaseNode }
        var hitNodes: [(node: BaseNode?, point: SCNVector3?)] = []

        for node in topNodes {
            if let hitNode = node.hitTest(ray: ray) {
                hitNodes.append(hitNode)
            }
        }

        hitNodes.sort { (node1, node2) -> Bool in
            guard let worldPosition1 = node1.node?.convertPosition(node1.point ?? SCNVector3.zero, to: nil),
                let worldPosition2 = node2.node?.convertPosition(node2.point ?? SCNVector3.zero, to: nil) else { return false }
            let dist1 = (worldPosition1 - ray.begin).lengthSq()
            let dist2 = (worldPosition2 - ray.begin).lengthSq()
            return dist1 < dist2
        }

        return hitNodes.first
    }

    func draggingHitTest(ray: Ray) -> Dragging? {
        guard let hitNode = hitTest(ray: ray) else { return nil }

        var node: SCNNode? = hitNode.node
        while node != nil {
            if node is Dragging {
                break
            }

            node = node?.parent
        }

        return node as? Dragging
    }
}
