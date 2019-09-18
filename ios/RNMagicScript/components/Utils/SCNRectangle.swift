//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import SceneKit

class SCNRectangle : SCNGeometry {

    convenience init(rect: CGRect, thickness: CGFloat, radius: CGFloat = 0) {
        let geom = SCNRectangle.createGeometry(rect: rect, thickness: thickness, radius: radius)
        self.init(sources: [geom.source], elements: [geom.element])
        firstMaterial?.lightingModel = .constant
    }

    convenience init(size: CGSize, thickness: CGFloat, radius: CGFloat = 0) {
        let rect = CGRect(x: -0.5 * size.width, y: -0.5 * size.height, width: size.width, height: size.height)
        self.init(rect: rect, thickness: thickness, radius: radius)
    }

    static fileprivate func createGeometry(rect: CGRect, thickness: CGFloat, radius: CGFloat) -> (source: SCNGeometrySource, element: SCNGeometryElement) {
        let source: SCNGeometrySource
        let element: SCNGeometryElement
        let cornerRadius: CGFloat = min(radius, min(0.5 * rect.width, 0.5 * rect.height))
        let borderWidth: CGFloat = max(0, min(thickness, min(0.5 * rect.width, 0.5 * rect.height)))
        if borderWidth == 0 {
            let vertices = SCNRectangle.createVertices(rect: rect, radius: cornerRadius)
            var indices: [Int16] = [0]
            for i in 1..<vertices.count {
                indices.append(Int16(i))
                indices.append(Int16(i))
            }
            indices.append(Int16(0))

            source = SCNGeometrySource(vertices: vertices)
            element = SCNGeometryElement(indices: indices, primitiveType: .line)
        } else {
            let outVertices = SCNRectangle.createVertices(rect: rect, radius: cornerRadius)
            let innerCornerRadius: CGFloat = (cornerRadius == 0) ? 0 : max(0.0001, cornerRadius - borderWidth)
            let inVertices = SCNRectangle.createVertices(rect: rect.insetBy(dx: borderWidth, dy: borderWidth), radius: innerCornerRadius)

            assert(outVertices.count == inVertices.count, "Outer and inner arrays must have the same number of vertices.")
            var vertices: [SCNVector3] = []
            for i in 0..<outVertices.count {
                vertices.append(outVertices[i])
                vertices.append(inVertices[i])
            }

            let indices: [Int16] = Array(0..<Int16(vertices.count)) + [0, 1]
            
            source = SCNGeometrySource(vertices: vertices)
            element = SCNGeometryElement(indices: indices, primitiveType: .triangleStrip)
        }

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
