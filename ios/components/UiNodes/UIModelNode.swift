//
//  UIModelNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/06/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

import SceneKit

@objc class UiModelNode: UiNode {

    @objc var url: URL?

    @objc override func setupNode() {
        super.setupNode()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let url = Convert.toFileURL(props["modelPath"]) {
            self.url = url
        }
    }

}
