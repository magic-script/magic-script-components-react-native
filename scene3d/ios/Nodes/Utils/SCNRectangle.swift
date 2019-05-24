//
//  SCNRectangle.swift
//  SceneKitComponents
//
//  Created by Pawel Leszkiewicz on 24/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

class SCNRectangle : SCNGeometry {

    convenience init(rect: CGRect, thickness: CGFloat, radius: CGFloat = 0) {
        let geom = SCNRectangle.createGeometry(rect: rect, thickness: thickness, radius: radius)
        self.init(sources: [geom.source], elements: [geom.element])
        firstMaterial?.lightingModel = .constant
//        self._rect = rect
    }

//    fileprivate var _rect: CGRect = CGRect.zero
//    var rect: CGRect {
//        get { return _rect }
//        set { _rect = newValue; updateGeometry() }
//    }
//
//    fileprivate var _thickness: CGFloat = 0
//    var thickness: CGFloat {
//        get { return _thickness }
//        set { _thickness = newValue; updateGeometry() }
//    }
//
//    fileprivate var _radius: CGFloat = 0
//    var radius: CGFloat {
//        get { return _radius }
//        set { _radius = newValue; updateGeometry() }
//    }
//
//    fileprivate func updateGeometry() {
//        let geom = SCNRectangle.createGeometry(rect: rect, thickness: thickness, radius: radius)
//        sources = [geom.source]
//        elements = [geom.element]
//    }

    static fileprivate func createGeometry(rect: CGRect, thickness: CGFloat, radius: CGFloat) -> (source: SCNGeometrySource, element: SCNGeometryElement) {
        let outMin = SCNVector3(rect.minX, rect.minY, 0)
        let outMax = SCNVector3(rect.maxX, rect.maxY, 0)
        let inMin = SCNVector3(rect.minX + thickness, rect.minY + thickness, 0)
        let inMax = SCNVector3(rect.maxX - thickness, rect.maxY - thickness, 0)
        let vertices: [SCNVector3] = [
            SCNVector3(outMin.x, outMax.y, outMin.z),
            SCNVector3(inMin.x, inMax.y, inMin.z),
            SCNVector3(outMax.x, outMax.y, outMin.z),
            SCNVector3(inMax.x, inMax.y, inMin.z),
            SCNVector3(outMax.x, outMin.y, outMin.z),
            SCNVector3(inMax.x, inMin.y, inMin.z),
            SCNVector3(outMin.x, outMin.y, outMin.z),
            SCNVector3(inMin.x, inMin.y, inMin.z),
        ]
        let indices: [Int32] = [0, 1, 2, 3, 4, 5, 6, 7, 0, 1]
        let source = SCNGeometrySource(vertices: vertices)
        let element = SCNGeometryElement(indices: indices, primitiveType: .triangleStrip)
        return (source: source, element: element)
    }
}
