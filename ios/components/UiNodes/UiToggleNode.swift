//
//  UiToggleNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 30/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import SceneKit

@objc class UiToggleNode: UiNode {

    static fileprivate let defaultTextSize: CGFloat = 0.234
    static fileprivate let defaultWidth: CGFloat = 0.07337 //0.172719
    static fileprivate let defaultHeight: CGFloat = 0.03359
    static fileprivate let defaultTextMargin: CGFloat = 0.03

    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; setNeedsLayout() }
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
        didSet { toggleGeometry.firstMaterial?.diffuse.contents = on ? ImageAsset.toggleOn.image : ImageAsset.toggleOff.image }
    }

    @objc var onChanged: ((_ sender: UiNode, _ on: Bool) -> (Void))?

    fileprivate var contentNode: SCNNode!
    fileprivate var labelNode: LabelNode!

    fileprivate var toggleNode: SCNNode!
    fileprivate var toggleGeometry: SCNPlane!

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        on = !on
        leaveFocus()
        onChanged?(self, on)
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "labelNode must not be initialized!")
        labelNode = LabelNode()
        labelNode.textAlignment = .left
        labelNode.textSize = UiToggleNode.defaultTextSize
        addChildNode(labelNode)

        assert(toggleNode == nil, "toggleNode must not be initialized!")
        let toggleSize = getToggleSize()
        toggleGeometry = SCNPlane(width: toggleSize.width, height: toggleSize.height)
        toggleGeometry.firstMaterial?.lightingModel = .constant
        toggleGeometry.firstMaterial?.diffuse.contents = ImageAsset.toggleOff.image
        toggleGeometry.firstMaterial?.isDoubleSided = true
        toggleNode = SCNNode(geometry: toggleGeometry)
        addChildNode(toggleNode)
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
    }

    @objc override func getSize() -> CGSize {
        let labelSize = labelNode.getSize()
        let toggleSize = getToggleSize()
        let textMargin: CGFloat = (labelSize.width > 0 && labelSize.height > 0) ? UiToggleNode.defaultTextMargin : 0

        let heightContent: CGFloat = max(toggleSize.height, labelSize.height)
        let widthContent: CGFloat = toggleSize.width + labelSize.height + textMargin
        return CGSize(width: widthContent, height: heightContent)
    }

    @objc override func updateLayout() {
        let size = getSize()
//        let labelSize = labelNode.getSize()
        let toggleSize = getToggleSize()

        toggleGeometry.width = toggleSize.width
        toggleGeometry.height = toggleSize.height

        let x1: Float = Float(-0.5 * size.width)
        labelNode.position = SCNVector3(x1, 0, 0)

//        let x2: Float = Float(0.5 * size.width - toggleSize.width)
        toggleNode.position = SCNVector3(x1, 0, 0)
    }

    fileprivate func getToggleSize() -> CGSize {
        let toggleWidth: CGFloat = (height > 0) ? height : UiToggleNode.defaultHeight
        return CGSize(width: toggleWidth, height:(UiToggleNode.defaultHeight / UiToggleNode.defaultWidth) * toggleWidth)
    }
}
