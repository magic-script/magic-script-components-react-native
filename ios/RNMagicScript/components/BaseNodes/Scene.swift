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
    static let centerPointPosition = CGPoint(x: -500.0, y: -500.0)
    static let boxSize: CGFloat = 1000.0

    @objc fileprivate(set) var contentNode: SCNNode!
    @objc fileprivate(set) var prisms: [Prism] = []

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
            return true
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
        for prism in prisms {
            if let hitNode = prism.hitTest(ray: ray) {
                return hitNode
            }
        }

        return self
    }

    func getHitTestPoint(ray: Ray) -> SCNVector3? {
        let localRay = convertRayToLocal(ray: ray)
        let localPlane = getPlane()
        guard let point = localPlane.intersectRay(localRay) else { return nil }
        let bounds = CGRect(x: Scene.centerPointPosition.x, y: Scene.centerPointPosition.y, width: Scene.boxSize, height: Scene.boxSize)
        if bounds.contains(CGPoint(x: CGFloat(point.x), y: CGFloat(point.y))) {
            return point
        }

        return nil
    }

    override func selfHitTest(ray: Ray) -> BaseNode? {
        guard let _ = getHitTestPoint(ray: ray) else { return nil }
        return self
    }
}
