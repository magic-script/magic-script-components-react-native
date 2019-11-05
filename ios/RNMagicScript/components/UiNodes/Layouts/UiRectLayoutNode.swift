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

@objc open class UiRectLayoutNode: UiLayoutNode {
    @objc var contentAlignment: Alignment {
        get { return gridLayout.defaultItemAlignment }
        set { gridLayout.defaultItemAlignment = newValue; setNeedsLayout() }
    }
    @objc var padding: UIEdgeInsets {
        get { return gridLayout.defaultItemPadding }
        set { gridLayout.defaultItemPadding = newValue; setNeedsLayout() }
    }

    var itemsCount: Int { return gridLayout.itemsCount }
    fileprivate var gridLayout = GridLayout()

    @objc override func setupNode() {
        super.setupNode()
        contentNode.addChildNode(gridLayout.container)
        gridLayout.columns = 1
        gridLayout.rows = 1
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        return gridLayout.hitTest(ray: ray, node: self)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let contentAlignment = Convert.toAlignment(props["contentAlignment"]) {
            self.contentAlignment = contentAlignment
        }

        if let padding = Convert.toPadding(props["padding"]) {
            self.padding = padding
        }
    }

    @objc override func addChild(_ child: TransformNode) {
        if gridLayout.itemsCount == 0 {
            gridLayout.addItem(child)
            setNeedsLayout()
        }
    }

    @objc override func removeChild(_ child: TransformNode) {
        if gridLayout.removeItem(child) {
            setNeedsLayout()
        }
    }

    @objc override func _calculateSize() -> CGSize {
        return gridLayout.getSize()
    }

    @objc override func updateLayout() {
        // Invoke getSize to make sure the grid's sizes are calcualted and cached in gridDesc.
        let _ = getSize()
        gridLayout.updateLayout()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        gridLayout.invalidate()
    }
}
