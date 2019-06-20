//
//  UIModelNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/06/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

import SceneKit

@objc class UiModelNode: UiNode {

    fileprivate var modelNode: SCNNode?

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
        modelNode?.removeFromParentNode()
        guard let modelURL = modelURL else { return }
        do {
            let sceneSource = GLTFSceneSource(url: modelURL, options: nil)
            let scene = try sceneSource.scene()

            addChildNode(scene.rootNode)
            modelNode = scene.rootNode
        } catch {
            print("\(error.localizedDescription)")
            return
        }
    }
}
