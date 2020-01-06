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

@objc open class UiToggleNode: UiNode {

    static let defaultRectangleSize: CGSize = CGSize(width: 0.07337, height: 0.03359)
    static let defaultSquareSize: CGSize = CGSize(width: 0.03359, height: 0.03359) // Checkbox & Radio

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; labelNode.reload(); setNeedsLayout() }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { labelNode.textColor = textColor }
    }
    @objc var textSize: CGFloat {
        get { return labelNode.textSize }
        set { labelNode.textSize = newValue; setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var on: Bool = false {
        didSet { toggleGeometry.firstMaterial?.diffuse.contents = getToggleAsset() }
    }

    @objc var type: ToggleType = .default {
        didSet { setNeedsLayout() }
    }

    @objc public var onChanged: ((_ sender: UiNode, _ on: Bool) -> (Void))?
    var onChangeGroup: ((_ sender: UiToggleNode) -> (Void))? = { sender in sender.on = !sender.on }

    fileprivate var labelNode: LabelNode!

    fileprivate var toggleNode: SCNNode!
    fileprivate var toggleGeometry: SCNPlane!

    @objc override var canHaveFocus: Bool {
        return enabled
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        onChangeGroup?(self)

        leaveFocus()
        onChanged?(self, on)
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.textAlignment = .left
        contentNode.addChildNode(labelNode)

        let toggleSize = getToggleSize()
        toggleGeometry = SCNPlane(width: toggleSize.width, height: toggleSize.height)
        toggleGeometry.firstMaterial?.lightingModel = .constant
        toggleGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        toggleNode = SCNNode(geometry: toggleGeometry)
        contentNode.addChildNode(toggleNode)
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

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let on = Convert.toBool(props["on"]) {
            self.on = on
        }

        if let type = Convert.toToggleType(props["type"]) {
            self.type = type
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let labelSize = labelNode.getSize()
        let toggleSize = getToggleSize()
        let textToggleGap: CGFloat = 0.5 * toggleSize.width
        let textMargin: CGFloat = (labelSize.width > 0 && labelSize.height > 0) ? textToggleGap : 0

        let heightContent: CGFloat = max(toggleSize.height, labelSize.height)
        let widthContent: CGFloat = toggleSize.width + labelSize.width + textMargin
        return CGSize(width: widthContent, height: heightContent)
    }

    @objc override func getBounds(parentSpace: Bool = false, scaled: Bool = true) -> CGRect {
        let size = getSize(scaled: scaled)
        let origin: CGPoint = parentSpace ? CGPoint(x: CGFloat(localPosition.x), y: CGFloat(localPosition.y)) : CGPoint.zero
        let offset = getOffset()
        return CGRect(origin: origin + offset, size: size)
    }

    fileprivate func getOffset() -> CGPoint {
        let size = getSize()
        let toggleSize = getToggleSize()
        return type == ToggleType.default ? CGPoint(x: -size.width + 0.5 * toggleSize.width, y: -0.5 * size.height) : CGPoint(x: -0.5 * toggleSize.width, y: -0.5 * size.height)
    }

    @objc override func updateLayout() {
        labelNode.reload()

        if let toggleGroupNode = findToggleGroupParent(node: self) {
            toggleGroupNode.childPresent(toggleNode: self)
        }

        toggleGeometry.firstMaterial?.diffuse.contents = getToggleAsset()

        let toggleSize = getToggleSize()
        toggleGeometry.width = toggleSize.width
        toggleGeometry.height = toggleSize.height

        labelNode.position = SCNVector3(getLabelXPosition(), 0, 0)
    }

    fileprivate func findToggleGroupParent(node: SCNNode?) -> UiToggleGroupNode? {
        if let parent = node?.parent {
            if let toggleNodeGroup = parent as? UiToggleGroupNode {
                return toggleNodeGroup
            }
            return findToggleGroupParent(node: parent)
        }
        return nil
    }

    fileprivate func getLabelXPosition() -> Float {
        let size = getSize()
        let labelSize = labelNode.getSize()
        let toggleSize = getToggleSize()
        return type == ToggleType.default ? -Float(size.width - 0.5 * (labelSize.width + toggleSize.width)) : Float(size.width - 0.5 * (labelSize.width + toggleSize.width))
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }

    fileprivate func getToggleSize() -> CGSize {
        let defaultSize = getDefaultSize()
        let toggleHeight: CGFloat = (height > 0) ? height : defaultSize.height
        let toggleWidth: CGFloat = (defaultSize.width / defaultSize.height) * toggleHeight
        return CGSize(width: toggleWidth, height: toggleHeight)
    }

    fileprivate func getDefaultSize() -> CGSize {
        return type == ToggleType.default ? UiToggleNode.defaultRectangleSize : UiToggleNode.defaultSquareSize
    }

    fileprivate func getToggleAsset() -> UIImage {
        switch type {
        case .checkbox:
            return on ? ImageAsset.checkboxChecked.image : ImageAsset.checkboxUnchecked.image
        case .radio:
                return on ? ImageAsset.radioChecked.image : ImageAsset.radioUnchecked.image
        default:
            return on ? ImageAsset.toggleOn.image : ImageAsset.toggleOff.image
        }
    }
}
