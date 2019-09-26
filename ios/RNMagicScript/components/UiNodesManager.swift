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

import Foundation
import SceneKit

@objc open class UiNodesManager: NSObject {
    @objc public static let instance = UiNodesManager(rootNode: TransformNode(), nodesById: [:], nodeByAnchorUuid: [:], focusedNode: nil)
    @objc public private (set) var rootNode: TransformNode
    @objc let focusableNodeBitMask: Int = 8

    var onInputFocused: ((_ input: InputDataProviding) -> (Void))?
    var onInputUnfocused: (() -> (Void))?

    fileprivate var nodesById: [String: TransformNode]
    fileprivate var nodeByAnchorUuid: [String: TransformNode]
    fileprivate var focusedNode: UiNode?

    init(rootNode: TransformNode, nodesById: [String: TransformNode], nodeByAnchorUuid: [String: TransformNode], focusedNode: UiNode?) {
        self.rootNode = rootNode
        self.nodesById = nodesById
        self.nodeByAnchorUuid = nodeByAnchorUuid
        self.focusedNode = focusedNode
    }

    @objc public func registerScene(_ scene: SCNScene) {
        scene.rootNode.addChildNode(rootNode)
    }

    @objc public func handleNodeTap(_ node: SCNNode?) {
        var componentNode: SCNNode? = node
        while componentNode != nil {
            if componentNode?.categoryBitMask == focusableNodeBitMask {
                break
            }
            componentNode = componentNode?.parent
        }
        
        focusedNode?.leaveFocus()
        if focusedNode != nil {
            onInputUnfocused?()
        }

        focusedNode = componentNode as? UiNode
        focusedNode?.enterFocus()
        if let input = focusedNode as? InputDataProviding {
            onInputFocused?(input)
        }
    }
    
    @objc public func findNodeWithId(_ nodeId: String) -> TransformNode? {
        return nodesById[nodeId]
    }
    
    @objc public func findNodeWithAnchorUuid(_ nodeId: String) -> TransformNode? {
        return nodeByAnchorUuid[nodeId]
    }

    @objc public func registerNode(_ node: TransformNode, nodeId: String) {
        node.name = nodeId
        nodesById[nodeId] = node
        if (node.anchorUuid != "rootUuid") {
            nodeByAnchorUuid[node.anchorUuid] = node;
        }

        if let node = node as? UiNode, node.canHaveFocus {
            node.categoryBitMask = focusableNodeBitMask
        }
    }

    @objc public func unregisterNode(_ nodeId: String) {
        if let node = nodesById[nodeId] {
            node.removeFromParentNode()
            nodesById.removeValue(forKey: nodeId)
        }
    }

    @objc public func addNode(_ nodeId: String, toParent parentId: String) {
        if let node = nodesById[nodeId],
           let parentNode = nodesById[parentId] {
            parentNode.addChild(node)
        }
    }

    @objc public func addNodeToRoot(_ nodeId: String) {
        if let node = nodesById[nodeId] {
            rootNode.addChildNode(node)
        }
    }

    @objc public func removeNode(_ nodeId: String, fromParent parentId: String) {
        if let node = nodesById[nodeId],
            let parentNode = nodesById[parentId] {
            parentNode.removeChild(node)
            removeNodeWithDescendants(node)
        }
    }

    @objc public func removeNodeFromRoot(_ nodeId: String) {
        if let node = nodesById[nodeId],
            rootNode == node.parent {
            node.removeFromParentNode()
            removeNodeWithDescendants(node)
        }
    }

    @objc fileprivate func removeNodeWithDescendants(_ node: TransformNode) {
        node.enumerateHierarchy { (item, stop) in
            if let node = item as? TransformNode,
                let id = node.name {
                unregisterNode(id)
            }
        }
    }

    @objc public func clear() {
        nodesById.forEach { (key: String, value: SCNNode) in
            value.removeFromParentNode()
        }
        nodesById.removeAll()
    }

    @objc public func updateNode(_ nodeId: String, properties: [String: Any]) -> Bool {
        guard let node = nodesById[nodeId] else { return false }
        node.update(properties)
        return true
    }

    @objc public func updateLayout() {
        assert(Thread.isMainThread, "updateLayout must be called in main thread!")
        updateLayoutFor(node: rootNode)
    }

    @objc fileprivate func updateLayoutFor(node: SCNNode) {
        node.childNodes.forEach { (child) in
            updateLayoutFor(node: child)
        }

        if let transformNode = node as? TransformNode {
            transformNode.layoutIfNeeded()
        }
    }

    @objc func textFieldShouldReturn() {
        focusedNode?.leaveFocus()
        onInputUnfocused?()
    }

    @objc func textFieldDidChange(text: String?) {
        if let textEdit = focusedNode as? UiTextEditNode {
            textEdit.text = text
        }
    }
}
