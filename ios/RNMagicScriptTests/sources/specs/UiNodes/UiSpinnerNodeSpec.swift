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
        describe("UiSpinnerNode") {
            var node: UiSpinnerNode!
            
            beforeEach {
                node = UiSpinnerNode()
                node.layoutIfNeeded()
            }
            
            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.centerCenter))
                    expect(node.height).to(beCloseTo(0.0))
                    expect(node.size).to(beCloseTo(CGSize.zero))
                    expect(node.value).to(beCloseTo(0.0))
                    expect(node.determinate).to(equal(false))
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

                it("should update 'height' prop") {
                    let referenceHeight = 0.6
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.getSize()).to(beCloseTo(CGSize(width: referenceHeight, height: referenceHeight)))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                
                it("should update 'size' prop") {
                    let referenceSize = CGSize(width: 0.5, height: 0.6)
                    node.update(["size" : referenceSize.toArrayOfFloat])
                    expect(node.size).to(beCloseTo(referenceSize))
                    expect(node.getSize()).to(beCloseTo(referenceSize))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'value' prop") {
                    let referenceValue1: CGFloat = 0.75
                    let referenceValue2: CGFloat = 1.25
                    let referenceValue3: CGFloat = -0.25
                    node.update(["value" : referenceValue1])
                    expect(node.value).to(beCloseTo(referenceValue1))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()

                    node.value = referenceValue2
                    expect(node.value).to(beCloseTo(1.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                    node.layoutIfNeeded()

                    node.value = referenceValue3
                    expect(node.value).to(beCloseTo(0.0))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'determinate' prop") {
                    let referenceDeterminate = true
                    node.update(["determinate" : referenceDeterminate])
                    expect(node.determinate).to(equal(referenceDeterminate))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }
        }
    }
}
