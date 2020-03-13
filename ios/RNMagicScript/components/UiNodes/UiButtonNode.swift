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

// The default values for width and height are 0, which instructs
// the button to use the default button height and automatically
// calculates button width based on the button text dimensions.
//
// Setting a value greater than 0 for width or height will override
// the defaults.
//
// Button text size is automatically set based on the button height
// unless overridden with UiButton::setTextSize().

@objc open class UiButtonNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.0167

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    private var _text: String? = nil
    private func convertTextForButtonType(text: String?) -> String? {
        return self.buttonType == .text || buttonType == .textWithIcon ? text?.uppercased() : text
    }
    @objc var text: String? {
        get { return labelNode.text }
        set { _text = newValue; labelNode.text = convertTextForButtonType(text: newValue); reloadOutline = true; setNeedsLayout() }
    }

    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { labelNode.textColor = textColor; reloadOutline = true; setNeedsLayout() }
    }
    @objc var iconColor: UIColor = UIColor.white {
        didSet { iconNode.geometry?.firstMaterial?.multiply.contents = iconColor }
    }
    @objc var textSize: CGFloat = 0 {
        didSet { reloadOutline = true; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    @objc var iconSize: CGFloat = 0.1
    @objc var iconType: String = "" {
        didSet {
            iconNode.geometry?.firstMaterial?.diffuse.contents = SystemIcon(iconType).getImage()
            setNeedsLayout()
        }
    }

    @objc var labelSide: Side = .right {
        didSet {
            setNeedsLayout()
        }
    }

    @objc var buttonType: ButtonType = .simple {
        didSet {
            switch buttonType {
            case .simple:
                labelNode.isHidden = false
                iconNode.isHidden = true
            case .iconWithLabel:
                labelNode.isHidden = true
                iconNode.isHidden = false
            case .textWithIcon:
                labelNode.isHidden = false
                iconNode.isHidden = false
            case .icon:
                labelNode.isHidden = true
                iconNode.isHidden = false
            case .text:
                labelNode.isHidden = false
                iconNode.isHidden = true
            }
            text = convertTextForButtonType(text: _text)
            setNeedsLayout()
        }
    }

    @objc var width: CGFloat = 0 {
        didSet { reloadOutline = true; setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { reloadOutline = true; updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    fileprivate var _roundness: CGFloat = 1.0
    @objc var roundness: CGFloat {
        get { return _roundness }
        set {
            let clampedValue: CGFloat = max(0, min(newValue, 1.0))
            if clampedValue != _roundness {
                _roundness = clampedValue
                reloadOutline = true
                setNeedsLayout()
            }
        }
    }

    fileprivate var outlineNode: SCNNode!
    fileprivate var labelNode: LabelNode!
    fileprivate var iconNode: SCNNode!
    fileprivate var reloadOutline: Bool = true

    deinit {
        contentNode.removeAllAnimations()
    }

    @objc override func setupNode() {
        super.setupNode()

        alignment = Alignment.centerCenter
        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.textAlignment = .center
        labelNode.defaultTextSize = UiButtonNode.defaultTextSize

        iconNode = NodesFactory.createPlaneNode(width: 1, height: 1)

        contentNode.addChildNode(labelNode)
        contentNode.addChildNode(iconNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let text = Convert.toString(props["text"]) {
            self.text = text
        }

        if let textColor = Convert.toColor(props["textColor"]) {
            self.textColor = textColor
        }

        if let textSize = Convert.toCGFloat(props["textSize"]) {
            self.textSize = textSize
        }

        if let iconColor = Convert.toColor(props["iconColor"]) {
            self.iconColor = iconColor
        }

        if let iconSize = Convert.toCGFloat(props["iconSize"]) {
            self.iconSize = iconSize
        }

        if let iconType = Convert.toString(props["iconType"]) {
            self.iconType = iconType
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let roundness = Convert.toCGFloat(props["roundness"]) {
            self.roundness = roundness
        }

        if let buttonType = Convert.toButtonType(props["type"]) {
            self.buttonType = buttonType
        }

        if let labelSide = Convert.toSide(props["labelSide"]) {
            self.labelSide = labelSide
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let buttonSize = getButtonSize()
        let contentWidth: CGFloat = (width > 0) ? width : buttonSize.width
        let contentHeight: CGFloat = (height > 0) ? height : buttonSize.height
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func getBounds(parentSpace: Bool = false, scaled: Bool = true) -> CGRect {
        let size = getSize(scaled: scaled)
        let origin: CGPoint = parentSpace ? CGPoint(x: CGFloat(localPosition.x), y: CGFloat(localPosition.y)) : CGPoint.zero
        let offset = getOffset()
        return CGRect(origin: origin + offset, size: size)
    }

    fileprivate func getOffset() -> CGPoint {
        let size = getSize()
        let buttonSize = getButtonSize()

        switch buttonType {
        case .iconWithLabel:
            switch labelSide {
            case .bottom:
                return CGPoint(x: -size.width + 0.5 * buttonSize.width, y: 0.5 * getOutlineNodeSize().height - buttonSize.height)
            case .top:
                return CGPoint(x: -size.width + 0.5 * buttonSize.width, y: -0.5 * getOutlineNodeSize().height)
            case .left:
                return CGPoint(x: -size.width + 0.5 * getOutlineNodeSize().width, y: -0.5 * buttonSize.height)
            case .right:
                return CGPoint(x: -0.5 * getOutlineNodeSize().width, y: -0.5 * buttonSize.height)
            }
        default:
            return CGPoint(x: -0.5 * size.width, y: -0.5 * size.height)
        }

    }

    @objc override func updateLayout() {
        labelNode.reload()
        _ = getSize()
        let iconSize = getIconSize()
        labelNode.position = getLabelPosition()

        if let iconPlane = iconNode.geometry as? SCNPlane {
            iconPlane.width = iconSize.width
            iconPlane.height = iconSize.height
        }

        iconNode.position = getIconPosition()

        if reloadOutline {
            reloadOutline = false
            reloadOutlineNode()
        }

        outlineNode?.position = getOutlinePosition()
    }

    fileprivate func getButtonSize(includeOutline: Bool = true) -> CGSize {
        let labelSize = labelNode.getSize()
        let iconSize = getIconSize()

        let contentHeight = height > 0 ? height : max(labelSize.height, iconSize.height)
        let gap: CGFloat = (labelSize.width > 0 && iconSize.width > 0) ? 0.15 * contentHeight : 0
        let contentWidth = width > 0 ? width : labelSize.width + gap + iconSize.width

        switch buttonType {
        case .iconWithLabel:
            switch labelSide {
            case .top, .bottom:
                let gap: CGFloat = (labelSize.height > 0 && iconSize.height > 0) ? 0.15 * height > 0 ? height : max(labelSize.height, iconSize.height) : 0
                let contentHeight = height > 0 ? height : getOutlineNodeSize().height + labelSize.height + 0.5 * gap
                let contentWidth = width > 0 ? width : max(labelSize.width, getOutlineNodeSize().width)
                return CGSize(width: contentWidth, height: contentHeight)
            default:
                let contentHeight = height > 0 ? height : getOutlineNodeSize().height
                let gap: CGFloat = (labelSize.width > 0 && iconSize.width > 0) ? 0.15 * contentHeight : 0
                let contentWidth = width > 0 ? width : labelSize.width + getOutlineNodeSize().width + gap
                return CGSize(width: contentWidth, height: contentHeight)
            }
        case .icon:
                return CGSize(width: getOutlineNodeSize().width, height: getOutlineNodeSize().height)
        default:
            if includeOutline {
                let buttonToTextHeightMultiplier: CGFloat = 1.6
                return CGSize(width: contentWidth + buttonToTextHeightMultiplier * contentHeight, height: buttonToTextHeightMultiplier * contentHeight)
            }
            return CGSize(width: contentWidth, height: contentHeight)
        }
    }

    func getIconSize() -> CGSize {
        getIconSize(height: height > 0 ? height : labelNode.getSize().height * 2.1)
    }

    fileprivate func getIconSize(height: CGFloat) -> CGSize {
        switch buttonType {
        case .iconWithLabel, .textWithIcon, .icon:
            return 0.65 * CGSize(width: height, height: height)
        default:
            return CGSize.zero
        }
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }

    fileprivate func updateLabelTextSizeBasedOnHeight() {
        guard textSize == 0 && height > 0 else  {
            labelNode.textSize = textSize
            return
        }

        labelNode.textSize = max(0, 0.333 * height)
    }

    fileprivate func reloadOutlineNode() {
        let size = getOutlineNodeSize()

        outlineNode?.removeFromParentNode()

        let radius: CGFloat = 0.5 * min(size.width, size.height) * roundness
        let thickness: CGFloat = 0.05 * min(size.width, size.height)
        guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
        outlineNode = NodesFactory.createOutlineNode(width: size.width, height: size.height, cornerRadius: radius, thickness: thickness)
        if (buttonType == .iconWithLabel || buttonType == .textWithIcon || buttonType == .text) {
            outlineNode.isHidden = true
        }
        contentNode.addChildNode(outlineNode)
    }

    private func getOutlineNodeSize() -> CGSize {
        let labelSize = labelNode.getSize()
        var outlineSize: CGSize = CGSize.zero
        switch buttonType {
        case .iconWithLabel, .icon:
            if height > 0 {
                outlineSize = getIconSize(height: height) * 1.6
            } else if labelSize.height > 0 {
                outlineSize = getIconSize(height: labelSize.height * 2.1) * 1.6
            } else {
                outlineSize = CGSize(width: 0.15, height: 0.15)
            }
        default:
            outlineSize = getSize()
        }
        return outlineSize
    }

    private func getLabelPosition() -> SCNVector3 {
        let buttonSize = getButtonSize(includeOutline: false)
        let labelSize = labelNode.getSize()
        let iconSize = getIconSize()
        let gap: CGFloat = (labelSize.width > 0 && iconSize.width > 0) ? 0.15 * buttonSize.height : 0

        switch buttonType {
        case .iconWithLabel:
            let outlineSize = getOutlineNodeSize()
            switch labelSide {
            case .right:
                return SCNVector3(0.5 * (outlineSize.width + labelSize.width) + gap, 0, 0)
            case .left:
                return SCNVector3(-0.5 * (outlineSize.width + labelSize.width) - gap, 0, 0)
            case .top:
                return SCNVector3(0, 0.5 * (outlineSize.height + labelSize.height) + gap, 0)
            case .bottom:
                return SCNVector3(0, -0.5 * (outlineSize.height + labelSize.height) - gap, 0)
            }
        case .textWithIcon:
            return SCNVector3(0.5 * buttonSize.width - 0.5 * labelSize.width + gap, 0, 0)
        default:
            return SCNVector3.zero
        }
    }

    private func getIconPosition() -> SCNVector3 {
        let buttonSize = getButtonSize(includeOutline: false)
        let iconSize = getIconSize()

        switch buttonType {
        case .iconWithLabel, .icon:
            return SCNVector3.zero
        default:
            return SCNVector3(-0.5 * buttonSize.width + 0.5 * iconSize.width, 0, 0)
        }
    }

    private func getOutlinePosition() -> SCNVector3 {
        return SCNVector3.zero
    }
}

extension UiButtonNode: TapSimulating, CAAnimationDelegate {
    func simulateTap() {

        let initialPosition = contentNode.position
        let animation = CABasicAnimation(keyPath: "position.z")
        animation.fromValue = initialPosition.z
        animation.toValue = initialPosition.z - 0.05
        animation.duration = 0.25
        animation.autoreverses = true
        animation.repeatCount = 1
        animation.delegate = self
        let objectAnimationKey = "UiButtonNode" + (name ?? "")
        contentNode.addAnimation(animation, forKey: objectAnimationKey)
    }

    public func animationDidStart(_ anim: CAAnimation) {
        switch buttonType {
        case .iconWithLabel:
            labelNode.isHidden = false
            outlineNode.isHidden = false
        case .textWithIcon, .text:
            outlineNode.isHidden = false
        default:
            print("")
        }
    }

    public func animationDidStop(_ anim: CAAnimation, finished flag: Bool) {
        switch buttonType {
        case .iconWithLabel:
            labelNode.isHidden = true
            outlineNode.isHidden = true
        case .textWithIcon, .text:
            outlineNode.isHidden = true
        default:
            print("")
        }
    }
}

