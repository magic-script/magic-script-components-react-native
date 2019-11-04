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

import Foundation
import SceneKit

@objc class DragGestureDeltaCalculator: NSObject {
    let draggedObject: Dragging
    let beginPoint: SCNVector3
    let beginDragValue: CGFloat
    init?(draggedObject: Dragging?, ray: Ray) {
        guard let dragged = draggedObject,
            let axisRay = dragged.dragAxis,
            let point = axisRay.getClosestPointTo(ray: ray) else { return nil }
        self.draggedObject = dragged
        self.beginPoint = point
        self.beginDragValue = dragged.dragValue
    }

    func calculateDelta(for ray: Ray) -> CGFloat {
        guard let dragAxis = draggedObject.dragAxis,
            let point = dragAxis.getClosestPointTo(ray: ray) else { return 0 }

        let dir = (point - beginPoint).normalized()
        let sign: CGFloat = (dir.dot(dragAxis.direction) >= 0) ? 1 : -1
        return CGFloat(point.distance(beginPoint)) * sign
    }

    func performDrag(by delta: CGFloat) {
        draggedObject.dragValue = beginDragValue + delta / draggedObject.contentLength
    }
}
