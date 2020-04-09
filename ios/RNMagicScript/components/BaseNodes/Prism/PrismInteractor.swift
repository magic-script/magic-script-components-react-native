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
    private weak var arView: RCTARView?
    var interactedPrism: Prism?

    private var gestureRecognizers: [UIGestureRecognizer] = [UIGestureRecognizer]()

    init(with arView: RCTARView) {
        super.init()

        self.arView = arView

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

    private var pinchTouchesInitialDistance: Float?
    private var initialScale: SCNVector3?
    private var localScaleChange: Float?

    private var initialOrientation: SCNQuaternion?
    private var localRotationChange: Float?

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
                if getInitialDistance { initialDistance = (prism.position - ray.begin).length() }
                var cameraToPrismCenterDistance = initialDistance ?? (prism.position - ray.begin).length()
                if let distanceChange = localDistanceChange, let initialDistance = initialDistance {
                    cameraToPrismCenterDistance -= initialDistance * distanceChange
                    localDistanceChange = nil
                }

                let newCameraToPrismCenterDistance = Math.clamp(cameraToPrismCenterDistance, prism.maxRadius, getMaxDistance()-prism.maxRadius)
                prism.updatePosition(withPosition: SCNVector3(0.0, 0.0, -newCameraToPrismCenterDistance), relativeTo: pointOfView)
                if let scaleChange = localScaleChange, let scale = initialScale {
                    prism.scale = scale * scaleChange
                }
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
            if arViewGestures.doesNotContain(gestureRecognizer) {
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
    // MARK: gesture handlers
    @objc func handlePinchGesture(_ sender: PinchGestureRecognizer) {
        // MARK: handlePinchGesture::internal function
        func CGPointDistanceSquared(from: CGPoint, to: CGPoint) -> CGFloat {
            return pow((from.x - to.x), 2) + pow((from.y - to.y), 2)
        }

        switch sender.state {
        case .began:
            guard let prism = interactedPrism else { return }
            initialScale = prism.scale
            var points: [CGPoint] = [CGPoint]()
            for index in 0..<sender.numberOfTouches {
                points.append(sender.location(ofTouch: index, in: sender.view))
            }
            pinchTouchesInitialDistance = Float(CGPointDistanceSquared(from: points[0], to: points[1]))
        case .changed:
            var points: [CGPoint] = [CGPoint]()
            for index in 0..<sender.numberOfTouches {
                points.append(sender.location(ofTouch: index, in: sender.view))
            }
            guard points.count > 1 else { localScaleChange = nil; pinchTouchesInitialDistance = nil; return }
            let distanceBetween = CGPointDistanceSquared(from: points[0], to: points[1])
            guard let initialDistance = pinchTouchesInitialDistance else { return }
            localScaleChange = Float(distanceBetween) / initialDistance
        case .cancelled, .ended, .failed:
            localScaleChange = nil
            pinchTouchesInitialDistance = nil
        default:
            print("Default: \(#function)")
        }
    }

    @objc func handleRotationGesture(_ sender: RotationGestureRecognizer) {

        switch sender.state {
        case .began:
            guard let prism = interactedPrism else { return }
            initialOrientation = prism.orientation
            localRotationChange = Float(sender.rotation)
        case .changed:
            localRotationChange = Float(sender.rotation)
        case .cancelled, .ended, .failed:
            initialOrientation = nil
            localRotationChange = nil
        default:
            print("Default: \(#function)")
        }

        guard let orientation = initialOrientation else { return }
        var glQuaternion = GLKQuaternionMake(orientation.x, orientation.y, orientation.z, orientation.w)

        // Rotate around Y axis
        let multiplier = GLKQuaternionMakeWithAngleAndAxis(Float(sender.rotation), 0, 1, 0)
        glQuaternion = GLKQuaternionMultiply(glQuaternion, multiplier)

        interactedPrism?.orientation = SCNQuaternion(x: glQuaternion.x, y: glQuaternion.y, z: glQuaternion.z, w: glQuaternion.w)
    }

    @objc func handlePanGesture(_ sender: PanGestureRecognizer) {
        // MARK: handlePanGesture::internal function
        func getGestureDistance(_ sender: PanGestureRecognizer) -> Float {
            let _ = sender.velocity(in: sender.view) // we should test if velocity or translation fits here better
            let translation = sender.translation(in: sender.view)
            if abs(translation.y) > abs(translation.x) { // vertical
                guard let view = sender.view else { return 0.0 }
                return Float(translation.y / view.frame.height)
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
            print("Default: \(#function)")
        }
    }
}
