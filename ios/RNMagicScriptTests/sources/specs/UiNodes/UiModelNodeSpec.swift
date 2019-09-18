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
import SwiftyMocky
@testable import RNMagicScriptHostApplication

import SceneKit

class UiModelNodeSpec: QuickSpec {
    override func spec() {
        describe("ModelNode") {

            var node: UiModelNode!
            let glbModelPath = "resources/assets/models/glb/box.glb"
            let glbAnimatedModelPath = "resources/assets/models/glb/hedra_anim.glb"
            let gltfModelPath = "resources/assets/models/glb/hedra_05.gltf"
            let objModelPath = "resources/assets/models/obj/hedra.obj"
            var downloaderMock: DownloadingMock!
            var sceneBuilderMock: GLTFSceneSourceBuilderingMock!
            var sceneMock: GLTFSceneSourceProtocolMock!
            let scene = SCNScene()

            beforeEach {
                downloaderMock = DownloadingMock()
                sceneBuilderMock = GLTFSceneSourceBuilderingMock()
                sceneMock = GLTFSceneSourceProtocolMock()
                node = UiModelNode(props: [:])
                node.downloader = downloaderMock
                node.sceneBuilder = sceneBuilderMock
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.url).to(beNil())
                }
            }

            func prepareMocksExpectations() {
                downloaderMock.perform(.downloadModel(modelURL: .any, completion: .any, perform: { (inputURL, completion) in
                    completion(inputURL)
                }))
                sceneBuilderMock.given(.build(path: .any, options: .any, extensions: .any, willReturn: sceneMock))
                sceneMock.given(.scene(options: .any, willReturn: scene))
            }

            func validateInitialExpectation(_ url: URL?, _ node: UiModelNode) {
                expect(url).notTo(beNil())
                expect(node.url).to(beNil())
            }

            func validatePostExpectation(_ node: UiModelNode) {
                expect(node.isLoaded).to(beTrue())
                expect(node.url).notTo(beNil())
            }

            context("base class") {
                it("should inherit from RenderNode class") {
                    let isRenderNode: Bool = (node as Any) is RenderNode
                    expect(isRenderNode).to(beTrue())
                }
            }

            context("load model") {
                it("should load .glb model") {
                    let url: URL? = urlForRelativePath(glbModelPath)
                    validateInitialExpectation(url, node)

                    prepareMocksExpectations()
                    node.url = url
                    validatePostExpectation(node)
                    sceneBuilderMock.verify(.build(path: .any, options: .any, extensions: .any))
                }

                it("should load .glb model with animation") {
                    let url: URL? = urlForRelativePath(glbAnimatedModelPath)
                    validateInitialExpectation(url, node)

                    prepareMocksExpectations()
                    node.url = url

                    validatePostExpectation(node)
                    sceneBuilderMock.verify(.build(path: .any, options: .any, extensions: .any))
                }

                // TODO: gltf does not load properly, needs further investigation
                it("should load .gltf model") {
                    let url: URL? = urlForRelativePath(gltfModelPath)
                    validateInitialExpectation(url, node)

                    prepareMocksExpectations()
                    node.url = url

                    validatePostExpectation(node)
                    sceneBuilderMock.verify(.build(path: .any, options: .any, extensions: .any))
                }

                it("should load .obj model") {
                    let url: URL? = urlForRelativePath(objModelPath)
                    validateInitialExpectation(url, node)

                    downloaderMock.perform(.downloadModel(modelURL: .any, completion: .any, perform: { (inputURL, completion) in
                        completion(inputURL)
                    }))

                    node.url = url
                    validatePostExpectation(node)
                }
            }
        }
    }
}
