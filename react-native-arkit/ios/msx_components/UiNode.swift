//
//  UiNode.swift
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc class UiNode: SCNNode {

    //var alignment: Alignment // TODO: property to define
    //var activateResponse: FocusRequest // TODO: property to define
    //var renderingLayer: RenderingLayer // TODO: property to define
    var enabled: Bool = true
    var eventPassThrough: Bool = true
    var eventPassThroughChildren: Bool = true
    var gravityWellEnabled: Bool = true
    //var eventSoundId: ClassProperty // TODO: property to define
    //var gravityWellProperties: GravityWellProperties // TODO: property to define

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
