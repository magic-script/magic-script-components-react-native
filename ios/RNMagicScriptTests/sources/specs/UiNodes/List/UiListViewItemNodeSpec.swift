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

@testable import RNMagicScriptHostApplication

class UiListViewItemNodeSpec: QuickSpec {
    override func spec() {
        describe("UiListViewItemNode") {
            var node: UiListViewItemNode!
            
            beforeEach {
                node = UiListViewItemNode(props: [:])
                node.layoutIfNeeded()
            }
            
            context("initial properties") {
                it("should have set default values") {
                    let referenceBackgroundColor = UIColor.clear
                    expect(node.backgroundColor).to(equal(referenceBackgroundColor))
                    expect(node.childNode).to(beNil())
                }
            }
            
            context("update properties") {
                it("should update 'backgroundColor' prop") {
                    let referenceBackgroundColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["backgroundColor" : referenceBackgroundColor.toArrayOfFloat])
                    expect(node.backgroundColor).to(beCloseTo(referenceBackgroundColor))
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }
            
            context("when item added") {
                it("should store it internally") {
                    let referenceNode = UiButtonNode(props: [:])
                    node.addChild(referenceNode)
                    node.layoutIfNeeded()
                    
                    expect(node.childNode).to(equal(referenceNode))
                }
            }
            
            context("when item removed") {
                it("should remove it from the list node") {
                    let referenceNode = UiButtonNode(props: [:])
                    node.addChild(referenceNode)
                    node.layoutIfNeeded()
                    
                    expect(node.childNode).to(equal(referenceNode))
                    
                    node.removeChild(referenceNode)
                    expect(node.childNode).to(beNil())
                }
            }
            
            context("when asked for size") {
                it("should calculate it according to childNode size") {
                    let referenceNode = UiButtonNode(props: ["width": 1.0, "height": 1.0])
                    referenceNode.text = "Text"
                    referenceNode.layoutIfNeeded()
                    node.addChild(referenceNode)
                    node.layoutIfNeeded()
                    
                    expect(node.getSize()).to(beCloseTo(CGSize(width: 1.0, height: 1.0)))
                }
            }
        }
    }
}
