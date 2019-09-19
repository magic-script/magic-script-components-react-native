//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import SceneKit

@objc open class UiSpinnerNode: UiNode {

    static let defaultSize: CGFloat = 0.07

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var size: CGSize = CGSize.zero {
        didSet { setNeedsLayout() }
    }
    @objc var value: CGFloat {
        get { return _value }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, 0.0, 1.0)
            if (_value != clampedValue) { _value = clampedValue; setNeedsLayout(); }
        }
    }
    @objc var determinate: Bool = false {
        didSet { reloadGeometry(); setNeedsLayout() }
    }

    fileprivate var _value: CGFloat = 0
    fileprivate var planeGeometry: SCNPlane!
    fileprivate var circleGeometry: SCNSpinnerCircle!
    fileprivate var spinnerNode: SCNNode!

    deinit {
        stopAnimation()
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(spinnerNode == nil, "Node must not be initialized!")
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let size = Convert.toCGSize(props["size"]) {
            self.size = size
        }

        if let value = Convert.toCGFloat(props["value"]) {
            self.value = value
        }

        if let determinate = Convert.toBool(props["determinate"]) {
            self.determinate = determinate
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let localWidth: CGFloat = (size.width > 0) ? size.width : (height > 0 ? height : UiSpinnerNode.defaultSize)
        let localHeight: CGFloat = (size.height > 0) ? size.height : (height > 0 ? height : UiSpinnerNode.defaultSize)
        return CGSize(width: localWidth, height: localHeight)
    }

    @objc override func updateLayout() {
        if circleGeometry == nil && planeGeometry == nil {
            reloadGeometry()
        }

        let spinnerSize = getSize()
        let scaleX: CGFloat = spinnerSize.width
        let scaleY: CGFloat = spinnerSize.height
        spinnerNode?.scale = SCNVector3(scaleX, scaleY, 1)
        circleGeometry?.progress = Float(value)
    }

    fileprivate func reloadGeometry() {
        stopAnimation()
        planeGeometry = nil
        circleGeometry = nil
        spinnerNode?.removeFromParentNode()

        if determinate {
            circleGeometry = SCNSpinnerCircle(size: CGSize(width: 1.0, height: 1.0), thickness: 0.08)
            circleGeometry.barBeginImage = ImageAsset.spinnerProgressBegin.image
            circleGeometry.barEndImage = ImageAsset.spinnerProgressEnd.image
            spinnerNode = SCNNode(geometry: circleGeometry)
            contentNode.addChildNode(spinnerNode)
        } else {
            let width: CGFloat = 196.0 / 256.0
            let height: CGFloat = 92.0 / 256.0
            let y: CGFloat = 88.0 / 256.0
            planeGeometry = SCNPlane(width: width, height: height)
            planeGeometry.firstMaterial?.lightingModel = .constant
            planeGeometry.firstMaterial?.diffuse.contents = ImageAsset.spinner.image
            planeGeometry.firstMaterial?.isDoubleSided = true
            planeGeometry.firstMaterial?.writesToDepthBuffer = false
            planeGeometry.firstMaterial?.transparencyMode = .singleLayer

            spinnerNode = SCNNode(geometry: planeGeometry)
            spinnerNode.pivot.position = SCNVector3(0, -y, 0)
            contentNode.addChildNode(spinnerNode)
            startAnimation()
        }
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
        spinnerNode?.removeAllAnimations()
    }
}
