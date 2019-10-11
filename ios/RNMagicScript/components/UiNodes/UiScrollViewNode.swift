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
    @objc var scrollValue: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    //@objc
    var scrollBounds: (min: SCNVector3, max: SCNVector3)? {
        didSet { setNeedsLayout() }
    }
    @objc var scrollBarVisibility: ScrollBarVisibility = .auto {
        didSet { setNeedsLayout() }
    }

    @objc public var onScrollChanged: ((_ sender: UiNode, _ value: CGFloat) -> (Void))?

    @objc override func setupNode() {
        super.setupNode()
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

    @objc override func _calculateSize() -> CGSize {
        return CGSize.zero
    }

    @objc override func updateLayout() {

    }
}