//
//  Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

import Foundation
import SceneKit

@objc open class Prism: BaseNode {
    @objc public var isPointed: Bool = false {
       didSet {
#if targetEnvironment(simulator)
        let activeColor = UIColor(red: 0.2, green: 1, blue: 0.2, alpha: 1)
        let inactiveColor = UIColor(red: 0, green: 0.5, blue: 0, alpha: 1)
        debugNode?.geometry?.firstMaterial?.diffuse.contents = isPointed ? activeColor : inactiveColor
#endif
       }
    }
    @objc public var size: SCNVector3 = SCNVector3.zero {
        didSet {
            updateSize()
            invalidateClippingPlanes()
        }
    }
    override open var position: SCNVector3 {
        didSet { invalidateClippingPlanes() }
    }
    override open var orientation: SCNQuaternion {
        didSet { invalidateClippingPlanes() }
    }
    override open var scale: SCNVector3 {
        didSet { invalidateClippingPlanes() }
    }
    override open var transform: SCNMatrix4 {
        didSet { invalidateClippingPlanes() }
    }
    @objc var debug: Bool = false {
        didSet { updateDebugMode() }
    }
    @objc public var editMode: Bool = false {
        didSet { updateEditMode() }
    }
    @objc var anchorUuid: String = "" {
        didSet { NodesManager.instance.updatePrismAnchorUuid(self, oldAnchorUuid: oldValue) }
    }

    @objc fileprivate(set) var rootNode: TransformNode = TransformNode()
#if targetEnvironment(simulator)
    @objc fileprivate(set) var debugNode: SCNNode?
    @objc fileprivate(set) var debugClippedRayNode: SCNNode?
#endif
    @objc fileprivate(set) var editNode: PrismOutlineNode?
    @objc fileprivate var clipNeeded: Bool = true
    @objc fileprivate var clippingPlanes: [Plane]?
    @objc fileprivate var clippingPlanesAsVector4: [SCNVector4]?

    @objc override init() {
        super.init()
        setupPrism()
    }

    @objc public required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    @objc public required init(props: [String: Any]) {
        super.init()
        setupPrism()
        update(props)
    }

    @objc func setupPrism() {
        addChildNode(rootNode)
    }

    @objc override func update(_ props: [String: Any]) {
        if let size = Convert.toVector3(props["size"]) {
            self.size = size
        }

        if let position = Convert.toVector3(props["position"]) {
            self.position = position
        }

        if let rotation = Convert.toQuaternion(props["rotation"]) {
            self.orientation = rotation
        }
        
        if let transform = Convert.toMatrix4(props["transform"]) {
            self.transform = transform
        }

        if let scale = Convert.toVector3(props["scale"]) {
            self.scale = scale
        }

        if let debug = Convert.toBool(props["debug"]) {
            self.debug = debug
        }

        if let anchorUuid = Convert.toString(props["anchorUuid"]) {
            self.anchorUuid = anchorUuid
        }
    }

    @objc override func addNode(_ node: SCNNode) -> Bool {
        if let transformNodeChild = node as? TransformNode {
            rootNode.addChildNode(transformNodeChild)
            return true
        }
        return false
    }

    @objc override func removeNode(_ node: SCNNode) {
        if let parent = node.parent, parent == rootNode {
            node.removeFromParentNode()
        }
    }

    override func hitTest(ray: Ray) -> HitTestResult? {
        var outRay: Ray?
        guard intersect(with: ray, clippedRay: &outRay) else { return nil }
        guard let clippedRay = outRay else { return nil }
        
#if targetEnvironment(simulator)
        if debug {
            debugClippedRayNode?.removeFromParentNode()
            let begin = convertPosition(clippedRay.begin, from: nil)
            let end = convertPosition(clippedRay.end, from: nil)
            debugClippedRayNode = NodesFactory.createSegmentNode(vertices: [begin, end])
            addChildNode(debugClippedRayNode!)
        }
#endif
        var hitResults: [HitTestResult] = []
        for child in rootNode.childNodes {
            if let transformNode = child as? TransformNode {
                if let hitResult = transformNode.hitTest(ray: clippedRay) {
                    hitResults.append(hitResult)
                }
            }
        }
        
        hitResults.sort { (hitResult1, hitResult2) -> Bool in
            let worldPosition1 = hitResult1.node.convertPosition(hitResult1.point, to: nil)
            let worldPosition2 = hitResult2.node.convertPosition(hitResult2.point, to: nil)
            let dist1 = (worldPosition1 - ray.begin).lengthSq()
            let dist2 = (worldPosition2 - ray.begin).lengthSq()
            return dist1 < dist2
        }
        
        return hitResults.first
    }

    fileprivate func updateSize() {
#if targetEnvironment(simulator)
        debugNode?.scale = size
#endif
        editNode?.size = size
    }
    
    @objc func updateDebugMode() {
#if targetEnvironment(simulator)
        if debug {
            if debugNode == nil {
                debugNode = NodesFactory.createWireBoxNode(width: 1.0, height: 1.0, depth: 1.0, color: UIColor.green)
                debugNode!.scale = size
                debugNode!.renderingOrder = 990
            }

            if debugNode!.parent == nil {
                addChildNode(debugNode!)
            }
        } else {
            debugNode?.removeFromParentNode()
        }
#endif
    }

    @objc func updateEditMode() {
        if editMode {
            if editNode == nil {
                editNode = PrismOutlineNode()
                editNode!.size = size
                editNode!.renderingOrder = 1000
            }

            if editNode!.parent == nil {
                addChildNode(editNode!)
            }
        } else {
            editNode?.removeFromParentNode()
        }
    }
}

// MARK: - BoundsClipping
extension Prism: BoundsClipping {
    fileprivate func invalidateClippingPlanes() {
        clippingPlanes = nil
        clippingPlanesAsVector4 = nil
        invalidateClipping()
    }
    
    @objc func getClippingPlanes() -> [Plane] {
        if clippingPlanes == nil {
            let min = -0.5 * size
            let max = 0.5 * size
            let planes: [SCNVector4] = [
                SCNVector4( 1, 0, 0,-min.x),
                SCNVector4(-1, 0, 0, max.x),
                SCNVector4(0, 1, 0,-min.y),
                SCNVector4(0,-1, 0, max.y),
                SCNVector4(0, 0, 1,-min.z),
                SCNVector4(0, 0,-1, max.z),
            ]
            
            clippingPlanes = planes.map { rootNode.convertPlane(Plane(vector: $0), to: nil) }
        }

        return clippingPlanes!
    }
    
    @objc func getClippingPlanesAsVector4() -> [SCNVector4] {
        if clippingPlanesAsVector4 == nil {
            clippingPlanesAsVector4 = getClippingPlanes().map { $0.toVector4() }
        }

        return clippingPlanesAsVector4!
    }
}

// MARK: - BoundsClippingManaging
extension Prism: BoundsClippingManaging {
    var isUpdateClippingNeeded: Bool { return clipNeeded }

    func invalidateClipping() {
        clipNeeded = true
    }

    func updateClipping(for node: SCNNode? = nil, recursive: Bool = true) {
        if let node = node {
            node.setClippingPlanes(getClippingPlanesAsVector4(), recursive: recursive)
        } else {
            guard clipNeeded else { return }
            rootNode.setClippingPlanes(getClippingPlanesAsVector4(), recursive: recursive)
            clipNeeded = false
        }
    }
}
