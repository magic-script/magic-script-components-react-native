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

    @objc var scrollDirection: ScrollDirection = .horizontal {
        didSet { setNeedsLayout() }
    }
    // Scroll speed in scene units per second.
    @objc var scrollSpeed: CGFloat = 0.1
    @objc var scrollOffset: SCNVector3 = SCNVector3() {
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
        didSet { setNeedsLayout() }
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

    @objc override func addChild(_ child: TransformNode) {
        if child is UiScrollBarNode {
            guard scrollBar == nil else { return }
            scrollBar = child as? UiScrollBarNode
            contentNode.addChildNode(child)
            setNeedsLayout()
        } else {
            guard scrollContent == nil else { return }
            scrollContent = child
            proxyNode.addChildNode(child)
            invalidateClippingPlanes = true
            setNeedsLayout()
        }
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

    @objc override func getBounds(parentSpace: Bool = false) -> CGRect {
        guard let scrollBounds = scrollBounds else { return CGRect.zero }
        let min = scrollBounds.min
        let max = scrollBounds.max
        return CGRect(x: CGFloat(min.x), y: CGFloat(min.y), width: CGFloat(max.x - min.x), height: CGFloat(max.y - min.y))
    }

    @objc override func updateLayout() {
        guard let scrollBounds = scrollBounds else { return }
        scrollBar?.layoutIfNeeded()
        scrollContent?.layoutIfNeeded()

        let bounds = getBounds()
        let scrollSize = bounds.size
        let contentSize: CGSize
        if let scrollContent = scrollContent {
            contentSize = scrollContent.getSize()

            let contentPositionNegated = scrollContent.position.negated()
            var offset = CGPoint(
                x: 0.5 * (contentSize.width - scrollSize.width),
                y: 0.5 * (contentSize.height - scrollSize.height)
            )

//            if let uiNode = scrollContent as? UiNode {
//                let alignOffset = uiNode.alignment.shiftDirection
//                offset.x -= alignOffset.x * contentSize.width
//                offset.y -= alignOffset.y * contentSize.height
//            }
            proxyNode.position = contentPositionNegated + SCNVector3(offset.x, offset.y, 0)
        } else {
            contentSize = CGSize.zero
            proxyNode.position = SCNVector3Zero
        }

        scrollBar?.thumbPosition = scrollValue
        var shift: CGPoint = CGPoint.zero
        switch scrollDirection {
        case .horizontal:
            shift.x = -scrollValue * max(0, contentSize.width - scrollSize.width)
        case .vertical:
            shift.y = (scrollValue - 1) * max(0, contentSize.height - scrollSize.height)
        }
        proxyNode.position += SCNVector3(shift.x, shift.y, 0)

        if invalidateClippingPlanes {
            invalidateClippingPlanes = false

            let min = scrollBounds.min
            let max = scrollBounds.max
            scrollContent?.setClippingPlanes([
                SCNVector4( 1, 0, 0,-min.x),
                SCNVector4(-1, 0, 0, max.x),
                SCNVector4(0, 1, 0,-min.y),
                SCNVector4(0,-1, 0, max.y),
                SCNVector4(0, 0, 1,-min.z),
                SCNVector4(0, 0,-1, max.z),
            ])
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

    var contentLength: CGFloat {
        guard let contentSize = scrollContent?.getSize() else { return 0 }
        return (scrollDirection == .horizontal) ? contentSize.width : contentSize.height
    }

    var dragValue: CGFloat {
        get { return scrollValue }
        set { scrollValue = newValue; layoutIfNeeded() }
    }
}
