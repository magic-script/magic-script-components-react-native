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

import SceneKit

class UiSpinnerNodeSpec: QuickSpec {
    override func spec() {
        describe("UiProgressBarNode") {
            var node: UiSpinnerNode!
            
            beforeEach {
                node = UiSpinnerNode(props: [:])
                node.layoutIfNeeded()
            }
            
            context("initial properties") {
                it("should have set default values") {
                    expect(node.size).to(beCloseTo(CGSize.zero))
                    expect(node.value).to(beCloseTo(0.0))
                }

                it("should have set default size") {
                    expect(node.getSize()).to(beCloseTo(CGSize(width: UiSpinnerNode.defaultSize, height: UiSpinnerNode.defaultSize)))
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
                
                it("should update 'size' prop") {
                    let referenceSize = CGSize(width: 0.5, height: 0.6)
                    node.update(["size" : referenceSize.toArrayOfFloat])
                    expect(node.size).to(beCloseTo(referenceSize))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }
        }
    }
}
