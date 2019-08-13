//
//  UiNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc open class UiNode: TransformNode {

    @objc var alignment: Alignment = Alignment.topLeft {
        didSet { setNeedsLayout() }
    }
    //var activateResponse: FocusRequest
    //var renderingLayer: RenderingLayer
    var enabled: Bool = true   // (check SCNNodeFocusBehavior)
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

        if let enabled = Convert.toBool(props["enabled"]) {
            self.enabled = enabled
        }
    }

    @objc override func getBounds(parentSpace: Bool = false) -> CGRect {
        let size = getSize()
        let origin: CGPoint = parentSpace ? CGPoint(x: CGFloat(localPosition.x), y: CGFloat(localPosition.y)) : CGPoint.zero
        let offset = alignment.boundsOffset
        let offsetOrigin = CGPoint(x: offset.x * size.width, y: offset.y * size.height)
        return CGRect(origin: origin, size: size).offsetBy(dx: offsetOrigin.x, dy: offsetOrigin.y)
    }

    @objc override func updateLayout() {
    }

    @objc override func updatePivot() {
        let size = getSize()
        let shift = alignment.shiftDirection
        contentNode.position = SCNVector3(shift.x * size.width, shift.y * size.height, 0)
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
