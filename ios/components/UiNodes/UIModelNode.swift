//
//  UIModelNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/06/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

import SceneKit

@objc class UiModelNode: UiNode {

    fileprivate var modelRefNode: SCNReferenceNode?

    @objc var url: URL? {
        didSet { loadModel(url) }
    }

    @objc override func setupNode() {
        super.setupNode()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let url = Convert.toFileURL(props["modelPath"]) {
            self.url = url
        }
    }

    fileprivate func loadModel(_ modelURL: URL?) {
        modelRefNode?.removeFromParentNode()
        guard let modelURL = modelURL, let node = SCNReferenceNode(url: modelURL) else {
            return
        }

        node.load();
        addChildNode(node)
        modelRefNode = node
    }

}
