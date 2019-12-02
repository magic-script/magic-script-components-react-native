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

@objc open class UiLinearLayoutNode: UiLayoutNode {
    @objc var layoutOrientation: Orientation = Orientation.vertical {
        didSet { updateOrientation(); setNeedsLayout() }
    }
    @objc var defaultItemAlignment: Alignment {
        get { return gridLayout.defaultItemAlignment }
        set { gridLayout.defaultItemAlignment = newValue; setNeedsLayout() }
    }
    @objc var defaultItemPadding: UIEdgeInsets {
        get { return gridLayout.defaultItemPadding }
        set { gridLayout.defaultItemPadding = newValue; setNeedsLayout() }
    }
    @objc var skipInvisibleItems: Bool {
        get { return gridLayout.skipInvisibleItems }
        set { gridLayout.skipInvisibleItems = newValue; setNeedsLayout() }
    }

    fileprivate var gridLayout = GridLayout()

    @objc override func setupNode() {
        super.setupNode()
        updateOrientation()
        contentNode.addChildNode(gridLayout.container)
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        return gridLayout.hitTest(ray: ray, node: self)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let orientation = Convert.toOrientation(props["orientation"]) {
            self.layoutOrientation = orientation
        }

        if let defaultItemAlignment = Convert.toAlignment(props["defaultItemAlignment"]) {
            self.defaultItemAlignment = defaultItemAlignment
        }

        if let defaultItemPadding = Convert.toPadding(props["defaultItemPadding"]) {
            self.defaultItemPadding = defaultItemPadding
        }

        if let skipInvisibleItems = Convert.toBool(props["skipInvisibleItems"]) {
            self.skipInvisibleItems = skipInvisibleItems
        }
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        gridLayout.addItem(child)
        setNeedsLayout()
        return true
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
        _ = getSize()
        gridLayout.updateLayout()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        gridLayout.invalidate()
    }

    fileprivate func updateOrientation() {
        switch layoutOrientation {
        case .vertical:
            gridLayout.columns = 1
            gridLayout.rows = 0
        case .horizontal:
            gridLayout.columns = 0
            gridLayout.rows = 1
        }
    }
}

extension UiLinearLayoutNode: TransformNodeContainer {
    var itemsCount: Int { return gridLayout.itemsCount }
}
