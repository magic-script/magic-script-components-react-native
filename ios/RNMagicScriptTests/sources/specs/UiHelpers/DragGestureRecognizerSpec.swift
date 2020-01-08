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
import SceneKit
@testable import RNMagicScriptHostApplication

class DragGestureRecognizerSpec: QuickSpec {
    override func spec() {
        describe("DragGestureRecognizer") {
            var nodeSelectorMock: NodeSelectingMock!
            var rayBuilderMock: RayBuildingMock!
            var sut: DragGestureRecognizer!

            beforeEach {
                nodeSelectorMock = NodeSelectingMock()
                rayBuilderMock = RayBuildingMock()
                sut = DragGestureRecognizer(nodeSelector: nodeSelectorMock, rayBuilder: rayBuilderMock, target: nil, action: nil)
                sut.getCameraNode = {
                    return SCNNode()
                }
            }

            let setInitialState = {
                let draggedNode = DraggableNode()
                let customRay = CustomizableRay(begin: SCNVector3(), direction: SCNVector3(), length: 1.0)
                customRay.closestPoint = SCNVector3(0.125, 0.125, 0.125)
                draggedNode.dragAxis = customRay
                draggedNode.dragRange = 0.75
                draggedNode.dragValue = 0.125
                nodeSelectorMock.given(.draggingHitTest(ray: .any, willReturn: draggedNode))
                let ray = Ray(begin: SCNVector3(), direction: SCNVector3(), length: 1.0)
                rayBuilderMock.given(.build(gesture: .any, cameraNode: .any, willReturn: ray))
                sut.touchesBegan([UITouch()], with: UIEvent())
                expect(sut.dragAxis).to(equal(customRay))
            }


            context("when initialized") {
                it("gesture should be 'possible'") {
                    expect(sut.state).to(equal(.possible))
                }
            }

            context("always") {
                context("when reset called") {
                    it("should reset initial state") {
                        setInitialState()

                        sut.reset()
                        expect(sut.dragAxis).to(beNil())
                        expect(sut.beginDragValue).to(beCloseTo(0.0))
                        expect(sut.dragDelta).to(beCloseTo(0.0))
                        expect(sut.state).to(equal(.began))
                    }
                }
            }

            context("when touchesBegan") {
                context("when no touches") {
                    it("should set gesture as failed") {
                        sut.touchesBegan([], with: UIEvent())
                        expect(sut.state).to(equal(.failed))
                    }
                }

                context("when correct touch") {
                    it("should store dragged & touch location") {
                        setInitialState()
                    }
                }

                context("when no node tapped") {
                    it("should set gesture as failed") {
                        nodeSelectorMock.given(.hitTest(ray: .any, willReturn: nil))
                        let ray = Ray(begin: SCNVector3(), direction: SCNVector3(), length: 1.0)
                        rayBuilderMock.given(.build(gesture: .any, cameraNode: .any, willReturn: ray))
                        sut.touchesBegan([UITouch()], with: UIEvent())
                        expect(sut.state).to(equal(.failed))
                    }
                }
            }

            context("when touchesMoved") {
                it("should continue gesture") {
                    setInitialState()
                    let movedTouchPoint = CustomUITouch()
                    movedTouchPoint.gestureLocation = CGPoint(x: 0.125, y: 0.125)
                    sut.touchesMoved([movedTouchPoint], with: UIEvent())
                    expect(sut.state).to(equal(.changed))
                }

                context("when no gesture started") {
                    it("should set gesture as failed") {
                        sut.touchesMoved([UITouch()], with: UIEvent())
                        expect(sut.state).to(equal(.began))
                    }
                }
            }

            context("when touchesEnded") {
                context("when gesture started") {
                    it("should set gesture as failed") {
                        sut.state = .changed

                        sut.touchesEnded([UITouch()], with: UIEvent())
                        expect(sut.state).to(equal(.ended))
                    }
                }

                context("when gesture not started") {
                    it("should set gesture as ended") {
                        sut.state = .possible

                        sut.touchesEnded([UITouch()], with: UIEvent())
                        expect(sut.state).to(equal(.ended))
                    }
                }
            }

            context("when touchesCancelled") {
                it("should set gesture as failed") {
                    sut.touchesCancelled([UITouch()], with: UIEvent())
                    expect(sut.state).to(equal(.failed))
                }
            }
        }
    }
}


fileprivate class CustomizableRay: Ray {
    var closestPoint: SCNVector3?

    override func getClosestPointTo(ray: Ray) -> SCNVector3? {
        return closestPoint
    }
}

fileprivate class DraggableNode: TransformNode, Dragging {
    var dragAxis: Ray?
    var dragRange: CGFloat = 0.0
    var dragValue: CGFloat = 0.0
}
