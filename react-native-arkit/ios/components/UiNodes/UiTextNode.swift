//
//  UiTextNode.swift
//  SceneKitDemo
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class UiTextNode: SCNNode {

    @objc var text: String? {
        get { return textGeometry.string as? String }
        set { textGeometry.string = newValue; updateTextNodePosition() }
    }
    @objc var textColor: UIColor? {
        get { return textGeometry.firstMaterial?.diffuse.contents as? UIColor }
        set { textGeometry.firstMaterial?.diffuse.contents = newValue }
    }
    @objc var textSize: CGFloat {
        get { return self.font.pointSize }
        set { self.font = UIFont(name: self.font.familyName, size: newValue)! }
    }

    // @objc var allCaps: Bool // TODO: property to defined
    // @objc var charSpacing: CGFloat // TODO: property to defined
    // @objc var lineSpacing: CGFloat // TODO: property to defined
    // @objc var textAlignment: HorizontalTextAlignment // TODO: property to defined
    // @objc var style: UIFont.TextStyle // TODO: property to defined
    // @objc var weight: UIFont.Weight // TODO: property to defined
    @objc var boundsSize: CGSize {
        get { return textGeometry.containerFrame.size }
        set {
            textGeometry.containerFrame = CGRect(origin: CGPoint.zero, size: newValue)
            updateTextNodePosition()
        }
    }
    @objc var wrap: Bool = true
    // @objc var font: FontParams // use UIFont instead
    @objc var font: UIFont {
        get { return textGeometry.font }
        set { textGeometry.font = newValue; updateTextNodePosition() }
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
        textGeometry.firstMaterial?.lightingModel = .constant
        textGeometry.firstMaterial?.diffuse.contents = UIColor.white
        textNode = SCNNode(geometry: textGeometry)
        textNode.position = SCNVector3()
        addChildNode(textNode)
    }

    fileprivate func updateTextNodePosition() {

        DispatchQueue.main.async() { [weak self] in
            guard let strongSelf = self else { return }
            let textBBox = strongSelf.textNode.boundingBox
            let textBBoxCenter: SCNVector3 = 0.5 * (textBBox.max + textBBox.min)
            strongSelf.textNode.pivot = SCNMatrix4MakeTranslation(textBBoxCenter.x, textBBoxCenter.y, textBBoxCenter.z)
        }

//        let frameSize = size
//        bboxNode?.removeFromParentNode()
//        bboxNode = SCNBBoxNode((min: SCNVector3(-0.5 * frameSize.width, -0.5 * frameSize.height, 0), max: SCNVector3(0.5 * frameSize.width, 0.5 * frameSize.height, 0)))
//        addChildNode(bboxNode!)

//        setBBox(visible: true, forceUpdate: true)
    }
}
