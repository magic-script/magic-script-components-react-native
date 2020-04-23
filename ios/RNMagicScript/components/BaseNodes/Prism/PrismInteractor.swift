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
protocol PrismInteractingDelegate: class {
    func onPrismUpdated(prism: Prism)
}

//sourcery: AutoMockable
protocol PrismInteracting: class {
    func toggleInteractions(for prism: Prism)
    func startInteractions(for prism: Prism)
    func update(cameraNode: SCNNode, time: TimeInterval)
    func stopInteractions(for prism: Prism)
}

class PrismInteractor: NSObject, PrismInteracting {
    weak var delegate: PrismInteractingDelegate?

    // MARK: Prism interaction variables
    weak var gesturable: GestureManaging?
    var interactedPrism: Prism?
    var gestureRecognizers: [GestureRecognizing] = []

    private var prevTime: TimeInterval = 0
    private var startInteractionTime: TimeInterval = 0
    private var startInteractionPosition: SCNVector3 = SCNVector3.zero
    private(set) var startDifferenceYaw: Float = 0.0
    private(set) var prismYawChange: Float = 0.0
    private(set) var prismDistanceChange: Float = 0.0
    private(set) var prismInitialScale: SCNVector3? = nil

    override init() {
        super.init()

        let panGestureRecogrnizer = PanGestureRecognizer(target: self, action: #selector(handlePanGesture))
        gestureRecognizers.append(panGestureRecogrnizer)

        let pinchGestureRecogrnizer = PinchGestureRecognizer(target: self, action: #selector(handlePinchGesture(_:)))
        gestureRecognizers.append(pinchGestureRecogrnizer)

        let rotationGestureRecogrnizer = RotationGestureRecognizer(target: self, action: #selector(handleRotationGesture(_:)))
        gestureRecognizers.append(rotationGestureRecogrnizer)
    }

    // MARK: PrismInteracting
    func toggleInteractions(for prism: Prism) {
        prism.editMode ? stopInteractions(for: prism) : startInteractions(for: prism)
    }

    func startInteractions(for prism: Prism) {
        self.interactedPrism = prism
        prism.editMode = true

        attacheGestureRecognizers()

        #if targetEnvironment(simulator)
        gesturable?.allowsCameraGestures = false
        #endif
    }

    func update(cameraNode: SCNNode, time: TimeInterval) {
        assert(Thread.isMainThread, "PrismInteractor.update must be called in main thread!")
        guard let camera = cameraNode.camera, let prism = interactedPrism, prism.editMode else { return }

        let deltaTime = Float(time - prevTime)
        prevTime = time
        guard deltaTime < 0.1 else {
            startInteractionTime = time
            startInteractionPosition = prism.position
            startDifferenceYaw = cameraNode.angleToWorldFront() - prism.angleToWorldFront() - Float.pi
            prismYawChange = 0.0
            return
        }

        let cameraPosition = cameraNode.position
        let cameraDirection = cameraNode.worldFront

        var prismDistanceToCamera = prism.position.distance(cameraPosition)

        // Update prism distance to camera change
        if prismDistanceChange != 0.0 {
            let cameraViewDistance = CGFloat(camera.zFar)
            let minDistance = Float(prism.maxRadius)
            let maxDistance = Float(cameraViewDistance) - prism.maxRadius
            let speedFactor: Float = prismDistanceToCamera / maxDistance

            let newDistance = prismDistanceToCamera + speedFactor * prismDistanceChange * deltaTime
            prismDistanceToCamera = Math.clamp(newDistance, minDistance, maxDistance)

            prismDistanceChange = 0.0
        }

        // Keep prism in front of camera
        let targetPosition = cameraPosition + prismDistanceToCamera * cameraDirection
        let animationDuration: Float = 0.3
        let t = Float(time - startInteractionTime) / animationDuration
        if t >= 0 && t < 1.0 {
            // Animate centering prism
            prism.position = startInteractionPosition.lerp(targetPosition, t)
        } else {
            prism.position = targetPosition
        }
        
        let prismYaw = (cameraNode.angleToWorldFront() - startDifferenceYaw) - prismYawChange
        prism.orientation = SCNQuaternion.fromAxis(SCNVector3.up, andAngle: prismYaw)
        prism.updateClipping()
        delegate?.onPrismUpdated(prism: prism)
    }

    func stopInteractions(for prism: Prism) {
        if self.interactedPrism == prism {
            self.interactedPrism = nil
            prism.editMode = false

            detachGestureRecognizers()

            #if targetEnvironment(simulator)
            gesturable?.allowsCameraGestures = true
            #endif
        }
    }

    private func attacheGestureRecognizers() {
        gestureRecognizers.forEach { gestureRecognizer in
            gesturable?.addGestureRecognizer(gestureRecognizer)
        }
    }

    private func detachGestureRecognizers() {
        gestureRecognizers.forEach { gestureRecognizer in
            gesturable?.removeGestureRecognizer(gestureRecognizer)
        }
    }
}

// MARK: gesture handlers
extension PrismInteractor {
    @objc func handlePanGesture(_ sender: PanGestureRecognizing) {
        switch sender.state {
        case .began, .changed:
            let speedFactor: Float = 40.0
            prismDistanceChange = speedFactor * Float(-sender.velocity(in: sender.view).y / sender.view!.frame.height)
        case .cancelled, .ended, .failed:
            prismDistanceChange = 0.0
        default:
            print("Default: \(#function)")
        }
    }

    @objc func handlePinchGesture(_ sender: PinchGestureRecognizing) {
        switch sender.state {
        case .began:
            prismInitialScale = interactedPrism?.scale
        case .changed:
            if let prism = interactedPrism, let initialScale = prismInitialScale {
                let minRealSize: Float = 0.3
                let maxRealSize: Float = 2.0
                let minScale = minRealSize / (prism.size * initialScale)
                let maxScale = maxRealSize / (prism.size * initialScale)
                var scaleFactor: Float = Float(sender.scale)
                scaleFactor = Math.clamp(scaleFactor, minScale.x, maxScale.x)
                scaleFactor = Math.clamp(scaleFactor, minScale.y, maxScale.y)
                scaleFactor = Math.clamp(scaleFactor, minScale.z, maxScale.z)
                prism.scale = initialScale * scaleFactor
                prism.updateClipping()
                delegate?.onPrismUpdated(prism: prism)
            }
        case .cancelled, .ended, .failed:
            prismInitialScale = nil
        default:
            print("Default: \(#function)")
        }
    }

    @objc func handleRotationGesture(_ sender: RotationGestureRecognizing) {
        switch sender.state {
        case .began, .changed:
            prismYawChange = Float(sender.rotation)
        case .cancelled, .ended, .failed:
            startDifferenceYaw += prismYawChange
            prismYawChange = 0.0
        default:
            print("Default: \(#function)")
        }
    }
}
