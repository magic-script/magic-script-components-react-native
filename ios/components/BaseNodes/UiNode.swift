//
//  UiNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

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

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let alignment = Convert.toAlignment(props["alignment"]) {
            self.alignment = alignment
        }
    }

    @objc override func getBounds() -> CGRect {
        let size = getSize()
        let offset = alignment.offset
        let origin: CGPoint = CGPoint(x: offset.x * size.width, y: offset.y * size.height)
        return CGRect(origin: origin, size: size).offsetBy(dx: CGFloat(localPosition.x), dy: CGFloat(localPosition.y))
    }

    @objc override func updateLayout() {
    }

    @objc override func updatePivot() {
        let size = getSize()
        let offset = alignment.offset
        contentNode.position = SCNVector3(offset.x * size.width, offset.y * size.height, 0)
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
