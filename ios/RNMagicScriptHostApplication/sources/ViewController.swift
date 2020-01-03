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

    fileprivate var arView: RCTARView!
    fileprivate var lastTime: TimeInterval = 0

    override var shouldAutorotate: Bool { return true }
    override var prefersStatusBarHidden: Bool { return true }

    fileprivate var rootNode: SCNNode {
        return arView.scene.rootNode
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupARView()
        setupScene()
        arView.register(self)
    }

    deinit {
        arView.unregister(self)
    }

    let groupId: String = "group"
    fileprivate func setupScene() {
        let _: UiGroupNode = createComponent(["localScale": [0.5, 0.5, 0.5]], nodeId: groupId)

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
                loadModel(path, index: index)
            } else {
                debugPrint("Unable to load \(filename) model.")
            }
        }
        UiNodesManager.instance.updateLayout()
    }

    fileprivate func loadModel(_ filePath: String, index: Int) {
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
        ], nodeId: nodeId, parentId: groupId)
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

    fileprivate let rectSize = CGSize(width: 0.4, height: 0.2)
    fileprivate var rectScale: CGFloat = 1.0
    fileprivate var rectLayout: UiRectLayoutNode!
    fileprivate func setupRectLayoutTest() {
        // Rect layout
        let rectLayoutId: String = "rect_layout"
        rectLayout = createComponent([
            "alignment": "top-center",
            "debug": true,
            "localPosition": [0, 0.7, 0],
            "height": rectSize.height,
            "width": rectSize.width
        ], nodeId: rectLayoutId, parentId: groupId)

        let _: UiButtonNode = createComponent([
            "enabled": false,
            "roundness": 0.5,
            "text": "Button",
            "textColor": [0,1,0,1],
            "textSize": 0.08,
            "width": rectSize.width,
            "height": rectSize.height
        ], nodeId: "button_id", parentId: rectLayoutId)

        let slider: UiSliderNode = createComponent([
            "localPosition": [0, 0.1, 0],
            "value": rectScale,
            "min": 0.3,
            "max": 2.0,
            "width": 1.0,
            "height": 0.06,
        ], nodeId: "slider_id", parentId: groupId)

        slider.onSliderChanged = { [weak self] sender, value in
            self?.rectScale = value
            self?.updateRectLayout()
        }
    }

    fileprivate func updateRectLayout() {
        rectLayout.width = rectScale * rectSize.width
        rectLayout.height = rectScale * rectSize.height
        rectLayout.layoutIfNeeded()
    }

    @discardableResult
    fileprivate func createComponent<T: TransformNode>(_ props: [String: Any], nodeId: String, parentId: String? = nil) -> T {
        let node = T.init(props: props)
        UiNodesManager.instance.registerNode(node, nodeId: nodeId)
        if let parentId = parentId {
            UiNodesManager.instance.addNode(nodeId, toParent: parentId)
        } else {
            UiNodesManager.instance.addNodeToRoot(nodeId)
        }
        return node
    }
}

extension ViewController: RCTARViewObserving {
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        let deltaTime = time - lastTime
        lastTime = time
        guard deltaTime < 0.5 else { return }

        DispatchQueue.main.async() { [weak self] in
            UiNodesManager.instance.updateLayout()
        }
    }
}
