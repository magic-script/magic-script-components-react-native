//
//  SCNBBoxNode.swift
//  SceneKitComponents
//
//  Created by Pawel Leszkiewicz on 24/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

class SCNBBoxNode: SCNNode {

    var color: UIColor? {
        get { return bboxNode.geometry?.firstMaterial?.diffuse.contents as? UIColor }
        set { bboxNode.geometry?.firstMaterial?.diffuse.contents = newValue }
    }

    fileprivate let bbox: (min: SCNVector3, max: SCNVector3)
    fileprivate var bboxNode: SCNNode!

    init(_ bbox: (min: SCNVector3, max: SCNVector3)) {
        self.bbox = bbox
        super.init()
        setupNode()
    }

    required init?(coder aDecoder: NSCoder) {
        self.bbox = (min: SCNVector3(), max: SCNVector3())
        super.init(coder: aDecoder)
        setupNode()
    }

    fileprivate func setupNode() {

        let vertices: [SCNVector3] = [
            SCNVector3(x: bbox.min.x, y: bbox.max.y, z: bbox.max.z),
            SCNVector3(x: bbox.max.x, y: bbox.max.y, z: bbox.max.z),
            SCNVector3(x: bbox.max.x, y: bbox.max.y, z: bbox.min.z),
            SCNVector3(x: bbox.min.x, y: bbox.max.y, z: bbox.min.z),

            SCNVector3(x: bbox.min.x, y: bbox.min.y, z: bbox.max.z),
            SCNVector3(x: bbox.max.x, y: bbox.min.y, z: bbox.max.z),
            SCNVector3(x: bbox.max.x, y: bbox.min.y, z: bbox.min.z),
            SCNVector3(x: bbox.min.x, y: bbox.min.y, z: bbox.min.z),
        ]
        let indices: [Int32] = [
            0,1, 1,2, 2,3, 3,0,
            4,5, 5,6, 6,7, 7,4,
            0,4, 1,5, 2,6, 3,7,
        ]

        let source = SCNGeometrySource(vertices: vertices)
        let element = SCNGeometryElement(indices: indices, primitiveType: .line)
        let geometry = SCNGeometry(sources: [source], elements: [element])
        geometry.firstMaterial?.lightingModel = .constant
        geometry.firstMaterial?.diffuse.contents = UIColor.red
        bboxNode = SCNNode(geometry: geometry)
        addChildNode(bboxNode)
    }
}
