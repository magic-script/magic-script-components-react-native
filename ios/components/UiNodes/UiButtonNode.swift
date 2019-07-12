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
        get { return textNode.text }
        set { textNode.text = newValue }
    }

    @objc var textColor: UIColor = UIColor.white {
        didSet {
            textNode.textColor = textColor
            outlineNode?.color = textColor
        }
    }

    @objc var iconColor: UIColor = UIColor.white

    @objc var textSize: CGFloat {
        get { return textNode.textSize }
        set { textNode.textSize = newValue }
    }

    @objc var iconSize: CGFloat = 0.1

    @objc var width: CGFloat = 2 {
        didSet { updateNodeSize() }
    }

    @objc var height: CGFloat = 1 {
        didSet { updateNodeSize() }
    }

    @objc var roundness: CGFloat = 0.5 {
        didSet { updateNodeSize() }
    }

    @objc var onTap: ((_ sender: UiNode) -> (Void))?

    fileprivate var contentNode: SCNNode!
    fileprivate var outlineNode: OutlineNode?
    fileprivate var textNode: UiTextNode!

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
        
        contentNode = SCNNode()
        addChildNode(contentNode)

        textNode = UiTextNode()
        textNode.textColor = textColor
        contentNode.addChildNode(textNode)

        updateNodeSize()
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

    fileprivate func updateNodeSize() {
        let size = CGSize(width: width, height: height)
        textNode.boundsSize = size

        outlineNode?.removeFromParentNode()
//        let sizeInMeters = getPrefferedSize()
        let radius: CGFloat = 0.5 * min(size.width, size.height) * roundness
        let thickness: CGFloat = min(0.01 * min(size.width, size.height), 0.005)
        outlineNode = OutlineNode(contentSize: size, cornerRadius: radius, lineWidth: thickness)

//        borderNode?.removeFromParentNode()
//        let rect: CGRect = CGRect(origin: CGPoint.zero, size: size)
//        let radius: CGFloat = 0.5 * min(rect.width, rect.height) * roundness
//        let thickness: CGFloat = min(0.01 * min(rect.width, rect.height), 0.005)
//        borderGeometry = SCNRectangle(rect: rect, thickness: thickness, radius: radius)
//        borderGeometry.firstMaterial?.diffuse.contents = textColor
//        borderGeometry.firstMaterial?.isDoubleSided = true
//        borderNode = SCNNode(geometry: borderGeometry)
//        contentNode.addChildNode(borderNode)
//
//        borderNode.position = SCNVector3(-width / 2, -height / 2, 0)
    }
}

