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

import Quick
import Nimble
import SceneKit
@testable import RNMagicScriptHostApplication

class NodesFactorySpec: QuickSpec {
    override func spec() {
        describe("NodesFactory") {
            context("createPlaneNode") {
                let validatePlaneNode: (_ node: SCNNode, _ size: CGSize, _ color: UIColor) -> (Void) = { node, size, color in
                    let planeGeometry = node.geometry as! SCNPlane
                    expect(planeGeometry.width).to(beCloseTo(size.width))
                    expect(planeGeometry.height).to(beCloseTo(size.height))
                    expect((planeGeometry.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(color))
                }
                
                it("should create plane node (size)") {
                    let referenceSize = CGSize(width: 0.3, height: 0.6)
                    let referenceColor = UIColor.red
                    let node = NodesFactory.createPlaneNode(size: referenceSize, color: referenceColor)
                    validatePlaneNode(node, referenceSize, referenceColor)
                }
                
                it("should create plane node (width, height)") {
                    let referenceSize = CGSize(width: 0.3, height: 0.6)
                    let referenceColor = UIColor.red
                    let node = NodesFactory.createPlaneNode(width: referenceSize.width, height: referenceSize.height, color: referenceColor)
                    validatePlaneNode(node, referenceSize, referenceColor)
                }
            }
                        
            context("createNinePatchNode") {
                let validateNinePatchNode: (_ node: SCNNode, _ size: CGSize, _ geometryCaps: UIEdgeInsets, _ image: UIImage, _ imageCaps: UIEdgeInsets) -> (Void) = { node, size, geometryCaps, image, imageCaps in
                    let geometry: SCNNinePatch = node.geometry as! SCNNinePatch
                    expect(geometry.firstMaterial!.diffuse.contents).to(beIdenticalTo(image))
                    expect(geometry.width).to(beCloseTo(size.width))
                    expect(geometry.height).to(beCloseTo(size.height))
                }
                
                it("should create nine patch node (size)") {
                    let referenceSize = CGSize(width: 0.7, height: 0.51)
                    let referenceGeometryCaps = UIEdgeInsets(top: 0.1, left: 0.1, bottom: 0.1, right: 0.1)
                    let referenceImage = SystemIcon("close").image
                    let referenceImageCaps = UIEdgeInsets(top: 5, left: 5, bottom: 5, right: 5)
                    let node = NodesFactory.createNinePatchNode(size: referenceSize, geometryCaps: referenceGeometryCaps, image: referenceImage, imageCaps: referenceImageCaps)
                    validateNinePatchNode(node, referenceSize, referenceGeometryCaps, referenceImage, referenceImageCaps)
                }
                
                it("should create nine patch node (width, height)") {
                    let referenceSize = CGSize(width: 0.44, height: 0.98)
                    let referenceGeometryCaps = UIEdgeInsets(top: 0.05, left: 0.05, bottom: 0.05, right: 0.05)
                    let referenceImage = SystemIcon("mesh").image
                    let referenceImageCaps = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
                    let node = NodesFactory.createNinePatchNode(width: referenceSize.width, height: referenceSize.height, geometryCaps: referenceGeometryCaps, image: referenceImage, imageCaps: referenceImageCaps)
                    validateNinePatchNode(node, referenceSize, referenceGeometryCaps, referenceImage, referenceImageCaps)
                }
            }
            
            context("createOutlineNode") {
                let validateOutlineNode: (_ node: SCNNode, _ size: CGSize, _ cornerRadius: CGFloat, _ thickness: CGFloat, _ color: UIColor) -> (Void) = { node, size, cornerRadius, thickness, color in
                    let bbox = node.boundingBox
                    let width = bbox.max.x - bbox.min.x
                    let height = bbox.max.y - bbox.min.y
                    let geometry = node.geometry as! SCNRectangle
                    expect(width).to(beCloseTo(size.width))
                    expect(height).to(beCloseTo(size.height))
                    expect((geometry.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(color))
                }
                
                it("should create outline node (size)") {
                    let referenceSize = CGSize(width: 0.3, height: 0.6)
                    let referenceRadius: CGFloat = 0.5
                    let referenceThickness: CGFloat = 0.01
                    let referenceColor = UIColor.brown
                    let node = NodesFactory.createOutlineNode(size: referenceSize, cornerRadius: referenceRadius, thickness: referenceThickness, color: referenceColor)
                    validateOutlineNode(node, referenceSize, referenceRadius, referenceThickness, referenceColor)
                }
                
                it("should create outline node (width, height)") {
                    let referenceSize = CGSize(width: 0.66, height: 0.49)
                    let referenceRadius: CGFloat = 0.2
                    let referenceThickness: CGFloat = 0.05
                    let referenceColor = UIColor.orange
                    let node = NodesFactory.createOutlineNode(width: referenceSize.width, height: referenceSize.height, cornerRadius: referenceRadius, thickness: referenceThickness, color: referenceColor)
                    validateOutlineNode(node, referenceSize, referenceRadius, referenceThickness, referenceColor)
                }
            }
            
            context("createLinesNode") {
                it("should create lines node") {
                    let referenceVertices: [SCNVector3] = [
                        SCNVector3(0.1, 0.2, 0.3),
                        SCNVector3(0.3, 0.4, 0.5),
                        SCNVector3(-0.5, -0.9, 0.2),
                        SCNVector3(0.5, 1.3, -0.1),
                    ]
                    let referenceColor = UIColor.gray
                    let node = NodesFactory.createLinesNode(vertices: referenceVertices, color: referenceColor)
                    
                    var min = SCNVector3(99999.0, 99999.0, 99999.0)
                    var max = min.negated()
                    referenceVertices.forEach { point in
                        min.x = Swift.min(min.x, point.x)
                        min.y = Swift.min(min.y, point.y)
                        min.z = Swift.min(min.z, point.z)
                        
                        max.x = Swift.max(max.x, point.x)
                        max.y = Swift.max(max.y, point.y)
                        max.z = Swift.max(max.z, point.z)
                    }
                    
                    let bbox = node.boundingBox
                    let width = bbox.max.x - bbox.min.x
                    let height = bbox.max.y - bbox.min.y
                    let depth = bbox.max.z - bbox.min.z
                    expect(width).to(beCloseTo(max.x - min.x))
                    expect(height).to(beCloseTo(max.y - min.y))
                    expect(depth).to(beCloseTo(max.z - min.z))
                    expect((node.geometry!.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(referenceColor))
                }
            }
            
            context("createSegmentNode") {
                it("should create segment node") {
                    let referenceVertices: [SCNVector3] = [
                        SCNVector3(0.1, 0.2, 0.3),
                        SCNVector3(0.3, 0.4, 0.5),
                        SCNVector3(-0.5, -0.9, 0.2),
                        SCNVector3(0.5, 1.3, -0.1),
                    ]
                    let referenceLineColor = UIColor.cyan
                    let referenceCornerColor = UIColor.red
                    let node = NodesFactory.createSegmentNode(vertices: referenceVertices, lineColor: referenceLineColor, cornerColor: referenceCornerColor)
                    expect(node.childNodes.count).to(equal(referenceVertices.count))
                    expect((node.geometry?.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(referenceLineColor))
                }
            }
            
            context("createWireBoxNode") {
                let validateWireBoxNode: (_ node: SCNNode, _ size: SCNVector3, _ color: UIColor) -> (Void) = { node, size, color in
                    let bbox = node.boundingBox
                    let width = bbox.max.x - bbox.min.x
                    let height = bbox.max.y - bbox.min.y
                    let depth = bbox.max.z - bbox.min.z
                    expect(width).to(beCloseTo(size.x))
                    expect(height).to(beCloseTo(size.y))
                    expect(depth).to(beCloseTo(size.z))
                    expect((node.geometry!.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(color))
                }
                
                it("should create wire box node (size)") {
                    let referenceSize = SCNVector3(0.51, 0.62, 0.73)
                    let referenceColor = UIColor.orange
                    let node = NodesFactory.createWireBoxNode(size: referenceSize, color: referenceColor)
                    validateWireBoxNode(node, referenceSize, referenceColor)
                }
                
                it("should create wire box node (width, height, depth)") {
                    let referenceSize = SCNVector3(0.51, 0.62, 0.73)
                    let referenceColor = UIColor.purple
                    let node = NodesFactory.createWireBoxNode(width: CGFloat(referenceSize.x), height: CGFloat(referenceSize.y), depth: CGFloat(referenceSize.z), color: referenceColor)
                    validateWireBoxNode(node, referenceSize, referenceColor)
                }
            }
            
            context("createSphereNode") {
                it("should create sphere node") {
                    let referenceRadius: CGFloat = 0.73
                    let referenceSegmentCount = 12
                    let referenceColor = UIColor.brown
                    let node = NodesFactory.createSphereNode(radius: referenceRadius, segmentCount: referenceSegmentCount, color: referenceColor)
                    
                    let geometry = node.geometry as! SCNSphere
                    expect(geometry.radius).to(beCloseTo(referenceRadius))
                    expect(geometry.segmentCount).to(equal(referenceSegmentCount))
                    expect((geometry.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(referenceColor))
                }
            }
        }
    }
}
