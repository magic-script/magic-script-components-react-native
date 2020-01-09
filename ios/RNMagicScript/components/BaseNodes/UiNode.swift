//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import SceneKit

@objc open class UiNode: TransformNode {

    @objc var alignment: Alignment = Alignment.topLeft {
        didSet { setNeedsLayout() }
    }
    //var activateResponse: FocusRequest
    //var renderingLayer: RenderingLayer
    @objc var enabled: Bool = true {
        didSet { if oldValue != enabled { enabled ? onEnabled?(self) : onDisabled?(self) } }
    }
    //var eventPassThrough: Bool = true
    //var eventPassThroughChildren: Bool = true
    //var gravityWellEnabled: Bool = true
    //var eventSoundId: ClassProperty
    //var gravityWellProperties: GravityWellProperties

    @objc public var onActivate: ((_ sender: UiNode) -> Void)?
    //@objc public var onPress: ((_ sender: UiNode) -> Void)?
    //@objc public var onLongPress: ((_ sender: UiNode) -> Void)?
    //@objc public var onRelease: ((_ sender: UiNode) -> Void)?
    ///@objc public var onHoverEnter: ((_ sender: UiNode) -> Void)?
    ///@objc public var onHoverExit: ((_ sender: UiNode) -> Void)?
    ///@objc public var onHoverMove: ((_ sender: UiNode) -> Void)?
    @objc public var onEnabled: ((_ sender: UiNode) -> Void)?
    @objc public var onDisabled: ((_ sender: UiNode) -> Void)?
    @objc public var onFocusGained: ((_ sender: UiNode) -> Void)?
    @objc public var onFocusLost: ((_ sender: UiNode) -> Void)?
    ///@objc public var onFocusInput: ((_ sender: UiNode) -> Void)?
    @objc public var onUpdate: ((_ sender: UiNode) -> Void)?
    @objc public var onDelete: ((_ sender: UiNode) -> Void)?

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let alignment = Convert.toAlignment(props["alignment"]) {
            self.alignment = alignment
        }

        if let enabled = Convert.toBool(props["enabled"]) {
            self.enabled = enabled
        }
    }

    @objc override func getBounds(parentSpace: Bool = false, scaled: Bool = true) -> CGRect {
        let size = getSize(scaled: scaled)
        let origin: CGPoint = parentSpace ? CGPoint(x: CGFloat(localPosition.x), y: CGFloat(localPosition.y)) : CGPoint.zero
        let boundsOffset = alignment.boundsOffset
        let offset = CGPoint(x: boundsOffset.x * size.width, y: boundsOffset.y * size.height)
        return CGRect(origin: origin + offset, size: size)
    }

    @objc override func updateLayout() {
    }

    @objc override func updatePivot() {
        let size = getSize(scaled: false)
        let shift = alignment.shiftDirection
        contentNode.position = SCNVector3(shift.x * size.width, shift.y * size.height, 0)
    }

    // MARK: - Activate
    @objc func activate() {
        if let simulator = self as? TapSimulating {
            simulator.simulateTap()
        }

        onActivate?(self)
    }

    // MARK: - Focus
    @objc var canHaveFocus: Bool {
        return false
    }

    @objc private(set) var hasFocus: Bool = false {
        didSet { if oldValue != hasFocus { hasFocus ? onFocusGained?(self) : onFocusLost?(self) } }
    }

    @objc func enterFocus() {
        guard canHaveFocus else { return }
        hasFocus = true
    }

    @discardableResult
    @objc func leaveFocus(onBehalfOf node: UiNode? = nil) -> Bool {
        if node != self {
            hasFocus = false
            return true
        }
        
        return false
    }

    @objc var canBeLongPressed: Bool {
        return false
    }

    @objc private(set) var isLongPressed: Bool = false

    @objc func longPressStarted() {
        guard canBeLongPressed else { return }
        isLongPressed = true
    }

    @objc func longPressEnded() {
        isLongPressed = false
    }
}
