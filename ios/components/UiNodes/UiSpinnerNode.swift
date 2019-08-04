//
//  UiSpinner.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 01/07/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class UiSpinnerNode: UiNode {

    static fileprivate let defaultSize: CGFloat = 0.07

    @objc var size: CGSize = CGSize.zero {
        didSet { setNeedsLayout() }
    }

    @objc var value: CGFloat = 0 {
        didSet { /*setNeedsLayout()*/ }
    }

    fileprivate var planeGeometry: SCNPlane!
    fileprivate var spinnerNode: SCNNode!

    deinit {
        stopAnimation()
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(spinnerNode == nil, "Node must not be initialized!")
        planeGeometry = SCNPlane(width: UiSpinnerNode.defaultSize, height: UiSpinnerNode.defaultSize)
        planeGeometry.firstMaterial?.lightingModel = .constant
        planeGeometry.firstMaterial?.diffuse.contents = ImageAsset.spinner.image
        planeGeometry.firstMaterial?.isDoubleSided = true
        spinnerNode = SCNNode(geometry: planeGeometry)
        contentNode.addChildNode(spinnerNode)
        startAnimation()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let size = Convert.toCGSize(props["size"]) {
            self.size = size
        }

        if let value = Convert.toCGFloat(props["value"]) {
            self.value = value
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let width: CGFloat = size.width > 0 ? size.width : UiSpinnerNode.defaultSize
        let height: CGFloat = size.height > 0 ? size.height : UiSpinnerNode.defaultSize
        return CGSize(width: width, height: height)
    }

    @objc override func updateLayout() {
        let spinnerSize = getSize()
        planeGeometry.width = spinnerSize.width
        planeGeometry.height = spinnerSize.height
    }

    fileprivate func startAnimation() {
        let animation = CABasicAnimation(keyPath: "rotation")
        animation.fromValue = SCNVector4(x: 0, y: 0, z: 1, w: 0)
        animation.toValue = SCNVector4(x: 0, y: 0, z: 1, w: -2 * Float.pi)
        animation.duration = 1
        animation.repeatCount = .infinity
        spinnerNode.addAnimation(animation, forKey: "spin around")
    }

    fileprivate func stopAnimation() {
        spinnerNode.removeAllAnimations()
    }
}
