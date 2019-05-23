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

    @objc var size: CGSize = CGSize(width: 2, height: 0.8) {
        didSet { updateNodeSize() }
    }

    @objc var color: UIColor = UIColor.blue {
        didSet {
            textNode.color = color
            shapeGeometry.materials.first?.diffuse.contents = color
        }
    }

    fileprivate var shapeGeometry: SCNShape!
    fileprivate var shapeNode: SCNNode!
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
        shapeGeometry = SCNShape(path: nil, extrusionDepth: 0)
        shapeGeometry.materials.first?.diffuse.contents = color
        shapeNode = SCNNode(geometry: shapeGeometry)
        addChildNode(shapeNode)

        textNode = RNTextNode()
        textNode.color = UIColor.white
        addChildNode(textNode)

        updateNodeSize()
    }

    fileprivate func updateNodeSize() {
        textNode.size = CGSize(width: size.width, height: size.height)
//        textNode.position = SCNVector3(-size.width / 2, -size.height / 2, 0.001)
        textNode.position = SCNVector3(-size.width / 3, -size.height, 0.001)
        let rect: CGRect = CGRect(x: 0, y: 0, width: size.width, height: size.height)
        let bezierPath = UIBezierPath(rect: rect)
        shapeGeometry.path = bezierPath
        shapeNode.position = SCNVector3(-size.width / 2, -size.height / 2, 0)
    }
}

