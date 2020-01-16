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
    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var width: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var layoutOrientation: Orientation = Orientation.vertical {
        didSet { updateLayoutOrientation(layoutOrientation) }
    }
    @objc var defaultItemAlignment: Alignment {
        get { return linearLayout.defaultItemAlignment }
        set { linearLayout.defaultItemAlignment = newValue; setNeedsLayout() }
    }
    @objc var defaultItemPadding: UIEdgeInsets {
        get { return linearLayout.defaultItemPadding }
        set { linearLayout.defaultItemPadding = newValue; setNeedsLayout() }
    }
    @objc var scrollingEnabled: Bool = true {
        didSet { scrollView.enabled = scrollingEnabled; setNeedsLayout() }
    }

    fileprivate var linearLayout: UiLinearLayoutNode!
    fileprivate var scrollView: UiScrollViewNode!
    fileprivate var scrollBar: UiScrollBarNode!
    fileprivate(set) var items: [UiListViewItemNode] = []

    @objc override func setupNode() {
        super.setupNode()
        linearLayout = UiLinearLayoutNode()
        linearLayout.alignment = .centerCenter

        scrollBar = UiScrollBarNode()
        scrollBar.thickness = 0.04

        scrollView = UiScrollViewNode()
        scrollView.scrollBarVisibility = .always
        scrollView.addChild(scrollBar)
        scrollView.addChild(linearLayout)
        contentNode.addChildNode(scrollView)

        updateLayoutOrientation(layoutOrientation)
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

        if let defaultItemPadding = Convert.toPadding(props["defaultItemPadding"]) {
            self.defaultItemPadding = defaultItemPadding
        }

        if let scrollingEnabled = Convert.toBool(props["scrollingEnabled"]) {
            self.scrollingEnabled = scrollingEnabled
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let linearLayoutSize = linearLayout.getSize()
        let outputWidth = (width > 0) ? width : linearLayoutSize.width
        let outputHeight = (height > 0) ? height : linearLayoutSize.height
        return CGSize(width: outputWidth, height: outputHeight)
    }

    @objc override func updateLayout() {
        linearLayout.layoutIfNeeded()

        let size = getSize()
        let min = SCNVector3(-0.5 * size.width, -0.5 * size.height, -0.1)
        let max = SCNVector3(0.5 * size.width, 0.5 * size.height, 0.1)
        scrollView.scrollBounds = (min: min, max: max)
        scrollView.layoutIfNeeded()

        scrollBar.length = (layoutOrientation == .vertical) ? size.height : size.width
        let bounds: CGRect = scrollView.getBounds()
        if layoutOrientation == .vertical {
            scrollBar.localPosition = SCNVector3(bounds.maxX - 0.5 * scrollBar.thickness, bounds.midY, 0)
        } else {
            scrollBar.localPosition = SCNVector3(bounds.midX, bounds.minY + 0.5 * scrollBar.thickness, 0)
        }
        scrollBar.layoutIfNeeded()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        scrollBar.setNeedsLayout()
        scrollView.setNeedsLayout()
        linearLayout.setNeedsLayout()
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        guard let listItem = child as? UiListViewItemNode else { return false }
        items.append(listItem)
        linearLayout.addChild(listItem)
        updateItemsSize()
        setNeedsLayout()
        return true
    }

    fileprivate func updateItemsSize() {
        switch layoutOrientation {
        case .vertical:
            let maxWidthNode = items.max { nodeA, nodeB in
                return nodeA.getSize().width < nodeB.getSize().width
            }
            items.forEach {
                $0.preferredWidth = maxWidthNode?.getSize().width ?? 0.0
                $0.preferredHeight = 0.0
            }
        case .horizontal:
            let maxHeightNode = items.max { nodeA, nodeB in
                return nodeA.getSize().height < nodeB.getSize().height
            }
            items.forEach {
                $0.preferredHeight = maxHeightNode?.getSize().height ?? 0.0
                $0.preferredWidth = 0.0
            }
        }
    }

    @objc override func removeChild(_ child: TransformNode) {
        guard let listItem = child as? UiListViewItemNode, let indexToRemove = items.firstIndex(of: listItem) else { return }
        items.remove(at: indexToRemove)
        linearLayout.removeChild(listItem)
        updateItemsSize()
        setNeedsLayout()
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        guard let _ = selfHitTest(ray: ray) else { return nil }
        return scrollView.hitTest(ray: ray) ?? self
    }

    fileprivate func updateLayoutOrientation(_ orientation: Orientation) {
        scrollBar.scrollOrientation = orientation
        scrollView.scrollDirection = (orientation == .vertical) ? ScrollDirection.vertical : ScrollDirection.horizontal
        linearLayout.layoutOrientation = orientation
        setNeedsLayout()
    }
}

