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

class UiImageNodeSpec: QuickSpec {
    override func spec() {
        describe("UiImageNode") {
            var node: UiImageNode!

            beforeEach {
                node = UiImageNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.url).to(beNil())
                    expect(node.image).to(beNil())
                    expect(node.width).to(beCloseTo(0))
                    expect(node.height).to(beCloseTo(0))
                    expect(node.useFrame).to(beFalse())
                    expect(node.color).to(beNil())
                    expect(node.fit).to(equal(ImageFitMode.stretch))
                    expect(node.useDefaultIcon).to(beFalse())
                }
            }

            context("initialization") {
                it("should throw exception if 'setupNode' has been called more than once") {
                    expect(node.setupNode()).to(throwAssertion())
                }
            }

            context("update properties") {
                it("should update 'filePath' prop") {
                    let referenceFilePath = "file://file_path"
                    node.update(["filePath" : referenceFilePath])
                    expect(node.url).to(equal(URL(string: referenceFilePath)!))
                    expect(node.isLayoutNeeded).to(beFalse())
                    
                    node.url = nil
                    expect(node.url).to(beNil())
                }

                it("should update 'width' prop") {
                    let referenceWidth = 2.75
                    node.update(["width" : referenceWidth])
                    expect(node.width).to(beCloseTo(referenceWidth))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'height' prop") {
                    let referenceHeight = 3.85
                    node.update(["height" : referenceHeight])
                    expect(node.height).to(beCloseTo(referenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'useFrame' prop") {
                    node.update(["useFrame" : true])
                    expect(node.useFrame).to(beTrue())
                    expect(node.isLayoutNeeded).to(beTrue())
                    
                    node.update(["useFrame" : false])
                    expect(node.useFrame).to(beFalse())
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'color' prop") {
                    let referenceIconColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["color" : referenceIconColor.toArrayOfFloat])
                    expect(node.color).to(beCloseTo(referenceIconColor))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
                
                it("should update 'fit' prop") {
                    let referenceValues: [ImageFitMode] = [
                        ImageFitMode.stretch,
                        ImageFitMode.aspectFit,
                        ImageFitMode.aspectFill
                    ]
                    node.update(["icon": "close"])
                    referenceValues.forEach { mode in
                        node.update(["fit" : mode.rawValue])
                        expect(node.fit).to(equal(mode))
                        expect(node.isLayoutNeeded).to(beFalse())
                    }
                }
                
                it("should update 'useDefaultIcon' prop") {
                    let referenceValue = true
                    node.update(["useDefaultIcon" : referenceValue])
                    expect(node.useDefaultIcon).to(equal(referenceValue))
                    expect(node.isLayoutNeeded).to(beTrue())
                }
            }
        }
    }
}
