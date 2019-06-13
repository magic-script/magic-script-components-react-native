//
//  TransformNode.swift
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc class TransformNode: SCNNode {

    // var name: String // native property
    // var parentedBoneName: String
    // var skipRaycast: Bool = false
    // var triggerable: Bool = true
    var visible: Bool {
        get { return !self.isHidden }
        set { self.isHidden = !newValue }
    }
    // var visibilityInherited: Bool = true
    var anchorPosition: SCNVector3 {
        get { return self.pivot.position }
        set { self.pivot.position = newValue }
    }
    var localPosition: SCNVector3 {
        get { return self.position }
        set { self.position = newValue }
    }
    var localRotation: SCNQuaternion {
        get { return self.orientation }
        set { self.orientation = newValue }
    }
    var localScale: SCNVector3 {
        get { return self.scale }
        set { self.scale = newValue }
    }
    var localTransform: SCNMatrix4 {
        get { return self.transform }
        set { self.transform = newValue }
    }
    // var cursorHoverState: CursorHoverState // ignore in mobile
    // var offset: SCNVector3 // ???

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    @objc init?(props: [String: Any]) {
        super.init()
        update(props)
    }

    @objc func update(_ props: [String: Any]) {
        visible = (props["visible"] as? Bool) ?? !self.isHidden
//        anchorPosition = props["anchorPosition"]
    }

    fileprivate func setupNode() {
    }
}
