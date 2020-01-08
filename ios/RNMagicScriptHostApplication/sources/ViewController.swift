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
        setupButton()
        setupCircleConfirmation()
        setupScrollView()
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
    }

    fileprivate let rectSize = CGSize(width: 0.4, height: 0.2)
    fileprivate var rectScale: CGFloat = 1.0
    fileprivate var rectLayout: UiRectLayoutNode!
    fileprivate func setupButton() {
        let button: UiButtonNode = createComponent([
            "enabled": true,
            "roundness": 0.5,
            "text": "Button",
            "textColor": [0,1,0,1],
            "textSize": 0.08,
            "width": rectSize.width,
            "height": rectSize.height,
            "localPosition": [0, 1.0, 0]
        ], nodeId: "button_id", parentId: groupId)

        button.onActivate = {
            print($0)
        }
    }

    fileprivate func setupSlider() {
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

    fileprivate func setupCircleConfirmation() {
        let _: UiCircleConfirmationNode = createComponent([
            "localPosition": [0, 0.7, 0],
            "radius": rectSize.height / 2
        ], nodeId: "circleConfirmation_id", parentId: groupId)
    }

    fileprivate func setupScrollView() {
        let size = CGSize(width: 1.0, height: 1.25)
        let scrollViewId = "scroll_view_id"
        let scrollView: UiScrollViewNode = createComponent([
            "localPosition": [0, -0.1, 0],
            "scrollBarVisibility": "always",
            "scrollBounds": [
                "min": [-0.5 * size.width, -0.5 * size.height, -0.1],
                "max": [0.5 * size.width, 0.5 * size.height, 0.1]
            ],
            "scrollDirection": "vertical"
        ], nodeId: scrollViewId, parentId: groupId)
        scrollView.onScrollChanged = { node, value in
            print("scroll: \(value)")
        }


        let barLength: CGFloat = size.height
        let barThickness: CGFloat = 0.04
        let _: UiScrollBarNode = createComponent([
            "localPosition": [0.5 * (size.width + barThickness), 0, 0],
            "orientation": "vertical",
            "width": barLength,
            "height": barThickness
        ], nodeId: "scroll_bar_id", parentId: scrollViewId)

        let linearLayoutId = "linear_layout_id"
        let _: UiLinearLayoutNode = createComponent([
            "alignment": "center-center",
            "defaultItemAlignment": "center-center",
            "orientation": "vertical"
        ], nodeId: linearLayoutId, parentId: scrollViewId)

        let colors = [
            [1,1,0.5,1],
            [1,0.5,1,1],
            [0.5,1,1,1],
            [1,0.5,0.5,1],
            [0.5,0.5,1,1],
            [0.5,1,0.5,1],
            [0.75,0.75,0.75,1],
            [1,1,1,1]
        ]
        for (index, color) in colors.enumerated() {
            let imageId = "image_\(index)"
            let _: UiImageNode = createComponent([
                "width": size.width,
                "height": size.width,
                "color": color
            ], nodeId: imageId, parentId: linearLayoutId)
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

//        DispatchQueue.main.async() { [weak self] in
//            UiNodesManager.instance.updateLayout()
//        }
    }
}
