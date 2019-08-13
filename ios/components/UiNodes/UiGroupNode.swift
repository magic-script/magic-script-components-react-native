//
//  UiGroupNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc open class UiGroupNode: UiNode {

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }

    @objc override func _calculateSize() -> CGSize {
        return getBoundsCollection().size
    }

    @objc override func updateLayout() {
    }

    @objc override func updatePivot() {
        // Do not update pivot for group node.
        // Group node has it's own size/bounds (based on child nodes), but
        // since it's a nodes' container, setting alignment does not make sense.
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
