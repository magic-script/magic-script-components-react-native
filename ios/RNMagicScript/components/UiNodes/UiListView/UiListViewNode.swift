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

@objc open class UiListViewNode: UiNode {
    @objc var width: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }

    @objc var layoutOrientation: Orientation = Orientation.vertical {
        didSet { setNeedsLayout() }
    }
    @objc var defaultItemAlignment: Alignment {
        get { return linearLayout.defaultItemAlignment }
        set { linearLayout.defaultItemAlignment = newValue; setNeedsLayout() }
    }
    @objc var itemAlignment: Alignment {
        get { return linearLayout.defaultItemAlignment }
        set { linearLayout.defaultItemAlignment = newValue; setNeedsLayout() }
    }
    @objc var defaultItemPadding: UIEdgeInsets {
        get { return linearLayout.defaultItemPadding }
        set { linearLayout.defaultItemPadding = newValue; setNeedsLayout() }
    }
    @objc var itemPadding: UIEdgeInsets {
        get { return linearLayout.defaultItemPadding }
        set { linearLayout.defaultItemPadding = newValue; setNeedsLayout() }
    }
    @objc var scrollingEnabled: Bool = true {
        didSet { setNeedsLayout() }
    }

    fileprivate var linearLayout: UiLinearLayoutNode!
    fileprivate var scrollView: UiScrollViewNode!
    fileprivate var scrollBar: UiScrollBarNode!
    fileprivate var items: [UiListViewItemNode] = []

    @objc override func setupNode() {
        super.setupNode()
        linearLayout = UiLinearLayoutNode(props: ["alignment": "center-center"])
        scrollBar = UiScrollBarNode(props: [:])
        scrollView = UiScrollViewNode(props: ["alignment": "center-center", "scrollBarVisibility": "always"])
        scrollView.addChild(scrollBar)
        scrollView.addChild(linearLayout)
        contentNode.addChildNode(scrollView)
        layoutIfNeeded()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let orientation = Convert.toOrientation(props["orientation"]) {
            self.layoutOrientation = orientation
        }

        if let defaultItemAlignment = Convert.toAlignment(props["defaultItemAlignment"]) {
            self.defaultItemAlignment = defaultItemAlignment
        }

        if let itemAlignment = Convert.toAlignment(props["itemAlignment"]) {
            self.itemAlignment = itemAlignment
        }

        if let defaultItemPadding = Convert.toPadding(props["defaultItemPadding"]) {
            self.defaultItemPadding = defaultItemPadding
        }

        if let itemPadding = Convert.toPadding(props["itemPadding"]) {
            self.itemPadding = itemPadding
        }

        if let scrollingEnabled = Convert.toBool(props["scrollingEnabled"]) {
            self.scrollingEnabled = scrollingEnabled
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let linearLayoutSize = linearLayout.getSize()
        return CGSize(width: linearLayoutSize.width, height: linearLayoutSize.height/4)
    }

    @objc override func updateLayout() {
        let size = getSize()
        let min = SCNVector3(-0.5 * size.width, -0.125 * size.height, -0.1)
        let max = SCNVector3(0.5 * size.width, 0.125 * size.height, 0.1)
        scrollView.scrollBounds = (min: min, max: max)
        scrollView.layoutIfNeeded()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        scrollView.setNeedsLayout()
    }

    @objc override func addChild(_ child: TransformNode) {
        guard let listItem = child as? UiListViewItemNode else { return }
        items.append(listItem)
        linearLayout.addChild(listItem)
        setNeedsLayout()
    }

    @objc override func removeChild(_ child: TransformNode) {
        guard let listItem = child as? UiListViewItemNode, let indexToRemove = items.firstIndex(of: listItem) else { return }
        items.remove(at: indexToRemove)
        linearLayout.removeChild(listItem)
        setNeedsLayout()
    }

    @objc override func setDebugMode(_ debug: Bool) {
//        super.setDebugMode(debug)
//        scrollBar.setDebugMode(debug)
        scrollView.setDebugMode(debug)
//        linearLayout.setDebugMode(debug)
//        items.forEach { $0.setDebugMode(debug) }
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        return scrollView.hitTest(ray: ray)
    }
}

