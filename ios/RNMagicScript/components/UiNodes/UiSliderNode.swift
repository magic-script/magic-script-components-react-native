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

@objc open class UiSliderNode: UiNode {
    static fileprivate let defaultWidth: CGFloat = 0.5
    static fileprivate let defaultHeight: CGFloat = 0.004

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
    @objc var minLabel: String = "min" {
        didSet {
            minLabelNode.text = minLabel
            minLabelNode.reload()
        }
    }
    @objc var max: CGFloat {
        get { return _max }
        set { if (newValue != _max && _min < newValue) { _max = newValue; setNeedsLayout(); } }
    }
    @objc var maxLabel: String = "max" {
        didSet {
            maxLabelNode.text = maxLabel
            maxLabelNode.reload()
        }
    }
    @objc var value: CGFloat {
        get { return _value }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, _min, _max)
            if (_value != clampedValue) { _value = clampedValue; setNeedsLayout(); }
        }
    }
    @objc var foregroundColor: UIColor = UIColor.white {
        didSet { if (foregroundColor != oldValue) { foregroundImage = nil; setNeedsLayout(); } }
    }

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        reloadOutlineNode()
    }

    @objc override func leaveFocus() {
        super.leaveFocus()
        outlineNode?.removeFromParentNode()
    }

    fileprivate var _min: CGFloat = 0.0
    fileprivate var _max: CGFloat = 1.0
    fileprivate var _value: CGFloat = 0.0
    fileprivate var backgroundGeometry: SCNPlane!
    fileprivate var progressGeometry: SCNPlane!
    fileprivate var foregroundImage: UIImage?
    fileprivate var progressNode: SCNNode!
    fileprivate var minLabelNode: LabelNode!
    fileprivate var maxLabelNode: LabelNode!
    fileprivate var outlineNode: SCNNode!

    @objc override func setupNode() {
        super.setupNode()

        assert(backgroundGeometry == nil, "Node must not be initialized!")
        backgroundGeometry = SCNPlane(width: width, height: height)
        backgroundGeometry.firstMaterial?.lightingModel = .constant
        backgroundGeometry.firstMaterial?.isDoubleSided = true
        let backgroundImage = UIImage.image(from: [.lightGray], size: 32)
        backgroundGeometry.firstMaterial?.diffuse.contents = backgroundImage

        progressGeometry = SCNPlane(width: width, height: height)
        progressGeometry.firstMaterial?.lightingModel = .constant
        progressGeometry.firstMaterial?.isDoubleSided = true
        progressGeometry.firstMaterial?.readsFromDepthBuffer = false

        minLabelNode = LabelNode()
        minLabelNode.defaultTextSize = 0.0167
        minLabelNode.textSize = 0.065
        minLabelNode.textColor = .white
        minLabelNode.textAlignment = .center

        maxLabelNode = LabelNode()
        maxLabelNode.defaultTextSize = 0.0167
        maxLabelNode.textSize = 0.065
        maxLabelNode.textColor = .white
        maxLabelNode.textAlignment = .center

        let bgNode = SCNNode(geometry: backgroundGeometry)
        progressNode = SCNNode(geometry: progressGeometry)

        progressNode.renderingOrder = 1
        contentNode.addChildNode(minLabelNode)
        contentNode.addChildNode(bgNode)
        contentNode.addChildNode(progressNode)
        contentNode.addChildNode(maxLabelNode)

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

        if let minLabel = Convert.toString(props["minLabel"]) {
            self.minLabel = minLabel
        }

        if let max = Convert.toCGFloat(props["max"]) {
            self.max = max
        }

        if let maxLabel = Convert.toString(props["maxLabel"]) {
            self.maxLabel = maxLabel
        }

        if let value = Convert.toCGFloat(props["value"]) {
            self.value = value
        }

        if let progressColor = Convert.toColor(props["progressColor"]) {
            self.foregroundColor = progressColor
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let contentWidth: CGFloat = (width > 0) ? width : UiSliderNode.defaultWidth
        let contentHeight: CGFloat = (height > 0) ? height : UiSliderNode.defaultHeight
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        let size = getSize()

        backgroundGeometry.width = size.width
        backgroundGeometry.height = size.height
        backgroundGeometry.cornerRadius = 0.5 * size.height

        let delta = max - min
        let progress: CGFloat = (delta > 0.0001) ? (value - min) / delta : 0

        if foregroundImage == nil {
            foregroundImage = UIImage.gradientImage(withSize: CGSize(width: 32.0, height: 32.0), colors: [foregroundColor.cgColor, foregroundColor.cgColor])
            progressGeometry.firstMaterial?.diffuse.contents = foregroundImage
        }

        let progressWidth = size.width * progress
        progressGeometry.width = progressWidth
        progressGeometry.height = size.height
        progressGeometry.cornerRadius = 0.5 * size.height
        progressNode.pivot = SCNMatrix4MakeTranslation(-0.5 * Float(progressWidth), 0.0, 0.0)
        progressNode.position = SCNVector3(-0.5 * size.width, 0.0, 0.0)

        reloadOutlineNode()

        let backgroundGeometryFactor = backgroundGeometry.width / 2
        let minLabelWidthFactor = minLabelNode.getSize().width / 2
        let maxLabelWidthFactor = maxLabelNode.getSize().width / 2
        let magicFactor: CGFloat = 0.01
        minLabelNode.position = SCNVector3(-backgroundGeometryFactor - minLabelWidthFactor - magicFactor, 0.0, 0.0)
        maxLabelNode.position = SCNVector3(backgroundGeometryFactor + maxLabelWidthFactor + magicFactor, 0.0, 0.0)
        minLabelNode.reload()
        maxLabelNode.reload()
    }

    fileprivate func reloadOutlineNode() {
        if hasFocus {
            let size = getSize()

            outlineNode?.removeFromParentNode()

            let minLabelWidthFactor = minLabelNode.getSize().width
            let maxLabelWidthFactor = maxLabelNode.getSize().width

            let radius: CGFloat = 0.85 * Swift.min(size.width, size.height)
            let thickness: CGFloat = 0.1 * Swift.min(size.width, size.height)
            guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
            outlineNode = NodesFactory.createOutlineNode(width: size.width + minLabelWidthFactor + maxLabelWidthFactor + 0.02 + 0.075, height: size.height + 0.075, cornerRadius: radius, thickness: thickness, color: .white)
            contentNode.addChildNode(outlineNode)
            outlineNode.position = SCNVector3((maxLabelWidthFactor-minLabelWidthFactor) / 2, 0.0, 0.0)
        }
    }
}

extension UiSliderNode: SliderDataProviding { }
