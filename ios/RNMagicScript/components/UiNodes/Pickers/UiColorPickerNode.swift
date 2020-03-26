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

@objc open class UiColorPickerNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.055
    static fileprivate let defaultLabelGap: CGFloat = 0.095

    fileprivate var _startingColor: UIColor = .white
    @objc var startingColor: UIColor {
        get { return _startingColor }
        set { _startingColor = newValue; updateHEXValue() }
    }

    fileprivate var _color: UIColor? = nil
    @objc var color: UIColor {
        get {
            guard let selectedColor = _color else { return startingColor }
            return selectedColor
        }
        set {
            _color = newValue
            if let colorBlockGeometry = colorBlockNode.geometry as? SCNPlane {
                colorBlockGeometry.firstMaterial?.diffuse.contents = color
            }
            updateHEXValue()
            setNeedsLayout()
        }
    }

    @objc var height: CGFloat = 0

    @objc public var onColorChanged: ((_ sender: UiColorPickerNode, _ selected: [CGFloat]) -> (Void))?
    @objc public var onColorConfirmed: ((_ sender: UiColorPickerNode, _ confirmed: [CGFloat]) -> (Void))?
    @objc public var onColorCanceled: ((_ sender: UiColorPickerNode, _ confirmed: [CGFloat]) -> (Void))?

    fileprivate var roundness: CGFloat = 1.0

    fileprivate var outlineNode: SCNNode!
    fileprivate var labelNode: LabelNode!
    fileprivate var colorBlockNode: SCNNode!
    fileprivate var reloadOutline: Bool = true

    deinit {
        contentNode.removeAllAnimations()
    }

    @objc override var canHaveFocus: Bool {
        return enabled
    }

    @objc override func setupNode() {
        super.setupNode()

        alignment = Alignment.centerCenter
        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        updateHEXValue()
        labelNode.textAlignment = .center
        labelNode.defaultTextSize = UiColorPickerNode.defaultTextSize
        labelNode.reload()

        let underlineGeometry = SCNPlane(width: 0.0, height: 0.0)
        underlineGeometry.firstMaterial?.lightingModel = .constant
        underlineGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        underlineGeometry.firstMaterial?.diffuse.contents = color
        colorBlockNode = SCNNode(geometry: underlineGeometry)

        contentNode.addChildNode(labelNode)
        contentNode.addChildNode(colorBlockNode)

        reloadOutlineNode()
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

    fileprivate func updateHEXValue() {
        labelNode.text = "#" + color.hexCode
    }

    @objc override func _calculateSize() -> CGSize {
        let labelSize = labelNode.getSize()
        let colorBlockSize = CGSize(width: labelSize.height * 2, height: labelSize.height)
        let buttonToTextHeightMultiplier: CGFloat = 2.3
        let contentWidth: CGFloat = labelSize.width + colorBlockSize.width + (buttonToTextHeightMultiplier * labelSize.height)
        let contentHeight: CGFloat = buttonToTextHeightMultiplier * labelSize.height
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        labelNode.reload()
        let labelNodeSize = labelNode.getSize()
        let size = getSize()
        let gap = UiColorPickerNode.defaultLabelGap

        if let colorBlockGeometry = colorBlockNode.geometry as? SCNPlane {
            colorBlockGeometry.firstMaterial?.diffuse.contents = color
            colorBlockGeometry.height = labelNodeSize.height
            colorBlockGeometry.width = labelNodeSize.height * 2
        }

        labelNode.position = SCNVector3(0.5 * (size.width - labelNodeSize.width - gap), 0.0, 0.0)
        colorBlockNode.position = SCNVector3(-0.5 * (size.width - (labelNodeSize.height * 2) - gap), 0.0, 0.0)

        reloadOutlineNode()
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
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

extension UiColorPickerNode: TapSimulating { }

extension UiColorPickerNode: ColorPickerDataProviding {
    var colorPickerValue: UIColor {
        get { return self.color }
        set {
            if self.color != newValue {
                self.color = newValue
                onColorChanged?(self, [])
                layoutIfNeeded()
            }
        }
    }

    func colorChanged() {
        onColorChanged?(self, color.toArrayOfCGFloat)
    }

    func colorConfirmed() {
        onColorConfirmed?(self, color.toArrayOfCGFloat)
    }

    func colorCanceled() {
        onColorCanceled?(self, color.toArrayOfCGFloat)
    }
}
