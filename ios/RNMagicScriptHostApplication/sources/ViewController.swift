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

class ViewController: UIViewController {

    fileprivate var sceneView: ARSCNView!

    override var shouldAutorotate: Bool { return true }
    override var prefersStatusBarHidden: Bool { return true }
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return (UIDevice.current.userInterfaceIdiom == .phone) ? .allButUpsideDown : .all
    }

    fileprivate var scene: SCNScene {
        return sceneView.scene
    }

    fileprivate var rootNode: SCNNode {
        return sceneView.scene.rootNode
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        setupScene()
        setupTests()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        resetConfiguration()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        sceneView.session.pause()
    }

    fileprivate func resetConfiguration() {
        let configuration = ARWorldTrackingConfiguration()
        let options: ARSession.RunOptions = [.resetTracking, .removeExistingAnchors]
        sceneView.session.run(configuration, options: options)
    }

    fileprivate func setupScene() {

        // create AR scene view
        sceneView = ARSCNView(frame: view.bounds)
        sceneView.backgroundColor = UIColor(white: 55.0 / 255.0, alpha: 1.0)
        sceneView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(sceneView)
        NSLayoutConstraint.activate([
            sceneView.leftAnchor.constraint(equalTo: view.leftAnchor),
            sceneView.topAnchor.constraint(equalTo: view.topAnchor),
            sceneView.rightAnchor.constraint(equalTo: view.rightAnchor),
            sceneView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
            ])

        sceneView.delegate = self

        // configure lighting
        sceneView.autoenablesDefaultLighting = true
        sceneView.automaticallyUpdatesLighting = true
        sceneView.rendersContinuously = true

        sceneView.debugOptions = [.showFeaturePoints]
        sceneView.showsStatistics = true
        #if targetEnvironment(simulator)
        // Allow for basic orbit gestures if we're running in the simulator
        sceneView.allowsCameraControl = true
        sceneView.defaultCameraController.interactionMode = SCNInteractionMode.orbitTurntable
        sceneView.defaultCameraController.maximumVerticalAngle = 45.0
        sceneView.defaultCameraController.inertiaEnabled = true
        sceneView.defaultCameraController.translateInCameraSpaceBy(x: 0, y: 0, z: 1.5)
        #endif

        // Resgister scene in nodes manager
        UiNodesManager.instance.registerScene(sceneView.scene)
    }

    fileprivate var spinner2: UiSpinnerNode!
    fileprivate var text: UiTextNode!

    fileprivate func setupTests() {

        let spinner1 = UiSpinnerNode()
        spinner1.height = 0.6
        spinner1.layoutIfNeeded()
        spinner1.position = SCNVector3(0, 0.4, 0)
        rootNode.addChildNode(spinner1)

        spinner2 = UiSpinnerNode()
        spinner2.determinate = true
        spinner2.value = 0.0
        spinner2.height = 0.6
        spinner2.layoutIfNeeded()
        spinner2.position = SCNVector3(0, -0.4, 0)
        rootNode.addChildNode(spinner2)

        text = UiTextNode()
        text.text = ""
        text.alignment = .centerCenter
        text.textSize = 0.08
        text.position = SCNVector3(0, -0.4, 0)
        rootNode.addChildNode(text)

        // Uncomment to check if resources load properly.
        //        let toggle = UiToggleNode()
        //        toggle.height = 0.1
        //        toggle.layoutIfNeeded()
        //        rootNode.addChildNode(toggle)

        // Uncomment to preview 3d models.
        //        let model = UiModelNode()
        //        model.url = Bundle.main.url(forResource: "box", withExtension: "glb", subdirectory: nil)
        //        model.url = Bundle.main.url(forResource: "hedra_06", withExtension: "gltf", subdirectory: nil)
        //        model.url = Bundle.main.url(forResource: "hedra", withExtension: "obj", subdirectory: nil)
        //        rootNode.addChildNode(model);
    }

    fileprivate var lastTime: TimeInterval = 0
}

extension ViewController: ARSCNViewDelegate {
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        let deltaTime = time - lastTime
        lastTime = time
        guard deltaTime < 0.5 else { return }

        var value = spinner2.value
        value += CGFloat(deltaTime) * 0.1
        if value > 1.0 {
            value = 0.0
        }
        text.text = String(format: "%.2f", value)
        text.layoutIfNeeded()
        spinner2.value = value
        spinner2.layoutIfNeeded()
    }
}
