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
        set { textGeometry.string = newValue; updateTextNodePosition() }
    }

    @objc var font: UIFont {
        get { return textGeometry.font }
        set { textGeometry.font = newValue; updateTextNodePosition() }
    }

    @objc var color: UIColor? {
        get { return textGeometry.materials.first?.diffuse.contents as? UIColor }
        set { textGeometry.materials.first?.diffuse.contents = newValue }
    }

    @objc var size: CGSize {
        get { return textGeometry.containerFrame.size }
        set {
            textGeometry.containerFrame = CGRect(origin: CGPoint.zero, size: newValue)
            updateTextNodePosition()
        }
    }
    
    fileprivate var textGeometry: SCNText!
    fileprivate var textNode: SCNNode!
    fileprivate var bboxNode: SCNBBoxNode?

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
        textNode.position = SCNVector3()
        addChildNode(textNode)
    }

    fileprivate func updateTextNodePosition() {
        let textBBox = textNode.boundingBox
        let textBBoxCenter: SCNVector3 = 0.5 * (textBBox.max + textBBox.min)
        textNode.pivot = SCNMatrix4MakeTranslation(textBBoxCenter.x, textBBoxCenter.y, textBBoxCenter.z)

//        let frameSize = size
//        bboxNode?.removeFromParentNode()
//        bboxNode = SCNBBoxNode((min: SCNVector3(-0.5 * frameSize.width, -0.5 * frameSize.height, 0), max: SCNVector3(0.5 * frameSize.width, 0.5 * frameSize.height, 0)))
//        addChildNode(bboxNode!)

//        setBBox(visible: true, forceUpdate: true)
    }
}
