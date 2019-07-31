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
    @objc var textAlignment: HorizontalTextAlignment = .left {
        didSet { reloadNeeded = true }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { reloadNeeded = true }
    }
    @objc var textSize: CGFloat = 0 {
        didSet { reloadNeeded = true }
    }
    @objc var defaultTextSize: CGFloat = LabelNode.defaultTextSizeInMeters {
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
#if targetEnvironment(simulator)
    fileprivate var originNode: SCNNode?
    fileprivate var borderNode: OutlineNode?
#endif

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
#if targetEnvironment(simulator)
        updateDebugLayout()
#endif
    }

    fileprivate func updateLabelContents() {
        if useGeometry {
            let scale = getTextSize() / LabelNode.geometryFixedTextSizeInMeters
            labelGeometry.string = text
            let size = getSize()
            let rect = CGRect(origin: CGPoint.zero, size: CGSize(width: size.width / scale, height: size.height / scale))
            labelGeometry.containerFrame = rect
            labelGeometry.firstMaterial?.diffuse.contents = textColor
            labelGeometry.isWrapped = wrap
            labelGeometry.alignmentMode = textAlignment.textLayerAlignmentMode.rawValue
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
            let fontSize: CGFloat = Measures.pixels(from: getTextSize())
            label.font = label.font.withSize(fontSize)
        }
    }

    fileprivate func getTextSize() -> CGFloat {
        return (textSize > 0) ? textSize : defaultTextSize
    }

    fileprivate func getFont() -> UIFont {
        if useGeometry {
            return labelGeometry.font
        } else {
            return label.font
        }
    }

    func getSize() -> CGSize {
        guard let text = text, !text.isEmpty else { return boundsSize }

        if boundsSize.width > 0 && boundsSize.height > 0 {
            return boundsSize
        }

        let scale: CGFloat = useGeometry ? (getTextSize() / LabelNode.geometryFixedTextSizeInMeters) : 1.0
        let preferredSizeInPixels = text.size(withAttributes: [NSAttributedString.Key.font : getFont()])
        let width: CGFloat = (boundsSize.width > 0) ? boundsSize.width : (ceil(preferredSizeInPixels.width) * scale)
        let height: CGFloat = (boundsSize.height > 0) ? boundsSize.height : (ceil(preferredSizeInPixels.height) * scale)
        return CGSize(width: width, height: height)
    }

    fileprivate func updateLabelSize() {
        guard let plane = labelNode.geometry as? SCNPlane else { return }

        let size = getSize()
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
        } else {
            scaleFactor = min(min(maxFrameSize / widthInPixels, maxFrameSize / heightInPixels), 1)
            sizeInPixels = CGSize(width: ceil(scaleFactor * widthInPixels), height: ceil(scaleFactor * heightInPixels))
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
        let size = getSize()

        if boundsSize.width > 0 {
            labelNode.position = SCNVector3(-0.5 * size.width, -0.5 * size.height, 0)
        } else {
//            switch textAlignment {
//            case .left:
                labelNode.position = SCNVector3(0, -0.5 * size.height, 0)
//            case .center, .justify:
//                labelNode.position = SCNVector3(-0.5 * size.width, -0.5 * size.height, 0)
//            case .right:
//                labelNode.position = SCNVector3(-size.width, -0.5 * size.height, 0)
//                borderNode?.position = SCNVector3(-0.5 * size.width, 0, 0)
//            }
        }
    }
}


// MARK: - Debug mode
extension LabelNode {
    @objc func setDebugMode(_ debug: Bool) {
#if targetEnvironment(simulator)
        guard debug else {
            originNode?.removeFromParentNode()
            borderNode?.removeFromParentNode()
            return
        }

        // origin
        if originNode == nil {
            let sphere = SCNSphere(radius: 0.008)
            sphere.segmentCount = 4
            sphere.firstMaterial?.lightingModel = .constant
            sphere.firstMaterial?.diffuse.contents = UIColor.white
            originNode = SCNNode(geometry: sphere)
        }
        addChildNode(originNode!)

        updateDebugLayout()
#endif
    }

#if targetEnvironment(simulator)
    @objc fileprivate var isDebugMode: Bool { return originNode?.parent != nil }
    @objc fileprivate func updateDebugLayout() {
        guard isDebugMode else { return }

        // border
        let size = getSize()
        borderNode?.removeFromParentNode()
        borderNode = OutlineNode(contentSize: size, cornerRadius: 0, lineWidth: 0.0005, color: UIColor.white)
        insertChildNode(borderNode!, at: 0)

        borderNode?.position = SCNVector3(0.5 * size.width, 0, 0)
    }
#endif
}
