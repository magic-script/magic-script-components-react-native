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

class SCNNodeClippingPlaneSpec: QuickSpec {
    override func spec() {
        describe("SCNNode+ClippingPlane") {
            context("shaders source code") {
                it("should load shaders source code") {
                    let bundle = Bundle.resourcesBundle
                    expect(bundle).notTo(beNil())

                    let geometryURL = bundle!.url(forResource: "ClippingPlane.geometry", withExtension: "txt")!
                    let geometryModifier: String! = try? String(contentsOf: geometryURL)
                    expect(geometryModifier).notTo(beNil())
                    expect(geometryModifier.isEmpty).to(beFalse())

                    let fragmentURL = bundle!.url(forResource: "ClippingPlane.fragment", withExtension: "txt")!
                    let fragmentModifier: String! = try? String(contentsOf: fragmentURL)
                    expect(fragmentModifier).notTo(beNil())
                    expect(fragmentModifier.isEmpty).to(beFalse())
                }
            }
            
            context("applyClippingPlanesShaderModifiers") {
                it("should apply shader modifiers for given node only") {
                    let referenceNode = self.createSampleHierarchy()
                    expect(referenceNode.geometry!.shaderModifiers).to(beNil())
                    referenceNode.enumerateChildNodes { (node, result) in
                        expect(node.geometry!.shaderModifiers).to(beNil())
                    }
                    
                    referenceNode.applyClippingPlanesShaderModifiers()
                    
                    expect(referenceNode.geometry!.shaderModifiers).notTo(beNil())
                    referenceNode.enumerateChildNodes { (node, result) in
                        expect(node.geometry!.shaderModifiers).to(beNil())
                    }
                }
                
                it("should apply shader modifiers for given node and all child nodes") {
                    let referenceNode = self.createSampleHierarchy()
                    expect(referenceNode.geometry!.shaderModifiers).to(beNil())
                    referenceNode.enumerateChildNodes { (node, result) in
                        expect(node.geometry!.shaderModifiers).to(beNil())
                    }
                    
                    referenceNode.applyClippingPlanesShaderModifiers(recursive: true)
                    
                    expect(referenceNode.geometry!.shaderModifiers).notTo(beNil())
                    referenceNode.enumerateChildNodes { (node, result) in
                        expect(node.geometry!.shaderModifiers).notTo(beNil())
                    }
                }
            }
            
            context("setClippingPlanes") {
                it("should set clipping planes to node") {
                    let referenceNode = SCNNode(geometry: SCNSphere(radius: 0.2))
                    let referenceChildNode = SCNNode(geometry: SCNSphere(radius: 0.1))
                    referenceNode.addChildNode(referenceChildNode)
                    let referencePlanes = self.getSampleClippingPlanes(1.0)
                    
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (referenceNode.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).to(beNil())
                    }
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (referenceChildNode.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).to(beNil())
                    }
                    
                    referenceNode.setClippingPlanes(referencePlanes)
                    
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (referenceNode.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).notTo(beNil())
                        expect(clippingPlane).to(beCloseTo(referencePlanes[i]))
                    }
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (referenceChildNode.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).to(beNil())
                    }
                }
                
                it("should set clipping planes recursively") {
                    let referenceNode = SCNNode(geometry: SCNSphere(radius: 0.2))
                    let referenceChildNode = SCNNode(geometry: SCNSphere(radius: 0.1))
                    referenceNode.addChildNode(referenceChildNode)
                    let referencePlanes = self.getSampleClippingPlanes(1.0)
                    
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (referenceNode.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).to(beNil())
                    }
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (referenceChildNode.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).to(beNil())
                    }
                    
                    referenceNode.setClippingPlanes(referencePlanes, recursive: true)
                    
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (referenceNode.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).notTo(beNil())
                        expect(clippingPlane).to(beCloseTo(referencePlanes[i]))
                    }
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (referenceChildNode.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).notTo(beNil())
                        expect(clippingPlane).to(beCloseTo(referencePlanes[i]))
                    }
                }
                
                it("should narrow the clipping bounds if there is scrollView in the hierarchy") {
                    let result = self.createSamplePrism()
                    let prism = result.prism
                    let text = result.text
                    let scrollView = result.scrollView
                    let images = result.images
                    
                    prism.invalidateClipping()
                    prism.updateClipping()
                    
                    let prismPlanes = prism.getClippingPlanesAsVector4()
                    let scrollViewPlanes = scrollView.getClippingPlanesAsVector4()
                    
                    // text should be clipped by prism's bounds
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (text.contentNode.childNodes[0].childNodes[0].geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).notTo(beNil())
                        expect(clippingPlane).to(beCloseTo(prismPlanes[i]))
                        expect(clippingPlane).notTo(beCloseTo(scrollViewPlanes[i]))
                    }
                    
                    // images should be clipped by scrollView's bounds
                    for image in images {
                        for i in 0..<6 {
                            let clippingPlane: SCNVector4? = (image.contentNode.childNodes[0].geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                            expect(clippingPlane).notTo(beNil())
                            expect(clippingPlane).notTo(beCloseTo(prismPlanes[i]))
                            expect(clippingPlane).to(beCloseTo(scrollViewPlanes[i]))
                        }
                    }
                }
            }
            
            context("invalidateBoundsClippingManager") {
                it("should invalidate prism's clipping") {
                    let result = self.createSamplePrism()
                    let prism = result.prism
                    let image = result.images[0]
                    
                    expect(prism.isUpdateClippingNeeded).to(beTrue())
                    prism.updateClipping()
                    expect(prism.isUpdateClippingNeeded).to(beFalse())
                    image.invalidateBoundsClippingManager()
                    expect(prism.isUpdateClippingNeeded).to(beTrue())
                }
            }
            
            context("forceUpdateClipping") {
                it("should update clipping for given node") {
                    let result = self.createSamplePrism()
                    let prism = result.prism
                    let image = result.images[0]
                    let imageWithGeometry = image.contentNode.childNodes[0]
                    
                    expect(prism.isUpdateClippingNeeded).to(beTrue())
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (imageWithGeometry.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).to(beNil())
                    }
                    
                    imageWithGeometry.forceUpdateClipping()
                    
                    // Prism should still be invalidated
                    expect(prism.isUpdateClippingNeeded).to(beTrue())
                    // image clip bounds should be updated
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (imageWithGeometry.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).notTo(beNil())
                    }
                }
                
                it("should update clipping recursively") {
                    let result = self.createSamplePrism()
                    let prism = result.prism
                    let scrollView = result.scrollView
                    let image = result.images[0]
                    let imageWithGeometry = image.contentNode.childNodes[0]
                    
                    expect(prism.isUpdateClippingNeeded).to(beTrue())
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (imageWithGeometry.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).to(beNil())
                    }
                    
                    scrollView.forceUpdateClipping(recursive: false)
                    
                    expect(prism.isUpdateClippingNeeded).to(beTrue())
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (imageWithGeometry.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).to(beNil())
                    }
                    
                    scrollView.forceUpdateClipping(recursive: true)
                    
                    // Prism should still be invalidated
                    expect(prism.isUpdateClippingNeeded).to(beTrue())
                    // image clip bounds should be updated
                    for i in 0..<6 {
                        let clippingPlane: SCNVector4? = (imageWithGeometry.geometry!.value(forKey: "clippingPlane\(i + 1)") as? NSValue)?.scnVector4Value
                        expect(clippingPlane).notTo(beNil())
                    }
                }
            }
        }
    }
    
    func getSampleClippingPlanes(_ size: Float) -> [SCNVector4] {
        let min = SCNVector3(-0.5 * size, -0.5 * size, -0.5 * size)
        let max = SCNVector3(0.5 * size, 0.5 * size, 0.5 * size)
        let planes: [SCNVector4] = [
            SCNVector4( 1, 0, 0,-min.x),
            SCNVector4(-1, 0, 0, max.x),
            SCNVector4(0, 1, 0,-min.y),
            SCNVector4(0,-1, 0, max.y),
            SCNVector4(0, 0, 1,-min.z),
            SCNVector4(0, 0,-1, max.z),
        ]
        
        return planes
    }
    
    func createSampleHierarchy() -> SCNNode {
        let createNode: (_ radius: CGFloat, _ parent: SCNNode?) -> (SCNNode) = { radius, parent in
            let node = SCNNode(geometry: SCNSphere(radius: radius))
            parent?.addChildNode(node)
            return node
        }
        let rootNode = createNode(0.5, nil)
        
        let node1 = createNode(0.1, rootNode)
        let node2 = createNode(0.2, rootNode)
        
        let _ = createNode(0.11, node1)
        let _ = createNode(0.12, node1)
        let _ = createNode(0.13, node1)
        
        let _ = createNode(0.21, node2)
        let _ = createNode(0.22, node2)
        let _ = createNode(0.23, node2)
        
        return rootNode
    }
    
    func createSamplePrism() -> (prism: Prism, group: UiGroupNode, text: UiTextNode, scrollView: UiScrollViewNode, linearLayout: UiLinearLayoutNode, images: [UiImageNode]) {
        let linearLayout = UiLinearLayoutNode()
        var images: [UiImageNode] = []
        for i in 0..<10 {
            let image = UiImageNode()
            image.width = 0.2
            image.height = 0.2
            image.color = i % 2 == 0 ? UIColor.yellow : UIColor.purple
            linearLayout.addChild(image)
            images.append(image)
        }
        
        let scrollView = UiScrollViewNode()
        scrollView.scrollBounds = (min: SCNVector3(-0.2, -0.2, -0.1), max: SCNVector3(0.2, 0.2, 0.1))
        scrollView.addChild(linearLayout)
        
        let text = UiTextNode()
        text.text = "Lorem ipsum"
        
        let group = UiGroupNode()
        group.addChild(text)
        group.addChild(scrollView)
        
        let prism = Prism()
        prism.size = SCNVector3(2, 2, 2)
        let _ = prism.addNode(group)
        
        return (prism, group, text, scrollView, linearLayout, images)
    }
}
