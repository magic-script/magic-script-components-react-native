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

import Quick
import Nimble
import SceneKit
@testable import RNMagicScriptHostApplication

class NodesFactorySpec: QuickSpec {
    override func spec() {
        describe("NodesFactory") {
            context("createPlaneNode") {
                it("should create PlaneNode (size)") {
                    let referenceSize = CGSize(width: 0.3, height: 0.6)
                    let referenceColor = UIColor.red
                    let planeNode = NodesFactory.createPlaneNode(size: referenceSize, color: referenceColor)
                    let planeGeometry = planeNode.geometry as! SCNPlane
                    expect(planeGeometry.width).to(beCloseTo(referenceSize.width))
                    expect(planeGeometry.height).to(beCloseTo(referenceSize.height))
                    expect((planeGeometry.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(referenceColor))
                }
                
                it("should create PlaneNode (width, height)") {
                    let referenceSize = CGSize(width: 0.3, height: 0.6)
                    let referenceColor = UIColor.red
                    let planeNode = NodesFactory.createPlaneNode(width: referenceSize.width, height: referenceSize.height, color: referenceColor)
                    let planeGeometry = planeNode.geometry as! SCNPlane
                    expect(planeGeometry.width).to(beCloseTo(referenceSize.width))
                    expect(planeGeometry.height).to(beCloseTo(referenceSize.height))
                    expect((planeGeometry.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(referenceColor))
                }
            }
                        
            context("createNinePatchNode") {
                it("should create NinePatchNode (size)") {
                    
                }
                
                it("should create NinePatchNode (width, height)") {
                    
                }
            }
            
            context("createOutlineNode") {
                it("should create OutlineNode (size)") {
                    
                }
                
                it("should create OutlineNode (width, height)") {
                    
                }
            }
            
            context("createLinesNode") {
                it("should create LinesNode") {
                    
                }
            }
            
            context("createSegmentNode") {
                it("should create SegmentNode") {
                    
                }
            }
            
            context("createWireBoxNode") {
                it("should create WireBoxNode") {
                    
                }
            }
            
            context("createWireBoxNode") {
                it("should create WireBoxNode (size)") {
                    
                }
            }
            
            context("createSphereNode") {
                it("should create SphereNode (width, height, depth)") {
                    
                }
            }
            
        }
    }
}
