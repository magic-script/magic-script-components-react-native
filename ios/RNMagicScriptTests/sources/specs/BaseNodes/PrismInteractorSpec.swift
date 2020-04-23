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
import SwiftyMocky

import SceneKit

class PrismInteractorSpec: QuickSpec {
    override func spec() {
        describe("PrismInteractor") {
            var gesturableMock: GestureManagingMock!
            var prismInteractor: PrismInteractor!
            var panGestureRecognizerMock: PanGestureRecognizingMock!
            var pinchGestureRecognizerMock: PinchGestureRecognizingMock!
            var rotationGestureRecognizerMock: RotationGestureRecognizingMock!
            var gestureRecognizerMocks: [GestureRecognizing]!
            var relatedPrism: Prism!

            beforeEach {
                prismInteractor = PrismInteractor()
                gesturableMock = GestureManagingMock()
                prismInteractor.gesturable = gesturableMock
                gestureRecognizerMocks = [GestureRecognizingMock]()
                panGestureRecognizerMock = PanGestureRecognizingMock()
                pinchGestureRecognizerMock = PinchGestureRecognizingMock()
                rotationGestureRecognizerMock = RotationGestureRecognizingMock()
                gestureRecognizerMocks.append(panGestureRecognizerMock)
                gestureRecognizerMocks.append(pinchGestureRecognizerMock)
                gestureRecognizerMocks.append(rotationGestureRecognizerMock)
                prismInteractor.gestureRecognizers = gestureRecognizerMocks
                relatedPrism = Prism()
            }

            context("when asked for toggle interaction") {
                it("should toggle it based on editMode") {
                    relatedPrism.editMode = true
                    prismInteractor.interactedPrism = relatedPrism
                    prismInteractor.toggleInteractions(for: relatedPrism)
                    expect(relatedPrism.editMode).to(beFalse())

                    prismInteractor.toggleInteractions(for: relatedPrism)
                    expect(relatedPrism.editMode).to(beTrue())

                    gestureRecognizerMocks.forEach { _ in
                        gesturableMock.verify(.removeGestureRecognizer(.any))
                        gesturableMock.verify(.addGestureRecognizer(.any))
                    }
                }
            }

            context("when asked to start interaction") {
                it("should set editMode to true & attach gestures") {
                    relatedPrism.editMode = false
                    prismInteractor.interactedPrism = relatedPrism
                    prismInteractor.startInteractions(for: relatedPrism)
                    expect(relatedPrism.editMode).to(beTrue())
                    gestureRecognizerMocks.forEach { _ in
                        gesturableMock.verify(.addGestureRecognizer(.any))
                    }
                }
            }

            context("when asked to stop interaction") {
                it("should set editMode to false & detach gestures") {
                    relatedPrism.editMode = true
                    prismInteractor.interactedPrism = relatedPrism
                    gesturableMock.given(.recognizers(getter: gestureRecognizerMocks))
                    prismInteractor.stopInteractions(for: relatedPrism)
                    expect(relatedPrism.editMode).to(beFalse())
                    gestureRecognizerMocks.forEach { _ in
                        gesturableMock.verify(.removeGestureRecognizer(.any))
                    }
                }
            }

            context("when receiving update") {
                context("when no camera") {
                    it("should skip update") {
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
                        relatedPrism.editMode = false
                        prismInteractor.interactedPrism = relatedPrism
                        let cameraNode = SCNNode()
                        cameraNode.camera = SCNCamera()
                        prismInteractor.update(cameraNode: cameraNode, time: TimeInterval())
                    }
                }

                context("when elapsed time is more than 0.1 seconds") {
                    it("should skip update") {
                        relatedPrism.editMode = true
                        prismInteractor.interactedPrism = relatedPrism
                        let cameraNode = SCNNode()
                        cameraNode.camera = SCNCamera()
                        prismInteractor.update(cameraNode: cameraNode, time: 0.15)
                    }
                }

                context("when elapsed time is less than 0.1 seconds") {
                    it("shouldn't skip update") {
                        relatedPrism.editMode = true
                        prismInteractor.interactedPrism = relatedPrism
                        let cameraNode = SCNNode()
                        cameraNode.position = SCNVector3.zero
                        cameraNode.camera = SCNCamera()
                        prismInteractor.update(cameraNode: cameraNode, time: 0.05)

                        expect(relatedPrism.position).to(beCloseTo(SCNVector3()))
                    }
                }

                context("when handling pan gesture") {
                    context("when gesture began or changed") {
                        it("should calculate distance change") {
                            for state in [UIGestureRecognizer.State.began, UIGestureRecognizer.State.changed] {
                                panGestureRecognizerMock.given(.state(getter: state))
                                panGestureRecognizerMock.given(.velocity(in: .any, willReturn: CGPoint(x: -10.0, y: -10.0)))
                                let view = UIView(frame: CGRect(x: 0.0, y: 0.0, width: 10.0, height: 10.0))
                                panGestureRecognizerMock.given(.view(getter: view))

                                prismInteractor.handlePanGesture(panGestureRecognizerMock)
                                expect(prismInteractor.prismDistanceChange).to(beCloseTo(40.0))
                            }
                        }
                    }

                    context("when gesture cancelled, ended or failed") {
                        it("should reset distance change") {
                            for state in [UIGestureRecognizer.State.cancelled, UIGestureRecognizer.State.ended, UIGestureRecognizer.State.failed] {
                                panGestureRecognizerMock.given(.state(getter: state))

                                prismInteractor.handlePanGesture(panGestureRecognizerMock)
                                expect(prismInteractor.prismDistanceChange).to(beCloseTo(0.0))
                            }
                        }
                    }

                    context("when other state") {
                        it("should do nothing") {
                            panGestureRecognizerMock.given(.state(getter: .possible))
                            expect(prismInteractor.prismDistanceChange).to(beCloseTo(0.0))
                            prismInteractor.handlePanGesture(panGestureRecognizerMock)
                            expect(prismInteractor.prismDistanceChange).to(beCloseTo(0.0))
                        }
                    }
                }

                context("when handling pan gesture") {
                    context("when gesture began or changed") {
                        it("should calculate rotation change") {
                            for state in [UIGestureRecognizer.State.began, UIGestureRecognizer.State.changed] {
                                rotationGestureRecognizerMock.given(.state(getter: state))
                                rotationGestureRecognizerMock.given(.rotation(getter: 10.0))

                                prismInteractor.handleRotationGesture(rotationGestureRecognizerMock)
                                expect(prismInteractor.prismYawChange).to(beCloseTo(10.0))
                            }
                        }
                    }

                    context("when gesture cancelled, ended or failed") {
                        it("should reset rotation change (calculate final rotation value)") {
                            for state in [UIGestureRecognizer.State.cancelled, UIGestureRecognizer.State.ended, UIGestureRecognizer.State.failed] {
                                rotationGestureRecognizerMock.given(.state(getter: state))
                                rotationGestureRecognizerMock.given(.rotation(getter: 0.0))

                                prismInteractor.handleRotationGesture(rotationGestureRecognizerMock)
                                expect(prismInteractor.prismYawChange).to(beCloseTo(0.0))
                                expect(prismInteractor.startDifferenceYaw).to(beCloseTo(00.0))
                            }
                        }

                        it("should reset rotation change (calculate final rotation value)") {
                            rotationGestureRecognizerMock.given(.state(getter: .began))
                            rotationGestureRecognizerMock.given(.rotation(getter: 10.0))

                            prismInteractor.handleRotationGesture(rotationGestureRecognizerMock)
                            expect(prismInteractor.prismYawChange).to(beCloseTo(10.0))

                            rotationGestureRecognizerMock.given(.state(getter: .ended))
                            rotationGestureRecognizerMock.given(.rotation(getter: 10.0))

                            prismInteractor.handleRotationGesture(rotationGestureRecognizerMock)
                            expect(prismInteractor.startDifferenceYaw).to(beCloseTo(10.0))
                        }
                    }

                    context("when other state") {
                        it("should do nothing") {
                            rotationGestureRecognizerMock.given(.state(getter: .possible))
                            expect(prismInteractor.prismYawChange).to(beCloseTo(0.0))
                            prismInteractor.handleRotationGesture(rotationGestureRecognizerMock)
                            expect(prismInteractor.prismYawChange).to(beCloseTo(0.0))
                        }
                    }
                }

                context("when handling pinch gesture") {
                    context("when gesture began") {
                        it("should store initial scale") {
                            let scale = SCNVector3(1.23, 4.56, 7.89)
                            relatedPrism.scale = scale
                            prismInteractor.interactedPrism = relatedPrism
                            pinchGestureRecognizerMock.given(.state(getter: .began))
                            prismInteractor.handlePinchGesture(pinchGestureRecognizerMock)
                            expect(prismInteractor.prismInitialScale).to(beCloseTo(scale))
                        }
                    }

                    context("when gesture changed") {
                        it("should update prism scale according to gesture") {
                            relatedPrism.size = SCNVector3(1.0, 1.0, 1.0)
                            relatedPrism.scale = SCNVector3(1.0, 1.0, 1.0)
                            prismInteractor.interactedPrism = relatedPrism
                            // preparation
                            pinchGestureRecognizerMock.given(.state(getter: .began))
                            prismInteractor.handlePinchGesture(pinchGestureRecognizerMock)

                            pinchGestureRecognizerMock.given(.state(getter: .changed))
                            pinchGestureRecognizerMock.given(.scale(getter: 1.23))
                            prismInteractor.handlePinchGesture(pinchGestureRecognizerMock)

                            let expectedScale = SCNVector3(1.23, 1.23, 1.23)
                            expect(relatedPrism.scale).to(beCloseTo(expectedScale))
                        }

                        context("when updating scale") {
                            it("should consider min/max real size") {
                                relatedPrism.size = SCNVector3(1.0, 1.0, 1.0)
                                relatedPrism.scale = SCNVector3(1.0, 1.0, 1.0)
                                prismInteractor.interactedPrism = relatedPrism
                                // preparation
                                pinchGestureRecognizerMock.given(.state(getter: .began))
                                prismInteractor.handlePinchGesture(pinchGestureRecognizerMock)

                                // too big scale - should set max real size
                                pinchGestureRecognizerMock.given(.state(getter: .changed))
                                pinchGestureRecognizerMock.given(.scale(getter: 2.25))
                                prismInteractor.handlePinchGesture(pinchGestureRecognizerMock)

                                var expectedScale = SCNVector3(2.0, 2.0, 2.0)
                                expect(relatedPrism.scale).to(beCloseTo(expectedScale))

                                // too small scale - should set max real size
                                pinchGestureRecognizerMock.given(.state(getter: .changed))
                                pinchGestureRecognizerMock.given(.scale(getter: 0.15))
                                prismInteractor.handlePinchGesture(pinchGestureRecognizerMock)

                                expectedScale = SCNVector3(0.3, 0.3, 0.3)
                                expect(relatedPrism.scale).to(beCloseTo(expectedScale))

                            }
                        }
                    }

                    context("when gesture cancelled, ended or failed") {
                        it("should reset initial scale") {
                            for state in [UIGestureRecognizer.State.cancelled, UIGestureRecognizer.State.ended, UIGestureRecognizer.State.failed] {
                                relatedPrism.scale = SCNVector3(1.0, 1.0, 1.0)
                                prismInteractor.interactedPrism = relatedPrism
                                // preparation
                                pinchGestureRecognizerMock.given(.state(getter: .began))
                                prismInteractor.handlePinchGesture(pinchGestureRecognizerMock)


                                pinchGestureRecognizerMock.given(.state(getter: state))
                                prismInteractor.handlePinchGesture(pinchGestureRecognizerMock)
                                expect(prismInteractor.prismInitialScale).to(beNil())
                            }
                        }
                    }
                }
            }
        }
    }
}
