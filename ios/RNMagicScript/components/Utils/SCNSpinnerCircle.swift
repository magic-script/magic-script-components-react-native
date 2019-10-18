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

class SCNSpinnerCircle : SCNGeometry {
    static let uTextCoordMultiplier: Float = 72.0

    var barBeginImage: UIImage? {
        didSet { firstMaterial?.diffuse.contents = barBeginImage }
    }

    var barEndImage: UIImage? {
        didSet { materials[1].diffuse.contents = barEndImage }
    }

    var progress: Float = 0.0 {
        didSet {
            let tx: Float = -SCNSpinnerCircle.uTextCoordMultiplier * progress
            materials[1].diffuse.contentsTransform = SCNMatrix4MakeTranslation(tx, 0, 0)
        }
    }

    convenience init(radius: CGFloat, thickness: CGFloat) {
        self.init(size: CGSize(width: 2 * radius, height: 2 * radius), thickness: thickness)
    }

    convenience init(size: CGSize, thickness: CGFloat) {
        let geom = SCNSpinnerCircle.createGeometry(size: size, thickness: thickness)
        self.init(sources: geom.sources, elements: geom.elements)
        materials.append(SCNMaterial())
        for (index, material) in materials.enumerated() {
            material.lightingModel = .constant
            material.diffuse.wrapS = .clamp
            material.isDoubleSided = NodeConfiguration.isDoubleSided
            material.diffuse.contents = UIColor.clear
            material.diffuse.mappingChannel = index
            material.transparencyMode = .singleLayer
        }
    }

    static fileprivate func createGeometry(size: CGSize, thickness: CGFloat) -> (sources: [SCNGeometrySource], elements: [SCNGeometryElement]) {
        let radius = min(0.5 * size.width, 0.5 * size.height)
        let borderWidth: CGFloat = max(0.05 * radius, min(thickness, radius))

        let outVertices = SCNSpinnerCircle.createVertices(size: size)
        let inVertices = SCNSpinnerCircle.createVertices(size: CGSize(width: size.width - borderWidth, height: size.height - borderWidth))

        assert(outVertices.count == inVertices.count, "Outer and inner arrays must have the same number of vertices.")
        var vertices: [SCNVector3] = []
        for i in 0..<outVertices.count {
            vertices.append(outVertices[i])
            vertices.append(inVertices[i])
        }

        // Begin part
        var texCoordsBegin: [CGPoint] = []
        for i in 0..<outVertices.count {
            let u: CGFloat = (i == outVertices.count - 1) ? 1.0 : 0.0
            texCoordsBegin.append(CGPoint(x: u, y: 0))
            texCoordsBegin.append(CGPoint(x: u, y: 1))
        }
        let firstIndexBegin: Int16 = Int16(vertices.count - 4)
        let indicesBegin: [Int16] = Array(firstIndexBegin..<Int16(vertices.count))

        // End part
        var texCoordsEnd: [CGPoint] = []
        for i in 0..<outVertices.count {
            let t: CGFloat = CGFloat(i) / CGFloat(outVertices.count - 1)
            let u: CGFloat = CGFloat(SCNSpinnerCircle.uTextCoordMultiplier) * t
            texCoordsEnd.append(CGPoint(x: u, y: 0))
            texCoordsEnd.append(CGPoint(x: u, y: 1))
        }
        let indicesEnd: [Int16] = Array(0..<Int16(vertices.count))

        let sources = [
            SCNGeometrySource(vertices: vertices),
            SCNGeometrySource(textureCoordinates: texCoordsBegin),
            SCNGeometrySource(textureCoordinates: texCoordsEnd)
        ]
        let elements = [
            SCNGeometryElement(indices: indicesBegin, primitiveType: .triangleStrip),
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
        let halfDeltaAngle: Float = 0.5 * deltaAngle
        for angle in stride(from: halfDeltaAngle, to: 2 * Float.pi, by: deltaAngle) {
            let x: Float = rH * sin(angle)
            let y: Float = rV * cos(angle)
            vertices.append(SCNVector3(x, y, z))
        }

        let angle = 2 * Float.pi + 1.1 * halfDeltaAngle
        let x: Float = rH * sin(angle)
        let y: Float = rV * cos(angle)
        vertices.append(SCNVector3(x, y, z))

        return vertices
    }
}
