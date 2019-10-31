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
//        setupScrollViewTest()
//        setupDropdownListTest()
        setupUiDatePickerNodeTest()
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

        arView.delegate = self
    }

    fileprivate var scrollView: UiScrollViewNode!
    fileprivate var scrollBar: UiScrollBarNode!
    fileprivate var scrollBarPosition: CGFloat = 0.0
    fileprivate var scrollBarSize: CGFloat = 0.1

    fileprivate func setupUiDatePickerNodeTest() {
        let uiDatePickerNodeId: String = "uiDatePickerNodeId"
        let uiDatePickerNode: UiDatePickerNode = createComponent(["defaultDate": "06/13/1983", "label": "Birth date", "dateFormat": "DD/YYYY"], nodeId: uiDatePickerNodeId)
        uiDatePickerNode.position = SCNVector3(-0.125, 0.250, 0.0)

        uiDatePickerNode.onDateConfirmed = { sender, value in
            print("\(sender) confirmed \(value)")
        }

        uiDatePickerNode.onDateChanged = { sender, value in
            print("\(sender) changed \(value)")
        }

        uiDatePickerNode.layoutIfNeeded()

        let uiTimePickerNodeId: String = "uiTimePickerNodeId"
        let uiTimePickerNode: UiTimePickerNode = createComponent(["label": "Current time", "timeFormat": "hh:mm:ss p", "time": "13:13:13"], nodeId: uiTimePickerNodeId)
        uiTimePickerNode.position = SCNVector3(-0.125, 0.0, 0.0)

        uiTimePickerNode.onTimeConfirmed = { sender, value in
            print("\(sender) confirmed \(value)")
        }

        uiTimePickerNode.onTimeChanged = { sender, value in
            print("\(sender) changed \(value)")
        }

        uiTimePickerNode.layoutIfNeeded()
    }

    fileprivate func setupScrollViewTest() {
        // Group
        let groupId: String = "group"
        let group: UiGroupNode = createComponent(["debug": true], nodeId: groupId)

        // Scroll view
        let scrollViewId: String = "scroll_view"
        scrollView = createComponent([
            "alignment": "center-center",
            "debug": true,
            "scrollBounds": ["min": [-0.25,-0.45,-0.1], "max": [0.25,0.45,0.1]]
        ], nodeId: scrollViewId, parentId: groupId)

        // Scroll bar
        let scrollBarId: String = "scroll_bar"
        scrollBar = createComponent([
            "debug": false,
            "localPosition": [0.25, 0, 0],
            "width": 0.9
        ], nodeId: scrollBarId, parentId: scrollViewId)
        createGridWithIcons(parentId: scrollViewId)

        scrollView.layoutIfNeeded()
        scrollBar.layoutIfNeeded()

        // Button
        let button: UiButtonNode = createComponent([
            "localPosition": [0, 0.6, 0],
            "textSize": 0.05,
            "text": "Button"
        ], nodeId: "button", parentId: groupId)
        button.layoutIfNeeded()

        group.layoutIfNeeded()
    }

    fileprivate func createGridWithIcons(parentId: String? = nil) {

        let gridId = "grid"
        let grid: UiGridLayoutNode = createComponent([
            "columns": 14,
//            "orientation": "vertical",
            "defaultItemPadding": [0.015, 0.005, 0.015, 0.005],
            "alignment": "top-center"
        ], nodeId: gridId, parentId: parentId)

        SystemIcon.names.enumerated().forEach { (index, name) in
            let nodeId: String = "icon_\(index)"
            let _: UiImageNode = createComponent(["icon": name, "height": 0.04, "skipRaycast": false], nodeId: nodeId, parentId: gridId)
        }

        grid.layoutIfNeeded()
    }

    fileprivate var dropdownItems: [UiDropdownListItemNode] = []
    fileprivate func setupDropdownListTest() {
        let dropdownList = UiDropdownListNode(props: ["text": "dropdownListId", "localPosition": [0, 0.5, 0], "textSize": 0.0235, "maxCharacterLimit": 0])
        let dropdownListId = "dropdownListId"
        UiNodesManager.instance.registerNode(dropdownList, nodeId: dropdownListId)
        UiNodesManager.instance.addNodeToRoot(dropdownListId)
        dropdownList.layoutIfNeeded()

        for index in 0...16 {
            let dropdownItem: UiDropdownListItemNode
            if index % 4 == 0 {
                dropdownItem = UiDropdownListItemNode(props: ["label": "\(index). Very long text for dropDownListItem to check how this looks when list appears", "textSize": 0.03])
            } else {
                dropdownItem = UiDropdownListItemNode(props: ["label": "\(index). Very short text", "textSize": 0.03])
            }
            dropdownItems.append(dropdownItem)

            UiNodesManager.instance.registerNode(dropdownItem, nodeId: String(index))
            UiNodesManager.instance.addNode(String(index), toParent: dropdownListId)
        }
        dropdownList.onTap = { sender in
        }

        dropdownList.onSelectionChanged = { [weak self] sender, selectedItems in 
//            print("dropDown onSelectionChanged \(sender) \(selectedItem)")
            if let index = selectedItems.first {
                sender.text = self?.dropdownItems[index].label
                sender.layoutIfNeeded()
            }
        }
    }

    @discardableResult
    fileprivate func createComponent<T: TransformNode>(_ props: [String: Any], nodeId: String, parentId: String? = nil) -> T {
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
//        scrollView.scrollValue = abs(scrollBarPosition)
//        scrollBar.thumbSize = max(0.1, abs(scrollBarSize))
//        scrollView.layoutIfNeeded()
    }
}
