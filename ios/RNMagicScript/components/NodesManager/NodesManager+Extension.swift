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
import ARKit

extension NodesManager: RCTARViewObserving {
    @objc internal func renderer(_ renderer: SCNSceneRenderer, didAdd node: SCNNode, for anchor: ARAnchor) {
        if let anchorId = anchor.name {
            registerAnchorNode(node, anchorId: anchorId)
        }

        if let name = anchor.name, let prism = findPrismWithAnchorUuid(name) {
            prism.applyTransform(from: node)
        }
    }

    @objc internal func renderer(_ renderer: SCNSceneRenderer, didUpdate node: SCNNode, for anchor: ARAnchor) {
        if let name = anchor.name, let prism = findPrismWithAnchorUuid(name) {
            prism.applyTransform(from: node)
        }
    }

    @objc internal func renderer(_ renderer: SCNSceneRenderer, didRemove node: SCNNode, for anchor: ARAnchor) {
        if let anchorId = anchor.name {
            unregisterAnchorNode(anchorId: anchorId)
        }
    }

    @objc internal func renderer(_ renderer: SCNSceneRenderer, didRenderScene scene: SCNScene, atTime time: TimeInterval) {
        if let pointOfView = renderer.pointOfView {
            
            let begin = pointOfView.position
            let direction = pointOfView.worldFront
            let ray = Ray(begin: begin, direction: direction, length: 5.0)
            
            var results: [(prism: Prism, intersectionPoint: SCNVector3)] = []
            prismsById.forEach {
                $0.value.isPointed = false
                var outRay: Ray? = nil
                if $0.value.intersect(with: ray, clippedRay: &outRay),
                    let clippedRay = outRay {
                    let value = (prism: $0.value, intersectionPoint: clippedRay.begin)
                    results.append(value)
                }
            }
            if results.count > 0 {
                results.sort { (item1, item2) -> Bool in
                    let distSq1 = (item1.intersectionPoint - ray.begin).lengthSq()
                    let distSq2 = (item2.intersectionPoint - ray.begin).lengthSq()
                    return distSq1 < distSq2
                }
                
                results.first!.prism.isPointed = true
            }
#if targetEnvironment(simulator)
            if let position = results.first?.intersectionPoint {
                getDebugIntersectionPointNode().position = position
                getDebugIntersectionPointNode().isHidden = false
            } else {
                getDebugIntersectionPointNode().isHidden = true
            }
#endif
        }
    }
    
#if targetEnvironment(simulator)
    fileprivate func getDebugIntersectionPointNode() -> SCNNode {
        let name = "debugIntersectionPointNode"
        if let node = rootNode.childNode(withName: name, recursively: false) {
            return node
        }
        
        let node = NodesFactory.createSphereNode(radius: 0.005, segmentCount: 20, color: UIColor.yellow)
        node.name = name
        rootNode.addChildNode(node)
        
        return node
    }
#endif
}

extension Prism {
    func applyTransform(from anchorNode: SCNNode) {
        // This prism may not yet be attached to its parent
        // so set the world transform directly
        setWorldTransform(anchorNode.worldTransform)
    }
}
