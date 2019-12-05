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

class SCNCircle : SCNGeometry {
    var barImage: UIImage? {
        didSet { firstMaterial?.diffuse.contents = barImage; updateProgress() }
    }

    var progress: Float = 0.0 {
        didSet { updateProgress() }
    }

    convenience init(radius: CGFloat, thickness: CGFloat) {
        self.init(size: CGSize(width: 2 * radius, height: 2 * radius), thickness: thickness)
    }

    convenience init(size: CGSize, thickness: CGFloat) {
        let geom = SCNCircle.createGeometry(size: size, thickness: thickness)
        self.init(sources: geom.sources, elements: geom.elements)
        for (index, material) in materials.enumerated() {
            material.lightingModel = .constant
            material.diffuse.wrapS = .clamp
            material.isDoubleSided = NodeConfiguration.isDoubleSided
            material.diffuse.contents = UIColor.clear
            material.diffuse.mappingChannel = index
            material.transparencyMode = .singleLayer
        }
    }

    fileprivate func updateProgress() {
        firstMaterial?.diffuse.contentsTransform = SCNMatrix4MakeTranslation(1.0 - progress, 0, 0)
    }

    static fileprivate func createGeometry(size: CGSize, thickness: CGFloat) -> (sources: [SCNGeometrySource], elements: [SCNGeometryElement]) {
        let radius = 0.5 * min(size.width, size.height)
        let borderWidth: CGFloat = max(0.05 * radius, min(thickness, radius))

        let outVertices = SCNCircle.createVertices(size: size)
        let inVertices = SCNCircle.createVertices(size: CGSize(width: size.width - borderWidth, height: size.height - borderWidth))

        assert(outVertices.count == inVertices.count, "Outer and inner arrays must have the same number of vertices.")
        var vertices: [SCNVector3] = []
        for i in 0..<outVertices.count {
            vertices.append(outVertices[i])
            vertices.append(inVertices[i])
        }

        // TexCoords
        var texCoords: [CGPoint] = []
        for i in 0..<outVertices.count {
            let u: CGFloat = CGFloat(i) / CGFloat(outVertices.count)
            texCoords.append(CGPoint(x: u, y: 0))
            texCoords.append(CGPoint(x: u, y: 1))
        }
        let indicesEnd: [Int16] = Array(0..<Int16(vertices.count))

        let sources = [
            SCNGeometrySource(vertices: vertices),
            SCNGeometrySource(textureCoordinates: texCoords)
        ]
        let elements = [
            SCNGeometryElement(indices: indicesEnd, primitiveType: .triangleStrip),
        ]

        return (sources: sources, elements: elements)
    }

    static fileprivate func createVertices(size: CGSize) -> [SCNVector3] {
        var vertices: [SCNVector3] = []

        let arcSegmentsCount: Int = 18
        let rH: Float = 0.5 * Float(size.width)
        let rV: Float = 0.5 * Float(size.height)
        let z: Float = 0.0
        let deltaAngle: Float = Float(90.0).toRadians / Float(arcSegmentsCount)
        for angle in stride(from: 0, to: 2 * Float.pi, by: deltaAngle) {
            let x: Float = rH * sin(angle)
            let y: Float = rV * cos(angle)
            vertices.append(SCNVector3(x, y, z))
        }

        let angle = 2 * Float.pi
        let x: Float = rH * sin(angle)
        let y: Float = rV * cos(angle)
        vertices.append(SCNVector3(x, y, z))

        return vertices
    }
}
