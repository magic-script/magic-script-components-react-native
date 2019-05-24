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
    }

    static fileprivate func createGeometry(rect: CGRect, thickness: CGFloat, radius: CGFloat) -> (source: SCNGeometrySource, element: SCNGeometryElement) {

        let outVertices = SCNRectangle.createVertices(rect: rect, radius: radius)
        let inVertices = SCNRectangle.createVertices(rect: rect.insetBy(dx: thickness, dy: thickness), radius: radius - thickness)

        assert(outVertices.count == inVertices.count, "Outer and input arrays must have the same number of vertices.")
        var vertices: [SCNVector3] = []
        for i in 0..<outVertices.count {
            vertices.append(outVertices[i])
            vertices.append(inVertices[i])
        }

        let indices: [Int32] = Array(0..<Int32(vertices.count)) + [0, 1]
        let source = SCNGeometrySource(vertices: vertices)
        let element = SCNGeometryElement(indices: indices, primitiveType: .triangleStrip)
        return (source: source, element: element)
    }

    static fileprivate func createVertices(rect: CGRect, radius: CGFloat) -> [SCNVector3] {
        guard radius > 0 else {
            return [
                SCNVector3(rect.minX, rect.maxY, 0),
                SCNVector3(rect.maxX, rect.maxY, 0),
                SCNVector3(rect.maxX, rect.minY, 0),
                SCNVector3(rect.minX, rect.minY, 0),
            ]
        }

        var vertices: [SCNVector3] = []

        let arcSegmentsCount: Int = 10
        let r: Float = Float(radius)
        let z: Float = 0.0
        let centerX: Float = Float(rect.midX)
        let centerY: Float = Float(rect.midY)
        let offsetX: Float = 0.5 * Float(rect.width) - r
        let offsetY: Float = 0.5 * Float(rect.height) - r
        let deltaAngle: Float = Float(90.0).toRadians / Float(arcSegmentsCount)
        for angle in stride(from: 0.0, to: 2 * Float.pi, by: deltaAngle) {
            let x: Float = r * sin(angle)
            let y: Float = r * cos(angle)
            let dx: Float = (x >= 0) ? offsetX : -offsetX
            let dy: Float = (y >= 0) ? offsetY : -offsetY
            vertices.append(SCNVector3(centerX + x + dx, centerY + y + dy, z))
        }

        vertices.append(SCNVector3(rect.minX + radius, rect.maxY, CGFloat(z)))
        vertices.insert(SCNVector3(rect.minX, rect.minY + radius, CGFloat(z)), at: 3 * arcSegmentsCount)
        vertices.insert(SCNVector3(rect.maxX - radius, rect.minY, CGFloat(z)), at: 2 * arcSegmentsCount)
        vertices.insert(SCNVector3(rect.maxX, rect.maxY - radius, CGFloat(z)), at: arcSegmentsCount)

        return vertices
    }
}
