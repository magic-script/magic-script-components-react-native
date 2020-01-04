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

    fileprivate(set) weak var innerLayout: TransformNode?
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
        if let toggleNode = child as? UiToggleNode {
            registerToggleGroupHandler(toggleNode)
            addToggleNode(toggleNode)
            return true
        }

        if let _ = child as? TransformNodeContainer {
            innerLayout = child
            contentNode.addChildNode(child)
            setNeedsLayout()
            return true
        }

        return false
    }

    func childPresent(toggleNode: UiToggleNode) {
        if itemsList.contains(toggleNode) { return }
        registerToggleGroupHandler(toggleNode)
        addToggleNode(toggleNode)
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

    fileprivate func addToggleNode(_ toggleNode: UiToggleNode) {
        itemsList.append(toggleNode)
        setNeedsLayout()
    }

    @objc override func removeChild(_ child: TransformNode) {
        if let toggleItem = child as? UiToggleNode {
            itemsList.removeAll { node -> Bool in
                return node == toggleItem
            }
        }

        if let storedInnerLayout = self.innerLayout, storedInnerLayout == child {
            storedInnerLayout.removeFromParentNode()
            self.innerLayout = nil
        }

        setNeedsLayout()
    }

    override func hitTest(ray: Ray) -> TransformNode? {
         if innerLayout != nil {
            return self.innerLayout?.hitTest(ray: ray)
        }

        for item in itemsList {
            let hitNode = item.hitTest(ray: ray)
            if hitNode != nil { return hitNode }
        }

        return selfHitTest(ray: ray)
    }

    @objc override func _calculateSize() -> CGSize {
        return innerLayout?.getSize() ?? CGSize.zero
    }

    @objc override func updateLayout() {
        _ = getSize()
        innerLayout?.updateLayout()
    }

    @objc override func updatePivot() {
        super.updatePivot()
        if let container = innerLayout as? UiNode {
            let size = getSize(scaled: false)
            let shift = container.alignment.shiftDirection
            contentNode.position -= SCNVector3(shift.x * size.width, shift.y * size.height, 0)
        }
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        innerLayout?.setNeedsLayout()
    }
}

extension UiToggleGroupNode: TransformNodeContainer {
    var itemsCount: Int { return itemsList.count }
}
