//
//  RenderNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 03/08/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import SceneKit

@objc class RenderNode: TransformNode {

    @objc var color: UIColor = UIColor.white {
        didSet { setNeedsLayout() }
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let color = Convert.toColor(props["color"]) {
            self.color = color
        }
    }

//    @objc override func getBounds() -> CGRect {
//        let size = getSize()
//        let offset = alignment.offset
//        let origin: CGPoint = CGPoint(x: offset.x * size.width, y: offset.y * size.height)
//        return CGRect(origin: origin, size: size).offsetBy(dx: CGFloat(localPosition.x), dy: CGFloat(localPosition.y))
//    }

    @objc override func updateLayout() {
    }
}
