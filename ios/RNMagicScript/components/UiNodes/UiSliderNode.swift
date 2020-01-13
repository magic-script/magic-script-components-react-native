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
    static fileprivate let defaultHeight: CGFloat = 0.018
    static fileprivate let backgroundHeightFactor: CGFloat = 0.75

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
    @objc var minLabel: String? {
        didSet {
            minLabelNode.text = minLabel
            minLabelNode.reload()
        }
    }
    @objc var max: CGFloat {
        get { return _max }
        set { if (newValue != _max && _min < newValue) { _max = newValue; setNeedsLayout(); } }
    }
    @objc var maxLabel: String? {
        didSet {
            maxLabelNode.text = maxLabel
            maxLabelNode.reload()
        }
    }
    @objc var value: CGFloat {
        get { return _value }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, _min, _max)
            if (_value != clampedValue) { _value = clampedValue; setNeedsLayout(); layoutIfNeeded(); }
        }
    }
    @objc var foregroundColor: UIColor = UIColor.white {
        didSet { if (foregroundColor != oldValue) { setNeedsLayout(); } }
    }

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }
        reloadOutlineNode()
    }

    @discardableResult
    @objc override func leaveFocus(onBehalfOf node: UiNode? = nil) -> Bool {
        let result = super.leaveFocus(onBehalfOf: node)
        outlineNode?.removeFromParentNode()
        return result
    }

    fileprivate var _min: CGFloat = 0.0
    fileprivate var _max: CGFloat = 1.0
    fileprivate var _value: CGFloat = 0.0
    fileprivate var backgroundGeometry: SCNPlane!
    fileprivate var foregroundGeometry: SCNPlane!
    fileprivate var progressNode: SCNNode!
    fileprivate var minLabelNode: LabelNode!
    fileprivate var maxLabelNode: LabelNode!
    fileprivate var outlineNode: SCNNode!

    @objc public var onSliderChanged: ((_ sender: UiSliderNode, _ value: CGFloat) -> (Void))?

    @objc override func setupNode() {
        super.setupNode()

        assert(backgroundGeometry == nil, "Node must not be initialized!")
        backgroundGeometry = SCNPlane(width: width, height: height * UiSliderNode.backgroundHeightFactor)
        backgroundGeometry.firstMaterial?.lightingModel = .constant
        backgroundGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        backgroundGeometry.firstMaterial?.diffuse.contents = UIColor.lightGray

        foregroundGeometry = SCNPlane(width: width, height: height)
        foregroundGeometry.firstMaterial?.lightingModel = .constant
        foregroundGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        foregroundGeometry.firstMaterial?.readsFromDepthBuffer = false

        minLabelNode = LabelNode()
        minLabelNode.defaultTextSize = 0.0167
        minLabelNode.textSize = 0.065
        minLabelNode.textColor = .white
        minLabelNode.textAlignment = .center
        minLabelNode.textPadding = UIEdgeInsets(top: 0.0, left: 0.0, bottom: 0.0, right: 0.0125)

        maxLabelNode = LabelNode()
        maxLabelNode.defaultTextSize = 0.0167
        maxLabelNode.textSize = 0.065
        maxLabelNode.textColor = .white
        maxLabelNode.textAlignment = .center
        maxLabelNode.textPadding = UIEdgeInsets(top: 0.0, left: 0.0125, bottom: 0.0, right: 0.0)

        let bgNode = SCNNode(geometry: backgroundGeometry)
        progressNode = SCNNode(geometry: foregroundGeometry)

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

        if let foregroundColor = Convert.toColor(props["foregroundColor"]) {
            self.foregroundColor = foregroundColor
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
        backgroundGeometry.height = size.height * UiSliderNode.backgroundHeightFactor
        backgroundGeometry.cornerRadius = 0.5 * size.height

        let delta = max - min
        let progress: CGFloat = (delta > 0.0001) ? (value - min) / delta : 0

        foregroundGeometry.firstMaterial?.diffuse.contents = foregroundColor.cgColor

        let slideWidth = Swift.max(size.height, size.width * progress)
        foregroundGeometry.width = slideWidth
        foregroundGeometry.height = size.height
        foregroundGeometry.cornerRadius = 0.5 * size.height
        progressNode.pivot = SCNMatrix4MakeTranslation(-0.5 * Float(slideWidth), 0.0, 0.0)
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

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        minLabelNode.setDebugMode(debug)
        maxLabelNode.setDebugMode(debug)
    }
}

extension UiSliderNode: SliderDataProviding {
    var sliderValue: CGFloat {
        get {
            return value
        }
        set {
            if value != newValue {
                value = newValue
                layoutIfNeeded()
                onSliderChanged?(self, value)
            }
        }
    }
}
