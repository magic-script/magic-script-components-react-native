//
//  UiProgressBarNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 01/08/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import SceneKit

@objc class UiProgressBarNode: UiNode {
    static fileprivate let defaultWidth: CGFloat = 0.5
    static fileprivate let defaultHeight: CGFloat = 0.02

    @objc var width: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }

    @objc var onProgressChanged: ((_ sender: UiNode, _ progress: CGFloat) -> (Void))?

    fileprivate var progressNode: SCNNode!

    @objc override func setupNode() {
        super.setupNode()

        assert(progressNode == nil, "Node must not be initialized!")
        progressNode = SCNNode()
        addChildNode(progressNode)

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
        let contentWidth: CGFloat = (width > 0) ? width : UiProgressBarNode.defaultWidth
        let contentHeight: CGFloat = (height > 0) ? height : UiProgressBarNode.defaultHeight
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
    }
}
