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

import UIKit
import ARKit
import SceneKit

@objc public class RCTARView: UIView {

    fileprivate(set) var arView: ARSCNView!
    fileprivate var inputResponder: UITextField?
#if targetEnvironment(simulator)
    fileprivate var rayCastNode: SCNNode?
#endif

#if targetEnvironment(simulator)
    static private(set) var instance: RCTARView!
#else
    static private var instance: RCTARView!
#endif

    @objc static public var arSession: ARSession {
        return instance.arView.session
    }

    public var scene: SCNScene {
        return arView.scene
    }

    public var cameraNode: SCNNode? {
        return arView.pointOfView
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
            if #available(iOS 11.3, *) {
                let videoFormatCount = ARWorldTrackingConfiguration.supportedVideoFormats.count
                if videoFormatCount > 0 {
                    _configuration!.videoFormat = ARWorldTrackingConfiguration.supportedVideoFormats[videoFormatCount - 1]
                }
                _configuration!.isAutoFocusEnabled = false
            }

            _configuration!.worldAlignment = ARConfiguration.WorldAlignment.gravity
            _configuration!.providesAudioData = false

            _configuration!.planeDetection = []

            return _configuration
        }

        return _configuration
    }

    func enablePlaneDetection(_ configuration: ARWorldTrackingConfiguration.PlaneDetection) {
        self.configuration?.planeDetection = configuration
        reset()
    }

    func disablePlaneDetection() {
        self.configuration?.planeDetection = []
        reset()
    }

    fileprivate let gestureHandler: GestureHandling

    //MARK: RCTARView Observable
    fileprivate(set) var observers: [WeakReference<RCTARViewObserving>] = []

    func register(_ observer: RCTARViewObserving) {
        observers.append(WeakReference(value: observer))
    }

    func unregister(_ observer: RCTARViewObserving) {
        observers.removeAll { storedObserver -> Bool in
            return storedObserver.value === observer
        }
    }

    public init() {
        self.gestureHandler = GestureHandler(nodesGestureHandler: NodesGestureHandler.instance)
        super.init(frame: CGRect.zero)
        self.arView = createARView()
        setupNodesManager(self.arView)
        setupNodesGestureHandler()
        setupGestureRecognizers(self.arView)
        PlaneDetector.instance.register(arView: self)
        RCTARView.instance = self
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
        view.delegate = self // ARSCNViewDelegate
        
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

        // Set camera's range
        view.pointOfView?.camera?.zNear = 0.001
        view.pointOfView?.camera?.zFar = 10

        return view
    }

    fileprivate func setupNodesManager(_ view: ARSCNView) {
        // Resgister scene in nodes manager
        NodesManager.instance.registerARView(self)

        if let rootView = UIApplication.shared.keyWindow?.rootViewController?.view {
            NodesManager.instance.dialogPresenter = DialogPresenter(parentView: rootView)
        }
    }

    private func setupNodesGestureHandler() {
        NodesGestureHandler.instance.onInputFocused = { [weak self] input in
            self?.presentInput(input)
        }

        NodesGestureHandler.instance.onInputUnfocused = { [weak self] in
            self?.dismissInput()
        }
    }

    private var nodeGestureRecognizers: [UIGestureRecognizer] = []
    fileprivate func setupGestureRecognizers(_ view: ARSCNView) {
        let rayBuilder = RayBuilder()

        // Add tap gesture
        let tapGestureRecognizer = TapGestureRecognizer(nodeSelector: NodesManager.instance.nodeSelector,
                                                        rayBuilder: rayBuilder,
                                                        target: gestureHandler,
                                                        action: #selector(GestureHandling.handleTapGesture(_:)))
        tapGestureRecognizer.getCameraNode = { [weak self] in return self?.arView.pointOfView }

        // Add drag gesture
        let dragGestureRecognizer = DragGestureRecognizer(nodeSelector: NodesManager.instance.nodeSelector,
                                                          rayBuilder: rayBuilder,
                                                          target: gestureHandler,
                                                          action: #selector(GestureHandling.handleDragGesture(_:)))
        dragGestureRecognizer.getCameraNode = { [weak self] in return self?.arView.pointOfView }

        // Add long press gesture
        let longPressGestureRecogrnizer = LongPressGestureRecognizer(nodeSelector: NodesManager.instance.nodeSelector,
                                                                     rayBuilder: rayBuilder,
                                                                     target: gestureHandler,
                                                                     action: #selector(GestureHandling.handleLongPressGesture(_:)))
        longPressGestureRecogrnizer.getCameraNode = { [weak self] in return self?.arView.pointOfView }

        nodeGestureRecognizers.append(contentsOf: [tapGestureRecognizer, dragGestureRecognizer, longPressGestureRecogrnizer])

        addGestureRecognizer(tapGestureRecognizer)
        addGestureRecognizer(dragGestureRecognizer)
        addGestureRecognizer(longPressGestureRecogrnizer)
    }

    fileprivate func presentInput(_ input: DataProviding) {
        if (inputResponder == nil) {
            inputResponder = UITextField()
            inputResponder!.isHidden = true
            addSubview(inputResponder!)
        }

        let inputAccessoryView = InputAccessoryViewFactory.createView(for: input, onFinishEditing: {
            NodesGestureHandler.instance.handleNodeTap(nil)
        })

        let inputView = InputViewFactory.createView(for: input, onFinishEditing: {
            NodesGestureHandler.instance.handleNodeTap(nil)
        })

        inputResponder!.inputAccessoryView = inputAccessoryView
        inputResponder!.inputView = inputView
        inputResponder!.becomeFirstResponder()
        inputAccessoryView?.becomeFirstResponder()
    }

    fileprivate func dismissInput() {
        // NOTE: This line generates the following warning:
        // First responder warning: '<GrowingTextView...>' rejected resignFirstResponder
        // when being removed from hierarchy.
        inputResponder?.inputAccessoryView?.resignFirstResponder()

        inputResponder?.inputView = nil
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
