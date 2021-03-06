//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

@objc open class UiCircleConfirmationNode: UiNode {

    static let defaultRadius: CGFloat = 0.02

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var radius: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }

    var nodeAnimator: NodeAnimating!

    fileprivate var _value: CGFloat = 0.0
    fileprivate var value: CGFloat {
        get { return _value }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, 0.0, 1.0)
            if (_value != clampedValue) { _value = clampedValue; updateValue() }
        }
    }

    @objc public var onConfirmationCompleted: ((_ sender: UiCircleConfirmationNode) -> (Void))?
    @objc public var onConfirmationUpdated: ((_ sender: UiCircleConfirmationNode,_ value: CGFloat) -> (Void))?
    @objc public var onConfirmationCanceled: ((_ sender: UiCircleConfirmationNode) -> (Void))?

    fileprivate var circleNode: SCNNode!
    fileprivate var reverseInitialValue: CGFloat = 0

    @objc override func setupNode() {
        super.setupNode()

        let circleGeometry = SCNCircle(size: CGSize(width: 1.0, height: 1.0), thickness: 0.04)
        circleGeometry.barImage = ImageAsset.circleConfirmation.image
        circleNode = SCNNode(geometry: circleGeometry)
        contentNode.addChildNode(circleNode)

        nodeAnimator = UiNodeAnimator(circleNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let radius = Convert.toCGFloat(props["radius"]) {
            self.radius = radius
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let size: CGFloat = (radius > 0) ? 2 * radius : 2 * UiCircleConfirmationNode.defaultRadius
        return CGSize(width: size, height: size)
    }

    @objc override func updateLayout() {
        let spinnerSize = getSize()
        let scaleX: CGFloat = spinnerSize.width
        let scaleY: CGFloat = spinnerSize.height
        circleNode.scale = SCNVector3(scaleX, scaleY, 1)
    }

    fileprivate func updateValue() {
        if let circleGeometry = circleNode.geometry as? SCNCircle {
            circleGeometry.progress = Float(value)
        }
    }

    fileprivate var isConfirmed: Bool {
        return value >= 0.99999
    }
    
    fileprivate func startForwardAnimation() {
        let duration: TimeInterval = 2.0
        nodeAnimator.startAnimation(duration: duration) { [weak self] (node, deltaTime) in
            guard let strongSelf = self else { return }
            let currentValue = deltaTime / CGFloat(duration)
            strongSelf.value = currentValue
            strongSelf.onConfirmationUpdated?(strongSelf, currentValue)
            strongSelf.layoutIfNeeded()
            if (strongSelf.isConfirmed) {
                strongSelf.onConfirmationCompleted?(strongSelf)
            }
        }
    }

    fileprivate func startBackwardAnimation() {
        reverseInitialValue = value
        let duration: TimeInterval = 0.5 * TimeInterval(value)
        nodeAnimator.startAnimation(duration: duration) { [weak self] (node, deltaTime) in
            guard let strongSelf = self else { return }
            let currentValue = (duration > 0.00001) ? (1.0 - deltaTime / CGFloat(duration)) * strongSelf.reverseInitialValue : 0.0
            strongSelf.value = currentValue
            strongSelf.onConfirmationUpdated?(strongSelf, currentValue)
            strongSelf.layoutIfNeeded()
            if (currentValue <= 0.00001) {
                strongSelf.onConfirmationCanceled?(strongSelf)
            }
        }
    }

    @objc override var canBeLongPressed: Bool {
        return true
    }

    @objc override func longPressStarted() {
        super.longPressStarted()
        startForwardAnimation()
    }

    @objc override func longPressEnded() {
        super.longPressEnded()

        guard !isConfirmed else { return }
        startBackwardAnimation()
    }
}
