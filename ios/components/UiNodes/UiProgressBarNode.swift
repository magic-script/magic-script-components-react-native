//
//  UiProgressBarNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 01/08/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import SceneKit

@objc class UiProgressBarNode: UiNode {
    static fileprivate let defaultWidth: CGFloat = 0.5
    static fileprivate let defaultHeight: CGFloat = 0.004

    @objc var width: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var min: CGFloat {
        get { return _min }
        set { if (newValue != _min && newValue < _max) { _min = newValue; setNeedsLayout(); } }
    }
    @objc var max: CGFloat {
        get { return _max }
        set { if (newValue != _max && _min < newValue) { _max = newValue; setNeedsLayout(); } }
    }
    @objc var value: CGFloat {
        get { return _value }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, _min, _max)
            if (_value != clampedValue) { _value = clampedValue; setNeedsLayout(); }
        }
    }
    @objc var beginColor: UIColor = UIColor.white {
        didSet { if (beginColor != oldValue) { progressImage = nil; setNeedsLayout(); } }
    }
    @objc var endColor: UIColor = UIColor.white {
        didSet { if (endColor != oldValue) { progressImage = nil; setNeedsLayout(); } }
    }

    //    @objc var onProgressChanged: ((_ sender: UiNode, _ progress: CGFloat) -> (Void))?

    fileprivate var _min: CGFloat = 0
    fileprivate var _max: CGFloat = 1
    fileprivate var _value: CGFloat = 0
    fileprivate var backgroundGeometry: SCNPlane!
    fileprivate var progressGeometry: SCNPlane!
    fileprivate var progressImage: UIImage?
    fileprivate var progressNode: SCNNode!

    @objc override func setupNode() {
        super.setupNode()

        assert(backgroundGeometry == nil, "Node must not be initialized!")
        backgroundGeometry = SCNPlane(width: width, height: height)
        backgroundGeometry.firstMaterial?.lightingModel = .constant
        backgroundGeometry.firstMaterial?.isDoubleSided = true
        let backgroundImage = UIImage.image(from: [.lightGray], size: 32)
        backgroundGeometry.firstMaterial?.diffuse.contents = backgroundImage

        assert(progressGeometry == nil, "Node must not be initialized!")
        progressGeometry = SCNPlane(width: width, height: height)
        progressGeometry.firstMaterial?.lightingModel = .constant
        progressGeometry.firstMaterial?.isDoubleSided = true
        progressGeometry.firstMaterial?.readsFromDepthBuffer = false

        let bgNode = SCNNode(geometry: backgroundGeometry)
        progressNode = SCNNode(geometry: progressGeometry)
        progressNode.renderingOrder = 1
        contentNode.addChildNode(bgNode)
        contentNode.addChildNode(progressNode)
        
        //        setDebugMode(true)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let min = Convert.toCGFloat(props["min"]) {
            self.min = min
        }

        if let max = Convert.toCGFloat(props["max"]) {
            self.max = max
        }

        if let value = Convert.toCGFloat(props["value"]) {
            self.value = value
        }

        if let progressColor = props["progressColor"] as? [String: Any] {
            if let beginColor = Convert.toColor(progressColor["beginColor"]) {
                self.beginColor = beginColor
            }

            if let endColor = Convert.toColor(progressColor["endColor"]) {
                self.endColor = endColor
            }
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let contentWidth: CGFloat = (width > 0) ? width : UiProgressBarNode.defaultWidth
        let contentHeight: CGFloat = (height > 0) ? height : UiProgressBarNode.defaultHeight
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        let size = getSize()

        backgroundGeometry.width = size.width
        backgroundGeometry.height = size.height
        backgroundGeometry.cornerRadius = 0.5 * size.height

        let progress: CGFloat = ((max - min) > 0) ? (value - min) / (max - min) : 0

        if progressImage == nil {
            progressImage = UIImage.gradientImageWithBounds(bounds: CGRect(x: 0, y: 0, width: 32.0, height: 32.0), colors: [beginColor.cgColor, endColor.cgColor])
            progressGeometry.firstMaterial?.diffuse.contents = progressImage
        }

        let progressWidth = size.width * progress
        progressGeometry.width = progressWidth
        progressGeometry.height = size.height
        progressGeometry.cornerRadius = 0.5 * size.height
        progressNode.pivot = SCNMatrix4MakeTranslation(-0.5 * Float(progressWidth), 0.0, 0.0)
        progressNode.position = SCNVector3(-0.5 * size.width, 0.0, 0.0)
    }
}
