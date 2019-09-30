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

class UiImageNodeSpec: QuickSpec {
    override func spec() {
        describe("UiImageNode") {
            var node: UiImageNode!

            beforeEach {
                node = UiImageNode(props: [:])
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.url).to(beNil())
                    expect(node.image).to(beNil())
                    expect(node.width).to(beCloseTo(0.5))
                    expect(node.height).to(beCloseTo(0.5))
                    expect(node.useFrame).to(beFalse())
                    expect(node.color).to(beNil())
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
                    let referenceUseFrame = true
                    node.update(["useFrame" : referenceUseFrame])
                    expect(node.useFrame).to(beTrue())
                    expect(node.isLayoutNeeded).to(beTrue())
                }

                it("should update 'color' prop") {
                    let referenceIconColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["color" : referenceIconColor.toArrayOfFloat])
                    expect(node.color).to(beCloseTo(referenceIconColor))
                    expect(node.isLayoutNeeded).to(beTrue())
                }

            }
        }
    }
}
