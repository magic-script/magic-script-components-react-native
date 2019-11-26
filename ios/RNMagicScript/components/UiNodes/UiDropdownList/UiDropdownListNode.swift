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

    @objc public var onSelectionChanged: ((_ sender: UiDropdownListNode, _ selectedItem: [Int]) -> (Void))?

    fileprivate var outlineNode: SCNNode!
    fileprivate var gridLayoutNode: UiGridLayoutNode!
    fileprivate(set) var labelNode: UiLabelNode!
    fileprivate var iconNode: UiImageNode!

    fileprivate var itemsList: Array<UiDropdownListItemNode> = []
    fileprivate(set) var selectedItem: UiDropdownListItemNode?
    fileprivate var listNode: SCNNode!
    fileprivate var backgroundNode: SCNNode!
    fileprivate(set) var listGridLayoutNode: UiGridLayoutNode!

    fileprivate var reloadOutline: Bool = true
    fileprivate var isListExpanded: Bool {
        return listGridLayoutNode.visible
    }

    deinit {
        contentNode.removeAllAnimations()
    }

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        toggleListNodeVisibility()
    }

    @objc override func leaveFocus() {
        super.leaveFocus()

        toggleListNodeVisibility()
    }

    fileprivate func toggleListNodeVisibility() {
        let buttonSize = gridLayoutNode.getSize()
        let listSize = listGridLayoutNode.getSize()
        listNode.position = SCNVector3(0.5 * listSize.width - 0.45 * buttonSize.width, -0.05, 0.03)
        if let plane = backgroundNode.geometry as? SCNPlane {
            plane.width = listSize.width
            plane.height = listSize.height
        }

        backgroundNode.position = SCNVector3(0, -0.5 * listSize.height, -0.01)
        backgroundNode.isHidden = listGridLayoutNode.visible

        listGridLayoutNode.visible = !listGridLayoutNode.visible
        listGridLayoutNode.layoutIfNeeded()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        labelNode.setNeedsLayout()
        gridLayoutNode.setNeedsLayout()
        listGridLayoutNode.setNeedsLayout()
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = UiLabelNode()
        labelNode.textSize = UiDropdownListNode.defaultTextSize

        iconNode = UiImageNode(props: ["icon": "chevron-down", "height": 0.04])

        gridLayoutNode = UiGridLayoutNode()
        gridLayoutNode.columns = 2
        gridLayoutNode.rows = 1
        gridLayoutNode.defaultItemPadding = UIEdgeInsets(top: 0.015, left: 0.005, bottom: 0.015, right: 0.005)
        gridLayoutNode.alignment = Alignment.centerCenter

        gridLayoutNode.addChild(labelNode)
        gridLayoutNode.addChild(iconNode)

        contentNode.addChildNode(gridLayoutNode)

        // List items node
        listNode = SCNNode()
        contentNode.addChildNode(listNode)

        backgroundNode = NodesFactory.createPlaneNode(width: 0, height: 0, image: ImageAsset.dropdownListBackground.image)
        backgroundNode.geometry?.firstMaterial?.readsFromDepthBuffer = false
        backgroundNode.isHidden = true
        backgroundNode.renderingOrder = 0
        listNode.addChildNode(backgroundNode)

        listGridLayoutNode = UiGridLayoutNode()
        listGridLayoutNode.isHidden = true
        listGridLayoutNode.columns = 1
        listGridLayoutNode.defaultItemPadding = UIEdgeInsets.zero
        listGridLayoutNode.defaultItemAlignment = Alignment.centerLeft
        listGridLayoutNode.alignment = Alignment.topCenter
        listGridLayoutNode.renderingOrder = 1
        listNode.addChildNode(listGridLayoutNode)
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

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        if isListExpanded {
            return listGridLayoutNode.hitTest(ray: ray)
        }

        return selfHitTest(ray: ray)
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
        listGridLayoutNode.layoutIfNeeded()

        if let plane = backgroundNode.geometry as? SCNPlane {
            let size = listGridLayoutNode.getSize()
            plane.width = size.width
            plane.height = size.height
        }

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
