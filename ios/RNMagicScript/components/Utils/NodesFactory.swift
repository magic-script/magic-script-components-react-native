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

@objc open class NodesFactory: NSObject {

    @objc static func createPlaneNode(size: CGSize, color: UIColor? = UIColor.white, image: UIImage? = nil) -> SCNNode {
        let geometry = SCNPlane(width: size.width, height: size.height)
        geometry.firstMaterial?.lightingModel = .constant
        geometry.firstMaterial?.diffuse.contents = image ?? color
        geometry.firstMaterial?.multiply.contents = (image != nil) ? color : nil
        return SCNNode(geometry: geometry)
    }

    @objc static func createPlaneNode(width: CGFloat, height: CGFloat, color: UIColor? = UIColor.white, image: UIImage? = nil) -> SCNNode {
        return NodesFactory.createPlaneNode(size: CGSize(width: width, height: height), color: color, image: image)
    }

    @objc static func createOutlineNode(size: CGSize, cornerRadius: CGFloat, thickness: CGFloat = 0, color: UIColor = UIColor.white) -> SCNNode {
        let geometry = SCNRectangle(size: size, thickness: thickness, radius: cornerRadius)
        geometry.firstMaterial?.lightingModel = .constant
        geometry.firstMaterial?.diffuse.contents = color
        return SCNNode(geometry: geometry)
    }

    @objc static func createOutlineNode(width: CGFloat, height: CGFloat, cornerRadius: CGFloat, thickness: CGFloat = 0, color: UIColor = UIColor.white) -> SCNNode {
        return NodesFactory.createOutlineNode(size: CGSize(width: width, height: height), cornerRadius: cornerRadius, thickness: thickness, color: color)
    }

    @objc static func createLinesNode(vertices: [SCNVector3], color: UIColor? = UIColor.white) -> SCNNode {
        var indices: [Int16] = []
        for i in 1..<vertices.count {
            indices.append(Int16(i - 1))
            indices.append(Int16(i))
        }
        let source = SCNGeometrySource(vertices: vertices)
        let element = SCNGeometryElement(indices: indices, primitiveType: .line)
        let geometry = SCNGeometry(sources: [source], elements: [element])
        geometry.firstMaterial?.lightingModel = .constant
        geometry.firstMaterial?.diffuse.contents = color
        return SCNNode(geometry: geometry)
    }
}
