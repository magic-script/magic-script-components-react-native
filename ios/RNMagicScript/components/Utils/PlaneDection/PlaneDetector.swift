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

import UIKit
import ARKit
import SceneKit

@objc open class PlaneDetector: NSObject, RCTARViewObserving {
    @objc public static var instance: PlaneDetector = PlaneDetector()

    fileprivate(set) var surfaces: [String: Surface] = [:]
    fileprivate(set) var debug: Bool = false
    fileprivate weak var arView: RCTARView?
    fileprivate(set) var configuration: [String: Any] = [:]

    @objc public func register(arView: RCTARView) {
        self.arView = arView

        // trigger stored configuration (if ARView was created after PlaneDetector)
        let planeDetection = Convert.toPlaneDetection(configuration["planeType"])

        if let debug = Convert.toBool(configuration["debug"]) {
            self.debug = debug
        }

        // Add tap gesture for Planes
        let uiTapGestureRecognizer = UITapGestureRecognizer(target: self,
                                                            action: #selector(handleTapGesture(_:)))
        uiTapGestureRecognizer.delegate = self
        arView.addGestureRecognizer(uiTapGestureRecognizer)

        arView.register(self)

        self.arView?.enablePlaneDetection(planeDetection)
    }

    @objc public func enablePlaneDetection(with configuration: [String: Any]) {
        self.configuration = configuration
        let planeDetection = Convert.toPlaneDetection(configuration["planeType"])
        arView?.register(self)
        self.arView?.enablePlaneDetection(planeDetection)
    }

    @objc public func disablePlaneDetection() {
        self.configuration = [:]
        arView?.unregister(self)
        self.arView?.disablePlaneDetection()
    }

    @objc public func reset() {
        self.arView?.unregister(self)
        self.arView?.disablePlaneDetection()
        self.surfaces.removeAll()
        self.arView?.register(self)
        let planeDetection = Convert.toPlaneDetection(configuration["planeType"])
        self.arView?.enablePlaneDetection(planeDetection)
    }

    @objc public func getAllPlanes(with configuration: [String: Any]) -> [String: Any] {
        var result: [String: Any] = [:]
        for (id, surface) in self.surfaces {
            var vertices = [[CGFloat]]()
            for vertex in surface.vertices {
                vertices.append([CGFloat(vertex.x), CGFloat(vertex.y), CGFloat(vertex.z)])
            }

            result[id] = [ "id": surface.id, "type": surface.type, "vertices": vertices, "center": surface.center, "normal": surface.normal ]
        }
        return result
    }

    @objc public var onPlaneDetected: ((_ sender: PlaneDetector, _ id: UUID?, _ type: String, _ vertices: [[CGFloat]], _ center: [CGFloat], _ normal: [CGFloat]) -> Void)?
    @objc public var onPlaneUpdated: ((_ sender: PlaneDetector, _ id: UUID?, _ type: String, _ vertices: [[CGFloat]], _ center: [CGFloat], _ normal: [CGFloat]) -> Void)?
    @objc public var onPlaneRemoved: ((_ sender: PlaneDetector, _ id: UUID?, _ type: String, _ vertices: [[CGFloat]], _ center: [CGFloat], _ normal: [CGFloat]) -> Void)?
    @objc public var onPlaneTapped: ((_ sender: PlaneDetector, _ id: UUID?, _ type: String, _ vertices: [[CGFloat]], _ center: [CGFloat], _ normal: [CGFloat], _ point: [CGFloat]) -> Void)?

    func handleNodeTap(_ surface: Surface, _ touchPoint: SCNVector3) {
        // notify JSX layer
        self.onPlaneTapped?(self,
                            surface.id,
                            surface.type,
                            surface.vertices.toArrayOfCGFloat(),
                            surface.center.toArrayOfCGFloat,
                            surface.normal.toArrayOfCGFloat,
                            touchPoint.toArrayOfCGFloat)
    }

    func renderer(_ renderer: SCNSceneRenderer, didAdd node: SCNNode, for anchor: ARAnchor) {
        guard let surfaceAnchor = anchor as? ARPlaneAnchor else { return }

        let surface = Surface(anchor: surfaceAnchor)
        surfaces[surface.id.uuidString] = surface

        // notify JSX layer
        self.onPlaneDetected?(self,
                              surface.id,
                              surface.type,
                              surface.vertices.toArrayOfCGFloat(),
                              surface.center.toArrayOfCGFloat,
                              surface.normal.toArrayOfCGFloat)

        if (debug) {
            // Create a custom object to visualize the plane geometry and extent.
            let surfaceNode = SurfaceNode(anchor: surfaceAnchor, in: (arView?.arView)!)
            surfaceNode.surface = surface

            // Add the visualization to the ARKit-managed node so that it tracks
            // changes in the plane anchor as plane estimation continues.
            node.addChildNode(surfaceNode)
            node.name = surface.id.uuidString
        }
    }

    func renderer(_ renderer: SCNSceneRenderer, didUpdate node: SCNNode, for anchor: ARAnchor) {
        guard let surfaceAnchor = anchor as? ARPlaneAnchor,
            let surface = surfaces[surfaceAnchor.identifier.uuidString],
            let surfaceNode = node.childNodes.first as? SurfaceNode else { return }

        surface.update(anchor: surfaceAnchor)

        // notify JSX layer
        self.onPlaneUpdated?(self,
                             surface.id,
                             surface.type,
                             surface.vertices.toArrayOfCGFloat(),
                             surface.center.toArrayOfCGFloat,
                             surface.normal.toArrayOfCGFloat)

        if (debug) {
            // Update ARSCNPlaneGeometry to the anchor's new estimated shape.
            if let planeGeometry = surfaceNode.meshNode.geometry as? ARSCNPlaneGeometry {
                planeGeometry.update(from: surfaceAnchor.geometry)
            }

            // Update extent visualization to the anchor's new bounding rectangle.
            if let extentGeometry = surfaceNode.extentNode.geometry as? SCNPlane {
                extentGeometry.width = CGFloat(surfaceAnchor.extent.x)
                extentGeometry.height = CGFloat(surfaceAnchor.extent.z)
                surfaceNode.extentNode.simdPosition = surfaceAnchor.center
            }

            surfaceNode.surface = surface
        }
    }

    func renderer(_ renderer: SCNSceneRenderer, didRemove node: SCNNode, for anchor: ARAnchor) {
        guard let surfaceAnchor = anchor as? ARPlaneAnchor,
            let surface = surfaces[surfaceAnchor.identifier.uuidString] else { return }

        surfaces.removeValue(forKey: surface.id.uuidString)

        // notify JSX layer
        self.onPlaneRemoved?(self,
                             surface.id,
                             surface.type,
                             surface.vertices.toArrayOfCGFloat(),
                             surface.center.toArrayOfCGFloat,
                             surface.normal.toArrayOfCGFloat)
    }
}

extension PlaneDetector: UIGestureRecognizerDelegate {
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return true
    }

    @objc func handleTapGesture(_ sender: UITapGestureRecognizer) {
        if sender.state == .ended {
            let location: CGPoint = sender.location(in: self.arView?.arView)
            if let hits = self.arView?.arView.hitTest(location, types: [.existingPlaneUsingGeometry]) {
                for hit in hits {
                    if let surfaceId = hit.anchor?.identifier.uuidString, let tappedSurface = surfaces[surfaceId] {
                        handleNodeTap(tappedSurface, SCNMatrix4(hit.worldTransform).position)
                    }
                }
            }
        }
    }
}
