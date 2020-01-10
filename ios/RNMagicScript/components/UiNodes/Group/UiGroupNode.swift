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

    fileprivate var group = GroupContainer()

    @objc override func setupNode() {
        super.setupNode()
        contentNode.addChildNode(group.container)
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        group.addItem(child)
        setNeedsLayout()
        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        group.removeItem(child)
        setNeedsLayout()
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        // NOTE: Uncomment this line to limit the hitTest only to the area of the group.
        // If the hitTest is limited to the group's area then any compnent that
        // change its size in runtime may not work properly.
        // TODO: We need a better mechanism for updating layout of parent containers
        // to make "hitTest limitation" to work correctly.
        //guard let _ = selfHitTest(ray: ray) else { return nil }
        return group.hitTest(ray: ray)
    }

    @objc override func _calculateSize() -> CGSize {
        return group.getSize()
    }

    @objc override func getBounds(parentSpace: Bool = false, scaled: Bool = true) -> CGRect {
        let bounds = group.getBounds()
        let offset: CGPoint = parentSpace ? CGPoint(x: CGFloat(position.x), y: CGFloat(position.y)) : CGPoint.zero
        return bounds.offsetBy(dx: offset.x, dy: offset.y)
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        group.invalidate()
    }
}

extension UiGroupNode: TransformNodeContainer {
    var itemsCount: Int { return group.container.childNodes.count }
}
