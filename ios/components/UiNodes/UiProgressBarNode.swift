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
    static fileprivate let defaultHeight: CGFloat = 0.02

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
    @objc var startColor: UIColor = UIColor.blue {
        didSet { if (startColor != oldValue) { barImage = nil; setNeedsLayout(); } }
    }
    @objc var endColor: UIColor = UIColor.gray {
        didSet { if (endColor != oldValue) { barImage = nil; setNeedsLayout(); } }
    }

//    @objc var onProgressChanged: ((_ sender: UiNode, _ progress: CGFloat) -> (Void))?

    fileprivate var _min: CGFloat = 0
    fileprivate var _max: CGFloat = 1
    fileprivate var _value: CGFloat = 0
    fileprivate var barGeometry: SCNPlane!
    fileprivate var barImage: UIImage?

    @objc override func setupNode() {
        super.setupNode()

        assert(barGeometry == nil, "Node must not be initialized!")
        barGeometry = SCNPlane(width: width, height: height)
        barGeometry.firstMaterial?.lightingModel = .constant
        barGeometry.firstMaterial?.diffuse.wrapS = SCNWrapMode.clamp
        barGeometry.firstMaterial?.isDoubleSided = true
        let bgNode = SCNNode(geometry: barGeometry)
        addChildNode(bgNode)

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

        if let startColor = Convert.toColor(props["startColor"]) {
            self.startColor = startColor
        }

        if let endColor = Convert.toColor(props["endColor"]) {
            self.endColor = endColor
        }
    }

    @objc override func getSize() -> CGSize {
        let contentWidth: CGFloat = (width > 0) ? width : UiProgressBarNode.defaultWidth
        let contentHeight: CGFloat = (height > 0) ? height : UiProgressBarNode.defaultHeight
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        if barImage == nil {
            barImage = UIImage.image(from: [startColor, endColor], size: 32)
            barGeometry.firstMaterial?.diffuse.contents = barImage
        }

        let size = getSize()
        barGeometry.width = size.width
        barGeometry.height = size.height
        barGeometry.cornerRadius = 0.5 * size.height

        let progress: CGFloat = ((max - min) > 0) ? (value - min) / (max - min) : 0
        let tx: Float = 0.5 - Float(progress)
        barGeometry.firstMaterial?.diffuse.contentsTransform = SCNMatrix4MakeTranslation(tx, 0, 0)
    }
}
