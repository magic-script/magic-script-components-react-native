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

class PrismOutlineNodeSpec: QuickSpec {
    override func spec() {
        describe("PrismOutlineNode") {
            context("init") {
                it("should init") {
                    let node = PrismOutlineNode()
                    expect(node.childNodes.count).to(equal(2))
                    expect(node.childNodes[1].childNodes.count).to(equal(8))
                }
                
                it("should throw exception when trying to init with codec") {
                    expect { _ = PrismOutlineNode(coder: NSCoder()) }.to(throwAssertion())
                }
            }
            
            context("default values") {
                it("should have default values set") {
                    let node = PrismOutlineNode()
                    expect(node.size).to(beCloseTo(SCNVector3(1, 1, 1)))
                    expect(node.color).to(beCloseTo(UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 0.3)))
                }
            }
            
            context("properties") {
                it("should update size") {
                    let node = PrismOutlineNode()
                    let referenceSize = SCNVector3(0.3, 0.9, 1.7)
                    expect(node.size).notTo(beCloseTo(referenceSize))
                    
                    node.size = referenceSize
                    
                    expect(node.size).to(beCloseTo(referenceSize))
                    expect(node.childNodes[0].scale).to(beCloseTo(referenceSize))
                    expect(node.childNodes[1].scale).to(beCloseTo(referenceSize))
                    
                    let cornersNode = node.childNodes[1]
                    cornersNode.childNodes.forEach {
                        expect($0.scale).to(beCloseTo(SCNVector3(1, 1, 1) / referenceSize))
                    }
                }
                
                it("should update color") {
                    let node = PrismOutlineNode()
                    let referenceColor = UIColor.yellow
                    expect(node.color).notTo(beCloseTo(referenceColor))
                    
                    node.color = referenceColor
                    
                    expect(node.color).to(beCloseTo(referenceColor))
                    expect((node.childNodes[0].geometry?.firstMaterial?.diffuse.contents as! UIColor)).to(beCloseTo(referenceColor))
                }
                
                it("should update rendering order") {
                    let node = PrismOutlineNode()
                    let referenceRenderingOrder = 129
                    expect(node.renderingOrder).notTo(equal(referenceRenderingOrder))
                    
                    node.renderingOrder = referenceRenderingOrder
                    
                    expect(node.renderingOrder).to(equal(referenceRenderingOrder))
                    expect((node.childNodes[0].renderingOrder)).to(equal(referenceRenderingOrder))
                }
            }
        }
    }
}
