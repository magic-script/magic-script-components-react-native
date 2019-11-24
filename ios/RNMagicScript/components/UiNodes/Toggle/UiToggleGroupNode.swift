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

@objc open class UiToggleGroupNode: UiNode {
    @objc var allowMultipleOn: Bool = false {
        didSet { }
    }

    @objc var allowAllOff: Bool = false {
        didSet { }
    }

    @objc var allTogglesOff: Bool = false {
        didSet { }
    }

    fileprivate var linearLayout: UiLinearLayoutNode?
    fileprivate var itemsList: Array<UiToggleNode> = []
    fileprivate var listNode: SCNNode!
    fileprivate(set) var listGridLayoutNode: UiGridLayoutNode!

    override func setupNode() {
        super.setupNode()

        // List items node
        listNode = SCNNode()
        contentNode.addChildNode(listNode)

        listGridLayoutNode = UiGridLayoutNode()
        listGridLayoutNode.columns = 1
        listGridLayoutNode.defaultItemPadding = UIEdgeInsets.zero
        listGridLayoutNode.defaultItemAlignment = Alignment.centerLeft
        listGridLayoutNode.alignment = Alignment.topCenter
        listGridLayoutNode.renderingOrder = 1
        listNode.addChildNode(listGridLayoutNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let allowMultipleOn = Convert.toBool(props["allowMultipleOn"]) {
            self.allowMultipleOn = allowMultipleOn
        }

        if let allowAllOff = Convert.toBool(props["allowAllOff"]) {
            self.allowAllOff = allowAllOff
        }

        if let allTogglesOff = Convert.toBool(props["allTogglesOff"]) {
            self.allTogglesOff = allTogglesOff
        }
    }

    @objc override func addChild(_ child: TransformNode) {
        if let toggleNode = child as? UiToggleNode {
            addToggleNode(toggleNode)
        }

        if let linearLayout = child as? UiLinearLayoutNode {
            for childNode in linearLayout.items {
                if let toggleNode = childNode as? UiToggleNode {
                    addToggleNode(toggleNode)
                }
            }
            self.linearLayout = linearLayout
            contentNode.addChildNode(linearLayout)
            setNeedsLayout()
        }
    }

    override func updateLayout() {
        super.updateLayout()

    }

    fileprivate func addToggleNode(_ toggleNode: UiToggleNode) {
        itemsList.append(toggleNode)
    }

    @objc override func removeChild(_ child: TransformNode) {
        guard let toggleItem = child as? UiToggleNode else { return }
        itemsList.removeAll { node -> Bool in
            return node == toggleItem
        }
        listGridLayoutNode.removeChild(child)
        setNeedsLayout()
    }

    override func hitTest(ray: Ray) -> TransformNode? {
        return self.linearLayout?.hitTest(ray: ray)
    }
}
