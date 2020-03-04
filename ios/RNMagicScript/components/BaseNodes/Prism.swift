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

    @objc var debug: Bool = false {
        didSet { setDebugMode(debug) }
    }
    @objc fileprivate(set) var debugNode: SCNNode!

    @objc public var size: SCNVector3 = SCNVector3(0.0, 0.0, 0.0) {
        didSet {
            debugNode.scale = size
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
        addChildNode(rootNode)

        let box = SCNBox(width: 1.0, height: 1.0, length: 1.0, chamferRadius: 0)
        box.firstMaterial?.diffuse.contents = UIColor.green.withAlphaComponent(0.25)
        box.firstMaterial?.isDoubleSided = true
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

    // TODO: check if Ray intersect Prism box
    @objc override func hitTest(ray: Ray) -> BaseNode? {
        for child in rootNode.childNodes {
            if let transformNode = child as? TransformNode {
                if let hitNode = transformNode.hitTest(ray: ray) {
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
