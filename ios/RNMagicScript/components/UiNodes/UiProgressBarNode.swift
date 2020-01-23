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

@objc open class UiProgressBarNode: UiNode {
    static let defaultWidth: CGFloat = 0.5
    static let defaultHeight: CGFloat = 0.004
    
    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
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
        backgroundGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        let backgroundImage = UIImage.image(from: [.lightGray], size: 32)
        backgroundGeometry.firstMaterial?.diffuse.contents = backgroundImage

        progressGeometry = SCNPlane(width: width, height: height)
        progressGeometry.firstMaterial?.lightingModel = .constant
        progressGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        progressGeometry.firstMaterial?.readsFromDepthBuffer = false

        let bgNode = SCNNode(geometry: backgroundGeometry)
        progressNode = SCNNode(geometry: progressGeometry)
        progressNode.renderingOrder = 1
        contentNode.addChildNode(bgNode)
        contentNode.addChildNode(progressNode)
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

        let delta = max - min
        let progress: CGFloat = (delta > 0.0001) ? (value - min) / delta : 0

        if progressImage == nil {
            progressImage = UIImage.gradientImage(withSize: CGSize(width: 32.0, height: 32.0), colors: [beginColor.cgColor, endColor.cgColor])
            progressGeometry.firstMaterial?.diffuse.contents = progressImage
        }

        let progressWidth = Swift.max(size.width * progress, size.height)
        progressGeometry.width = progressWidth
        progressGeometry.height = size.height
        progressGeometry.cornerRadius = 0.5 * size.height
        progressNode.pivot = SCNMatrix4MakeTranslation(-0.5 * Float(progressWidth), 0.0, 0.0)
        progressNode.position = SCNVector3(-0.5 * size.width, 0.0, 0.0)
    }
}
