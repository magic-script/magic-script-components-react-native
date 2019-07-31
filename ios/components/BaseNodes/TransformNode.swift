//
//  TransformNode.swift
//  RNMagicScript
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
        set { self.pivot.position = newValue; setNeedsLayout() }
    }
    @objc var localPosition: SCNVector3 {
        get { return self.position }
        set { self.position = newValue; setNeedsLayout() }
    }
    @objc var localRotation: SCNQuaternion {
        get { return self.orientation }
        set { self.orientation = newValue; setNeedsLayout() }
    }
    @objc var localScale: SCNVector3 {
        get { return self.scale }
        set { self.scale = newValue; setNeedsLayout() }
    }
    @objc var localTransform: SCNMatrix4 {
        get { return self.transform }
        set { self.transform = newValue; setNeedsLayout() }
    }
    @objc var anchorUuid: String = "rootUuid";
    // var cursorHoverState: CursorHoverState // ignore in mobile
    // var offset: SCNVector3 // ???

    fileprivate var originNode: SCNNode?
    fileprivate var layoutNeeded: Bool = false
    @objc func setNeedsLayout() { layoutNeeded = true }
    @objc var isLayoutNeeded: Bool { return layoutNeeded }

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
        update(props)
    }

    @objc func setupNode() {
    }

    @objc func addChild(_ child: TransformNode) {
        addChildNode(child)
    }

    @objc func removeChild(_ child: TransformNode) {
        if let parent = child.parent, parent == self {
            child.removeFromParentNode()
        }
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
        if let uuid = Convert.toString(props["anchorUuid"]) {
            self.anchorUuid = uuid;
        }
    }

    @objc func getSize() -> CGSize {
        return CGSize.zero
    }

    @objc func getBounds() -> CGRect {
        let size = getSize()
        let origin: CGPoint = CGPoint(x: CGFloat(localPosition.x), y: CGFloat(localPosition.y))
        return CGRect(origin: origin, size: size)
    }

    @objc func getEdgeInsets() -> UIEdgeInsets {
        let bounds: CGRect = getBounds()
        return UIEdgeInsets(top: bounds.minY, left: bounds.minX, bottom: bounds.maxY, right: bounds.maxX)
    }

    @objc func layoutIfNeeded() {
        if layoutNeeded {
            layoutNeeded = false
            updateLayout()
        }
    }

    @objc func updateLayout() {
    }

    @objc func setOriginVisible(_ visible: Bool) {
        guard visible else {
            originNode?.removeFromParentNode()
            return
        }

        if originNode == nil {
            let sphere = SCNSphere(radius: 0.01)
            sphere.segmentCount = 4
            sphere.firstMaterial?.lightingModel = .constant
            sphere.firstMaterial?.diffuse.contents = UIColor.yellow
            originNode = SCNNode(geometry: sphere)
        }

        guard let originNode = originNode else { return }
        addChildNode(originNode)
    }
}
