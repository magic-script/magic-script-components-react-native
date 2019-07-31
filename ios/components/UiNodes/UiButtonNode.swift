//
//  UiButtonNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class UiButtonNode: UiNode {

    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; setNeedsLayout() }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet {
            labelNode.textColor = textColor
            reloadOutline = true
        }
    }
    @objc var iconColor: UIColor = UIColor.white
    @objc var textSize: CGFloat {
        get { return labelNode.textSize }
        set { labelNode.textSize = newValue; setNeedsLayout() }
    }
    @objc var iconSize: CGFloat = 0.1
    @objc var width: CGFloat = 0 {
        didSet { reloadOutline = true; setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { reloadOutline = true; setNeedsLayout() }
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
        labelNode.textSize = 0.0167
        contentNode.addChildNode(labelNode)
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

    @objc override func getSize() -> CGSize {
        let labelSize = labelNode.getSize()
        let margin: CGFloat = 0.01
        let contentWidth: CGFloat = (width > 0) ? width : labelSize.width + 2 * margin
        let contentHeight: CGFloat = (height > 0) ? height : labelSize.height + 2 * margin
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        labelNode.reload()
        if reloadOutline {
            reloadOutline = false
            reloadOutlineNode()
        }

        let labelSize = labelNode.getSize()
        labelNode.position = SCNVector3(-0.5 * labelSize.width, 0.0, 0.0)
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

