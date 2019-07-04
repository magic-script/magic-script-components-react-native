//
//  UiNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit
import SpriteKit

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

    fileprivate var outlineNode: SCNNode?

    @objc override func update(_ props: [String: Any]) {
        super.update(props)
    }

    // MARK: - Focus
    @objc var onFocusChanged: ((_ sender: UiNode) -> (Void))?

    @objc var canHaveFocus: Bool {
        return false
    }

    @objc private(set) var hasFocus: Bool = false {
        didSet { if oldValue != hasFocus { onFocusChanged?(self) } }
    }

    @objc func enterFocus() {
        guard canHaveFocus else { return }
        hasFocus = true
    }

    @objc func leaveFocus() {
        hasFocus = false
    }
}
