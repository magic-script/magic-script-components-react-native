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
import SwiftyMocky

class GestureHandlerSpec: QuickSpec {
    override func spec() {
        describe("GestureHandler") {
            var sut: GestureHandler!
            var nodesGestureHandlerMock: NodesGestureHandlingMock!

            beforeEach {
                nodesGestureHandlerMock = NodesGestureHandlingMock()
                sut = GestureHandler(nodesGestureHandler: nodesGestureHandlerMock)
            }

            context("when tap gesture received") {
                context("when state is ended") {
                    it("should propagate gesture to nodes manager") {
                        let tapGestureRecognizerMock = TapGestureRecognizingMock()
                        tapGestureRecognizerMock.given(.state(getter: UIGestureRecognizer.State.ended))
                        let tappedNode = TransformNode()
                        tapGestureRecognizerMock.given(.tappedNode(getter: tappedNode))

                        sut.handleTapGesture(tapGestureRecognizerMock)

                        nodesGestureHandlerMock.verify(.handleNodeTap(.value(tappedNode)))
                    }
                }

                context("when state is other than ended") {
                    it("nothing should happend") {
                        let tapGestureRecognizerMock = TapGestureRecognizingMock()
                        tapGestureRecognizerMock.given(.state(getter: UIGestureRecognizer.State.cancelled))
                        let tappedNode = TransformNode()
                        tapGestureRecognizerMock.given(.tappedNode(getter: tappedNode))

                        sut.handleTapGesture(tapGestureRecognizerMock)

                        nodesGestureHandlerMock.verify(.handleNodeTap(.any), count: 0)
                        nodesGestureHandlerMock.verify(.handleNodeLongPress(.any, .any), count: 0)

                    }
                }
            }

            context("when drag gesture received") {
                context("when state is changed") {
                    it("should propagate drag value to dragged node") {
                        let dragGestureRecognizerMock = DragGestureRecognizingMock()
                        dragGestureRecognizerMock.given(.state(getter: UIGestureRecognizer.State.changed))
                        dragGestureRecognizerMock.given(.beginDragValue(getter: 0.75))
                        dragGestureRecognizerMock.given(.dragDelta(getter: 0.35))
                        let draggedNode = SimpleDraggingNode()
                        dragGestureRecognizerMock.given(.dragNode(getter: draggedNode))

                        sut.handleDragGesture(dragGestureRecognizerMock)
                        expect(draggedNode.dragValue).to(beCloseTo(1.1))
                    }
                }

                context("when state is other than changed") {
                    it("nothing should happend") {
                        let dragGestureRecognizerMock = DragGestureRecognizingMock()
                        dragGestureRecognizerMock.given(.state(getter: UIGestureRecognizer.State.ended))
                        let draggedNode = SimpleDraggingNode()
                        dragGestureRecognizerMock.given(.dragNode(getter: draggedNode))

                        sut.handleDragGesture(dragGestureRecognizerMock)
                        expect(draggedNode.dragValue).to(beCloseTo(0.0))
                    }
                }
            }

            context("when long press gesture received") {
                it("should propagate gesture to nodes manager") {
                    let longPressGestureRecognizerMock = LongPressGestureRecognizingMock()
                    let longPressedNode = TransformNode()
                    longPressGestureRecognizerMock.given(.longPressedNode(getter: longPressedNode))
                    longPressGestureRecognizerMock.given(.state(getter: UIGestureRecognizer.State.recognized))

                    sut.handleLongPressGesture(longPressGestureRecognizerMock)
                    nodesGestureHandlerMock.verify(.handleNodeLongPress(.value(longPressedNode), .value(UIGestureRecognizer.State.recognized)))
                }
            }
        }
    }
}

class SimpleDraggingNode: Dragging {
    var dragAxis: Ray? = nil
    var dragRange: CGFloat = 0.0
    var dragValue: CGFloat = 0.0
}
