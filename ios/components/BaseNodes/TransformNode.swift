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
    @objc var visible: Bool {
        get { return !self.isHidden }
        set { self.isHidden = !newValue }
    }
    // var visibilityInherited: Bool = true
    @objc var anchorPosition: SCNVector3 {
        get { return self.pivot.position }
        set { self.pivot.position = newValue }
    }
    @objc var localPosition: SCNVector3 {
        get { return self.position }
        set { self.position = newValue }
    }
    @objc var localRotation: SCNQuaternion {
        get { return self.orientation }
        set { self.orientation = newValue }
    }
    @objc var localScale: SCNVector3 {
        get { return self.scale }
        set { self.scale = newValue }
    }
    @objc var localTransform: SCNMatrix4 {
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

    @objc convenience init(props: [String: Any]) {
        self.init()
//        setupNode()
        update(props)
    }

    @objc func setupNode() {
    }

    @objc func update(_ props: [String: Any]) {
        if let name = Convert.toString(props["id"]) {
            self.name = name
        }
        if let visible = Convert.toBool(props["visible"]) {
            self.visible = visible
        }
        if let anchorPosition = Convert.toVector3(props["anchorPosition"]) {
            self.anchorPosition = anchorPosition
        }
        if let localPosition = Convert.toVector3(props["localPosition"]) {
            self.localPosition = localPosition
        }
        if let localRotation = Convert.toQuaternion(props["localRotation"]) {
            self.localRotation = localRotation
        }
        if let localScale = Convert.toVector3(props["localScale"]) {
            self.localScale = localScale
        }
        if let localTransform = Convert.toMatrix4(props["localTransform"]) {
            self.localTransform = localTransform
        }
    }
}
