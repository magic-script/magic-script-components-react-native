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

@objc open class UiCircleConfirmationNode: UiNode {

    static let defaultSize: CGFloat = 0.07

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }

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

    fileprivate var backgroundNode: SCNNode!
    fileprivate var circleNode: SCNNode!
    fileprivate var animationAction: SCNAction?
    fileprivate var reverseInitialValue: CGFloat = 0

    deinit {
        stopAnimation()
    }

    @objc override func setupNode() {
        super.setupNode()

        let circleGeometry = SCNCircle(size: CGSize(width: 1.0, height: 1.0), thickness: 0.04)
        circleGeometry.barImage = ImageAsset.circleConfirmation.image
        circleNode = SCNNode(geometry: circleGeometry)
        contentNode.addChildNode(circleNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let size: CGFloat = height > 0 ? height : UiSpinnerNode.defaultSize
        return CGSize(width: size, height: size)
    }

    @objc override func updateLayout() {
        let spinnerSize = getSize()
        let scaleX: CGFloat = spinnerSize.width
        let scaleY: CGFloat = spinnerSize.height
        circleNode.scale = SCNVector3(scaleX, scaleY, 1)
        updateValue()
    }

    fileprivate func updateValue() {
        if let circleGeometry = circleNode.geometry as? SCNCircle {
            circleGeometry.progress = Float(value)
        }
    }

    fileprivate var isConfirmed: Bool {
        return value >= 0.99999
    }
    
    fileprivate func startAnimation() {
        let duration: TimeInterval = 2.0
        let action = SCNAction.customAction(duration: duration) { [weak self] (node, deltaTime) in
            guard let strongSelf = self else { return }
            let currentValue = deltaTime / CGFloat(duration)
            strongSelf.value = currentValue
            strongSelf.onConfirmationUpdated?(strongSelf, currentValue)
            strongSelf.layoutIfNeeded()
            if (strongSelf.isConfirmed) {
                strongSelf.onConfirmationCompleted?(strongSelf)
            }
        }
        circleNode.runAction(action, forKey: "forward")
    }

    fileprivate func stopAnimation() {
        circleNode.removeAllActions()
    }

    @objc override var canBeLongPressed: Bool {
        return true
    }

    @objc override func longPressStarted() {
        super.longPressStarted()
        startAnimation()
    }

    @objc override func longPressEnded() {
        super.longPressEnded()
        stopAnimation()
        guard !isConfirmed else { return }
        
        reverseInitialValue = value
        let duration: TimeInterval = 0.5 * TimeInterval(value)
        let action = SCNAction.customAction(duration: duration) { [weak self] (node, deltaTime) in
            guard let strongSelf = self else { return }
            let currentValue = (1.0 - deltaTime / CGFloat(duration)) * strongSelf.reverseInitialValue
            strongSelf.value = currentValue
            strongSelf.onConfirmationUpdated?(strongSelf, currentValue)
            strongSelf.layoutIfNeeded()
            if (currentValue <= 0.00001) {
                strongSelf.onConfirmationCanceled?(strongSelf)
            }
        }
        circleNode.runAction(action, forKey: "backward")

    }
}
