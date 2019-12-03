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

@objc open class UiListViewItemNode: UiNode {
    @objc var backgroundColor: UIColor = UIColor.clear {
        didSet {
            backgroundNode.geometry?.firstMaterial?.diffuse.contents = backgroundColor
        }
    }

    fileprivate var backgroundNode: SCNNode!

    var width: CGFloat = 0.0 {
        didSet {
            (backgroundNode.geometry as? SCNPlane)?.width = width
            setNeedsLayout()
        }
    }

    @objc override func setupNode() {
        super.setupNode()

        backgroundNode = NodesFactory.createPlaneNode(width: 0.0, height: 0.0, color: backgroundColor)
        backgroundNode.geometry?.firstMaterial?.readsFromDepthBuffer = false
        backgroundNode.renderingOrder = 0
        contentNode.addChildNode(backgroundNode)
    }

    fileprivate(set) var childNode: TransformNode? = nil

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let backgroundColor = Convert.toColor(props["backgroundColor"]) {
            self.backgroundColor = backgroundColor
        }
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        super.addChild(child)
        childNode = child
        child.renderingOrder = 1
        setNeedsLayout()
        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        super.removeChild(child)
        childNode = nil
        setNeedsLayout()
    }

    override func _calculateSize() -> CGSize {
        if let childNodeSize = childNode?.getSize() {
            return CGSize(width: width > 0.0 ? width : childNodeSize.width, height: childNodeSize.height)
        }
        return CGSize.zero
    }

    override func updateLayout() {
        super.updateLayout()
        let size = getSize()

        if let plane = backgroundNode.geometry as? SCNPlane {
            plane.width = size.width
            plane.height = size.height
            plane.firstMaterial?.diffuse.contents = backgroundColor
        }
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        guard let _ = selfHitTest(ray: ray) else { return nil }
        return childNode
    }
}
