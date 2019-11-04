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

@objc open class UiColorPickerNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.0167

    fileprivate var _startingColor: UIColor = .white
    @objc var startingColor: UIColor {
        get { return _startingColor }
        set { _startingColor = newValue }
    }

    fileprivate var _color: UIColor? = nil
    @objc var color: UIColor {
        get {
            guard let selectedColor = _color else { return startingColor }
            return selectedColor
        }
        set { _color = newValue }
    }

    @objc var height: CGFloat = 0

    @objc public var onColorChanged: ((_ sender: UiColorPickerNode, _ selected: [CGFloat]) -> (Void))?
    @objc public var onConfirm: ((_ sender: UiColorPickerNode, _ confirmed: [CGFloat]) -> (Void))?
    @objc public var onCancel: ((_ sender: UiColorPickerNode) -> (Void))?

    fileprivate var roundness: CGFloat = 1.0

    fileprivate var outlineNode: SCNNode!
    fileprivate var labelNode: LabelNode!
    fileprivate var reloadOutline: Bool = true

    deinit {
        contentNode.removeAllAnimations()
    }

    @objc override var canHaveFocus: Bool {
        return enabled
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        simulateTap()
    }

    @objc func simulateTap() {
        assert(enabled, "Button must be enabled in order to tap it!")
        let initialPosition = contentNode.position
        let animation = CABasicAnimation(keyPath: "position.z")
        animation.fromValue = initialPosition.z
        animation.toValue = initialPosition.z - 0.05
        animation.duration = 0.1
        animation.autoreverses = true
        animation.repeatCount = 1
        contentNode.addAnimation(animation, forKey: "colorPicker_tap")
    }

    @objc override func setupNode() {
        super.setupNode()

        alignment = Alignment.centerCenter
        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.text = "Color picker"
        labelNode.textAlignment = .center
        labelNode.defaultTextSize = UiColorPickerNode.defaultTextSize
        labelNode.reload()
        reloadOutlineNode()
        contentNode.addChildNode(labelNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let startingColor = Convert.toColor(props["startingColor"]) {
            self.startingColor = startingColor
        }

        if let color = Convert.toColor(props["color"]) {
            self.color = color
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let labelSize = labelNode.getSize()
        let buttonToTextHeightMultiplier: CGFloat = 2.3
        let contentWidth: CGFloat = labelSize.width + buttonToTextHeightMultiplier * labelSize.height
        let contentHeight: CGFloat = buttonToTextHeightMultiplier * labelSize.height
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        labelNode.reload()
        if reloadOutline {
            reloadOutline = false
            reloadOutlineNode()
        }
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }

    fileprivate func updateLabelTextSizeBasedOnHeight() {
        labelNode.textSize = UiColorPickerNode.defaultTextSize
    }

    fileprivate func reloadOutlineNode() {
        let size = getSize()

        outlineNode?.removeFromParentNode()

        let radius: CGFloat = 0.5 * min(size.width, size.height) * roundness
        let thickness: CGFloat = 0.05 * min(size.width, size.height)
        guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
        outlineNode = NodesFactory.createOutlineNode(width: size.width, height: size.height, cornerRadius: radius, thickness: thickness)
        contentNode.addChildNode(outlineNode)
    }
}

extension UiColorPickerNode: ColorPickerDataProviding {
    var colorPickerValue: UIColor {
        get { return color }
        set {
            if color != newValue {
                color = newValue
                onColorChanged?(self, [])
            }
        }
    }

    func colorChanged() {
        onColorChanged?(self, color.toArrayOfCGFloat)
    }

    func colorConfirmed() {
        onConfirm?(self, color.toArrayOfCGFloat)
    }

    func colorCanceled() {
        onCancel?(self)
    }
}

extension UIColor {
    var toArrayOfCGFloat: [CGFloat] {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        return [red, green, blue, alpha]
    }
}
