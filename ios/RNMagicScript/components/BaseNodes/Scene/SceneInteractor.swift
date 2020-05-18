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
import ARKit
import SceneKit

//sourcery: AutoMockable
protocol SceneInteractingDelegate: class {
    func onSceneUpdate(scene: Scene)
}

//sourcery: AutoMockable
protocol SceneInteracting: class {
    func onUpdate(pointOfView: SCNNode, atTime: TimeInterval)
}

class SceneInteractor {
    weak var scene: Scene? {
        didSet {
            if let scene = scene {
                scene.prismContextMenu = PrismContextMenuBuilder.build(prismInteractor: prismInteractor)
            } else {
                scene?.prismContextMenu = nil
            }
        }
    }

    weak var arView: RCTARView? {
        didSet {
            prismInteractor.gesturable = arView
        }
    }

    var prismInteractor: PrismInteractor

    init(with prismInteractor: PrismInteractor) {
        self.prismInteractor = prismInteractor
        self.prismInteractor.delegate = self
    }

    func onUpdate(pointOfView: SCNNode, atTime: TimeInterval) {
        let begin = pointOfView.position
        let direction = pointOfView.worldFront
        let ray = Ray(begin: begin, direction: direction, length: Prism.defaultRayLenght)

        var results: [(prism: Prism, intersectionPoint: SCNVector3)] = []
        let lastPointedPrims = prismInteractor.interactedPrism
        if let prisms = scene?.prisms {
            prisms.forEach {
                var outRay: Ray? = nil
                if $0.intersect(with: ray, clippedRay: &outRay),
                    let clippedRay = outRay {
                    let value = (prism: $0, intersectionPoint: clippedRay.begin)
                    results.append(value)
                }
            }
            if results.count > 0 {
                scene?.prismContextMenu.isHidden = false
                results.sort { (item1, item2) -> Bool in
                    let distSq1 = (item1.intersectionPoint - ray.begin).lengthSq()
                    let distSq2 = (item2.intersectionPoint - ray.begin).lengthSq()
                    return distSq1 < distSq2
                }

                let pointedPrism = results.first!.prism
                if pointedPrism != lastPointedPrims {
                    pointedPrism.operationMode = .highlighted
                    lastPointedPrims?.operationMode = .normal
                    prismInteractor.interactedPrism = pointedPrism
                }
                updatePrismMenu(for: pointedPrism)
                prismInteractor.update(cameraNode: pointOfView, time: atTime)
            } else {
                scene?.prismContextMenu.isHidden = true
                prismInteractor.interactedPrism = nil
                lastPointedPrims?.operationMode = .normal
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

    private func updatePrismMenu(for prism: Prism) {
        scene?.prismContextMenu.prism = prism
        scene?.prismContextMenu.position = SCNVector3(prism.position.x, prism.position.y + (prism.size.y * prism.scale.y) / 2 + Prism.distanceToContextMenu, prism.position.z)

        scene?.prismContextMenu.setNeedsLayout()
        scene?.prismContextMenu.layoutIfNeeded()
        scene?.prismContextMenu.resetClippingPlanesShaderModifiers(recursive: true)
    }


    #if targetEnvironment(simulator)
    private func getDebugIntersectionPointNode() -> SCNNode {
        guard let rootNode = scene?.parent else { return SCNNode() }
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


extension SceneInteractor: PrismInteractingDelegate {
    func onPrismUpdated(prism: Prism) {
        updatePrismMenu(for: prism)
    }
}
