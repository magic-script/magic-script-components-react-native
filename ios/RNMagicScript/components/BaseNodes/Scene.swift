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

@objc open class Scene: BaseNode {
    @objc fileprivate(set) var contentNode: SCNNode!

    @objc var prisms: [Prism] = []

    @objc override init() {
        super.init()
        setupScene()
    }

    @objc public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    @objc public required init(props: [String: Any]) {
        super.init()
        setupScene()
        update(props)
    }

    @objc func setupScene() {
        contentNode = SCNNode()
        addChildNode(contentNode)
    }

    @objc override func update(_ props: [String: Any]) {
    }

    @objc override func addNode(_ node: SCNNode) -> Bool {
        if let prismChild = node as? Prism {
            contentNode.addChildNode(prismChild)
            prisms.append(prismChild)

        }
        return false
    }

    @objc override func removeNode(_ node: SCNNode) {
        if let parent = node.parent, parent == contentNode {
            node.removeFromParentNode()
            prisms.removeAll { $0 == node }
        }
    }

    @objc override func hitTest(ray: Ray) -> BaseNode? {
//        guard let _ = selfHitTest(ray: ray) else { return nil }

        for prism in prisms {
            if let hitNode = prism.hitTest(ray: ray) {
                return hitNode
            }
        }

        return self
    }

    override func getHitTestPoint(ray: Ray) -> SCNVector3? {
        let localRay = convertRayToLocal(ray: ray)
        let localPlane = getPlane()
        guard let point = localPlane.intersectRay(localRay) else { return nil }
        let bounds = CGRect(x: -500.0, y: -500.0, width: 1000.0, height: 1000.0)
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
