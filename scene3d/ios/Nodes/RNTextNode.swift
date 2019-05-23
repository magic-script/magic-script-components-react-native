//
//  RNTextNode.swift
//  SceneKitDemo
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class RNTextNode: SCNNode {

    @objc var text: String? {
        get { return textGeometry.string as? String }
        set { textGeometry.string = newValue }
    }

    @objc var font: UIFont {
        get { return textGeometry.font }
        set { textGeometry.font = newValue }
    }

    @objc var color: UIColor? {
        get { return textGeometry.materials.first?.diffuse.contents as? UIColor }
        set { textGeometry.materials.first?.diffuse.contents = newValue }
    }

    @objc var size: CGSize {
        get { return textGeometry.containerFrame.size }
        set {
            textGeometry.containerFrame = CGRect(origin: CGPoint.zero, size: newValue)
            updateTextNodePoisition()
        }
    }
    
    fileprivate var textGeometry: SCNText!
    fileprivate var textNode: SCNNode!

    override init() {
        super.init()
        setupNode()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    fileprivate func setupNode() {
        textGeometry = SCNText(string: "", extrusionDepth: 0)
        textGeometry.font = UIFont.systemFont(ofSize: 0.4)
        textGeometry.alignmentMode = CATextLayerAlignmentMode.center.rawValue
        textGeometry.flatness = 0.5
        textGeometry.materials.first?.diffuse.contents = UIColor.white
        textNode = SCNNode(geometry: textGeometry)
        addChildNode(textNode)
    }

    fileprivate func updateTextNodePoisition() {
        let frameSize = size
        let textBBox = textGeometry.boundingBox
        let textSize = CGSize(width: CGFloat(textBBox.max.x - textBBox.min.x), height: CGFloat(textBBox.max.y - textBBox.min.y))

        let x: CGFloat = -frameSize.width / 2 + 0.5 * (frameSize.width - textSize.width)
        let y: CGFloat = -frameSize.height / 2 + 0.5 * (frameSize.height - textSize.height)
        textNode.position = SCNVector3(x, y, 0)
    }
}
