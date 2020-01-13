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
    @objc var scrollDirection: ScrollDirection = .horizontal {
        didSet { setNeedsLayout() }
    }
    // Scroll speed in scene units per second.
    @objc var scrollSpeed: CGFloat = 0.1
    @objc var scrollOffset: SCNVector3 = SCNVector3Zero {
        didSet { setNeedsLayout() }
    }
    fileprivate var _scrollValue: CGFloat = 0
    @objc var scrollValue: CGFloat {
        get { return _scrollValue }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, 0.0, 1.0)
            if (_scrollValue != clampedValue) {
                _scrollValue = clampedValue
                scrollBar?.thumbPosition = clampedValue
                setNeedsLayout()
            }
        }
    }
    //@objc
    var scrollBounds: (min: SCNVector3, max: SCNVector3)? {
        didSet { invalidateClippingPlanes = true; setNeedsLayout() }
    }
    @objc var scrollBarVisibility: ScrollBarVisibility = .auto {
        didSet { updateScrollBarVisibility() }
    }

    @objc public var onScrollChanged: ((_ sender: UiNode, _ value: CGFloat) -> (Void))?

    fileprivate weak var scrollBar: UiScrollBarNode?
    fileprivate weak var scrollContent: TransformNode?
    fileprivate var proxyNode: SCNNode!
    fileprivate var invalidateClippingPlanes: Bool = false

    deinit {
        scrollContent?.resetClippingPlanes()
    }

    @objc override func setupNode() {
        super.setupNode()

        proxyNode = SCNNode()
        contentNode.addChildNode(proxyNode)
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        guard let _ = selfHitTest(ray: ray) else { return nil }

        let nodes: [TransformNode?] = [scrollContent, scrollBar]
        for node in nodes {
            if let hitNode = node?.hitTest(ray: ray) {
                return hitNode
            }
        }

        return self
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
        if child is UiScrollBarNode {
            guard scrollBar == nil else { return false }
            scrollBar = child as? UiScrollBarNode
            contentNode.addChildNode(child)
            setNeedsLayout()
            updateScrollBarVisibility()
            return true
        }

        guard scrollContent == nil else { return false }
        scrollContent = child
        proxyNode.addChildNode(child)
        invalidateClippingPlanes = true
        setNeedsLayout()
        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        if child == scrollBar {
            scrollBar?.removeFromParentNode()
            scrollBar = nil
            setNeedsLayout()
        } else if child == scrollContent {
            scrollContent?.removeFromParentNode()
            scrollContent = nil
            invalidateClippingPlanes = true
            setNeedsLayout()
        }
    }

    @objc override func _calculateSize() -> CGSize {
        guard let scrollBounds = scrollBounds else { return CGSize.zero }
        let width: CGFloat = CGFloat(scrollBounds.max.x - scrollBounds.min.x)
        let height: CGFloat = CGFloat(scrollBounds.max.y - scrollBounds.min.y)
        return CGSize(width: width, height: height)
    }

    @objc override func getBounds(parentSpace: Bool = false, scaled: Bool = true) -> CGRect {
        guard let scrollBounds = scrollBounds else { return CGRect.zero }
        let min = scrollBounds.min
        let size = getSize(scaled: scaled)
        let origin = CGPoint(x: CGFloat(min.x), y: CGFloat(min.y))
        let offset = parentSpace ? CGPoint(x: CGFloat(localPosition.x), y: CGFloat(localPosition.y)) : CGPoint.zero
        return CGRect(origin: origin + offset, size: size)
    }

    @objc override func updateLayout() {
        scrollBar?.layoutIfNeeded()
        scrollContent?.layoutIfNeeded()

        // Update scroll content
        let bounds = getBounds()
        let scrollSize = bounds.size
        let contentSize: CGSize
        if let scrollContent = scrollContent {
            contentSize = scrollContent.getSize()
            let contentBounds = scrollContent.getBounds(parentSpace: true)
            let contentOffset = SCNVector3(bounds.minX - contentBounds.minX, bounds.maxY - contentBounds.maxY, 0)
            proxyNode.position = contentOffset
        } else {
            contentSize = CGSize.zero
            proxyNode.position = SCNVector3Zero
        }

        var shift: CGPoint = CGPoint.zero
        switch scrollDirection {
        case .horizontal:
            shift.x = -scrollValue * max(0, contentSize.width - scrollSize.width)
        case .vertical:
            shift.y = scrollValue * max(0, contentSize.height - scrollSize.height)
        }
        proxyNode.position += SCNVector3(shift.x, shift.y, 0)

        // Update scroll bar
        scrollBar?.thumbPosition = scrollValue
    }

    @objc override func postUpdate() {
        guard let scrollBounds = scrollBounds else { return }

        // Update clipping planes
        if invalidateClippingPlanes {
            invalidateClippingPlanes = false

            let min = scrollBounds.min
            let max = scrollBounds.max
            let planes: [SCNVector4] = [
                SCNVector4( 1, 0, 0,-min.x),
                SCNVector4(-1, 0, 0, max.x),
                SCNVector4(0, 1, 0,-min.y),
                SCNVector4(0,-1, 0, max.y),
                SCNVector4(0, 0, 1,-min.z),
                SCNVector4(0, 0,-1, max.z),
            ]

            let worldSpacePlanes: [SCNVector4] = planes.map { convertPlane(Plane(vector: $0), to: nil).toVector4() }
            scrollContent?.setClippingPlanes(worldSpacePlanes)
        }
    }


    fileprivate func updateScrollBarVisibility() {
        switch scrollBarVisibility {
            case .always:
                scrollBar?.visible = true
            case .auto:
                scrollBar?.setVisible(true, animated: true, delay: 0.0) { [weak self] in
                    self?.scrollBar?.setVisible(false, animated: true, delay: 2.0)
                }
            case .off:
                scrollBar?.visible = false
        }
    }
}

extension UiScrollViewNode: Dragging {
    var dragAxis: Ray? {
        guard let scrollBounds = scrollBounds else { return nil }
        let min = scrollBounds.min
        let max = scrollBounds.max
        let center: SCNVector3 = 0.5 * (min + max)
        let direction: SCNVector3
        if scrollDirection == .horizontal {
            direction = SCNVector3(center.x - max.x, center.y, center.z)
        } else {
            direction = SCNVector3(center.x, max.y - center.y, center.z)
        }

        return Ray(begin: center - direction, direction: direction.normalized(), length: CGFloat(2 * direction.length()))
    }

    var dragRange: CGFloat {
        guard let contentSize = scrollContent?.getSize() else { return 0 }
        let size = getSize()
        return (scrollDirection == .horizontal) ? contentSize.width - size.width : contentSize.height - size.height
    }

    var dragValue: CGFloat {
        get { return scrollValue }
        set {
            let prevScrollValue = scrollValue
            scrollValue = newValue
            if prevScrollValue != scrollValue {
                layoutIfNeeded()
                onScrollChanged?(self, scrollValue)

                if scrollBarVisibility == .auto {
                    scrollBar?.setVisible(true, animated: true, delay: 0.0) { [weak self] in
                        self?.scrollBar?.setVisible(false, animated: true, delay: 2.0)
                    }
                }
            }
        }
    }
}
