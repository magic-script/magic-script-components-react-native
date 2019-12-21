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

@objc open class GroupContainer: NSObject {

    let container: SCNNode = SCNNode()
    fileprivate var bounds: CGRect?

    deinit {
        container.removeFromParentNode()
    }

    func addItem(_ item: TransformNode) {
        container.addChildNode(item)
        invalidate()
    }

    func removeItem(_ item: TransformNode) {
        if item.parent == container {
            item.removeFromParentNode()
            invalidate()
        }
    }

    @objc func hitTest(ray: Ray) -> TransformNode? {
        guard let _ = bounds else { return nil }
        let nodes: [TransformNode] = container.childNodes.compactMap { $0 as? TransformNode }
        for node in nodes {
            if let hitNode = node.hitTest(ray: ray) {
                return hitNode
            }
        }

        return nil
    }

    func invalidate() {
        bounds = nil
    }

    var isRecalculationNeeded: Bool { return bounds == nil}

    func recalculateIfNeeded() {
        if bounds == nil {
            bounds = getBoundsCollection()
        }
    }

    func getSize() -> CGSize {
        return getBounds().size
    }

    func getBounds() -> CGRect {
        recalculateIfNeeded()
        return bounds!
    }

    fileprivate func getBoundsCollection() -> CGRect {
        let nodes: [TransformNode] = container.childNodes.compactMap { $0 as? TransformNode }
        guard !nodes.isEmpty else { return CGRect.zero }
        var bounds: CGRect = nodes[0].getBounds(parentSpace: true)
        for i in 1..<nodes.count {
            let b = nodes[i].getBounds(parentSpace: true)
            bounds = bounds.union(b)
        }

        return bounds
    }
}
