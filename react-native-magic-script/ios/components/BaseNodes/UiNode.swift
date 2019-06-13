//
//  UiNode.swift
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc class UiNode: TransformNode {

    //var alignment: Alignment
    //var activateResponse: FocusRequest
    //var renderingLayer: RenderingLayer
    //var enabled: Bool = true   // (check SCNNodeFocusBehavior)
    //var eventPassThrough: Bool = true
    //var eventPassThroughChildren: Bool = true
    //var gravityWellEnabled: Bool = true
    //var eventSoundId: ClassProperty
    //var gravityWellProperties: GravityWellProperties

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)
    }

    fileprivate func setupNode() {
    }
}
