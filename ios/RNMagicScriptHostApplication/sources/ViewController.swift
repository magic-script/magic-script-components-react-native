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

class ViewController: UIViewController {

    fileprivate var arView: RCTARView!
    fileprivate var lastTime: TimeInterval = 0

    override var shouldAutorotate: Bool { return true }
    override var prefersStatusBarHidden: Bool { return true }

    fileprivate var rootNode: SCNNode {
        return arView.scene.rootNode
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupScene()
        
//        setupPrismWithModels()
//        setupPrismForHitTest()
//        setupPrismForDialog()
        setupPrismForFontTest()

        setupARView()
        arView.register(self)
    }

    deinit {
        arView.unregister(self)
    }
    
    let sceneId = "scene"
    fileprivate func setupScene() {
        let scene = Scene()
        NodesManager.instance.registerScene(scene, sceneId: sceneId)
        NodesManager.instance.addNodeToRoot(sceneId)
    }

    var dialogNodeId = "dialogNodeId"
    var dialogNode: UiDialogNode?
    private func setupPrismForDialog() {
        let prismId = "AppName"
        let prism: Prism = Prism()
        prism.size = SCNVector3(1.0, 1.0, 1.0)
        prism.debug = true
        prism.interactions = [.scale, .position, .rotation]
        prism.onModeChanged = { sender, mode in
            print("INFO onModeChanged \(sender) \(mode)")
        }

        prism.onRotationChanged = { sender, rotation in
            print("INFO onRotationChanged  \(sender) \(rotation)")
        }

        prism.onPositionChanged = { sender, position in
            print("INFO onPositionChanged \(sender) \(position)")
        }

        prism.onScaleChanged = { sender, scale in
            print("INFO onScaleChanged \(sender) \(scale)")
        }

        NodesManager.instance.registerPrism(prism, prismId: prismId)
        NodesManager.instance.addNode(prismId, toParent: sceneId)

        let dialogButton : UiButtonNode = createComponent([
            "width": 0.75,
            "height": 0.25,
            "localPosition": [0, 0, 0],
            "color": [1, 1, 0, 1],
            "text": "Dialog"
        ], nodeId: "button_dialog", parentId: prismId)

        dialogNode = UiDialogNode(props: ["buttonType": "text-with-icon", "title": "Dialog title", "message": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", "confirmText": "Confirm", "confirmIcon": "check", "cancelText": "Confirm", "cancelIcon": "close", "scrolling": true	])
        dialogNode?.onDialogConfirmed = { [weak self] dialog in
            if let dialogId = self?.dialogNodeId { NodesManager.instance.removeNode(dialogId, fromParent: prismId) }
        }
        dialogNode?.onDialogCanceled = { [weak self] dialog in
            if let dialogId = self?.dialogNodeId { NodesManager.instance.removeNode(dialogId, fromParent: prismId) }
        }

        dialogButton.onActivate = { [weak self] button in
            if let dialogId = self?.dialogNodeId, let dialog = self?.dialogNode {
                NodesManager.instance.registerNode(dialog, nodeId: dialogId)
                NodesManager.instance.addNode(dialogId, toParent: prismId)
            }
        }

        NodesManager.instance.updateLayout()
    }

    fileprivate func setupPrismForHitTest() {
        let prismId = "prism_hit_test"
        let prism: Prism = Prism()
        prism.size = SCNVector3(1.0, 1.0, 1.0)
        prism.debug = true
        
        NodesManager.instance.registerPrism(prism, prismId: prismId)
        NodesManager.instance.addNode(prismId, toParent: sceneId)
        
        let _ : UiButtonNode = createComponent([
            "width": 1,
            "height": 0.3,
            "localPosition": [0, 0, -0.1],
            "color": [1, 1, 0, 1],
            "text": "Back button"
        ], nodeId: "button_back", parentId: prismId)
        
        let _ : UiButtonNode = createComponent([
            "width": 1,
            "height": 0.3,
            "localPosition": [0, 0, 0.1],
            "color": [0, 1, 0, 1],
            "text": "Front button"
        ], nodeId: "button_front", parentId: prismId)
        
        NodesManager.instance.updateLayout()
    }

    fileprivate func setupPrismWithModels() {
        let prismId = "prism_models"
        let prism: Prism = Prism()
        prism.size = SCNVector3(1.0, 1.0, 1.0)
        prism.debug = true
        //        prism.editMode = true

        NodesManager.instance.registerPrism(prism, prismId: prismId)
        NodesManager.instance.addNode(prismId, toParent: sceneId)

        let groupId: String = "group"
        let _: UiGroupNode = createComponent(["localScale": [0.5, 0.5, 0.5]], nodeId: groupId, parentId: prismId)

        let filenames = [
            "static.obj",
            "static.obj",
            "static.gltf",
            "animated.gltf",
            "static.glb",
            "animated.glb"
        ]
        for (index, filename) in filenames.enumerated() {
            if let path = Bundle.main.path(forResource: filename, ofType: nil),
                FileManager.default.fileExists(atPath: path) {
                loadModel(path, index: index, parentId: groupId)
            } else {
                debugPrint("Unable to load \(filename) model.")
            }
        }

        // Slider
        let slider: UiSliderNode = createComponent([
            "min": 0.1,
            "max": 1.0,
            "value": 1.0,
            "localPosition": [0, 0.6, 0]
        ], nodeId: "slider", parentId: groupId)
        slider.onSliderChanged = { (sender, value) in
            prism.size = SCNVector3(value, value, value)
            //            prism.position = SCNVector3(0.5 * (value - 1), 0, 0)
            //            prism.rotation = SCNQuaternion.fromAxis(SCNVector3.up, andAngle: Float(5 * value))
            NodesManager.instance.updateLayout()
        }

        let checkbox1: UiToggleNode = createComponent([
            "height": 0.08,
            "text": "Debug mode",
            "textSize": 0.06,
            "localPosition": [-0.2, 0.7, 0],
            "on": prism.debug,
            "type": "checkbox"
        ], nodeId: "checkbox1", parentId: groupId)
        checkbox1.onChanged = { (sender, on) in
            prism.debug = on
        }

        let checkbox2: UiToggleNode = createComponent([
            "height": 0.08,
            "text": "Edit mode",
            "textSize": 0.06,
            "localPosition": [-0.2, 0.8, 0],
            "on": prism.operationMode == .edit,
            "type": "checkbox"
        ], nodeId: "checkbox2", parentId: groupId)
        checkbox2.onChanged = { (sender, on) in
            prism.operationMode = .edit
        }
        
        // ScrollView
        let scrollViewId = "scrollView"
        let _ : UiScrollViewNode = createComponent([
            "scrollBounds": [
                "min": [-0.1, -0.2, -0.1],
                "max": [0.1, 0.2, 0.1]
            ],
            "debug": true,
            "localPosition": [0.3, 0, 0],
            "scrollDirection": "vertical"
        ], nodeId: scrollViewId, parentId: groupId)
        //        scrollView.localRotation = SCNQuaternion.fromAxis(SCNVector3.up, andAngle: 0.5 * Float.pi)
        let linearLayoutId = "linear_layout"
        let _: UiLinearLayoutNode = createComponent([:], nodeId: linearLayoutId, parentId: scrollViewId)
        let colors = [
            [1,0,0,1],
            [0,1,0,1],
            [0,0,1,1],
            [1,1,0,1],
            [1,1,1,1],
        ]
        for i in 0..<colors.count {
            let _: UiImageNode = createComponent([
                "color": colors[i],
                "width": 0.4,
                "height": 0.4,
            ], nodeId: "image_\(i)", parentId: linearLayoutId)
        }

        NodesManager.instance.updateLayout()
    }

    fileprivate func setupPrismForFontTest() {
        let prismId = "prism_hit_test"
        let prism: Prism = Prism()
        prism.size = SCNVector3(1.0, 1.0, 1.0)
        prism.debug = true
        
        NodesManager.instance.registerPrism(prism, prismId: prismId)
        NodesManager.instance.addNode(prismId, toParent: sceneId)
        
        let styles: [FontStyle] = [.normal, .italic]
        let weights: [FontWeight] = [.extraLight, .light, .regular, .medium, .bold, .extraBold]
        
        for style in styles {
            let y: Float = (style == .normal) ? 0.3 : -0.1
            for (index, weight) in weights.enumerated() {
                let text : UiTextNode = createComponent([
                    "text": "\(weight.rawValue): abcdefghijklmnopqrstuwvxyz",
                    "textSize": 0.04,
                    "localPosition": [-0.3, y - Float(index) * 0.05, 0],
                ], nodeId: "text_\(weight.rawValue)", parentId: prismId)
                text.style = style
                text.weight = weight
                text.updateLayout()
            }
        }
        
        NodesManager.instance.updateLayout()
    }
    
    fileprivate func loadModel(_ filePath: String, index: Int, parentId: String) {
        let columns: Int = 2
        let x: CGFloat = -0.3 + CGFloat(index % columns) * 0.3
        let y: CGFloat = 0.3 - CGFloat(index / columns) * 0.3
        let scale: CGFloat = 0.1
        let nodeId = "model_id_\(Date().timeIntervalSince1970)"
        let _: UiModelNode = createComponent([
            "modelPath": "file://\(filePath)",
            "debug": true,
            "localPosition": [x, y, 0],
            "localScale": [scale, scale, scale]
        ], nodeId: nodeId, parentId: parentId)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        arView.reset()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        arView.pause()
    }

    fileprivate func setupARView() {
        arView = RCTARView()
        arView.backgroundColor = UIColor(white: 55.0 / 255.0, alpha: 1.0)
        arView.debug = true
        arView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(arView)
        NSLayoutConstraint.activate([
            arView.leftAnchor.constraint(equalTo: view.leftAnchor),
            arView.topAnchor.constraint(equalTo: view.topAnchor),
            arView.rightAnchor.constraint(equalTo: view.rightAnchor),
            arView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }

    @discardableResult
    fileprivate func createComponent<T: TransformNode>(_ props: [String: Any], nodeId: String, parentId: String? = nil) -> T {
        let node = T.init(props: props)
        NodesManager.instance.registerNode(node, nodeId: nodeId)
        if let parentId = parentId {
            NodesManager.instance.addNode(nodeId, toParent: parentId)
        }
        return node
    }
    
    var tmpValue: Double = 0.0
}

extension ViewController: RCTARViewObserving {
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        let deltaTime = time - lastTime
        lastTime = time
        guard deltaTime < 0.5 else { return }
    }
}
