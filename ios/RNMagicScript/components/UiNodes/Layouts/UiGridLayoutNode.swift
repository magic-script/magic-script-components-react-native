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

@objc open class UiGridLayoutNode: UiNode {

    @objc var columns: Int {
        get { return gridLayout.columns }
        set { gridLayout.columns = newValue; setNeedsLayout() }
    }
    @objc var rows: Int {
        get { return gridLayout.rows }
        set { gridLayout.rows = newValue; setNeedsLayout() }
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

    var itemsCount: Int { return gridLayout.itemsCount }
    fileprivate var gridLayout = GridLayout()

    @objc override func setupNode() {
        super.setupNode()
        contentNode.addChildNode(gridLayout.container)
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        return gridLayout.hitTest(ray: ray, node: self)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let columns = Convert.toInt(props["columns"]) {
            self.columns = columns
        }

        if let rows = Convert.toInt(props["rows"]) {
            self.rows = rows
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

    @objc override func addChild(_ child: TransformNode) {
        gridLayout.addItem(child)
        setNeedsLayout()
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
}
