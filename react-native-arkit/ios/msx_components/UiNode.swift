//
//  UiNode.swift
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc class UiNode: SCNNode {

    var enabled: Bool = true
    var eventPassThrough: Bool = true
    var eventPassThroughChildren: Bool = true
    var gravityWellEnabled: Bool = true

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    fileprivate func setupNode() {
    }
}
