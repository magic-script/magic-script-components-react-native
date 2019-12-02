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

@objc open class UiDropdownListItemNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.065

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var label: String? {
        get { return labelNode.text }
        set { labelNode.text = alignTextLenght(newValue, maxCharacterLimit); setNeedsLayout() }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { labelNode.textColor = textColor; setNeedsLayout() }
    }
    @objc var textSize: CGFloat = 0 {
        didSet { labelNode.textSize = textSize; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    @objc var width: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var maxCharacterLimit: Int = 0 {
        didSet {
            labelNode.text = alignTextLenght(label, maxCharacterLimit)
            setNeedsLayout()
        }
    }

    fileprivate func alignTextLenght(_ text: String?, _ maxCharacterLimit: Int) -> String? {
        guard let text = text else { return nil }
        if text.count > maxCharacterLimit && maxCharacterLimit > 0 {
            let trailingCharacters = "..."
            return text.prefix(maxCharacterLimit) + trailingCharacters
        }
        return text
    }

    var tapHandler: DropdownListItemTapHandling?

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }
        tapHandler?.handleTap(self)
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        labelNode.setNeedsLayout()
        gridLayoutNode.setNeedsLayout()
    }

    @objc fileprivate(set) var isSelected: Bool = false

    fileprivate var gridLayoutNode: UiGridLayoutNode!
    fileprivate(set) var labelNode: UiLabelNode!

    fileprivate var backgroundNode: SCNNode!
    fileprivate var backgroundGeometry: SCNPlane!

    @objc override func setupNode() {
        super.setupNode()
        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = UiLabelNode()
        labelNode.textSize = UiDropdownListItemNode.defaultTextSize
        labelNode.layoutIfNeeded()

        gridLayoutNode = UiGridLayoutNode(props: [
            "columns": 2,
            "rows": 1,
            "defaultItemPadding": [0.005, 0.005, 0.005, 0.005],
            "alignment": "center-center"
        ])
        _ = gridLayoutNode.addChild(labelNode)
        gridLayoutNode.layoutIfNeeded()

        backgroundGeometry = SCNPlane(width: gridLayoutNode.getSize().width, height: gridLayoutNode.getSize().height)
        backgroundGeometry.firstMaterial?.lightingModel = .constant
        backgroundGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        backgroundGeometry.firstMaterial?.diffuse.contents = UIColor(red: 236.0/256.0, green: 240.0/256.0, blue: 241.0/256.0, alpha: 0.5)
        backgroundNode = SCNNode(geometry: backgroundGeometry)

        gridLayoutNode.renderingOrder = 1
        contentNode.addChildNode(gridLayoutNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let label = Convert.toString(props["label"]) {
            self.label = label
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

        if let maxCharacterLimit = Convert.toInt(props["maxCharacterLimit"]) {
            self.maxCharacterLimit = maxCharacterLimit
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let buttonToTextHeightMultiplier: CGFloat = 1.4
        let contentWidth: CGFloat = (width > 0) ? width : gridLayoutNode.getSize().width + buttonToTextHeightMultiplier * gridLayoutNode.getSize().height
        let contentHeight: CGFloat = (height > 0) ? height : buttonToTextHeightMultiplier * gridLayoutNode.getSize().height
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        labelNode.layoutIfNeeded()
        gridLayoutNode.layoutIfNeeded()

        if isSelected {
            backgroundGeometry.width = gridLayoutNode.getSize().width
            backgroundGeometry.height = gridLayoutNode.getSize().height
            contentNode.addChildNode(backgroundNode)
        } else {
            backgroundNode.removeFromParentNode()
        }
    }

    fileprivate func updateLabelTextSizeBasedOnHeight() {
        guard textSize == 0 && height > 0 else  {
            labelNode.textSize = textSize
            return
        }

        labelNode.textSize = max(0, 0.333 * height)
    }

    func toggleSelection() {
        isSelected = !isSelected
        setNeedsLayout()
        layoutIfNeeded()
    }
}
