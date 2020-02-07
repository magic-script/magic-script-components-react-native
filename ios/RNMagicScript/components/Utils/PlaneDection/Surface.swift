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

import Foundation
import ARKit

class Surface: NSObject {
    fileprivate(set) var anchor: ARPlaneAnchor

    var id: UUID {
        return anchor.identifier
    }

    var center: SCNVector3 {
        // transform center to world coordinates
        return SCNVector3(anchor.transform * simd_float4(anchor.center, 1))
    }

    var normal: SCNVector3 {
        return SCNMatrix4(anchor.transform).forward
    }

    var vertices: [SCNVector3] {
        // Convert vertices to correct format
        var result = [SCNVector3]()
        for vertex in anchor.geometry.boundaryVertices {
            result.append(SCNMatrix4(anchor.transform) * SCNVector3(vertex))
        }
        return result
    }

    var type: String {
        if #available(iOS 12.0, *), ARPlaneAnchor.isClassificationSupported {
            return anchor.classification.description
        }

        return "Unknown"
    }

    init(anchor: ARPlaneAnchor) {
        self.anchor = anchor
    }

    func update(anchor: ARPlaneAnchor) {
        self.anchor = anchor
    }
}

extension Array where Element == SCNVector3 {
    func toArrayOfCGFloat() -> [[CGFloat]] {
        var vertices = [[CGFloat]]()
        self.forEach {
            vertices.append([CGFloat($0.x), CGFloat($0.y), CGFloat($0.z)])
        }
        return vertices
    }
}
