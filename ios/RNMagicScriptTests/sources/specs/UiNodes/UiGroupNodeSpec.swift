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

class UiGroupNodeSpec: QuickSpec {
    override func spec() {
        describe("UiGroupNode") {
            var node: UiGroupNode!
            
            beforeEach {
                node = UiGroupNode(props: [:])
            }
            
            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                }
            }
            
            context("update properties") {
                it("should not update 'alignment' prop") {
                    let referenceAlignment = Alignment.bottomRight
                    node.update(["alignment" : referenceAlignment.rawValue])
                    expect(node.alignment).notTo(equal(referenceAlignment))
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }
            
            context("when asked for size") {
                it("should calculate it") {
                    let referenceNode1 = UiButtonNode(props: ["width": 1.0, "height": 1.0])
                    referenceNode1.text = "Text"
                    referenceNode1.layoutIfNeeded()
                    let referenceNode2 = UiButtonNode(props: ["width": 1.0, "height": 1.0])
                    referenceNode2.text = "Text"
                    referenceNode2.position = SCNVector3(0.5, 0.5, 0.0)
                    referenceNode2.layoutIfNeeded()
                    
                    node.contentNode.addChildNode(referenceNode1)
                    node.contentNode.addChildNode(referenceNode2)
                    node.layoutIfNeeded()
                    
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 1.5, height: 1.5)))
                }
                
                context("when no child nodes") {
                    it("should return zero") {
                        expect(node.getSize()).to(beCloseTo(CGSize.zero))
                    }
                }
            }
        }
    }
}
