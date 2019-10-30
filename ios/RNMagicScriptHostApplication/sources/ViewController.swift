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
        setupScrollViewTest()
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
    fileprivate var linearLayout: UiLinearLayoutNode!
    fileprivate let contentSize: CGFloat = 0.5

    fileprivate func setupScrollViewTest() {
        let imageSize = CGSize(width: contentSize, height: contentSize)

        // Group
        let groupId: String = "group"
        let _: UiGroupNode = createComponent(["debug": true], nodeId: groupId)

        // Toggle
        let toggle: UiToggleNode = createComponent([
            "localPosition": [0.15, 0.4, 0],
            "height": 0.04,
            "textSize": 0.04,
            "on": true,
            "text": "Vertical scroll"
        ], nodeId: "toggle", parentId: groupId)
        toggle.onChanged = { [weak self] sender, on in self?.onOrientationChange(on) }

        // Scroll view
        let scrollViewId: String = "scroll_view"
        scrollView = createComponent([
            "alignment": "center-center",
            "debug": true,
            "scrollBarVisibility": "always",
        ], nodeId: scrollViewId)

        // Scroll bar
        let scrollBarId: String = "scroll_bar"
        scrollBar = createComponent([
            "debug": false,
            "height": 0.02,
        ], nodeId: scrollBarId, parentId: scrollViewId)
//        createGridWithIcons(parentId: scrollViewId)

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

        scrollView.scrollBounds = (min: SCNVector3(-0.5 * size.width, -0.5 * size.height, -0.1),
                                   max: SCNVector3(0.5 * size.width, 0.5 * size.height, 0.1))
        scrollBar.width = 1.25 * contentSize
        let bounds: CGRect = scrollView.getBounds()
        if orientation == .vertical {
            scrollBar.localPosition = SCNVector3(bounds.maxX, bounds.midY, 0)
        } else {
            scrollBar.localPosition = SCNVector3(bounds.midX, bounds.minY, 0)
        }

        scrollView.scrollValue = 0.1
        UiNodesManager.instance.updateLayout()
    }

    fileprivate func createGridWithIcons(parentId: String? = nil) {
        let gridId = "grid"
        let grid: UiGridLayoutNode = createComponent([
            "columns": 14,
            "defaultItemPadding": [0.015, 0.005, 0.015, 0.005],
            "alignment": "center-center"
        ], nodeId: gridId, parentId: parentId)

        SystemIcon.names.enumerated().forEach { (index, name) in
            let nodeId: String = "icon_\(index)"
            let _: UiImageNode = createComponent(["icon": name, "height": 0.04, "skipRaycast": false], nodeId: nodeId, parentId: gridId)
        }

        grid.layoutIfNeeded()
    }

    fileprivate func createLinearLayoutWithImages(_ imageSize: CGSize, parentId: String? = nil) -> UiLinearLayoutNode {
        let linearId = "linear"
        let linear: UiLinearLayoutNode = createComponent([
//            "defaultItemPadding": [0.015, 0.005, 0.015, 0.005],
            "alignment": "center-center"
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
        colors.enumerated().forEach { (index, rgba) in
            let nodeId: String = "image_\(index)"
            let _: UiImageNode = createComponent(["color": rgba, "width": imageSize.width, "height": imageSize.height], nodeId: nodeId, parentId: linearId)
        }

        return linear
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

        scrollBarPosition += 0.4 * CGFloat(deltaTime)
        if scrollBarPosition > 1.0 {
            scrollBarPosition -= 2.0
        }

        scrollBarSize += CGFloat(deltaTime * 0.1)
        if scrollBarSize > 1.0 {
            scrollBarSize -= 2.0
        }

        DispatchQueue.main.async() { [weak self] in
            guard let strongSelf = self else { return }
            strongSelf.scrollView.scrollValue = abs(strongSelf.scrollBarPosition)
//            strongSelf.scrollBar.thumbSize = max(0.1, abs(strongSelf.scrollBarSize))
            UiNodesManager.instance.updateLayout()
        }
    }
}
