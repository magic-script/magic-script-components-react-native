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

import ARKit
import SceneKit

class SurfaceNode: SCNNode {
    let meshNode: SCNNode
    let extentNode: SCNNode
    var surface: Surface!

    init(anchor: ARPlaneAnchor, in sceneView: ARSCNView) {
        // Create a mesh to visualize the estimated shape of the plane.
        guard let meshGeometry = ARSCNPlaneGeometry(device: sceneView.device!)
            else { fatalError("Can't create plane geometry") }

        meshGeometry.update(from: anchor.geometry)
        meshNode = SCNNode(geometry: meshGeometry)
        meshNode.opacity = 0.25
        meshNode.geometry?.firstMaterial?.diffuse.contents = UIColor.red

        let extentPlane: SCNPlane = SCNPlane(width: CGFloat(anchor.extent.x), height: CGFloat(anchor.extent.z))
        extentNode = SCNNode(geometry: extentPlane)
        extentNode.simdPosition = anchor.center
        extentNode.opacity = 0.6
        extentNode.geometry?.firstMaterial?.diffuse.contents = UIColor.clear

        // `SCNPlane` is vertically oriented in its local coordinate space, so
        // rotate it to match the orientation of `ARPlaneAnchor`.
        extentNode.eulerAngles.x = -.pi / 2

        super.init()

        addChildNode(meshNode)
        addChildNode(extentNode)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
