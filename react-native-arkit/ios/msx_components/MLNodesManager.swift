//
//  MLNodesManager.swift
//  RCTARKit
//
//  Created by Pawel Leszkiewicz on 06/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import Foundation
import SceneKit

@objc class MLNodesManager: NSObject {
    @objc static let instance = MLNodesManager()
    @objc let rootNode: SCNNode = SCNNode()
    @objc let componentNodeBitMask: Int = 8
    fileprivate var nodesById: [String: SCNNode] = [:]

    private override init() {
        super.init()
    }

    @objc func registerScene(_ scene: SCNScene) {
        print("[MLNodesManager] registerScene")
        scene.rootNode.addChildNode(rootNode)
    }

    @objc func hitTest(from: SCNVector3, to: SCNVector3) -> SCNNode? {
        let options: [String: Any] = [
            SCNHitTestOption.boundingBoxOnly.rawValue: true,
            SCNHitTestOption.ignoreHiddenNodes.rawValue: true,
            SCNHitTestOption.rootNode.rawValue: rootNode
        ]
        let results = rootNode.hitTestWithSegment(from: from, to: to, options: options)
        return results.first?.node
    }

    @objc func handleNodeTap(_ node: SCNNode?) {
        var componentNode: SCNNode? = node
        while componentNode != nil {
            if componentNode?.categoryBitMask == componentNodeBitMask {
                break
            }
            componentNode = componentNode?.parent
        }
        guard let button = componentNode as? MLButtonNode else { return }
        print("button tap: \(button)")
        button.simulateTap()
    }

    @objc func findNodeWithId(_ nodeId: String) -> SCNNode? {
        return nodesById[nodeId]
    }

    @objc func registerNode(_ node: SCNNode, nodeId: String) {
        node.name = nodeId
        if node is MLButtonNode {
            node.categoryBitMask = componentNodeBitMask
            node.setBBox(visible: true)
        }
        nodesById[nodeId] = node
        print("register node: \(node)")
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
            print("[MLNodesManager] Nodes tree hierarchy and nodes list are empty.")
            return true
        }

        if nodesById.isEmpty != rootNode.childNodes.isEmpty {
            print("[MLNodesManager] One nodes container (either nodes tree hierarchy (\(rootNode.childNodes.count)) or nodes list (\(nodesById.count)) is empty!")
            return true
        }

        let looseNodes = rootNode.childNodes(passingTest: { (node, stop) -> Bool in
            guard let _ = node.parent else { return false }
            guard let nodeId = node.name, let _ = nodesById[nodeId] else { return false }
            return true
        })

        if (!looseNodes.isEmpty) {
            print("[MLNodesManager] Found \(looseNodes.count) loose nodes.")
            return false
        }

        return true
    }
}
