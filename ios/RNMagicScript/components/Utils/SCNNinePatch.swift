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

class SCNNinePatch : SCNGeometry {
    var width: CGFloat = 0
    var height: CGFloat = 0

    // For both 'geometryCaps' and 'texCoordsCaps' all properties
    // should be in range [0..1] and follow the following rules:
    // •  0 <= left <= right <= 1
    // •  0 <= bottom <= top <= 1
    convenience init(size: CGSize, geometryCaps: UIEdgeInsets, texCoordsCaps: UIEdgeInsets) {
        let geom = SCNNinePatch.createGeometry(size: size, geometryCaps: geometryCaps, texCoordsCaps: texCoordsCaps)
        self.init(sources: geom.sources, elements: geom.elements)
        self.width = size.width
        self.height = size.height
    }

    static fileprivate func createGeometry(size: CGSize, geometryCaps: UIEdgeInsets, texCoordsCaps: UIEdgeInsets) -> (sources: [SCNGeometrySource], elements: [SCNGeometryElement]) {
        // Vertices
        let x0: CGFloat = -0.5 * size.width
        let x1: CGFloat = x0 + geometryCaps.left
        let x3: CGFloat = 0.5 * size.width
        let x2: CGFloat = x3 - geometryCaps.right
        let y0: CGFloat = 0.5 * size.height
        let y1: CGFloat = y0 - geometryCaps.top
        let y3: CGFloat = -0.5 * size.height
        let y2: CGFloat = y3 + geometryCaps.bottom
        let vertices: [SCNVector3] = [
            SCNVector3(x0, y0, 0), SCNVector3(x1, y0, 0), SCNVector3(x2, y0, 0), SCNVector3(x3, y0, 0),
            SCNVector3(x0, y1, 0), SCNVector3(x1, y1, 0), SCNVector3(x2, y1, 0), SCNVector3(x3, y1, 0),
            SCNVector3(x0, y2, 0), SCNVector3(x1, y2, 0), SCNVector3(x2, y2, 0), SCNVector3(x3, y2, 0),
            SCNVector3(x0, y3, 0), SCNVector3(x1, y3, 0), SCNVector3(x2, y3, 0), SCNVector3(x3, y3, 0)
        ]

        // TexCoords
        let u0: CGFloat = 0
        let u1: CGFloat = texCoordsCaps.left
        let u2: CGFloat = 1.0 - texCoordsCaps.right
        let u3: CGFloat = 1
        let v0: CGFloat = 0
        let v1: CGFloat = texCoordsCaps.top
        let v2: CGFloat = 1.0 - texCoordsCaps.bottom
        let v3: CGFloat = 1
        let texCoords: [CGPoint] = [
            CGPoint(x: u0, y: v0), CGPoint(x: u1, y: v0), CGPoint(x: u2, y: v0), CGPoint(x: u3, y: v0),
            CGPoint(x: u0, y: v1), CGPoint(x: u1, y: v1), CGPoint(x: u2, y: v1), CGPoint(x: u3, y: v1),
            CGPoint(x: u0, y: v2), CGPoint(x: u1, y: v2), CGPoint(x: u2, y: v2), CGPoint(x: u3, y: v2),
            CGPoint(x: u0, y: v3), CGPoint(x: u1, y: v3), CGPoint(x: u2, y: v3), CGPoint(x: u3, y: v3)
        ]

        // Indices
        let indices: [Int16] = [
            0, 4, 1,    1, 4, 5,
            1, 5, 2,    2, 5, 6,
            2, 6, 3,    3, 6, 7,

            4, 8, 5,    5, 8, 9,
            5, 9, 6,    6, 9, 10,
            6, 10, 7,   7, 10, 11,

            8, 12, 9,   9, 12, 13,
            9, 13, 10,  10, 13, 14,
            10, 14, 11, 11, 14, 15
        ]

        let sources = [
            SCNGeometrySource(vertices: vertices),
            SCNGeometrySource(textureCoordinates: texCoords)
        ]
        let elements = [
            SCNGeometryElement(indices: indices, primitiveType: .triangles),
        ]

        return (sources: sources, elements: elements)
    }
}
