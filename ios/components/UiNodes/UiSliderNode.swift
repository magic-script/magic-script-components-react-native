//
//  UiSliderNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 01/08/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import SceneKit

@objc class UiSliderNode: UiNode {
    static fileprivate let defaultWidth: CGFloat = 0.5
    static fileprivate let defaultHeight: CGFloat = 0.02

    @objc var width: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }

    @objc var onValueChanged: ((_ sender: UiNode, _ value: CGFloat) -> (Void))?

    fileprivate var sliderNode: SCNNode!

    @objc override func setupNode() {
        super.setupNode()

        assert(sliderNode == nil, "Node must not be initialized!")
        sliderNode = SCNNode()
        addChildNode(sliderNode)

        setDebugMode(true)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }
    }

    @objc override func getSize() -> CGSize {
        let contentWidth: CGFloat = (width > 0) ? width : UiSliderNode.defaultWidth
        let contentHeight: CGFloat = (height > 0) ? height : UiSliderNode.defaultHeight
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
    }
}

