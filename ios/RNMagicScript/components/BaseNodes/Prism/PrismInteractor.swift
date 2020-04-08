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

class PrismInteractor: NSObject, PrismInteracting {
    static let instance: PrismInteractor = PrismInteractor()

    weak var arView: RCTARView?
    var interactedPrism: Prism?

    private var gestureRecognizers: [UIGestureRecognizer] = [UIGestureRecognizer]()

    override init() {
        super.init()

        let pinchGestureRecogrnizer = PinchGestureRecognizer(target: self, action: #selector(handlePinchGesture(_:)))
        pinchGestureRecogrnizer.delegate = self
        gestureRecognizers.append(pinchGestureRecogrnizer)

        let rotationGestureRecogrnizer = RotationGestureRecognizer(target: self, action: #selector(handleRotationGesture(_:)))
        rotationGestureRecogrnizer.delegate = self
        gestureRecognizers.append(rotationGestureRecogrnizer)

        let panGestureRecogrnizer = PanGestureRecognizer(target: self, action: #selector(handlePanGesture))
        panGestureRecogrnizer.delegate = self
        gestureRecognizers.append(panGestureRecogrnizer)
    }

    // MARK: Prism interaction variables
    private var getInitialDistance: Bool = false
    private var initialDistance: Float?
    private var localDistanceChange: Float?

    // MARK: PrismInteracting
    func toggleInteractions(for prism: Prism) {
        prism.editMode ? stopInteractions(for: prism) : startInteractions(for: prism)
    }

    func startInteractions(for prism: Prism) {
        self.interactedPrism = prism
        prism.editMode = true

        attacheGestureRecognizers()

        #if targetEnvironment(simulator)
        RCTARView.instance.arView.allowsCameraControl = false
        #endif
    }

    func update(for camera: SCNNode?) {
        guard let prism = self.interactedPrism else { return }

        func getMaxDistance() -> Float {
            if let zFar = camera?.camera?.zFar {
                return Float(zFar)
            }
            return Float(Prism.defaultRayLenght)
        }

        if let pointOfView = camera {

            let begin = pointOfView.position
            let direction = pointOfView.worldFront
            let ray = Ray(begin: begin, direction: direction, length: Prism.defaultRayLenght)

            if prism.editMode == true {
                if getInitialDistance { initialDistance = sqrt((prism.position - ray.begin).lengthSq()) }
                var cameraToPrismCenterDistance = initialDistance ?? sqrt((prism.position - ray.begin).lengthSq())
                if let distanceChange = localDistanceChange, let initialDistance = initialDistance {
                    cameraToPrismCenterDistance -= initialDistance * distanceChange
                    localDistanceChange = nil
                }

                let newCameraToPrismCenterDistance = Math.clamp(cameraToPrismCenterDistance, prism.maxRadius, getMaxDistance())
                prism.updatePosition(withPosition: SCNVector3(0.0, 0.0, -newCameraToPrismCenterDistance), relativeTo: pointOfView)
                prism.updateClipping()
            }
        }
    }

    func stopInteractions(for prism: Prism) {
        if self.interactedPrism == prism {
            self.interactedPrism = nil
            prism.editMode = false

            detachGestureRecognizers()

            #if targetEnvironment(simulator)
            RCTARView.instance.arView.allowsCameraControl = true
            #endif
        }
    }

    private func attacheGestureRecognizers() {
        guard let arViewGestures = arView?.gestureRecognizers else { return }
        gestureRecognizers.forEach { gestureRecognizer in
            if !arViewGestures.contains(gestureRecognizer) {
                arView?.addGestureRecognizer(gestureRecognizer)
            }
        }
    }

    private func detachGestureRecognizers() {
        guard let arViewGestures = arView?.gestureRecognizers else { return }
        gestureRecognizers.forEach { gestureRecognizer in
            if arViewGestures.contains(gestureRecognizer) {
                arView?.removeGestureRecognizer(gestureRecognizer)
            }
        }
    }
}


extension PrismInteractor: UIGestureRecognizerDelegate {
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        if gestureRecognizer is UIRotationGestureRecognizer && otherGestureRecognizer is UIPinchGestureRecognizer {
            return true
        }

        if gestureRecognizer is UIPinchGestureRecognizer && otherGestureRecognizer is UIRotationGestureRecognizer {
            return true
        }
        return false
    }

    // MARK: gesture handlers
    @objc func handlePinchGesture(_ sender: PinchGestureRecognizer) {
                func CGPointDistanceSquared(from: CGPoint, to: CGPoint) -> CGFloat {
                    return pow((from.x - to.x), 2) + pow((from.y - to.y), 2)
                }

                if let result = NodesManager.instance.scene?.prisms.filter({ $0.editMode }) {
                    if let prismScale = result.first?.scale {
                        if let size = sender.view?.frame {

                            let numberOfPoints = sender.numberOfTouches
                            var points: [CGPoint] = [CGPoint]()
                            for index in 0..<numberOfPoints {
                                points.append(sender.location(ofTouch: index, in: sender.view))
                            }

        //                    assert(points.count == 2, "Wrong numbers of touch points.")
                            let distanceBetween = CGPointDistanceSquared(from: points[0], to: points[1])
                            let percent = distanceBetween / (pow(size.width, 2) + pow(size.height, 2)) * 100
                            switch sender.velocity.sign {
                            case .minus:
                                result.first?.scale = prismScale - (prismScale * percent / 1000)
                            case .plus:
                                result.first?.scale = prismScale + (prismScale * percent / 1000)
                            }
                            result.first?.updateClipping()
                        }
                    }
                }
    }

    @objc func handleRotationGesture(_ sender: RotationGestureRecognizer) {
        if let result = NodesManager.instance.scene?.prisms.filter({ $0.editMode }) {
            if let prismOrientation = result.first?.orientation {
                var glQuaternion = GLKQuaternionMake(prismOrientation.x, prismOrientation.y, prismOrientation.z, prismOrientation.w)

                // Rotate around Y axis
                let multiplier = GLKQuaternionMakeWithAngleAndAxis(0.25, 0, 1, 0)
                glQuaternion = GLKQuaternionMultiply(glQuaternion, multiplier)

                result.first?.orientation = SCNQuaternion(x: glQuaternion.x, y: glQuaternion.y, z: glQuaternion.z, w: glQuaternion.w)
            }
        }
    }

    @objc func handlePanGesture(_ sender: PanGestureRecognizer) {
        // MARK: handlePanGesture::internal
        func getGestureDistance(_ sender: PanGestureRecognizer) -> Float {
            let velocity = sender.velocity(in: sender.view)
            if abs(velocity.y) > abs(velocity.x) { // vertical
                guard let view = sender.view else { return 0.0 }
                return Float(velocity.y / view.frame.height)
            }
            return 0.0
        }

        switch sender.state {
        case .began:
            getInitialDistance = true
            localDistanceChange = getGestureDistance(sender)
        case .changed:
            localDistanceChange = getGestureDistance(sender)
        case .cancelled, .ended, .failed:
            getInitialDistance = false
            initialDistance = nil
            localDistanceChange = nil
        default:
            print()
        }
    }
}
