//
//  UiButtonNode.swift
//  SceneKitDemo
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class UiButtonNode: SCNNode {

    @objc var title: String? {
        get { return textNode.text }
        set { textNode.text = newValue }
    }

    @objc var size: CGSize = CGSize(width: 2, height: 1) {
        didSet { updateNodeSize() }
    }

    @objc var color: UIColor = UIColor.blue {
        didSet {
            textNode.textColor = color
            borderGeometry.materials.first?.diffuse.contents = color
        }
    }

    @objc var onTap: ((_ sender: SCNNode) -> (Void))?

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
        animation.toValue = initialPosition.z - 0.4
        animation.duration = 0.1
        animation.autoreverses = true
        animation.repeatCount = 1
        contentNode.addAnimation(animation, forKey: "button_tap")
    }

    fileprivate func setupNode() {
        contentNode = SCNNode()
        addChildNode(contentNode)

        textNode = UiTextNode()
        textNode.textColor = color
        contentNode.addChildNode(textNode)

        updateNodeSize()
    }

    fileprivate func updateNodeSize() {
        textNode.boundsSize = CGSize(width: size.width, height: size.height)

        borderNode?.removeFromParentNode()
        let rect: CGRect = CGRect(origin: CGPoint.zero, size: size)
        let radius: CGFloat = 0.5 * min(rect.width, rect.height)
        borderGeometry = SCNRectangle(rect: rect, thickness: 0.07, radius: radius)
        borderGeometry.firstMaterial?.diffuse.contents = color
        borderNode = SCNNode(geometry: borderGeometry)
        contentNode.addChildNode(borderNode)

        borderNode.position = SCNVector3(-size.width / 2, -size.height / 2, 0)
    }
}

