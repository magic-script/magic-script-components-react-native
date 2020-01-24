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

open class ScrollView {
    var scrollDirection: ScrollDirection = .horizontal {
        didSet { setNeedsLayout() }
    }
    // Scroll speed in scene units per second.
    var scrollSpeed: CGFloat = 0.1
    var scrollOffset: SCNVector3 = SCNVector3Zero {
        didSet { setNeedsLayout() }
    }
    fileprivate var _scrollValue: CGFloat = 0
    var scrollValue: CGFloat {
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
    var scrollBounds: (min: SCNVector3, max: SCNVector3)? {
        didSet { invalidateClippingPlanes = true; setNeedsLayout() }
    }
    var scrollBarVisibility: ScrollBarVisibility = .auto {
        didSet { updateScrollBarVisibility() }
    }

    var onScrollChanged: ((_ sender: ScrollView, _ value: CGFloat) -> (Void))?

    fileprivate(set) weak var ownerNode: UiNode?
    let contentNode = SCNNode()
    fileprivate let proxyNode = SCNNode()
    fileprivate(set) weak var scrollBar: UiScrollBarNode?
    fileprivate(set) weak var scrollContent: Layouting?
    fileprivate var updateLayoutNeeded: Bool = false
    fileprivate var invalidateClippingPlanes: Bool = false

    init(ownerNode: UiNode) {
        self.ownerNode = ownerNode
        contentNode.addChildNode(proxyNode)
    }

    deinit {
        contentNode.removeFromParentNode()
    }

    @objc func hitTest(ray: Ray) -> TransformNode? {
        guard let _ = scrollBounds else { return nil }
        let nodes: [HitTesting] = [scrollContent, scrollBar].compactMap { $0 as? HitTesting }
        for node in nodes {
            if let hitNode = node.hitTest(ray: ray) {
                return hitNode
            }
        }

        return nil
    }

    @discardableResult
    func addItem(_ item: Layouting) -> Bool {
        if item is UiScrollBarNode {
            guard scrollBar == nil else { return false }
            scrollBar = item as? UiScrollBarNode
            contentNode.addChildNode(scrollBar!)
            setNeedsLayout()
            updateScrollBarVisibility()
            return true
        }

        guard scrollContent == nil else { return false }
        scrollContent = item
        proxyNode.addChildNode(item.layoutContentNode)
        invalidateClippingPlanes = true
        setNeedsLayout()

        return true
    }

    func removeItem(_ item: Layouting) -> Bool {
        if item === scrollBar {
            scrollBar?.removeFromParentNode()
            scrollBar = nil
            setNeedsLayout()
            return true
        } else if item === scrollContent {
            item.layoutContentNode.removeFromParentNode()
            scrollContent = nil
            invalidateClippingPlanes = true
            setNeedsLayout()
            return true
        }

        return false
    }

    func updateLayout() {
        guard let ownerNode = ownerNode else { return }
        guard updateLayoutNeeded else { return }
        updateLayoutNeeded = false

        scrollBar?.layoutIfNeeded()
        scrollContent?.layoutIfNeeded()

        // Update scroll content
        let bounds = ownerNode.getBounds()
        let scrollSize = bounds.size
        let contentSize: CGSize
        if let scrollContent = scrollContent {
            contentSize = scrollContent.getSize(scaled: true)
            let contentBounds = scrollContent.getBounds(parentSpace: true, scaled: true)
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

    func updateClippingPlanes() {
        guard let scrollBounds = scrollBounds else { return }
        guard let scrollContentNode = scrollContent?.layoutContentNode else { return }

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

            let worldSpacePlanes: [SCNVector4] = planes.map { scrollContentNode.convertPlane(Plane(vector: $0), to: nil).toVector4() }
            scrollContentNode.setClippingPlanes(worldSpacePlanes)
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

extension ScrollView: Layouting {
    @objc var layoutContentNode: SCNNode {
        return contentNode
    }
    @objc func getSize(scaled: Bool = true) -> CGSize {
        guard let scrollBounds = scrollBounds else { return CGSize.zero }
        let width: CGFloat = CGFloat(scrollBounds.max.x - scrollBounds.min.x)
        let height: CGFloat = CGFloat(scrollBounds.max.y - scrollBounds.min.y)
        return CGSize(width: width, height: height)
    }
    @objc func getBounds(parentSpace: Bool, scaled: Bool) -> CGRect {
        guard let scrollBounds = scrollBounds else { return CGRect.zero }
        let min = scrollBounds.min
        let size = getSize(scaled: scaled)
        let origin = CGPoint(x: CGFloat(min.x), y: CGFloat(min.y))
        return CGRect(origin: origin, size: size)
    }
    @objc func setNeedsLayout() {
        updateLayoutNeeded = true
    }
    @objc var isLayoutNeeded: Bool {
        return updateLayoutNeeded
    }
    @objc func layoutIfNeeded() {
        if isLayoutNeeded {
            updateLayoutNeeded = false
            updateLayout()
        }
    }
}

extension ScrollView: Dragging {
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
        guard let contentSize = scrollContent?.getSize(scaled: true) else { return 0 }
        let size = getSize()
        return (scrollDirection == .horizontal) ? contentSize.width - size.width : contentSize.height - size.height
    }

    var dragValue: CGFloat {
        get { return scrollValue }
        set {
            let prevScrollValue = scrollValue
            scrollValue = newValue
            if prevScrollValue != scrollValue {
                updateLayout()
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
