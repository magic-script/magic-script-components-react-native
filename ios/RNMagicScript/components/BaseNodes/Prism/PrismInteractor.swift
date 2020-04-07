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

import Foundation
import ARKit
import SceneKit

//sourcery: AutoMockable
protocol PrismInteracting: class {
    func toggleInteractions(for prism: Prism)
    func startInteractions(for prism: Prism)
    func update(for camera: SCNNode?)
    func stopInteractions(for prism: Prism)
}

class PrismInteractor: PrismInteracting {
    static let instance: PrismInteractor = PrismInteractor()

    var interactedPrism: Prism?

    func toggleInteractions(for prism: Prism) {
        prism.editMode ? stopInteractions(for: prism) : startInteractions(for: prism)
    }

    func startInteractions(for prism: Prism) {
        self.interactedPrism = prism
        prism.editMode = true
        #if targetEnvironment(simulator)
        RCTARView.instance.arView.allowsCameraControl = false
        #endif
    }

    func update(for camera: SCNNode?) {
        guard let prism = self.interactedPrism else { return }
        if let pointOfView = camera {

            let begin = pointOfView.position
            let direction = pointOfView.worldFront
            let ray = Ray(begin: begin, direction: direction, length: 5.0)

            if prism.editMode == true {
                let cameraToPrismCenterDistance = sqrt((prism.position - ray.begin).lengthSq())
                prism.updatePosition(withPosition: SCNVector3(0.0, 0.0, -cameraToPrismCenterDistance), relativeTo: pointOfView)
            }
        }
    }

    func stopInteractions(for prism: Prism) {
        if self.interactedPrism == prism {
            self.interactedPrism = nil
            prism.editMode = false

            #if targetEnvironment(simulator)
            RCTARView.instance.arView.allowsCameraControl = true
            #endif
        }
    }
}
