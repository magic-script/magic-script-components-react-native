//
//  RNButtonNode.swift
//  SceneKitDemo
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class RNButtonNode: SCNNode {

    @objc var title: String? {
        get { return textNode.text }
        set { textNode.text = newValue }
    }

    @objc var size: CGSize = CGSize(width: 2, height: 1) {
        didSet { updateNodeSize() }
    }

    @objc var color: UIColor = UIColor.blue {
        didSet {
            textNode.color = color
            borderGeometry.materials.first?.diffuse.contents = color
        }
    }

    fileprivate var borderGeometry: SCNRectangle!
    fileprivate var borderNode: SCNNode!
    fileprivate var textNode: RNTextNode!

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    fileprivate func setupNode() {
        textNode = RNTextNode()
        textNode.color = color
        addChildNode(textNode)

        updateNodeSize()
    }

    fileprivate func updateNodeSize() {
        textNode.size = CGSize(width: size.width, height: size.height)

        borderNode?.removeFromParentNode()
        let rect: CGRect = CGRect(origin: CGPoint.zero, size: size)
        let radius: CGFloat = 0.5 * min(rect.width, rect.height)
        borderGeometry = SCNRectangle(rect: rect, thickness: 0.07, radius: radius)
        borderGeometry.materials.first?.diffuse.contents = color
        borderNode = SCNNode(geometry: borderGeometry)
        addChildNode(borderNode)

        borderNode.position = SCNVector3(-size.width / 2, -size.height / 2, 0)
    }
}

