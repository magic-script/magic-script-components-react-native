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
import ARKit
import SwiftyMocky
@testable import RNMagicScriptHostApplication

class RCTARViewSpec: QuickSpec {
    override func spec() {
        describe("RCTARView") {
            var arView: RCTARView!

            context("always") {
                context("RCTARViewObservable") {
                    context("when registering observers") {
                        it("should store NodesManager as observer") {
                            let nodesManager = UiNodesManager.instance
                            arView = RCTARView()
                            expect(arView.observers.count).to(equal(2))
                            let observer = arView.observers[0].value
                            expect(observer).to(beIdenticalTo(nodesManager))
                        }

                        it("should store observers internally") {
                            arView = RCTARView()
                            let registeredObserverMock1 = RCTARViewObservingMock()
                            let registeredObserverMock2 = RCTARViewObservingMock()
                            arView.register(registeredObserverMock1)
                            expect(arView.observers.count).to(equal(3))
                            arView.register(registeredObserverMock2)
                            expect(arView.observers.count).to(equal(4))
                            let observer1 = arView.observers[2].value
                            expect(observer1).to(beIdenticalTo(registeredObserverMock1))
                            let observer2 = arView.observers[3].value
                            expect(observer2).to(beIdenticalTo(registeredObserverMock2))
                        }

                        it("should reference them with weak reference") {
                            arView = RCTARView()
                            let registeredObserverMock = RCTARViewObservingMock()
                            var referenceCounter = CFGetRetainCount(registeredObserverMock)
                            expect(referenceCounter).to(equal(2))
                            arView.register(registeredObserverMock)
                            referenceCounter = CFGetRetainCount(registeredObserverMock)
                            expect(referenceCounter).to(equal(2))
                        }
                    }

                    context("when registering observers") {
                        it("should manage internal store") {
                            arView = RCTARView()
                            let registeredObserverMock1 = RCTARViewObservingMock()
                            arView.register(registeredObserverMock1)
                            expect(arView.observers.count).to(equal(3))
                            arView.unregister(registeredObserverMock1)
                            expect(arView.observers.count).to(equal(2))
                        }
                    }
                }
            }


            // MARK: ARSCNViewDelegate
            context("ARSCNViewDelegate") {
                context("when receiving callback") {
                    context("should notify registered observers") {
                        let session = ARSession()
                        let error = SimpleError()
                        let renderer = SCNView()
                        let node = SCNNode()
                        let anchor = ARAnchor(name: "fakeAnchor", transform: simd_float4x4())
                        let time = TimeInterval()
                        let scene = SCNScene()

                        var registeredObserverMock1: RCTARViewObservingMock!
                        var registeredObserverMock2: RCTARViewObservingMock!
                        var unregisteredObserverMock: RCTARViewObservingMock!

                        beforeEach {
                            arView = RCTARView()
                            registeredObserverMock1 = RCTARViewObservingMock()
                            registeredObserverMock2 = RCTARViewObservingMock()
                            unregisteredObserverMock = RCTARViewObservingMock()

                            arView.register(registeredObserverMock1)
                            arView.register(registeredObserverMock2)
                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, didAdd node: SCNNode, for anchor: ARAnchor)") {
                            arView.renderer(renderer, didAdd: node, for: anchor)

                            registeredObserverMock1.verify(.renderer(.any, didAdd: .any, for: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, didAdd: .any, for: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, didAdd: .any, for: .any), count: 0)

                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, willUpdate node: SCNNode, for anchor: ARAnchor)") {
                            arView.renderer(renderer, willUpdate: node, for: anchor)

                            registeredObserverMock1.verify(.renderer(.any, willUpdate: .any, for: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, willUpdate: .any, for: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, willUpdate: .any, for: .any), count: 0)

                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, didUpdate node: SCNNode, for anchor: ARAnchor)") {
                            arView.renderer(renderer, didUpdate: node, for: anchor)

                            registeredObserverMock1.verify(.renderer(.any, didUpdate: .any, for: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, didUpdate: .any, for: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, didUpdate: .any, for: .any), count: 0)

                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, didRemove node: SCNNode, for anchor: ARAnchor)") {
                            arView.renderer(renderer, didRemove: node, for: anchor)

                            registeredObserverMock1.verify(.renderer(.any, didRemove: .any, for: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, didRemove: .any, for: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, didRemove: .any, for: .any), count: 0)

                        }

                        it("about session(_ session: ARSession, didFailWithError error: Error)") {
                            arView.session(session, didFailWithError: error)

                            registeredObserverMock1.verify(.session(.any, didFailWithError: .any), count: 1)
                            registeredObserverMock2.verify(.session(.any, didFailWithError: .any), count: 1)
                            unregisteredObserverMock.verify(.session(.any, didFailWithError: .any), count: 0)

                        }

                        it("about session(_ session: ARSession, cameraDidChangeTrackingState camera: ARCamera)") {
                            // there is no way to create ARCamera object to trigger this test
                        }

                        it("about sessionWasInterrupted(_ session: ARSession)") {
                            arView.sessionWasInterrupted(session)

                            registeredObserverMock1.verify(.sessionWasInterrupted(.any), count: 1)
                            registeredObserverMock2.verify(.sessionWasInterrupted(.any), count: 1)
                            unregisteredObserverMock.verify(.sessionWasInterrupted(.any), count: 0)

                        }

                        it("about sessionWasInterrupted(_ session: ARSession)") {
                            arView.sessionInterruptionEnded(session)

                            registeredObserverMock1.verify(.sessionInterruptionEnded(.any), count: 1)
                            registeredObserverMock2.verify(.sessionInterruptionEnded(.any), count: 1)
                            unregisteredObserverMock.verify(.sessionInterruptionEnded(.any), count: 0)

                        }

                        it("about session(_ session: ARSession, didOutputAudioSampleBuffer audioSampleBuffer: CMSampleBuffer)") {
                            // there is no way to create CMSampleBuffer object to trigger this test
                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval)") {
                            arView.renderer(renderer, updateAtTime: time)

                            registeredObserverMock1.verify(.renderer(.any, updateAtTime: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, updateAtTime: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, updateAtTime: .any), count: 0)
                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, didApplyAnimationsAtTime time: TimeInterval)") {
                            arView.renderer(renderer, didApplyAnimationsAtTime: time)

