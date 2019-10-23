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

@objc open class TransformNode: SCNNode {

    // var name: String // native property
    // var parentedBoneName: String
     var skipRaycast: Bool = false
    // var triggerable: Bool = true // not related to iOS
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

#if targetEnvironment(simulator)
    fileprivate var originNode: SCNNode?
    fileprivate var borderNode: SCNNode?
#endif
    @objc fileprivate(set) var contentNode: SCNNode!

    fileprivate var currentSize: CGSize?
    fileprivate var layoutNeeded: Bool = false
    @objc func setNeedsLayout() { layoutNeeded = true; currentSize = nil }
    @objc var isLayoutNeeded: Bool { return layoutNeeded }

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    @objc public required init(props: [String: Any]) {
        super.init()
        setupNode()
        update(props)
    }

    @objc func setupNode() {
        contentNode = SCNNode()
        addChildNode(contentNode)
    }

    @objc func addChild(_ child: TransformNode) {
        contentNode.addChildNode(child)
        setNeedsLayout()
        setNeedsLayoutForAllParents()
    }

    @objc func removeChild(_ child: TransformNode) {
        if let parent = child.parent, parent == contentNode {
            child.removeFromParentNode()
            setNeedsLayout()
            setNeedsLayoutForAllParents()
        }
    }

    @objc func hitTest(ray: Ray) -> TransformNode? {
        return selfHitTest(ray: ray)
    }

    fileprivate func setNeedsLayoutForAllParents() {
        var node: SCNNode? = parent
        while node != nil {
            if let transformNode = node as? TransformNode {
                transformNode.setNeedsLayout()
            }
            node = node?.parent
        }
    }

    @objc func update(_ props: [String: Any]) {
        assert(childNodes[0] == contentNode, "contentNode does not exist!")

        if let name = Convert.toString(props["id"]) {
            self.name = name
        }
        if let skipRaycast = Convert.toBool(props["skipRaycast"]) {
            self.skipRaycast = skipRaycast
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
        if let debug = Convert.toBool(props["debug"]) {
            self.setDebugMode(debug)
        }
    }

    @objc func getSize() -> CGSize {
        if currentSize == nil {
            currentSize = _calculateSize()
        }
        return currentSize!
    }

    func _calculateSize() -> CGSize {
        return CGSize.zero
    }

    @objc func getBounds(parentSpace: Bool = false) -> CGRect {
        let size = getSize()
        let origin: CGPoint = parentSpace ? CGPoint(x: CGFloat(contentNode.position.x), y: CGFloat(contentNode.position.y)) : CGPoint.zero
        return CGRect(origin: origin, size: size).offsetBy(dx: -0.5 * size.width, dy: -0.5 * size.height)
    }

    @objc func getEdgeInsets() -> UIEdgeInsets {
        let bounds: CGRect = getBounds()
        return UIEdgeInsets(top: bounds.minY, left: bounds.minX, bottom: bounds.maxY, right: bounds.maxX)
    }

    @objc func layoutIfNeeded() {
        if layoutNeeded {
            layoutNeeded = false
            updateLayout()
            updatePivot()
#if targetEnvironment(simulator)
            updateDebugLayout()
#endif
        }
    }

    @objc func updateLayout() {
    }

    @objc func updatePivot() {
    }
}

// MARK: - HitTest
extension TransformNode {
    func getHitTestPoint(ray: Ray) -> SCNVector3? {
        let localRay = convertRayToLocal(ray: ray)
        let localPlane = getPlane()
        guard let point = localPlane.intersectRay(localRay) else { return nil }
        let bounds = getBounds()
        if bounds.contains(CGPoint(x: CGFloat(point.x), y: CGFloat(point.y))) {
            return point
        }

        return nil
    }

    @objc func selfHitTest(ray: Ray) -> TransformNode? {
        guard !skipRaycast && visible else { return nil }
        guard let _ = getHitTestPoint(ray: ray) else { return nil }
        return self
    }

    @objc func convertRayToLocal(ray: Ray) -> Ray {
       let localRayBegin = convertPosition(ray.begin, from: nil)
       let localRayDirection = convertVector(ray.direction, from: nil)
       return Ray(begin: localRayBegin, direction: localRayDirection, length: ray.length)
    }

    @objc func getPlane() -> Plane {
        return Plane(center: position, normal: transform.forward)
    }
}

// MARK: - Debug mode
extension TransformNode {
    @objc func setDebugMode(_ debug: Bool) {
#if targetEnvironment(simulator)
        guard debug else {
            originNode?.removeFromParentNode()
            borderNode?.removeFromParentNode()
            return
        }

        // origin
        if originNode == nil {
            let sphere = SCNSphere(radius: 0.01)
            sphere.segmentCount = 4
            sphere.firstMaterial?.lightingModel = .constant
            sphere.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
            sphere.firstMaterial?.diffuse.contents = UIColor.yellow
            originNode = SCNNode(geometry: sphere)
        }
        addChildNode(originNode!)

        updateDebugLayout()
#endif
    }

#if targetEnvironment(simulator)
    @objc fileprivate var isDebugMode: Bool { return originNode?.parent != nil }
    @objc fileprivate func updateDebugLayout() {
        guard isDebugMode else { return }

        // border
        borderNode?.removeFromParentNode()
        let bounds = getBounds()
        borderNode = NodesFactory.createOutlineNode(size: bounds.size, cornerRadius: 0, thickness: 0, color: UIColor.yellow)
        borderNode?.position = SCNVector3(bounds.midX, bounds.midY, 0.0)
        addChildNode(borderNode!)
    }
#endif
}
