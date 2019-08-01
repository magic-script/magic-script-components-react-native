//
//  UiNodesManager.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 06/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import Foundation
import SceneKit

@objc class UiNodesManager: NSObject {
    @objc static let instance = UiNodesManager()
    @objc let rootNode: TransformNode = TransformNode()
    @objc let focusableNodeBitMask: Int = 8

    @objc var onInputFocused: ((_ textEdit: UiTextEditNode) -> (Void))?
    @objc var onInputUnfocused: (() -> (Void))?

    fileprivate var nodesById: [String: TransformNode] = [:]
    fileprivate var nodeByAnchorUuid: [String: TransformNode] = [:]
    fileprivate var focusedNode: UiNode?
    fileprivate weak var view: SCNView?

    private override init() {
        super.init()
    }

    @objc func registerScene(_ view: SCNView) {
        self.view = view
        view.scene?.rootNode.addChildNode(rootNode)
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
        if focusedNode != nil {
            onInputUnfocused?()
        }

        focusedNode = componentNode as? UiNode
        focusedNode?.enterFocus()
        if let textEdit = focusedNode as? UiTextEditNode {
            onInputFocused?(textEdit)
        }
    }
    
    @objc func findNodeWithId(_ nodeId: String) -> TransformNode? {
        return nodesById[nodeId]
    }
    
    @objc func findNodeWithAnchorUuid(_ nodeId: String) -> TransformNode? {
        return nodeByAnchorUuid[nodeId]
    }

    @objc func registerNode(_ node: TransformNode, nodeId: String) {
        node.name = nodeId
        nodesById[nodeId] = node
        if (node.anchorUuid != "rootUuid") {
            nodeByAnchorUuid[node.anchorUuid] = node;
        }

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
            parentNode.addChild(node)
        }
    }

    @objc func addNodeToRoot(_ nodeId: String) {
        if let node = nodesById[nodeId] {
            rootNode.addChildNode(node)
        }
    }

    @objc func removeNode(_ nodeId: String, fromParent parentId: String) {
        if let node = nodesById[nodeId],
            let parentNode = nodesById[parentId] {
            parentNode.removeChild(node)
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

    @objc func updateLayout() {
        updateLayoutFor(node: rootNode)
        DispatchQueue.main.async { [weak self] in
            self?.view?.setNeedsDisplay()
        }
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
