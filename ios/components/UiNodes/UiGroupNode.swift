//
//  UiGroupNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 10/06/2019.
//  Copyright © 2019 nomtek. All rights reserved.
//

import SceneKit

@objc class UiGroupNode: UiNode {
    @objc override func getBounds() -> UIEdgeInsets {
        guard !childNodes.isEmpty else { return UIEdgeInsets.zero }
        var bounds: UIEdgeInsets = (childNodes[0] as! TransformNode).getBounds()
        for i in 1..<childNodes.count {
            let b = (childNodes[i] as! TransformNode).getBounds()
            bounds.top = max(bounds.top, b.top)
            bounds.left = max(bounds.left, b.left)
            bounds.bottom = max(bounds.bottom, b.bottom)
            bounds.right = max(bounds.right, b.right)
        }

        return bounds
    }

    @objc override func updateLayout() {
        childNodes.forEach { (child) in
            (child as? TransformNode)?.updateLayout()
        }
    }
}
