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
@testable import RNMagicScriptHostApplication

import SceneKit

class PrismInteractorSpec: QuickSpec {
    override func spec() {
        describe("PrismInteractor") {
            var arView: RCTARView!
            var prismInteractor: PrismInteractor!

            beforeEach {
                arView = RCTARView()
                prismInteractor = PrismInteractor(with: arView)
            }

            context("when asked for toggle interaction") {
                it("should toggle it based on editMode") {
                    let relatedPrism = SimplePrims()
                    relatedPrism.editMode = true
                    prismInteractor.interactedPrism = relatedPrism
                    prismInteractor.toggleInteractions(for: relatedPrism)
                    expect(relatedPrism.editMode).to(beFalse())
                    expect(arView.gestureRecognizers?.count).to(equal(4)) // default gestures set by RCTARView

                    prismInteractor.toggleInteractions(for: relatedPrism)
                    expect(relatedPrism.editMode).to(beTrue())
                    expect(arView.gestureRecognizers?.count).to(equal(7)) // default gestures set by RCTARView + gestures for Prism interaction
                }
            }

            context("when asked to start interaction") {
                it("should set editMode to true & attach gestures") {
                    let relatedPrism = SimplePrims()
                    relatedPrism.editMode = false
                    prismInteractor.interactedPrism = relatedPrism
                    prismInteractor.startInteractions(for: relatedPrism)
                    expect(relatedPrism.editMode).to(beTrue())
                    expect(arView.gestureRecognizers?.count).to(equal(7)) // default gestures set by RCTARView + gestures for Prism interaction
                }
            }

            context("when asked to stop interaction") {
                it("should set editMode to false & detach gestures") {
                    let relatedPrism = SimplePrims()
                    relatedPrism.editMode = true
                    prismInteractor.interactedPrism = relatedPrism
                    prismInteractor.stopInteractions(for: relatedPrism)
                    expect(relatedPrism.editMode).to(beFalse())
                    expect(arView.gestureRecognizers?.count).to(equal(4)) // default gestures set by RCTARView
                }
            }

            context("when receiving update") {
                context("when no camera") {
                    it("should skip update") {
                        let relatedPrism = SimplePrims()
                        prismInteractor.interactedPrism = relatedPrism
                        prismInteractor.update(cameraNode: SCNNode(), time: TimeInterval())
                    }
                }

                context("when no prism") {
                    it("should skip update") {
                        let cameraNode = SCNNode()
                        cameraNode.camera = SCNCamera()
                        prismInteractor.update(cameraNode: SCNNode(), time: TimeInterval())
                    }
                }

                context("when isn't in editMode") {
                    it("should skip update") {
                        let relatedPrism = SimplePrims()
                        relatedPrism.editMode = false
                        prismInteractor.interactedPrism = relatedPrism
                        let cameraNode = SCNNode()
                        cameraNode.camera = SCNCamera()
                        prismInteractor.update(cameraNode: cameraNode, time: TimeInterval())
                    }
                }

                context("when elapsed time is more than 0.1 seconds") {
                    it("should skip update") {
                        let relatedPrism = SimplePrims()
                        relatedPrism.editMode = true
                        prismInteractor.interactedPrism = relatedPrism
                        let cameraNode = SCNNode()
                        cameraNode.camera = SCNCamera()
                        prismInteractor.update(cameraNode: cameraNode, time: 0.15)
                    }
                }

                context("when elapsed time is less than 0.1 seconds") {
                    it("shouldn't skip update") {
                        let relatedPrism = SimplePrims()
                        relatedPrism.editMode = true
                        prismInteractor.interactedPrism = relatedPrism
                        let cameraNode = SCNNode()
                        cameraNode.position = SCNVector3.zero
                        cameraNode.camera = SCNCamera()
                        prismInteractor.update(cameraNode: cameraNode, time: 0.05)

                        expect(relatedPrism.position).to(beCloseTo(SCNVector3()))
                    }
                }
            }
        }
    }
}

private class SimplePrims: Prism { }
