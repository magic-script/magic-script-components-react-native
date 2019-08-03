//
//  UiLineNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 03/08/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import SceneKit

@objc class UiLineNode: RenderNode {

    @objc var points: [SCNVector3] = [] {
        didSet { setNeedsLayout() }
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let array: Array<Any> = (props["points"] as? Array<Any>) {
            var newPoints = [SCNVector3]()
            for item in array {
                if let point = Convert.toVector3(item) {
                    newPoints.append(point)
                } else {
                    newPoints.removeAll()
                    break
                }
            }
            self.points = newPoints
        }
    }
    
    @objc override func _calculateSize() -> CGSize {
        guard !points.isEmpty else { return CGSize.zero }

        let firstPoint = points.first!
        var bbox: (min: CGPoint, max: CGPoint) = (min: CGPoint(x: CGFloat(firstPoint.x), y: CGFloat(firstPoint.y)),
                                                  max: CGPoint(x: CGFloat(firstPoint.x), y: CGFloat(firstPoint.y)))
        for i in 1..<points.count {
            let x: CGFloat = CGFloat(points[i].x)
            bbox.min.x = min(bbox.min.x, x)
            bbox.max.x = max(bbox.max.x, x)
            let y: CGFloat = CGFloat(points[i].y)
            bbox.min.y = min(bbox.min.y, y)
            bbox.max.y = max(bbox.max.y, y)
        }

        return CGSize(width: bbox.max.x - bbox.min.x, height: bbox.max.y - bbox.min.y)
    }

    @objc override func updateLayout() {
        
    }
}
