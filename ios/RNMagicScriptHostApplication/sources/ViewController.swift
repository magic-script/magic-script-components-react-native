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
        setupTests()
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
        arView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(arView)
        NSLayoutConstraint.activate([
            arView.leftAnchor.constraint(equalTo: view.leftAnchor),
            arView.topAnchor.constraint(equalTo: view.topAnchor),
            arView.rightAnchor.constraint(equalTo: view.rightAnchor),
            arView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
            ])

        arView.delegate = self
    }

    fileprivate var spinner2: UiSpinnerNode!
    fileprivate var text: UiTextNode!

    fileprivate func setupTests() {

        let grid = UiGridLayoutNode(props: [
            "columns": 14,
            "defaultItemPadding": [0.003, 0.003, 0.003, 0.003],
            "localPosition": [0, 0.5, 0],
            "alignment": "top-center"
        ])
        let gridId = "grid"
        UiNodesManager.instance.registerNode(grid, nodeId: gridId)
        UiNodesManager.instance.addNodeToRoot(gridId)

        SystemIcon.names.enumerated().forEach { (index, name) in
            let icon = UiImageNode(props: [
                "icon": name,
                "height": 0.04,
            ])
            let nodeId: String = "icon_\(index)"
            UiNodesManager.instance.registerNode(icon, nodeId: nodeId)
            UiNodesManager.instance.addNode(nodeId, toParent: gridId)
        }

        grid.layoutIfNeeded()
    }

    fileprivate func convertString(_ text: String) -> String {
        let result = text.split(separator: "-").map { $0.prefix(1).uppercased() + $0.dropFirst() }.joined()
        return result.prefix(1).lowercased() + result.dropFirst()
    }

    fileprivate func createComponent(_ props: [String: Any], nodeId: String) {
        let node = UiImageNode(props: props)
        node.layoutIfNeeded()
        UiNodesManager.instance.registerNode(node, nodeId: nodeId)
        UiNodesManager.instance.addNodeToRoot(nodeId)
    }
}

extension ViewController: ARSCNViewDelegate {
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        let deltaTime = time - lastTime
        lastTime = time
        guard deltaTime < 0.5 else { return }
    }
}
