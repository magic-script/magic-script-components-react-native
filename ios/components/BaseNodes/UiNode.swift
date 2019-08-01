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

    @objc var alignment: Alignment = Alignment.centerCenter {
        didSet { setNeedsLayout() }
    }
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

    @objc override func getBounds() -> CGRect {
        let size = getSize()
        let origin: CGPoint = getOriginForSize(size)
        return CGRect(origin: origin, size: size).offsetBy(dx: CGFloat(localPosition.x), dy: CGFloat(localPosition.y))
    }

    @objc override func updateLayout() {
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

// MARK: - Helpers
extension UiNode {
    @objc fileprivate func getOriginForSize(_ size: CGSize) -> CGPoint {
        switch (self.alignment) {
        case .topLeft:
            return CGPoint(x: -0.5 * size.width, y: 0.5 * size.height)
        case .topCenter:
            return CGPoint(x: 0, y: 0.5 * size.height)
        case .topRight:
            return CGPoint(x: 0.5 * size.width, y: 0.5 * size.height)
        case .centerLeft:
            return CGPoint(x: -0.5 * size.width, y: 0)
        case .centerCenter:
            return CGPoint(x: 0, y: 0)
        case .centerRight:
            return CGPoint(x: 0.5 * size.width, y: 0)
        case .bottomLeft:
            return CGPoint(x: -0.5 * size.width, y: -0.5 * size.height)
        case .bottomCenter:
            return CGPoint(x: 0, y: -0.5 * size.height)
        case .bottomRight:
            return CGPoint(x: 0.5 * size.width, y: -0.5 * size.height)
        }
    }
}
