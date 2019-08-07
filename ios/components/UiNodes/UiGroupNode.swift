//
//  UiGroupNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc class UiGroupNode: UiNode {

    @objc override func setupNode() {
        super.setupNode()
        alignment = Alignment.centerCenter
    }

    @objc override func _calculateSize() -> CGSize {
        return getBoundsCollection().size
    }

    @objc override func updateLayout() {
    }
}

// MARK: - Helpers
extension UiGroupNode {
    @objc fileprivate func getBoundsCollection() -> CGRect {
        let nodes: [SCNNode] = contentNode.childNodes.filter { $0 is TransformNode }
        guard !nodes.isEmpty else { return CGRect.zero }
        var bounds: CGRect = (nodes[0] as! TransformNode).getBounds(parentSpace: true)
        for i in 1..<nodes.count {
            let b = (nodes[i] as! TransformNode).getBounds(parentSpace: true)
            bounds = bounds.union(b)
        }

        return bounds
    }
}
