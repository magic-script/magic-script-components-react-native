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
        set { labelNode.text = newValue }
    }
    @objc var textColor: UIColor {
        get { return labelNode.textColor }
        set { labelNode.textColor = newValue }
    }
    @objc var textSize: CGFloat {
        get { return labelNode.textSize }
        set { labelNode.textSize = newValue }
    }

    // @objc var allCaps: Bool // TODO: property to defined
    // @objc var charSpacing: CGFloat // TODO: property to defined
    // @objc var lineSpacing: CGFloat // TODO: property to defined
    @objc var textAlignment: HorizontalTextAlignment {
        get { return labelNode.textAlignment }
        set { labelNode.textAlignment = newValue }
    }
    // @objc var style: UIFont.TextStyle // TODO: property to defined
    // @objc var weight: UIFont.Weight // TODO: property to defined
    @objc var boundsSize: CGSize {
        get { return labelNode.boundsSize }
        set { labelNode.boundsSize = newValue }
    }
    @objc var wrap: Bool {
        get { return labelNode.wrap }
        set { labelNode.wrap = newValue }
    }
    // @objc var font: FontParams // use UIFont instead

    fileprivate var labelNode: LabelNode!

    @objc override func setupNode() {
        super.setupNode()
        labelNode = LabelNode()
        addChildNode(labelNode)
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

        if let boundsSize = Convert.toCGSize(props["boundsSize"]) {
            self.boundsSize = boundsSize
        }

        if let wrap = Convert.toBool(props["wrap"]) {
            self.wrap = wrap
        }

//        if let font = Convert.toFont(props["font"]) {
//            self.font = font
//        }

        labelNode.reload()
    }
}
