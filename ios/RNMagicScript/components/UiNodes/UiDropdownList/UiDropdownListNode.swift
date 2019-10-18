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

@objc open class UiDropdownListNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.065

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; reloadOutline = true; setNeedsLayout() }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { labelNode.textColor = textColor; reloadOutline = true; setNeedsLayout() }
    }
    @objc var textSize: CGFloat = 0 {
        didSet { reloadOutline = true; labelNode.textSize = textSize; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    @objc var width: CGFloat = 0 {
        didSet { reloadOutline = true; setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { reloadOutline = true; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    fileprivate var roundness: CGFloat = 1.0
    @objc var maxHeight: CGFloat = 0.0 {
        didSet { listGridLayoutNode.update(["height" : maxHeight]); setNeedsLayout() }
    }
    @objc var maxCharacterLimit: Int = 0 {
        didSet {
            itemsList.forEach { (node: UiDropdownListItemNode) in
                node.maxCharacterLimit = maxCharacterLimit
            }
            setNeedsLayout()
        }
    }
    @objc var multiSelectMode: Bool = false {
        didSet {
            // multiselect logic
            setNeedsLayout()
        }
    }
    @objc var listFont: UIFont = UIFont.systemFont(ofSize: 14.0) 

    @objc public var onTap: ((_ sender: UiNode) -> (Void))?
    @objc public var onSelectionChanged: ((_ sender: UiDropdownListNode, _ selectedItem: [Int]) -> (Void))?

    fileprivate var outlineNode: SCNNode!
    fileprivate var gridLayoutNode: UiGridLayoutNode!
    fileprivate(set) var labelNode: UiLabelNode!
    fileprivate var iconNode: UiImageNode!

    fileprivate var itemsList: Array<UiDropdownListItemNode> = []
    fileprivate(set) var selectedItem: UiDropdownListItemNode?
    fileprivate let listGridLayoutNodeId = "dropDownListGridLayout"
    fileprivate(set) var listGridLayoutNode: UiGridLayoutNode!

    fileprivate var reloadOutline: Bool = true

    deinit {
        contentNode.removeAllAnimations()
    }

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        simulateTap()
        toggleListNodeVisibility()
    }

    @objc override func leaveFocus() {
        super.leaveFocus()

        toggleListNodeVisibility()
    }

    fileprivate func toggleListNodeVisibility() {
        listGridLayoutNode.position = SCNVector3(position.x, -0.05, position.z)
        listGridLayoutNode.visible = !listGridLayoutNode.visible
        listGridLayoutNode.layoutIfNeeded()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        labelNode.setNeedsLayout()
        gridLayoutNode.setNeedsLayout()
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = UiLabelNode()
        labelNode.textSize = UiDropdownListNode.defaultTextSize
        labelNode.layoutIfNeeded()

        iconNode = UiImageNode(props: ["icon": "chevron-down", "height": 0.04])
        iconNode.layoutIfNeeded()

        gridLayoutNode = UiGridLayoutNode(props: [
            "columns": 2,
            "rows": 1,
            "defaultItemPadding": [0.015, 0.005, 0.015, 0.005],
            "alignment": "center-center"
        ])

        gridLayoutNode.addChild(labelNode)
        gridLayoutNode.addChild(iconNode)
        gridLayoutNode.layoutIfNeeded()

        contentNode.addChildNode(gridLayoutNode)

        listGridLayoutNode = UiGridLayoutNode(props: [
            "columns": 1,
            "defaultItemPadding": [0.0, 0.0, 0.0, 0.0],
            "defaultItemAlignment": "center-left",
            "alignment": "top-center"
        ])

        contentNode.addChildNode(listGridLayoutNode)

        listGridLayoutNode.visible = false
        listGridLayoutNode.layoutIfNeeded()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let text = Convert.toString(props["text"]) {
            self.text = text
        }

        if let textColor = Convert.toColor(props["textColor"]) {
            self.textColor = textColor
        }

        if let textSize = Convert.toCGFloat(props["textSize"]) {
            self.textSize = textSize
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let maxHeight = Convert.toCGFloat(props["maxHeight"]) {
            self.maxHeight = maxHeight
        }

        if let maxCharacterLimit = Convert.toInt(props["maxCharacterLimit"]) {
            self.maxCharacterLimit = maxCharacterLimit
        }

        if let multiSelectMode = Convert.toBool(props["multiSelectMode"]) {
            self.multiSelectMode = multiSelectMode
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let dropDownToTextHeightMultiplier: CGFloat = 0.75
        let contentWidth: CGFloat = (width > 0) ? width : gridLayoutNode.getSize().width + dropDownToTextHeightMultiplier * gridLayoutNode.getSize().height
        let contentHeight: CGFloat = (height > 0) ? height : dropDownToTextHeightMultiplier * gridLayoutNode.getSize().height
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        labelNode.layoutIfNeeded()
        gridLayoutNode.layoutIfNeeded()
        listGridLayoutNode.updateLayout()
        if reloadOutline {
            reloadOutline = false
            reloadOutlineNode()
        }
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }

    fileprivate func updateLabelTextSizeBasedOnHeight() {
        guard textSize == 0 && height > 0 else  {
            labelNode.textSize = textSize
            return
        }

        labelNode.textSize = max(0, 0.333 * height)
    }

    fileprivate func reloadOutlineNode() {
        let size = getSize()

        outlineNode?.removeFromParentNode()

        let radius: CGFloat = 0.5 * min(size.width, size.height) * roundness
        let thickness: CGFloat = 0.045 * min(size.width, size.height)
        guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
        outlineNode = NodesFactory.createOutlineNode(width: size.width, height: size.height, cornerRadius: radius, thickness: thickness)
        contentNode.addChildNode(outlineNode)
    }

    @objc override func addChild(_ child: TransformNode) {
        guard let dropDownListItem = child as? UiDropdownListItemNode else { return }
        dropDownListItem.maxCharacterLimit = maxCharacterLimit
        itemsList.append(dropDownListItem)
        dropDownListItem.tapHandler = self
        listGridLayoutNode.addChild(dropDownListItem)
        setNeedsLayout()
    }

    @objc override func removeChild(_ child: TransformNode) {
        guard let dropDownListItem = child as? UiDropdownListItemNode else { return }
        itemsList.removeAll { node -> Bool in
            return node == dropDownListItem
        }
        dropDownListItem.tapHandler = nil
        listGridLayoutNode.removeChild(child)
        setNeedsLayout()
    }
}

extension UiDropdownListNode: DropdownListItemTapHandling {
    func handleTap(_ sender: UiDropdownListItemNode) {
        if sender != selectedItem {
            selectedItem?.toggleSelection()
        }
        sender.toggleSelection()
        selectedItem = sender.isSelected ? sender : nil
        // notify about item selection
        onSelectionChanged?(self, selectedItemIndex(selectedItem: selectedItem))
    }

    private func selectedItemIndex(selectedItem: UiDropdownListItemNode?) -> [Int] {
        if let selectedItem = selectedItem, let selectedIndex = itemsList.firstIndex(of: selectedItem) {
            return [selectedIndex]
        }
        return []
    }
}

extension UiDropdownListNode: TapSimulating { }
