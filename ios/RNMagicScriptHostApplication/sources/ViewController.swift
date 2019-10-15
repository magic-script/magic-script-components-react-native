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

    fileprivate var scrollView: UiScrollViewNode!
    fileprivate var scrollBar: UiScrollBarNode!
    fileprivate var scrollBarPosition: CGFloat = 0.0
    fileprivate var scrollBarSize: CGFloat = 0.1
    fileprivate func setupTests() {
        let dropdownList = UiDropdownListNode(props: ["text": "dropdownListId", "localPosition": [0, 0.25, 0], "textSize": 0.0235, "maxCharacterLimit": 35])
        let dropdownListId = "dropdownListId"
        UiNodesManager.instance.registerNode(dropdownList, nodeId: dropdownListId)
        UiNodesManager.instance.addNodeToRoot(dropdownListId)
        dropdownList.layoutIfNeeded()

        for index in 0...16 {
            var dropdownItem: UiDropdownListItemNode
            if index % 4 == 0 {
                dropdownItem = UiDropdownListItemNode(props: ["text": "Very long text for dropDownListItem to check how this looks when list appears"])
            } else {
                dropdownItem = UiDropdownListItemNode(props: ["text": "Very short text"])
            }

            UiNodesManager.instance.registerNode(dropdownItem, nodeId: String(index))
            UiNodesManager.instance.addNode(String(index), toParent: dropdownListId)
        }
        dropdownList.onTap = { sender in
            print("BUKA: dropDown onTap \(sender)")
        }

        dropdownList.onSelectionItemChanged = { sender, selectedItem in
            print("BUKA: dropDown onSelectedItemChanged \(sender) \(selectedItem)")
        }
    }

    @discardableResult
    fileprivate func createComponent<T: UiNode>(_ props: [String: Any], nodeId: String, parentId: String? = nil) -> T {
        let node = T.init(props: props)
        node.layoutIfNeeded()
        UiNodesManager.instance.registerNode(node, nodeId: nodeId)
        if let parentId = parentId {
            UiNodesManager.instance.addNode(nodeId, toParent: parentId)
        } else {
            UiNodesManager.instance.addNodeToRoot(nodeId)
        }
        return node
    }
}

extension ViewController: ARSCNViewDelegate {
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        let deltaTime = time - lastTime
        lastTime = time
        guard deltaTime < 0.5 else { return }

        scrollBarPosition += CGFloat(deltaTime)
        if scrollBarPosition > 1.0 {
            scrollBarPosition -= 2.0
        }

        scrollBarSize += CGFloat(deltaTime * 0.1)
        if scrollBarSize > 1.0 {
            scrollBarSize -= 2.0
        }
//        scrollBar.thumbPosition = abs(scrollBarPosition)
//        scrollBar.thumbSize = max(0.1, abs(scrollBarSize))
//        scrollBar.layoutIfNeeded()
    }
}
