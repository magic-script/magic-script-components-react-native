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
        didSet { reloadOutline = true; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    @objc var width: CGFloat = 0 {
        didSet { reloadOutline = true; setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { reloadOutline = true; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    @objc var maxHeight: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var maxCharacterLimit: Int = 0 {
        didSet {
            itemsList.forEach { $0.maxCharacterLimit = maxCharacterLimit }
            setNeedsLayout()
        }
    }
    @objc var multiSelect: Bool = false {
        didSet { selectedItems.forEach { $0.toggleSelection() } }
    }
    @objc var isListExpanded: Bool { return !listNode.isHidden }
    @objc var selectedItems: [UiDropdownListItemNode] {
        return itemsList.filter { $0.selected }
    }

    @objc public var onSelectionChanged: ((_ sender: UiDropdownListNode, _ selectedItems: [UiDropdownListItemNode]) -> (Void))?

    fileprivate var outlineNode: SCNNode!
    fileprivate var labelNode: LabelNode!
    fileprivate var iconNode: SCNNode!

    fileprivate var itemsList: [UiDropdownListItemNode] = []
    fileprivate var listNode: SCNNode!
    fileprivate var backgroundNode: SCNNode?
    fileprivate var listGridLayoutNode: UiGridLayoutNode!

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

        setListNodeVisible(!isListExpanded)
    }

    @discardableResult
    @objc override func leaveFocus(onBehalfOf node: UiNode? = nil) -> Bool {
        if node != nil && node is UiDropdownListItemNode {
            return false
        }

        let result = super.leaveFocus(onBehalfOf: node)
        if result {
            setListNodeVisible(false)
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
        labelNode.defaultTextSize = UiDropdownListNode.defaultTextSize
        contentNode.addChildNode(labelNode)

        iconNode = NodesFactory.createPlaneNode(width: 1, height: 1, image: SystemIcon("chevron-down").getImage())
        contentNode.addChildNode(iconNode)

        // List items node
        listNode = SCNNode()
        listNode.isHidden = true
        contentNode.addChildNode(listNode)

        listGridLayoutNode = UiGridLayoutNode()
        listGridLayoutNode.columns = 1
        listGridLayoutNode.defaultItemPadding = UIEdgeInsets.zero
        listGridLayoutNode.defaultItemAlignment = Alignment.centerLeft
        listGridLayoutNode.alignment = Alignment.topCenter
        listGridLayoutNode.renderingOrder = 1
        listNode.addChildNode(listGridLayoutNode)
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        guard let dropDownListItem = child as? UiDropdownListItemNode else {
            return super.addChild(child)
        }

        dropDownListItem.textSize = getPreferredTextHeight()
        dropDownListItem.maxCharacterLimit = maxCharacterLimit
        itemsList.append(dropDownListItem)
        dropDownListItem.tapHandler = self
        listGridLayoutNode.addChild(dropDownListItem)
        setNeedsLayout()
        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        guard let dropDownListItem = child as? UiDropdownListItemNode else {
            super.removeChild(child)
            return
        }

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

        if let multiSelect = Convert.toBool(props["multiSelect"]) {
            self.multiSelect = multiSelect
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
        let iconSize = getIconSize(height: labelSize.height)

        let contentHeight = max(labelSize.height, iconSize.height)
        let gap: CGFloat = (labelSize.width > 0 && iconSize.width > 0) ? 0.4 * contentHeight : 0
        let contentWidth = labelSize.width + gap + iconSize.width

        if includeOutline {
            let dropdownToTextHeightMultiplier: CGFloat = 2.3
            return CGSize(width: contentWidth + dropdownToTextHeightMultiplier * contentHeight, height: dropdownToTextHeightMultiplier * contentHeight)
        }

        return CGSize(width: contentWidth, height: contentHeight)
    }

    fileprivate func getIconSize(height: CGFloat) -> CGSize {
        return 0.6 * CGSize(width: height, height: height)
    }

    @objc override func updateLayout() {
        labelNode.reload()
        listGridLayoutNode.layoutIfNeeded()
        _ = getSize()

        let buttonSize = getButtonSize(includeOutline: false)
        let labelSize = labelNode.getSize()
        labelNode.position = SCNVector3(-0.5 * buttonSize.width + 0.5 * labelSize.width, 0, 0)

        let iconSize = getIconSize(height: labelSize.height)
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
    }

    fileprivate func getPreferredTextHeight() -> CGFloat {
        let textHeight: CGFloat
        if textSize == 0 && height > 0 {
            textHeight = max(0, 0.333 * height)
        } else {
            textHeight = (textSize > 0) ? textSize : labelNode.defaultTextSize
        }

        return textHeight
    }

    fileprivate func updateLabelTextSizeBasedOnHeight() {
        let textHeight = getPreferredTextHeight()
        labelNode.textSize = textHeight
        itemsList.forEach { $0.textSize = textHeight }
    }

    fileprivate func reloadOutlineNode() {
        let size = getSize()

        outlineNode?.removeFromParentNode()

        let radius: CGFloat = 0.5 * min(size.width, size.height)
        let thickness: CGFloat = 0.045 * min(size.width, size.height)
        guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
        outlineNode = NodesFactory.createOutlineNode(width: size.width, height: size.height, cornerRadius: radius, thickness: thickness)
        contentNode.addChildNode(outlineNode)
    }

    fileprivate func setListNodeVisible(_ visible: Bool) {
        listGridLayoutNode.layoutIfNeeded()

        let buttonSize = getButtonSize(includeOutline: true)
        let listSize = listGridLayoutNode.getSize()
        listNode.position = SCNVector3(0.5 * (listSize.width - buttonSize.width), -0.5 * buttonSize.height, 0.03)
        listNode.isHidden = !visible
        outlineNode?.isHidden = visible
        updateBackground()
    }

    fileprivate func updateBackground() {
        let listSize = listGridLayoutNode.getSize()
        let isVisible = !listNode.isHidden
        let inset: CGFloat = min(0.5, 0.8 * min(listSize.width, listSize.height))
        let geometryCaps = UIEdgeInsets(top: inset, left: inset, bottom: inset, right: inset)
        let imageCaps = UIEdgeInsets(top: 208, left: 137, bottom: 208, right: 137)
        let width: CGFloat = listSize.width + 1.5 * inset
        let height: CGFloat = listSize.height + 1.5 * inset
        if let bgNode = backgroundNode,
            let bgGeometry = bgNode.geometry as? SCNNinePatch,
            isVisible,
            abs(bgGeometry.width - width) < 0.0001,
            abs(bgGeometry.height - height) < 0.0001 {
            // no need to update
            return
        }

        backgroundNode?.removeFromParentNode()
        backgroundNode = nil

        guard itemsList.count > 0 && listSize.width > 0 && listSize.height > 0 && isVisible else { return }

        backgroundNode = NodesFactory.createNinePatchNode(width: width, height: height, geometryCaps: geometryCaps, image: ImageAsset.dropdownListBackground.image, imageCaps: imageCaps)
        backgroundNode?.geometry?.firstMaterial?.readsFromDepthBuffer = false
        backgroundNode?.position = SCNVector3(0, -0.5 * listSize.height, -0.01)
        backgroundNode?.renderingOrder = 0
        listNode.insertChildNode(backgroundNode!, at: 0)
    }
}

extension UiDropdownListNode: DropdownListItemTapHandling {
    func handleTap(_ sender: UiDropdownListItemNode) {
        if !multiSelect {
            selectedItems.forEach { $0.toggleSelection() }
        }

        sender.toggleSelection()

        // notify about item selection
        onSelectionChanged?(self, selectedItems)

        if !multiSelect {
            setListNodeVisible(false)
        }
    }
}

extension UiDropdownListNode: TapSimulating { }
