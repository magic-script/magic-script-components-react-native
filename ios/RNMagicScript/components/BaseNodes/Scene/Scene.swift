//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
    @objc fileprivate(set) var rootNode: SCNNode = SCNNode()
    @objc fileprivate(set) var prisms: [Prism] = []


    @objc var editingPrism: Prism? {
        return prisms.filter{ $0.editMode }.first
    }

    @objc var prismContextMenu: PrismContextMenu! {
        didSet {
            addChildNode(prismContextMenu)
        }
    }

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
        addChildNode(rootNode)
    }

    @objc override func update(_ props: [String: Any]) { }

    @objc override func addNode(_ node: SCNNode) -> Bool {
        if let prismChild = node as? Prism {
            rootNode.addChildNode(prismChild)
            prisms.append(prismChild)
            return true
        }
        return false
    }

    @objc override func removeNode(_ node: SCNNode) {
        if let parent = node.parent, parent == rootNode {
            node.removeFromParentNode()
            prisms.removeAll { $0 == node }
        }
    }

    override func hitTest(ray: Ray) -> HitTestResult? {
        if editingPrism != nil { return prismContextMenu?.hitTest(ray: ray) }
        if let menuHit = prismContextMenu?.hitTest(ray: ray) { return menuHit }
        for prism in prisms {
            if let hitResult = prism.hitTest(ray: ray) {
                return hitResult
            }
        }

        return nil
    }
}
