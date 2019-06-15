//
//  SCNNode+Debug.swift
//  SceneKitComponents
//
//  Created by Pawel Leszkiewicz on 24/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc extension SCNNode {

    fileprivate static let debugNodeName: String = "debug_bbox_node"

    @objc var isBBoxVisible: Bool {
        return getDebugBBoxNode() != nil
    }

    @objc func setBBox(visible: Bool, forceUpdate: Bool = false) {
        if forceUpdate {
            getDebugBBoxNode()?.removeFromParentNode()
        }

        let bboxNode: SCNNode? = getDebugBBoxNode()
        let bboxVisible: Bool = bboxNode != nil

        guard visible != bboxVisible else { return }

        if visible {
            let node = SCNBBoxNode(self.boundingBox)
            node.name = SCNNode.debugNodeName
            addChildNode(node)
        } else {
            bboxNode?.removeFromParentNode()
        }
    }

    fileprivate func getDebugBBoxNode() -> SCNNode? {
        return childNode(withName: SCNNode.debugNodeName, recursively: false)
    }

    @objc func printBBoxData() {
        guard let bboxNode = getDebugBBoxNode() else { return }
        let bbox = bboxNode.boundingBox
        print("bbox: [\(bbox.min), \(bbox.max)]")
    }
}
