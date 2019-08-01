//
//  UiTextNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class UiTextNode: UiNode {
    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; setNeedsLayout() }
    }
    @objc var textColor: UIColor {
        get { return labelNode.textColor }
        set { labelNode.textColor = newValue }
    }
    @objc var textSize: CGFloat {
        get { return labelNode.textSize }
        set { labelNode.textSize = newValue; setNeedsLayout() }
    }

    // @objc var allCaps: Bool = false // TODO: property to defined
    // @objc var charSpacing: CGFloat = 0 // TODO: property to defined
    // @objc var lineSpacing: CGFloat = 1// TODO: property to defined
    @objc var textAlignment: HorizontalTextAlignment {
        get { return labelNode.textAlignment }
        set { labelNode.textAlignment = newValue; setNeedsLayout() }
    }
    // @objc var style: UIFont.TextStyle // TODO: property to defined
    // @objc var weight: UIFont.Weight // TODO: property to defined
    @objc var boundsSize: CGSize {
        get { return labelNode.boundsSize }
        set { labelNode.boundsSize = newValue; setNeedsLayout() }
    }
    @objc var wrap: Bool {
        get { return labelNode.wrap }
        set { labelNode.wrap = newValue; setNeedsLayout() }
    }
    // @objc var font: FontParams // use UIFont instead

    fileprivate var labelNode: LabelNode!

    @objc override func setupNode() {
        super.setupNode()
        alignment = Alignment.bottomLeft // default alignment of UiText

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        addChildNode(labelNode)

        setDebugMode(true)
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

        if let textAlignment = Convert.toHorizontalTextAlignment(props["textAlignment"]) {
            self.textAlignment = textAlignment
        }

        if let boundsSizeProps = props["boundsSize"] as? [String: Any] {
            if let boundsSize = Convert.toCGSize(boundsSizeProps["boundsSize"]) {
                self.boundsSize = boundsSize
            }

            if let wrap = Convert.toBool(boundsSizeProps["wrap"]) {
                self.wrap = wrap
            }
        }
    }

    @objc override func _calculateSize() -> CGSize {
        return labelNode.getSize()
    }

    @objc override func updateLayout() {
        labelNode.reload()
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }
}
