//
//  UiButtonNode.swift
//  SceneKitDemo
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class UiButtonNode: UiNode {

    @objc var title: String? {
        get { return textNode.text }
        set { textNode.text = newValue }
    }

    @objc var width: CGFloat = 2 {
        didSet { updateNodeSize() }
    }

    @objc var height: CGFloat = 1 {
        didSet { updateNodeSize() }
    }

    @objc var textSize: CGFloat {
        get { return textNode.textSize }
        set { textNode.textSize = newValue }
    }

    @objc var roundness: CGFloat = 0.5 {
        didSet { updateNodeSize() }
    }

    @objc var color: UIColor = UIColor.white {
        didSet {
            textNode.textColor = color
            borderGeometry.materials.first?.diffuse.contents = color
        }
    }

    @objc var onTap: ((_ sender: UiNode) -> (Void))?

    fileprivate var borderGeometry: SCNRectangle!
    fileprivate var contentNode: SCNNode!
    fileprivate var borderNode: SCNNode!
    fileprivate var textNode: UiTextNode!

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    @objc func simulateTap() {

        onTap?(self)
        let initialPosition = contentNode.position
        let animation = CABasicAnimation(keyPath: "position.z")
        animation.fromValue = initialPosition.z
        animation.toValue = initialPosition.z - 0.2
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
        textNode.textColor = color
        contentNode.addChildNode(textNode)

        updateNodeSize()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let title = Convert.toString(props["title"]) {
            self.title = title
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let textSize = Convert.toCGFloat(props["textSize"]) {
            self.textSize = textSize
        }

        if let roundness = Convert.toCGFloat(props["roundness"]) {
            self.roundness = roundness
        }

        if let color = Convert.toColor(props["color"]) {
            self.color = color
        }
    }

    fileprivate func updateNodeSize() {
        let size = CGSize(width: width, height: height)
        textNode.boundsSize = size

        borderNode?.removeFromParentNode()
        let rect: CGRect = CGRect(origin: CGPoint.zero, size: size)
        let radius: CGFloat = 0.5 * min(rect.width, rect.height) * roundness
        borderGeometry = SCNRectangle(rect: rect, thickness: 0.005, radius: radius)
        borderGeometry.firstMaterial?.diffuse.contents = color
        borderNode = SCNNode(geometry: borderGeometry)
        contentNode.addChildNode(borderNode)

        borderNode.position = SCNVector3(-width / 2, -height / 2, 0)
    }
}

