//
//  Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

class SceneSpec: QuickSpec {
    override func spec() {
        describe("Scene") {
            var scene: Scene!

            beforeEach {
                scene = Scene()
            }

            context("when initialized") {
                it("prisms container should be empty") {
                    expect(scene.prisms).toNot(beNil())
                    expect(scene.prisms.count).to(equal(0))
                }

                it("content node should be initialized") {
                    expect(scene.contentNode).toNot(beNil())
                    expect(scene.childNodes.count).to(equal(1))
                    expect(scene.childNodes.first).to(equal(scene.contentNode))
                }
            }

            context("when asked to add child node") {

            }

            context("when asked to remove child node") {
                context("") {

                }
            }
        }
    }
}
