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
    @objc var allowMultipleOn: Bool = false
    @objc var allowAllOff: Bool = false
    @objc var allTogglesOff: Bool = false // not implemented

    fileprivate(set) weak var customNodeContainer: TransformNode?
    fileprivate(set) var defaultNodeContainer: GroupContainer?
    fileprivate(set) var itemsList: Array<UiToggleNode> = []

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

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        if let toggleNode = child as? UiToggleNode, customNodeContainer == nil {
            if defaultNodeContainer == nil {
                defaultNodeContainer = GroupContainer()
                contentNode.addChildNode(defaultNodeContainer!.container)
            }

            defaultNodeContainer?.addItem(toggleNode)
            registerToggleGroupHandler(toggleNode)
            itemsList.append(toggleNode)
            setNeedsLayout()
            return true
        }

        if defaultNodeContainer == nil && customNodeContainer == nil && child is TransformNodeContainer {
            customNodeContainer = child
        }

        contentNode.addChildNode(child)
        setNeedsLayout()

        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        if let toggleItem = child as? UiToggleNode {
            itemsList.removeAll { $0 == toggleItem }
            if itemsList.isEmpty {
                defaultNodeContainer = nil
            }
            setNeedsLayout()
        }

        if self.customNodeContainer == child {
            child.removeFromParentNode()
            customNodeContainer = nil
            setNeedsLayout()
        }

        super.removeChild(child)
    }

    override func hitTest(ray: Ray) -> TransformNode? {
        guard let _ = selfHitTest(ray: ray) else { return nil }
        if let container = customNodeContainer, let result = container.hitTest(ray: ray) {
            return result
        } else if let container = defaultNodeContainer, let result = container.hitTest(ray: ray) {
            return result
        }

        return self
    }

    @objc override func _calculateSize() -> CGSize {
        if customNodeContainer != nil {
            return customNodeContainer!.getSize()
        } else if defaultNodeContainer != nil {
            return defaultNodeContainer!.getSize()
        }

        return CGSize.zero
    }

    @objc override func updateLayout() {
        _ = getSize()
        customNodeContainer?.updateLayout()
    }

    @objc override func updatePivot() {
        super.updatePivot()
        if let container = customNodeContainer as? UiNode {
            let size = getSize(scaled: false)
            let shift = container.alignment.shiftDirection
            contentNode.position -= SCNVector3(shift.x * size.width, shift.y * size.height, 0)
        } else if let container = defaultNodeContainer {
            let bounds = container.getBounds()
            contentNode.position -= SCNVector3(bounds.midX, bounds.midY, 0)
        }
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        customNodeContainer?.setNeedsLayout()
        defaultNodeContainer?.invalidate()
    }

    fileprivate func registerToggleGroupHandler(_ node: UiToggleNode) {
        node.onChangeGroup = { [weak self] node in
            self?.toggleSelection(node)
        }
    }

    func toggleSelection(_ node: UiToggleNode) {
        if allowMultipleOn && allowAllOff {
            node.on = !node.on
        } else if allowMultipleOn && allowAllOff == false {
            let filteredItems = itemsList.filter { $0.on }
            if filteredItems.count == 1 && filteredItems.first == node { return }
            node.on = !node.on
        } else if allowMultipleOn == false && allowAllOff {
            let filteredItems = itemsList.filter { $0.on }
            if filteredItems.count >= 1 && filteredItems.contains { $0 != node }  { filteredItems.forEach { $0.on = false } }
            node.on = !node.on
        } else {
            let filteredItems = itemsList.filter { $0 != node }
            filteredItems.forEach { $0.on = false }
            node.on = true
        }
    }

    func childPresent(toggleNode: UiToggleNode) {
        if itemsList.contains(toggleNode) { return }
        registerToggleGroupHandler(toggleNode)
        itemsList.append(toggleNode)
    }
}

extension UiToggleGroupNode: TransformNodeContainer {
    var itemsCount: Int { return itemsList.count }
}