                            registeredObserverMock1.verify(.renderer(.any, didApplyAnimationsAtTime: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, didApplyAnimationsAtTime: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, didApplyAnimationsAtTime: .any), count: 0)
                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, didSimulatePhysicsAtTime time: TimeInterval)") {
                            arView.renderer(renderer, didSimulatePhysicsAtTime: time)

                            registeredObserverMock1.verify(.renderer(.any, didSimulatePhysicsAtTime: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, didSimulatePhysicsAtTime: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, didApplyAnimationsAtTime: .any), count: 0)
                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, willRenderScene scene: SCNScene, atTime time: TimeInterval)") {
                            arView.renderer(renderer, willRenderScene: scene, atTime: time)

                            registeredObserverMock1.verify(.renderer(.any, willRenderScene: .any, atTime: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, willRenderScene: .any, atTime: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, willRenderScene: .any, atTime: .any), count: 0)
                        }

                        it("about renderer(_ renderer: SCNSceneRenderer, didRenderScene scene: SCNScene, atTime time: TimeInterval)") {
                            arView.renderer(renderer, didRenderScene: scene, atTime: time)

                            registeredObserverMock1.verify(.renderer(.any, didRenderScene: .any, atTime: .any), count: 1)
                            registeredObserverMock2.verify(.renderer(.any, didRenderScene: .any, atTime: .any), count: 1)
                            unregisteredObserverMock.verify(.renderer(.any, didRenderScene: .any, atTime: .any), count: 0)
                        }
                    }
                }
            }
        }
    }
}

fileprivate class SimpleError: Error { }
