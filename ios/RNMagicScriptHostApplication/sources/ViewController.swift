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

//        let textEdit = UiTextEditNode()
//        textEdit.alignment = .topCenter
//        textEdit.width = 0.4
//        textEdit.height = 0.6
//        textEdit.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
//        textEdit.textSize = 0.04
//        textEdit.multiline = true
//        textEdit.hint = "Enter text..."
//        textEdit.hintColor = UIColor.gray
//        textEdit.position = SCNVector3(0, 0.4, 0)
//        textEdit.setDebugMode(true)
//        textEdit.layoutIfNeeded()
//
//        UiNodesManager.instance.registerNode(textEdit, nodeId: "text_edit")
//        UiNodesManager.instance.addNodeToRoot("text_edit")


        let text = UiTextNode()
        text.boundsSize = CGSize(width: 0.1, height: 0)
        text.wrap = true
        text.textSize = 0.015
        text.text = "Very very very very very very very very very very very very long Info text to be set in UiTextNode."
//        text.position = SCNVector3(0, 0.4, 0)
//        text.localScale = SCNVector3(10, 10, 10)
        text.setDebugMode(true)
        text.layoutIfNeeded()
        UiNodesManager.instance.registerNode(text, nodeId: "text")
        UiNodesManager.instance.addNodeToRoot("text")
        print("text.boundsSize: \(text.boundsSize)")
        print("text.getSize(): \(text.getSize())")
    }
}

extension ViewController: ARSCNViewDelegate {
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        let deltaTime = time - lastTime
        lastTime = time
        guard deltaTime < 0.5 else { return }
    }
}
