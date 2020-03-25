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

import SceneKit

@objc open class Prism: BaseNode {
    @objc fileprivate(set) var rootNode: TransformNode = TransformNode()

    @objc var anchorUuid: String = "" {
        didSet { NodesManager.instance.updatePrismAnchorUuid(self, oldAnchorUuid: oldValue) }
    }

    @objc var debug: Bool = false {
        didSet { setDebugMode(debug) }
    }

    @objc public var size: SCNVector3 = SCNVector3.zero {
        didSet { invalidateClippingPlanes(); debugNode.scale = size }
    }
    
    @objc fileprivate(set) var debugNode: SCNNode!
    @objc fileprivate var clippingPlanes: [Plane]?

    @objc override init() {
        super.init()
        setupPrism()
    }

    @objc public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    @objc public required init(props: [String: Any]) {
        super.init()
        setupPrism()
        update(props)
    }

    @objc func setupPrism() {
        addChildNode(rootNode)

        let box = SCNBox(width: 1.0, height: 1.0, length: 1.0, chamferRadius: 0)
        box.firstMaterial?.diffuse.contents = UIColor.green.withAlphaComponent(0.25)
        debugNode = SCNNode(geometry: box)
        debugNode.scale = SCNVector3.zero
        debugNode.renderingOrder = 1000
    }

    @objc override func update(_ props: [String: Any]) {
        if let size = Convert.toVector3(props["size"]) {
            self.size = size
        }

        if let position = Convert.toVector3(props["position"]) {
            self.position = position
        }

        if let rotation = Convert.toQuaternion(props["rotation"]) {
            self.rotation = rotation
        }

        if let scale = Convert.toVector3(props["scale"]) {
            self.scale = scale
        }

        if let debug = Convert.toBool(props["debug"]) {
            self.debug = debug
        }

        if let anchorUuid = Convert.toString(props["anchorUuid"]) {
            self.anchorUuid = anchorUuid;
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

    @objc override func hitTest(ray: Ray) -> BaseNode? {
        guard intersect(with: ray) else { return nil }
        let clippedRay = clipRay(ray)
        
        for child in rootNode.childNodes {
            if let transformNode = child as? TransformNode {
                if let hitNode = transformNode.hitTest(ray: clippedRay) {
                    return hitNode
                }
            }
        }

        return self
    }

    @objc func setDebugMode(_ debug: Bool) {
        if debug {
            if !rootNode.childNodes.contains(debugNode) {
                rootNode.addChildNode(debugNode)
            }
        } else {
            if debugNode.parent == rootNode {
                debugNode.removeFromParentNode()
            }
        }
    }
}

// MARK: - Clipping
extension Prism {
    fileprivate func invalidateClippingPlanes() {
        clippingPlanes = nil
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
}
