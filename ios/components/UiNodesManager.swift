//
//  UiNodesManager.swift
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 06/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import Foundation
import SceneKit

@objc class UiNodesManager: NSObject {
    @objc static let instance = UiNodesManager()
    @objc let rootNode: SCNNode = SCNNode()
    @objc let focusableNodeBitMask: Int = 8
    fileprivate var nodesById: [String: TransformNode] = [:]
    fileprivate var focusedNode: UiNode?

    private override init() {
        super.init()
    }

    @objc func registerScene(_ scene: SCNScene) {
        scene.rootNode.addChildNode(rootNode)
    }

    @objc func handleNodeTap(_ node: SCNNode?) {
        var componentNode: SCNNode? = node
        while componentNode != nil {
            if componentNode?.categoryBitMask == focusableNodeBitMask {
                break
            }
            componentNode = componentNode?.parent
        }
        
        focusedNode?.leaveFocus()
        focusedNode = componentNode as? UiNode
        focusedNode?.enterFocus()
    }

    @objc func findNodeWithId(_ nodeId: String) -> TransformNode? {
        return nodesById[nodeId]
    }

    @objc func registerNode(_ node: TransformNode, nodeId: String) {
        node.name = nodeId
        nodesById[nodeId] = node
        if let node = node as? UiNode, node.canHaveFocus {
            node.categoryBitMask = focusableNodeBitMask
        }
    }

    @objc func unregisterNode(_ nodeId: String) {
        if let node = nodesById[nodeId] {
            node.removeFromParentNode()
            nodesById.removeValue(forKey: nodeId)
        }
    }

    @objc func addNode(_ nodeId: String, toParent parentId: String) {
        if let node = nodesById[nodeId],
           let parentNode = nodesById[parentId] {
            parentNode.addChildNode(node)
        }
    }

    @objc func addNodeToRoot(_ nodeId: String) {
        if let node = nodesById[nodeId] {
            rootNode.addChildNode(node)
        }
    }

    @objc func removeNode(_ nodeId: String, fromParent parentId: String) {
        if let node = nodesById[nodeId],
            let parentNode = nodesById[parentId],
            parentNode == node.parent {
            node.removeFromParentNode()
        }
    }

    @objc func removeNodeFromRoot(_ nodeId: String) {
        if let node = nodesById[nodeId],
            rootNode == node.parent {
            node.removeFromParentNode()
        }
    }

    @objc func clear() {
        nodesById.forEach { (key: String, value: SCNNode) in
            value.removeFromParentNode()
        }
        nodesById.removeAll()
        assert(validateScene() == true, "Nodes containers must be empty!")
    }

    @discardableResult
    @objc func validateScene() -> Bool {
        if nodesById.isEmpty && rootNode.childNodes.isEmpty {
            print("[UiNodesManager] Nodes tree hierarchy and nodes list are empty.")
            return true
        }

        if nodesById.isEmpty != rootNode.childNodes.isEmpty {
            print("[UiNodesManager] One nodes container (either nodes tree hierarchy (\(rootNode.childNodes.count)) or nodes list (\(nodesById.count)) is empty!")
            return true
        }

        let looseNodes = rootNode.childNodes(passingTest: { (node, stop) -> Bool in
            guard let _ = node.parent else { return false }
            guard let nodeId = node.name, let _ = nodesById[nodeId] else { return false }
            return true
        })

        if (!looseNodes.isEmpty) {
            print("[UiNodesManager] Found \(looseNodes.count) loose nodes.")
            return false
        }

        return true
    }

    @objc func updateNode(_ nodeId: String, properties: [String: Any]) -> Bool {
        guard let node = nodesById[nodeId] else { return false }
        node.update(properties)
        return true
    }
}
