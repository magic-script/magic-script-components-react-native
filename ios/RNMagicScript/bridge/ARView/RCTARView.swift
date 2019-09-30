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

import UIKit
import ARKit
import SceneKit

@objc public class RCTARView: UIView {

    fileprivate(set) var arView: ARSCNView!
    fileprivate var inputResponder: UITextField?

    public var scene: SCNScene {
        return arView.scene
    }

    @objc public var debug: Bool {
        get { return arView.showsStatistics }
        set {
            arView.showsStatistics = newValue
            arView.debugOptions = newValue ? [.showWorldOrigin, .showFeaturePoints] : []
        }
    }

    @objc public var rendersContinuously: Bool {
        get { return arView.rendersContinuously }
        set { arView.rendersContinuously = newValue }
    }

    fileprivate var _configuration: ARWorldTrackingConfiguration?
    fileprivate var configuration: ARWorldTrackingConfiguration? {
        guard ARWorldTrackingConfiguration.isSupported else { return nil }

        guard let _ = _configuration else  {
            _configuration = ARWorldTrackingConfiguration()
            // _configuration.planeDetection = ARPlaneDetectionHorizontal;
            if #available(iOS 11.3, *) {
                let videoFormatCount = ARWorldTrackingConfiguration.supportedVideoFormats.count
                if videoFormatCount > 0 {
                    _configuration!.videoFormat = ARWorldTrackingConfiguration.supportedVideoFormats[videoFormatCount - 1]
                }
                _configuration!.isAutoFocusEnabled = false
            }

            _configuration!.worldAlignment = ARConfiguration.WorldAlignment.gravity
            _configuration!.providesAudioData = false

            return _configuration
        }

        return _configuration
    }

    public weak var delegate: ARSCNViewDelegate? {
        get { return arView.delegate }
        set { arView.delegate = newValue }
    }

    public init() {
        super.init(frame: CGRect.zero)
        self.arView = createARView()
        resume()
    }

    required public init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    fileprivate func createARView() -> ARSCNView {
        let view = ARSCNView()
        view.autoenablesDefaultLighting = true
        view.automaticallyUpdatesLighting = true
        view.backgroundColor = UIColor(white: 55.0 / 255.0, alpha: 1.0)
        view.rendersContinuously = true
        view.scene.rootNode.name = "root"

        // Add AR view as a child
        view.translatesAutoresizingMaskIntoConstraints = false
        addSubview(view)
        NSLayoutConstraint.activate([
            view.leftAnchor.constraint(equalTo: leftAnchor),
            view.topAnchor.constraint(equalTo: topAnchor),
            view.rightAnchor.constraint(equalTo: rightAnchor),
            view.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])

    #if targetEnvironment(simulator)
        // Allow for basic orbit gestures if we're running in the simulator
        view.allowsCameraControl = true
        view.defaultCameraController.interactionMode = SCNInteractionMode.orbitTurntable
        view.defaultCameraController.maximumVerticalAngle = 45
        view.defaultCameraController.inertiaEnabled = true
        view.defaultCameraController.translateInCameraSpaceBy(x: 0.0, y: 0.0, z: 1.5)
    #endif

        // Add gesture recognizer
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleTapAction(_:)))
        tapGestureRecognizer.numberOfTapsRequired = 1
        addGestureRecognizer(tapGestureRecognizer)

        // Resgister scene in nodes manager
        UiNodesManager.instance.registerScene(view.scene)
        UiNodesManager.instance.onInputFocused = { [weak self] input in
            self?.presentInput(input)
        }
        UiNodesManager.instance.onInputUnfocused = { [weak self] in
            self?.dismissInput()
        }

        return view
    }

    fileprivate func presentInput(_ input: InputDataProviding) {
        if (inputResponder == nil) {
            inputResponder = UITextField()
            inputResponder!.isHidden = true
            addSubview(inputResponder!)
        }

        let inputAccessoryView = InputAccessoryViewFactory.createView(for: input, onFinishEditing: {
            UiNodesManager.instance.handleNodeTap(nil)
        })
        inputResponder!.inputAccessoryView = inputAccessoryView
        inputResponder!.becomeFirstResponder()
        inputAccessoryView.becomeFirstResponder()
    }

    fileprivate func dismissInput() {
        // NOTE: This line generates the following warning:
        // First responder warning: '<GrowingTextView...>' rejected resignFirstResponder
        // when being removed from hierarchy.
        inputResponder?.inputAccessoryView?.resignFirstResponder()

        inputResponder?.inputAccessoryView = nil
        inputResponder?.resignFirstResponder()
    }

    public func pause() {
        arView.session.pause()
    }

    public func resume() {
        if let configuration = self.configuration {
            arView.session.run(configuration, options: [])
        }
    }

    public func reset() {
        if let configuration = self.configuration {
            arView.session.run(configuration, options: [.removeExistingAnchors, .resetTracking])
        }
    }
}

// MARK: - Event handlers
extension RCTARView {
    @objc fileprivate func handleTapAction(_ sender: UITapGestureRecognizer) {
        let tapPoint: CGPoint = sender.location(in: arView)
        let options: [SCNHitTestOption : Any] = [
            SCNHitTestOption.boundingBoxOnly: true,
            SCNHitTestOption.ignoreHiddenNodes: true,
            // For some reason, the categoryBitMask test option does not work
            // (nodes with given categoryBitMask are ignored).
            // SCNHitTestOption.categoryBitMask: UiNodesManager.instance.focusableNodeBitMask
        ]
        let node = arView.hitTest(tapPoint, options: options).first?.node
        UiNodesManager.instance.handleNodeTap(node)
    }
}
