//
//  UiGroupNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc class UiGroupNode: UiNode {
    @objc override func _calculateSize() -> CGSize {
        return getBoundsCollection().size
    }

    @objc override func updateLayout() {
    }
}

// MARK: - Helpers
extension UiGroupNode {
    @objc fileprivate func getBoundsCollection() -> CGRect {
        guard !childNodes.isEmpty else { return CGRect.zero }
        var bounds: CGRect = (childNodes[0] as! TransformNode).getBounds()
        for i in 1..<childNodes.count {
            let b = (childNodes[i] as! TransformNode).getBounds()
            bounds = bounds.union(b)
        }

        return bounds
    }
}
