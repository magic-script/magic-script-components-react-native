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

@objc open class UiLineNode: RenderNode {

    @objc var points: [SCNVector3] = [] {
        didSet { linesNode.geometry = nil; setNeedsLayout() }
    }

    fileprivate var linesNode: SCNNode!

    @objc override func setupNode() {
        super.setupNode()
        linesNode = SCNNode()
        contentNode.addChildNode(linesNode)
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
        if linesNode.geometry == nil {
            linesNode.geometry = generateLinesGeometry()
        }

        linesNode.geometry?.firstMaterial?.diffuse.contents = self.color
    }

    fileprivate func generateLinesGeometry() -> SCNGeometry? {
        guard points.count >= 2 else { return nil }
        
        var indices: [Int16] = []
        for i in 1..<points.count {
            indices.append(Int16(i - 1))
            indices.append(Int16(i))
        }
        let source = SCNGeometrySource(vertices: points)
        let element = SCNGeometryElement(indices: indices, primitiveType: .line)
        let linesGeometry = SCNGeometry(sources: [source], elements: [element])
        linesGeometry.firstMaterial?.lightingModel = .constant
        return linesGeometry
    }
}
