//
//  LabelNode.swift
//  SceneKitComponents
//
//  Created by Pawel Leszkiewicz on 15/07/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit
import UIKit

class LabelNode: SCNNode {
    static fileprivate let defaultTextSizeInMeters: CGFloat = 0.015
    static fileprivate let geometryFixedTextSizeInMeters: CGFloat = 20.0

    @objc var text: String? {
        didSet { reloadNeeded = true }
    }
    @objc var textColor: UIColor = UIColor.white {
        didSet { reloadNeeded = true }
    }
    @objc var textSize: CGFloat = LabelNode.defaultTextSizeInMeters {
        didSet { reloadNeeded = true }
    }
    @objc var boundsSize: CGSize = CGSize.zero {
        didSet { reloadNeeded = true }
    }
    @objc var wrap: Bool = false {
        didSet { reloadNeeded = true }
    }

    fileprivate var labelGeometry: SCNText!
    fileprivate var label: UILabel!
    fileprivate var labelNode: SCNNode!
    fileprivate var reloadNeeded: Bool = false
    fileprivate let useGeometry: Bool = true

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    deinit {
        labelNode.geometry?.firstMaterial?.diffuse.contents = nil
    }

//    #if targetEnvironment(simulator)
    fileprivate func setupNode() {
        if useGeometry {
            labelGeometry = SCNText(string: "", extrusionDepth: 0)
            labelGeometry.font = UIFont.systemFont(ofSize: LabelNode.geometryFixedTextSizeInMeters)
            labelGeometry.alignmentMode = CATextLayerAlignmentMode.left.rawValue
            labelGeometry.flatness = 0.5
            labelGeometry.firstMaterial?.lightingModel = .constant
            labelGeometry.firstMaterial?.diffuse.contents = UIColor.white
            labelGeometry.firstMaterial?.isDoubleSided = true
            labelNode = SCNNode(geometry: labelGeometry)
            addChildNode(labelNode)
        } else {
            label = UILabel(frame: CGRect(x: 0, y: 0, width: 0, height: 0))
            label.text = ""
            label.textColor = UIColor.white
            label.backgroundColor = UIColor.clear
//            label.layer.masksToBounds = true
            label.layer.contentsScale = UIScreen.main.scale
            label.layer.rasterizationScale = UIScreen.main.scale
            label.layer.shouldRasterize = true

            let planeGeometry = SCNPlane(width: 0.0, height: 0.0)
            planeGeometry.firstMaterial?.transparencyMode = .aOne
            planeGeometry.firstMaterial?.lightingModel = .constant
            planeGeometry.firstMaterial?.diffuse.contents = label.layer
            planeGeometry.firstMaterial?.isDoubleSided = true

            labelNode = SCNNode(geometry: planeGeometry)
            addChildNode(labelNode)
            SCNTransaction.flush()
        }

        reload()
    }

    func reload() {
        guard reloadNeeded else { return }
        reloadNeeded = false

        updateLabelContents()
        updateLabelSize()
    }

    fileprivate func updateLabelContents() {
        if useGeometry {
            let scale = textSize / LabelNode.geometryFixedTextSizeInMeters
            let size = CGSize(width: boundsSize.width / scale, height: boundsSize.height / scale)
            labelGeometry.string = text
            labelGeometry.containerFrame = CGRect(origin: CGPoint.zero, size: size)
            labelGeometry.firstMaterial?.diffuse.contents = textColor
            labelGeometry.isWrapped = wrap
//            labelGeometry.alignmentMode = CATextLayerAlignmentMode.left.rawValue // kCAAlignmentNatural
//            labelGeometry.truncationMode = CATextLayerTruncationMode.end.rawValue
            labelNode?.removeFromParentNode()
            labelNode = SCNNode(geometry: labelGeometry)
            labelNode.scale = SCNVector3(scale, scale, scale)
            addChildNode(labelNode)
            updateTextNodePosition()
        } else {
            label.text = text
            label.textColor = textColor
            label.numberOfLines = wrap ? 0 : 1
            label.lineBreakMode = wrap ? .byWordWrapping : .byTruncatingTail
            let fontSize: CGFloat = Measures.pixels(from: textSize)
            label.font = label.font.withSize(fontSize)
        }
    }

    fileprivate func getPreferredSize() -> CGSize {
        guard let text = text, !text.isEmpty else { return CGSize.zero }

        let preferredSizeInPixels = text.size(withAttributes: [NSAttributedString.Key.font : label.font])
        let width: CGFloat = (boundsSize.width > 0) ? boundsSize.width : Measures.meters(from: preferredSizeInPixels.width)
        let height: CGFloat = (boundsSize.height > 0) ? boundsSize.height : Measures.meters(from: preferredSizeInPixels.height)
        return CGSize(width: width, height: height)
    }

    fileprivate func updateLabelSize() {
        guard let plane = labelNode.geometry as? SCNPlane else { return }

        let size = getPreferredSize()
        plane.width = size.width
        plane.height = size.height
        let widthInPixels = CGFloat(Int(Int(Measures.pixels(from: size.width)) / 16) * 16)
        let heightInPixels = CGFloat(Int(Int(Measures.pixels(from: size.height)) / 16) * 16)

        let maxFrameSize: CGFloat = 2048
        let scaleFactor: CGFloat
        let sizeInPixels: CGSize
        if widthInPixels < maxFrameSize && heightInPixels < maxFrameSize {
            scaleFactor = 1
            sizeInPixels = CGSize(width: ceil(widthInPixels), height: ceil(heightInPixels))
//            print("sizeInPixels1: \(sizeInPixels)")
        } else {
            scaleFactor = min(min(maxFrameSize / widthInPixels, maxFrameSize / heightInPixels), 1)
            sizeInPixels = CGSize(width: ceil(scaleFactor * widthInPixels), height: ceil(scaleFactor * heightInPixels))
//            print("sizeInPixels2: \(sizeInPixels) - \(scaleFactor)")
        }

        label.frame = CGRect(x: 0, y: 0, width: sizeInPixels.width, height: sizeInPixels.height)
        label.contentScaleFactor = 1.0 / scaleFactor
        label.layer.setNeedsLayout()
        label.layer.layoutIfNeeded()
        label.setNeedsDisplay()
        plane.firstMaterial?.diffuse.contents = label.layer
        SCNTransaction.flush()
    }

    fileprivate func updateTextNodePosition() {
        DispatchQueue.main.async() { [weak self] in
            guard let strongSelf = self else { return }
            let textBBox = strongSelf.labelNode.boundingBox
            let textBBoxCenter: SCNVector3 = 0.5 * (textBBox.max + textBBox.min)
            strongSelf.labelNode.pivot = SCNMatrix4MakeTranslation(textBBoxCenter.x, textBBoxCenter.y, textBBoxCenter.z)
        }
    }
}
