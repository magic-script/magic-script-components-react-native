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
    }

    let groupId: String = "group"
    fileprivate func setupScene() {
        let _: UiGroupNode = createComponent(["localScale": [0.5, 0.5, 0.5]], nodeId: groupId)
//        setupScrollViewTest()
//        setupDropdownListTest()
//        setupUiDatePickerNodeTest()
//        setupUiColorPickerNodeTest()
//        setupTextEditTest()
//        setupUiListViewNodeTest()
//        setupAudioNodeTest()
        setupUiCircleConfirmationNodeTest()
        UiNodesManager.instance.updateLayout()
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

    fileprivate func setupUiCircleConfirmationNodeTest() {
        let circleConfirmationNodeId = "circleConfirmationNodeId"
        let circleConfirmationNode: UiCircleConfirmationNode = createComponent(["value": 0.0], nodeId: circleConfirmationNodeId, parentId: groupId)

        circleConfirmationNode.setNeedsLayout()
        circleConfirmationNode.layoutIfNeeded()
    }

    fileprivate func setupUiListViewNodeTest() {
        let listViewId: String = "listView"
        let listView: UiListViewNode = createComponent(["debug": true, "defaultItemAlignment": "center-left", "defaultItemPadding": [0, 0.04, 0, 0.0]], nodeId: listViewId, parentId: groupId)
        listView.width = 0
        listView.height = 1.0

        let animals = ["bear", "sheep", "pig", "cat", "tiger", "snake", "dog", "rat", "octopus"]
        for (index, animal) in animals.enumerated() {
            let itemNodeId: String = "listViewItem_\(index)"
            let _: UiListViewItemNode = createComponent(["backgroundColor": [0.95, 0.85, 0.75, 0.25]], nodeId: itemNodeId, parentId: listViewId)
            let buttonId: String = "button_\(index)"
            let _: UiButtonNode = createComponent(["text": animal, "textSize": 0.08], nodeId: buttonId, parentId: itemNodeId)
        }
    }

    fileprivate func setupUiColorPickerNodeTest() {
        let uiColorPickerNodeId: String = "uiColorPickerNodeId"
        let uiColorPickerNode: UiColorPickerNode = createComponent(["color": [0.95, 0.85, 0.75, 1]], nodeId: uiColorPickerNodeId)
        uiColorPickerNode.position = SCNVector3(0.0, 0.250, 0.0)

        uiColorPickerNode.onColorChanged = { sender, value in
            print("\(sender) changed \(value)")
        }

        uiColorPickerNode.onColorConfirmed = { sender, value in
            print("\(sender) confirmed \(value)")
        }

        uiColorPickerNode.onColorCanceled = { sender, value in
            print("\(sender) canceled ")
        }

        uiColorPickerNode.layoutIfNeeded()
    }

    fileprivate func setupUiDatePickerNodeTest() {

        let uiDatePickerNodeId: String = "uiDatePickerNodeId"
        let uiDatePickerNode: UiDatePickerNode = createComponent(["defaultDate": "06/13/1983", "label": "Birth date", "dateFormat": "DD/YYYY"], nodeId: uiDatePickerNodeId, parentId: groupId)
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

    fileprivate var scrollView: UiScrollViewNode!
    fileprivate var scrollBar: UiScrollBarNode!
    fileprivate var value1: CGFloat = 0.0
    fileprivate var value2: CGFloat = 0.0
    fileprivate var linearLayout: UiLinearLayoutNode!
    fileprivate let contentSize: CGFloat = 1
    fileprivate func setupScrollViewTest() {
        let imageSize = CGSize(width: contentSize, height: contentSize)

        // Toggle
        let toggle: UiToggleNode = createComponent([
            "localPosition": [0.2, 0.8, 0],
            "height": 0.08,
            "textSize": 0.08,
            "on": true,
            "text": "Vertical scroll"
        ], nodeId: "toggle", parentId: groupId)
        toggle.onChanged = { [weak self] sender, on in self?.onOrientationChange(on) }

        // Scroll view
        let scrollViewId: String = "scroll_view"
        scrollView = createComponent([
            "alignment": "center-center",
            "debug": true,
            "scrollBarVisibility": "auto",
        ], nodeId: scrollViewId, parentId: groupId)

        // Scroll bar
        let scrollBarId: String = "scroll_bar"
        scrollBar = createComponent([
            "debug": false,
            "height": 0.04,
        ], nodeId: scrollBarId, parentId: scrollViewId)

        // Linear
        linearLayout = createLinearLayoutWithImages(imageSize, parentId: scrollViewId)
        onOrientationChange(true)
    }

    fileprivate func onOrientationChange(_ isVertical: Bool) {
        let direction: ScrollDirection = isVertical ? .vertical : .horizontal
        let orientation: Orientation = isVertical ? .vertical : .horizontal
        let size: CGSize = isVertical ? CGSize(width: contentSize, height: 1.25 * contentSize) : CGSize(width: 1.25 * contentSize, height: contentSize)
        scrollView.scrollDirection = direction
        scrollBar.scrollOrientation = orientation
        linearLayout.layoutOrientation = orientation

        scrollView.scrollBounds = (min: SCNVector3(-0.25 * size.width, -0.5 * size.height, -0.1),
                                   max: SCNVector3(0.75 * size.width, 0.5 * size.height, 0.1))
        scrollBar.width = 1.25 * contentSize
        let bounds: CGRect = scrollView.getBounds()
        if orientation == .vertical {
            scrollBar.localPosition = SCNVector3(bounds.maxX - 0.5 * scrollBar.height, bounds.midY, 0)
        } else {
            scrollBar.localPosition = SCNVector3(bounds.midX, bounds.minY + 0.5 * scrollBar.height, 0)
        }

        scrollView.scrollValue = 0
        UiNodesManager.instance.updateLayout()
    }

    fileprivate func createLinearLayoutWithImages(_ imageSize: CGSize, parentId: String? = nil) -> UiLinearLayoutNode {
        let linearId = "linear"
        let linear: UiLinearLayoutNode = createComponent([
            "alignment": "top-right"
        ], nodeId: linearId, parentId: parentId)

        let alpha: CGFloat = 0.2
        let colors: [[CGFloat]] = [
            [1,1,0.5,alpha],
            [1,0.5,1,alpha],
            [0.5,1,1,alpha],
            [1,0.5,0.5,alpha],
            [0.5,0.5,1,alpha],
            [0.5,1,0.5,alpha],
            [0.75,0.75,0.25,alpha],
            [1,1,1,alpha]
        ]
        let _: UiButtonNode = createComponent(["textSize": 0.1, "text": "Button"], nodeId: "button", parentId: linearId)
        colors.enumerated().forEach { (index, rgba) in
            let nodeId: String = "image_\(index)"
            let _: UiImageNode = createComponent(["skipRaycast": true, "color": rgba, "width": imageSize.width, "height": imageSize.height], nodeId: nodeId, parentId: linearId)
        }

        return linear
    }

    fileprivate func setupTextEditTest() {
        // Group
        let groupId: String = "group"
        let _: UiGroupNode = createComponent(["localScale": [0.5, 0.5, 0.5]], nodeId: groupId)

        // TextEdit
        let textEditId: String = "text_edit"
        let _ : UiTextEditNode = createComponent([
            "alignment": "center-center",
            "debug": true,
            "multiline": true,
            "text": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            "textSize": 0.08,
            "width": 1.0,
            "height": 1.0,
        ], nodeId: textEditId, parentId: groupId)
    }

    fileprivate var audioNode : UiAudioNode!
    fileprivate var sliderNode : UiSliderNode!
    fileprivate func setupAudioNodeTest() {
        // Group
        let groupId: String = "group"
        let _: UiGroupNode = createComponent(["localScale": [0.5, 0.5, 0.5]], nodeId: groupId)

        // Audio node
        let audioNodeId: String = "audio"
        let audioURL: URL = Bundle.main.url(forResource: "bg_mono", withExtension: "mp3")!
//        let audioURL: URL = URL(string: "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3")!
        print("audioURL: \(audioURL.absoluteString)")
        audioNode = createComponent([
            "fileName": audioURL.absoluteString,
            "debug": true,
            "soundLooping": true,
            "soundVolumeLinear": 5.0,
            "spatialSoundEnable": true
        ], nodeId: audioNodeId, parentId: groupId)
        audioNode.action = .start

        sliderNode = createComponent([
            "localPosition": [0,-0.5,0],
            "height": 0.05,
            "width": 1,
            "max": 2.0,
        ], nodeId: "slider", parentId: groupId)
        sliderNode.onSliderChanged = { [weak self] sender, value in
            self?.value1 = value
            self?.audioNode.layoutIfNeeded()
        }
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

extension ViewController: ARSCNViewDelegate {
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        let deltaTime = time - lastTime
        lastTime = time
        guard deltaTime < 0.5 else { return }

        value1 += 0.4 * CGFloat(deltaTime)
        if value1 > CGFloat.pi {
            value1 -= 2.0 * CGFloat.pi
        }
//
//        value2 += CGFloat(deltaTime * 0.1)
//        if value2 > 1.0 {
//            value2 -= 2.0
//        }

        DispatchQueue.main.async() { [weak self] in
            guard let strongSelf = self else { return }
//            strongSelf.scrollView.scrollValue = abs(strongSelf.scrollBarPosition)
//            strongSelf.scrollBar.thumbSize = max(0.1, abs(strongSelf.scrollBarSize))

            let radius: CGFloat = 1
            let angle: CGFloat = strongSelf.value1
            let x: CGFloat = radius * sin(angle)
            let y: CGFloat = 0
            let z: CGFloat = radius * cos(angle)
//            let quat = SCNQuaternion.fromAxis(SCNVector3(0,1,0), andAngle: Float(angle))
//            strongSelf.audioNode.spatialSoundPosition = UiAudioNode.SpatialSoundPosition(channel: 0, position: SCNVector3(x, y, z))
//            strongSelf.audioNode.spatialSoundDirection = UiAudioNode.SpatialSoundDirection(channel: 0, direction: quat)
            UiNodesManager.instance.updateLayout()
        }
    }
}
