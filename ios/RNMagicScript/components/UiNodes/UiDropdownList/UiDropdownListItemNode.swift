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

class UiDropdownListItemNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.0235

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; setNeedsLayout() }
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

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        labelNode.setNeedsLayout()
        gridLayoutNode.setNeedsLayout()
    }

    @objc var isNested: Bool {
        return false
    }

    fileprivate var gridLayoutNode: UiGridLayoutNode!
    fileprivate var labelNode: UiLabelNode!
    fileprivate var iconNode: UiImageNode!

    @objc override func setupNode() {
        super.setupNode()
        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = UiLabelNode()
        labelNode.layoutIfNeeded()

        iconNode = UiImageNode(props: ["icon": "chevron-right", "height": 0.04])
        iconNode.layoutIfNeeded()

        gridLayoutNode = UiGridLayoutNode(props: [
            "columns": 2,
            "rows": 1,
            "defaultItemPadding": [0.005, 0.005, 0.005, 0.005],
            "alignment": "center-center"
        ])
        gridLayoutNode.setDebugMode(true)
        gridLayoutNode.addChild(labelNode)
        if isNested {
            gridLayoutNode.addChild(iconNode)
        }
        gridLayoutNode.layoutIfNeeded()

        contentNode.addChildNode(gridLayoutNode)
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
    }

    fileprivate func updateLabelTextSizeBasedOnHeight() {
        guard textSize == 0 && height > 0 else  {
            labelNode.textSize = textSize
            return
        }

        labelNode.textSize = max(0, 0.333 * height)
    }
}
