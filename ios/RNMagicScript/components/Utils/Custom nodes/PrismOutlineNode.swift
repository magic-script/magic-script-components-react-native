//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
import SceneKit

class PrismOutlineNode : SCNNode {
    fileprivate let boxNode: SCNNode
    fileprivate let cornersNode: SCNNode
    fileprivate let cornerColor = UIColor(red: 0.3725, green: 0.3529, blue: 0.4706, alpha: 1.0)

    var size: SCNVector3 = SCNVector3(1, 1, 1) {
        didSet { updateSize() }
    }

    var color: UIColor = UIColor.white.withAlphaComponent(0.25){
        didSet {
            self.boxNode.geometry?.firstMaterial?.diffuse.contents = color
        }
    }

    override init() {
        let box = SCNBox(width: 1.0, height: 1.0, length: 1.0, chamferRadius: 0)
        box.firstMaterial?.diffuse.contents = UIColor.white.withAlphaComponent(0.15)
        box.firstMaterial?.lightingModel = .constant
        self.boxNode = SCNNode(geometry: box)
        self.cornersNode = SCNNode()
        super.init()
        addChildNode(boxNode)
        addChildNode(cornersNode)
        setupCornerNodes()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override var renderingOrder: Int {
        didSet { boxNode.renderingOrder = renderingOrder }
    }

    fileprivate func updateSize() {
        boxNode.scale = size
        cornersNode.scale = size
        let invertedSize = SCNVector3(1, 1, 1) / size
        cornersNode.childNodes.forEach { $0.scale = invertedSize }
    }

    fileprivate func setupCornerNodes() {
        let transforms: [(position: SCNVector3, rotation: SCNQuaternion)] = [
            (position: SCNVector3(-0.5, -0.5, -0.5), rotation: SCNQuaternionIdentity),
            (position: SCNVector3( 0.5, -0.5, -0.5), rotation: SCNQuaternion.fromAxis(SCNVector3.up, andAngle: 1.5 * Float.pi)),
            (position: SCNVector3( 0.5, -0.5,  0.5), rotation: SCNQuaternion.fromAxis(SCNVector3.up, andAngle: Float.pi)),
            (position: SCNVector3(-0.5, -0.5,  0.5), rotation: SCNQuaternion.fromAxis(SCNVector3.up, andAngle: 0.5 * Float.pi)),

            (position: SCNVector3(-0.5,  0.5, -0.5), rotation: SCNQuaternion.fromAxis(SCNVector3.right, andAngle: 0.5 * Float.pi)),
            (position: SCNVector3( 0.5,  0.5, -0.5), rotation: SCNQuaternion.fromAxis(SCNVector3.forward, andAngle: Float.pi)),
            (position: SCNVector3( 0.5,  0.5,  0.5), rotation: SCNQuaternion.fromAxis(SCNVector3(0.707, 0, -0.707), andAngle: Float.pi)),
            (position: SCNVector3(-0.5,  0.5,  0.5), rotation: SCNQuaternion.fromAxis(SCNVector3.right, andAngle: Float.pi)),
        ]
        transforms.forEach { cornersNode.addChildNode(createCornerNode(position: $0.position, rotation: $0.rotation)) }
    }

    fileprivate func createCornerNode(position: SCNVector3, rotation: SCNQuaternion) -> SCNNode {
        let length: CGFloat = 0.25
        let radius: CGFloat = 0.0075
        let sphere = SCNSphere(radius: radius)
        sphere.firstMaterial?.diffuse.contents = cornerColor
        sphere.firstMaterial?.lightingModel = .constant
        sphere.segmentCount = 8
        let node = SCNNode(geometry: sphere)
        node.position = position
        node.orientation = rotation

        let bottomRadius: CGFloat = 0.666 * radius
        let topRadius: CGFloat = 0.4 * bottomRadius
        let coneX = SCNCone(topRadius: topRadius, bottomRadius: bottomRadius, height: length)
        coneX.radialSegmentCount = 5
        coneX.firstMaterial?.diffuse.contents = cornerColor
        coneX.firstMaterial?.lightingModel = .constant
        let nodeX = SCNNode(geometry: coneX)
        nodeX.orientation = SCNQuaternion.fromAxis(SCNVector3.forward, andAngle: -0.5 * Float.pi)
        nodeX.position = SCNVector3(0.5 * length, 0, 0)
        node.addChildNode(nodeX)

        let coneY = SCNCone(topRadius: topRadius, bottomRadius: bottomRadius, height: length)
        coneY.radialSegmentCount = 5
        coneY.firstMaterial?.diffuse.contents = cornerColor
        coneY.firstMaterial?.lightingModel = .constant
        let nodeY = SCNNode(geometry: coneY)
        nodeY.position = SCNVector3(0, 0.5 * length, 0)
        node.addChildNode(nodeY)

        let coneZ = SCNCone(topRadius: topRadius, bottomRadius: bottomRadius, height: length)
        coneZ.radialSegmentCount = 5
        coneZ.firstMaterial?.diffuse.contents = cornerColor
        coneZ.firstMaterial?.lightingModel = .constant
        let nodeZ = SCNNode(geometry: coneZ)
        nodeZ.orientation = SCNQuaternion.fromAxis(SCNVector3.right, andAngle: 0.5 * Float.pi)
        nodeZ.position = SCNVector3(0, 0, 0.5 * length)
        node.addChildNode(nodeZ)

        return node
    }
}
