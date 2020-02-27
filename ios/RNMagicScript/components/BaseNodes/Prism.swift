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

@objc open class Prism: BaseNode {
    @objc fileprivate(set) var contentNode: SCNNode!
    @objc fileprivate(set) var debugNode: SCNNode!

    @objc public var size: SCNVector3 = SCNVector3(0.0, 0.0, 0.0) {
        didSet {
            if let debugNodeGeometry = debugNode.geometry as? SCNBox {
                debugNodeGeometry.width = CGFloat(size.x)
                debugNodeGeometry.height = CGFloat(size.y)
                debugNodeGeometry.length = CGFloat(size.z)
            }
        }
    }

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
        contentNode = SCNNode()
        addChildNode(contentNode)

        //
        let box = SCNBox(width: CGFloat(size.x), height: CGFloat(size.y), length: CGFloat(size.z), chamferRadius: 0)
        box.firstMaterial?.diffuse.contents = UIColor.green.withAlphaComponent(0.25)
        box.firstMaterial?.isDoubleSided = true

        debugNode = SCNNode(geometry: box)
        contentNode.addChildNode(debugNode)
    }

    @objc override func update(_ props: [String: Any]) {

        if let size = Convert.toVector3(props["size"]) {
            self.size = size
        }

        if let position = Convert.toVector3(props["position"]) {
            self.contentNode.position = position
        }
    }

    @objc override func addNode(_ node: SCNNode) -> Bool {
        if let transformNodeChild = node as? TransformNode {
            contentNode.addChildNode(transformNodeChild)
        }
        return false
    }

    @objc override func removeNode(_ node: SCNNode) {
        if let parent = node.parent, parent == contentNode {
            node.removeFromParentNode()
        }
    }

    @objc override func hitTest(ray: Ray) -> BaseNode? {
//        guard let _ = selfHitTest(ray: ray) else { return nil }
        if contentNode.childNodes.count == 2, let prismRoot = contentNode.childNodes[1] as? TransformNode {
            return prismRoot.hitTest(ray: ray)
        }
        return self
    }

    override func getHitTestPoint(ray: Ray) -> SCNVector3? {
        let localRay = convertRayToLocal(ray: ray)
        let localPlane = getPlane()
        guard let point = localPlane.intersectRay(localRay) else { return nil }
        let bounds = CGRect(x: CGFloat(position.x), y: CGFloat(position.y), width: CGFloat(size.x), height: CGFloat(size.z))
        if bounds.contains(CGPoint(x: CGFloat(point.x), y: CGFloat(point.y))) {
            return point
        }

        return nil
    }

    @objc override func selfHitTest(ray: Ray) -> BaseNode? {
        //        guard !skipRaycast && visible else { return nil }
        guard let _ = getHitTestPoint(ray: ray) else { return nil }
        return self
    }
}
