//
//  UiButtonNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

// The default values for width and height are 0, which instructs
// the button to use the default button height and automatically
// calculates button width based on the button text dimensions.
//
// Setting a value greater than 0 for width or height will override
// the defaults.
//
// Button text size is automatically set based on the button height
// unless overridden with UiButton::setTextSize().

@objc class UiButtonNode: UiNode {
    static fileprivate let defaultHeight: CGFloat = 0.02
    static fileprivate let defaultTextSize: CGFloat = 0.0167
    static fileprivate let borderInset: CGFloat = 0.01

    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; setNeedsLayout() }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { labelNode.textColor = textColor; reloadOutline = true }
    }
    @objc var iconColor: UIColor = UIColor.white
    @objc var textSize: CGFloat = 0 {
        didSet { updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    @objc var iconSize: CGFloat = 0.1
    @objc var width: CGFloat = 0 {
        didSet { reloadOutline = true; setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { reloadOutline = true; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    @objc var roundness: CGFloat = 0.5 {
        didSet { reloadOutline = true; setNeedsLayout() }
    }

    @objc var onTap: ((_ sender: UiNode) -> (Void))?

    fileprivate var contentNode: SCNNode!
    fileprivate var outlineNode: OutlineNode!
    fileprivate var labelNode: LabelNode!
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
        leaveFocus()
    }

    @objc func simulateTap() {
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

        assert(contentNode == nil, "Node must not be initialized!")
        contentNode = SCNNode()
        addChildNode(contentNode)

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.textAlignment = .center
        labelNode.defaultTextSize = UiButtonNode.defaultTextSize
        contentNode.addChildNode(labelNode)

//        setDebugMode(true)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let text = Convert.toString(props["text"]) {
            self.text = text
        }

        if let textColor = Convert.toColor(props["textColor"]) {
            self.textColor = textColor
        }

        if let iconColor = Convert.toColor(props["iconColor"]) {
            self.iconColor = iconColor
        }

        if let textSize = Convert.toCGFloat(props["textSize"]) {
            self.textSize = textSize
        }

        if let iconSize = Convert.toCGFloat(props["iconSize"]) {
            self.iconSize = iconSize
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
        let labelSize = labelNode.getSize()
        let contentWidth: CGFloat = (width > 0) ? width : labelSize.width + 2 * UiButtonNode.borderInset
        let contentHeight: CGFloat = (height > 0) ? height : labelSize.height + 2 * UiButtonNode.borderInset
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        labelNode.reload()
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

        labelNode.textSize = max(0, height - 2 * UiButtonNode.borderInset)
    }

    fileprivate func reloadOutlineNode() {
        let size = getSize()

        outlineNode?.removeFromParentNode()

        let radius: CGFloat = 0.5 * min(size.width, size.height) * roundness
        let thickness: CGFloat = min(0.01 * min(size.width, size.height), 0.005)
        guard thickness > 0 else { return }
        outlineNode = OutlineNode(contentSize: size, cornerRadius: radius, lineWidth: thickness)
        contentNode.addChildNode(outlineNode)
    }
}
