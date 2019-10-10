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

class UiDropdownListNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.0167

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
    fileprivate var _roundness: CGFloat = 1.0
    @objc var roundness: CGFloat {
        get { return _roundness }
        set {
            let clampedValue: CGFloat = max(0, min(newValue, 1.0))
            if clampedValue != _roundness {
                _roundness = clampedValue
                reloadOutline = true
                setNeedsLayout()
            }
        }
    }

    @objc public var onTap: ((_ sender: UiNode) -> (Void))?

    fileprivate var outlineNode: SCNNode!
    fileprivate var gridLayoutNode: UiGridLayoutNode!
    fileprivate var labelNode: UiLabelNode!
    fileprivate var iconNode: UiImageNode!
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
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        labelNode.setNeedsLayout()
        gridLayoutNode.setNeedsLayout()
    }

    @objc func simulateTap() {
        assert(enabled, "Button must be enabled in order to tap it!")
        onTap?(self)
        let initialPosition = contentNode.position
        let animation = CABasicAnimation(keyPath: "position.z")
        animation.fromValue = initialPosition.z
        animation.toValue = initialPosition.z - 0.05
        animation.duration = 0.1
        animation.autoreverses = true
        animation.repeatCount = 1
        contentNode.addAnimation(animation, forKey: "button_tap")
    }

    @objc override func setupNode() {
        super.setupNode()

        alignment = Alignment.centerCenter
        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = UiLabelNode()
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

        if let roundness = Convert.toCGFloat(props["roundness"]) {
            self.roundness = roundness
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let buttonToTextHeightMultiplier: CGFloat = 1.1
        let contentWidth: CGFloat = (width > 0) ? width : gridLayoutNode.getSize().width + buttonToTextHeightMultiplier * gridLayoutNode.getSize().height
        let contentHeight: CGFloat = (height > 0) ? height : buttonToTextHeightMultiplier * gridLayoutNode.getSize().height
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        labelNode.layoutIfNeeded()
        gridLayoutNode.layoutIfNeeded()
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
        let thickness: CGFloat = 0.05 * min(size.width, size.height)
        guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
        outlineNode = NodesFactory.createOutlineNode(width: size.width, height: size.height, cornerRadius: radius, thickness: thickness)
        contentNode.addChildNode(outlineNode)
    }
}
