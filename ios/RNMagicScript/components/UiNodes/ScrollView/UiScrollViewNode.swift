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

@objc open class UiScrollViewNode: UiNode {

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var scrollDirection: ScrollDirection {
        get { return scrollView.scrollDirection }
        set { scrollView.scrollDirection = newValue; setNeedsLayout() }
    }
    // Scroll speed in scene units per second.
    @objc var scrollSpeed: CGFloat {
        get { return scrollView.scrollSpeed }
        set { scrollView.scrollSpeed = newValue }
    }
    @objc var scrollOffset: SCNVector3 {
        get { return scrollView.scrollOffset }
        set { scrollView.scrollOffset = newValue; setNeedsLayout() }
    }
    @objc var scrollValue: CGFloat {
        get { return scrollView.scrollValue }
        set { scrollView.scrollValue = newValue; setNeedsLayout() }
    }

    var scrollBounds: (min: SCNVector3, max: SCNVector3)? {
        get { return scrollView.scrollBounds }
        set { scrollView.scrollBounds = newValue; setNeedsLayout() }
    }
    @objc var scrollBarVisibility: ScrollBarVisibility {
        get { return scrollView.scrollBarVisibility }
        set { scrollView.scrollBarVisibility = newValue }
    }

    @objc public var onScrollChanged: ((_ sender: UiNode, _ value: CGFloat) -> (Void))?

    fileprivate var scrollView: ScrollView!

    @objc override func setupNode() {
        super.setupNode()

        scrollView = ScrollView(ownerNode: self)
        scrollView.onScrollChanged = { [weak self] sender, value in
            guard let strongSelf = self else { return }
            strongSelf.onScrollChanged?(strongSelf, value)
        }
        contentNode.addChildNode(scrollView.contentNode)
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        guard let _ = selfHitTest(ray: ray) else { return nil }
        return scrollView.hitTest(ray: ray) ?? self
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let scrollDirection = Convert.toScrollDirection(props["scrollDirection"]) {
            self.scrollDirection = scrollDirection
        }

        if let scrollSpeed = Convert.toCGFloat(props["scrollSpeed"]) {
            self.scrollSpeed = scrollSpeed
        }

        if let scrollOffset = Convert.toVector3(props["scrollOffset"]) {
            self.scrollOffset = scrollOffset
        }

        if let scrollValue = Convert.toCGFloat(props["scrollValue"]) {
            self.scrollValue = scrollValue
        }

        if let scrollBounds = props["scrollBounds"] as? [String: Any],
            let min = Convert.toVector3(scrollBounds["min"]),
            let max = Convert.toVector3(scrollBounds["max"]) {
            self.scrollBounds = (min: min, max: max)
        }

        if let scrollBarVisibility = Convert.toScrollBarVisibility(props["scrollBarVisibility"]) {
            self.scrollBarVisibility = scrollBarVisibility
        }
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        if !scrollView.addItem(child) {
            return super.addChild(child)
        }

        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        if !scrollView.removeItem(child) {
            super.removeChild(child)
        }
    }

    @objc override func _calculateSize() -> CGSize {
        return scrollView.getSize()
    }

    @objc override func getBounds(parentSpace: Bool = false, scaled: Bool = true) -> CGRect {
        guard let scrollBounds = scrollView.scrollBounds else { return CGRect.zero }
        let min = scrollBounds.min
        let size = getSize(scaled: scaled)
        let origin = CGPoint(x: CGFloat(min.x), y: CGFloat(min.y))
        let offset = parentSpace ? CGPoint(x: CGFloat(localPosition.x), y: CGFloat(localPosition.y)) : CGPoint.zero
        return CGRect(origin: origin + offset, size: size)
    }

    @objc override func updateLayout() {
        scrollView.updateLayout()
    }

    @objc override func postUpdate() {
        scrollView.updateClippingPlanes()
    }
}

extension UiScrollViewNode: Dragging {
    var dragAxis: Ray? {
        return scrollView.dragAxis
    }
    var dragRange: CGFloat {
        return scrollView.dragRange
    }
    var dragValue: CGFloat {
        get { return scrollView.dragValue }
        set { scrollView.dragValue = newValue }
    }
}
