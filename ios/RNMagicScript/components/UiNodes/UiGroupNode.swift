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

import SceneKit

@objc open class UiGroupNode: UiNode {

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        // Do not perform selfHitTest on group node becuase group may include
        // child components which can change their size during interaction
        // (e.g. UiDropdownList). Therefore, the bounds of the group may not cover
        // some parts of components. Instead, perform hitTest on each child node.
        //guard let _ = selfHitTest(ray: ray) else { return nil }
        let nodes: [TransformNode] = contentNode.childNodes.filter { $0 is TransformNode }.map { $0 as! TransformNode }
        for node in nodes {
            if let hitNode = node.hitTest(ray: ray) {
                return hitNode
            }
        }

        return nil
    }

    @objc override func _calculateSize() -> CGSize {
        return getBoundsCollection().size
    }

    @objc override func getBounds(parentSpace: Bool = false, scaled: Bool = true) -> CGRect {
        let bounds = getBoundsCollection()
        let offset: CGPoint = parentSpace ? CGPoint(x: CGFloat(localPosition.x), y: CGFloat(localPosition.y)) : CGPoint.zero
        return bounds.offsetBy(dx: offset.x, dy: offset.y)
    }

    @objc override func updateLayout() {
    }

    @objc override func updatePivot() {
        // Do not update pivot for group node.
        // Group node has it's own size/bounds (based on child nodes), but
        // since it's a nodes' container, setting alignment does not make sense.
    }
}

// MARK: - Helpers
extension UiGroupNode {
    @objc fileprivate func getBoundsCollection() -> CGRect {
        let nodes: [TransformNode] = contentNode.childNodes.filter { $0 is TransformNode }.map { $0 as! TransformNode }
        guard !nodes.isEmpty else { return CGRect.zero }
        var bounds: CGRect = nodes[0].getBounds(parentSpace: true)
        for i in 1..<nodes.count {
            let b = nodes[i].getBounds(parentSpace: true)
            bounds = bounds.union(b)
        }

        return bounds
    }
}
