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
        didSet {
            reloadOutline = true
            labelNode.textSize = textSize
            itemsList.forEach { $0.textSize = textSize }
            updateLabelTextSizeBasedOnHeight()
            setNeedsLayout()
        }
    }
    @objc var width: CGFloat = 0 {
        didSet { reloadOutline = true; setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { reloadOutline = true; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    fileprivate var roundness: CGFloat = 1.0
    @objc var maxHeight: CGFloat = 0.0
    @objc var maxCharacterLimit: Int = 0 {
        didSet {
            itemsList.forEach { $0.maxCharacterLimit = maxCharacterLimit }
            setNeedsLayout()
        }
    }
    @objc var multiSelectMode: Bool = false

    @objc public var onSelectionChanged: ((_ sender: UiDropdownListNode, _ selectedItems: [UiDropdownListItemNode]) -> (Void))?

    fileprivate var outlineNode: SCNNode!
    fileprivate var labelNode: LabelNode!
    fileprivate var iconNode: SCNNode!

    fileprivate var itemsList: [UiDropdownListItemNode] = []
    fileprivate var selectedItems: [UiDropdownListItemNode] = []
    fileprivate var listNode: SCNNode!
    fileprivate var backgroundNode: SCNNode?
    fileprivate var listGridLayoutNode: UiGridLayoutNode!

    fileprivate var reloadOutline: Bool = true
    fileprivate var isListExpanded: Bool {
        return listGridLayoutNode.visible
    }

    deinit {
        contentNode.removeAllAnimations()
    }

    @objc override var canHaveFocus: Bool {
        return enabled
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        toggleListNodeVisibility()
    }

    @objc override func leaveFocus(onBehalfOf node: UiNode? = nil) -> Bool {
        if node != nil && node is UiDropdownListItemNode {
            return false
        }

        let result = super.leaveFocus(onBehalfOf: node)
        if result {
            toggleListNodeVisibility()
        }
        return result
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        listGridLayoutNode.setNeedsLayout()
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "labelNode must not be initialized!")
        labelNode = LabelNode()
        labelNode.textSize = UiDropdownListNode.defaultTextSize

        iconNode = NodesFactory.createPlaneNode(width: 1, height: 1, image: SystemIcon("chevron-down").getImage())
        contentNode.addChildNode(labelNode)
        contentNode.addChildNode(iconNode)

        // List items node
        listNode = SCNNode()
        contentNode.addChildNode(listNode)

        listGridLayoutNode = UiGridLayoutNode()
        listGridLayoutNode.isHidden = true
        listGridLayoutNode.columns = 1
        listGridLayoutNode.defaultItemPadding = UIEdgeInsets.zero
        listGridLayoutNode.defaultItemAlignment = Alignment.centerLeft
        listGridLayoutNode.alignment = Alignment.topCenter
        listGridLayoutNode.renderingOrder = 1
        listNode.addChildNode(listGridLayoutNode)
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        guard let dropDownListItem = child as? UiDropdownListItemNode else { return false }
        dropDownListItem.textSize = textSize
        dropDownListItem.maxCharacterLimit = maxCharacterLimit
        itemsList.append(dropDownListItem)
        dropDownListItem.tapHandler = self
        listGridLayoutNode.addChild(dropDownListItem)
        setNeedsLayout()
        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        guard let dropDownListItem = child as? UiDropdownListItemNode else { return }
        itemsList.removeAll { $0 == dropDownListItem }
        dropDownListItem.tapHandler = nil
        listGridLayoutNode.removeChild(child)
        setNeedsLayout()
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        if isListExpanded {
            return listGridLayoutNode.hitTest(ray: ray) ?? selfHitTest(ray: ray)
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
        let buttonSize = getButtonSize()
        let contentWidth: CGFloat = (width > 0) ? width : buttonSize.width
        let contentHeight: CGFloat = (height > 0) ? height : buttonSize.height
        return CGSize(width: contentWidth, height: contentHeight)
    }

    fileprivate func getButtonSize(includeOutline: Bool = true) -> CGSize {
        let labelSize = labelNode.getSize()
        let iconSize = CGSize(width: labelSize.height, height: labelSize.height)

        let contentHeight = max(labelSize.height, iconSize.height)
        let gap: CGFloat = (labelSize.width > 0 && iconSize.width > 0) ? 0.1 * contentHeight : 0
        let contentWidth = labelSize.width + gap + iconSize.width

        if includeOutline {
            let dropdownToTextHeightMultiplier: CGFloat = 2.3
            return CGSize(width: contentWidth + dropdownToTextHeightMultiplier * contentHeight, height: dropdownToTextHeightMultiplier * contentHeight)
        } else {
            return CGSize(width: contentWidth, height: contentHeight)
        }
    }

    @objc override func updateLayout() {
        labelNode.reload()
        listGridLayoutNode.layoutIfNeeded()
        _ = getSize()

        let buttonSize = getButtonSize(includeOutline: false)
        let labelSize = labelNode.getSize()
        labelNode.position = SCNVector3(-0.5 * buttonSize.width + 0.5 * labelSize.width, 0, 0)

        let iconSize = CGSize(width: labelSize.height, height: labelSize.height)
        if let iconPlane = iconNode.geometry as? SCNPlane {
            iconPlane.width = iconSize.width
            iconPlane.height = iconSize.height
        }
        iconNode.position = SCNVector3(0.5 * buttonSize.width - 0.5 * iconSize.width, 0, 0)

        updateBackground()

        if reloadOutline {
            reloadOutline = false
            reloadOutlineNode()
        }
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
        listGridLayoutNode.setDebugMode(debug)
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

    fileprivate func toggleListNodeVisibility() {
        let buttonSize = getButtonSize(includeOutline: true)
        let listSize = listGridLayoutNode.getSize()
        listNode.position = SCNVector3(0.5 * (listSize.width - buttonSize.width), -0.5 * buttonSize.height, 0.03)
        outlineNode?.isHidden = !listGridLayoutNode.visible

        listGridLayoutNode.layoutIfNeeded()
        listGridLayoutNode.visible = !listGridLayoutNode.visible
        updateBackground()
    }

    fileprivate func updateBackground() {
        let listSize = listGridLayoutNode.getSize()
        let isVisible = listGridLayoutNode.visible
        if let bgNode = backgroundNode,
            let bgGeometry = bgNode.geometry as? SCNNinePatch,
            isVisible,
            bgGeometry.width == listSize.width,
            bgGeometry.height == listSize.height {
            // no need to update
            return
        }

        backgroundNode?.removeFromParentNode()
        backgroundNode = nil

        guard listSize.width > 0 && listSize.height > 0 && isVisible else { return }

        let inset: CGFloat = 0.3
        let geometryCaps = UIEdgeInsets(top: inset, left: inset, bottom: inset, right: inset)
        let imageCaps = UIEdgeInsets(top: 208, left: 137, bottom: 208, right: 137)
        let width: CGFloat = listSize.width + 1.5 * inset
        let height: CGFloat = listSize.height + 1.5 * inset
        backgroundNode = NodesFactory.createNinePatchNode(width: width, height: height, geometryCaps: geometryCaps, image: ImageAsset.dropdownListBackground.image, imageCaps: imageCaps)
        backgroundNode?.geometry?.firstMaterial?.readsFromDepthBuffer = false
        backgroundNode?.opacity = 0.6
        backgroundNode?.position = SCNVector3(0, -0.5 * listSize.height, -0.01)
        backgroundNode?.renderingOrder = 0
        listNode.insertChildNode(backgroundNode!, at: 0)
    }
}

extension UiDropdownListNode: DropdownListItemTapHandling {
    func handleTap(_ sender: UiDropdownListItemNode) {
        if !multiSelectMode {
            selectedItems.forEach { $0.toggleSelection() }
            selectedItems.removeAll()
        }

        sender.toggleSelection()
        if sender.isSelected {
            selectedItems.append(sender)
        } else {
            selectedItems.removeAll { $0 == sender }
        }

        // notify about item selection
        onSelectionChanged?(self, selectedItems)

        if !multiSelectMode {
            toggleListNodeVisibility()
        }
    }
}

extension UiDropdownListNode: TapSimulating { }
