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
import SceneKit

struct PrismContextMenuBuilder {
    static func build(for prism: Prism, prismInteractor: PrismInteracting) -> PrismContextMenu? {
        let prismContextMenu = PrismContextMenu()
        prismContextMenu.prism = prism
        prismContextMenu.prismInteractor = prismInteractor
        return prismContextMenu
    }
}

class PrismContextMenu: UiNode {
    var text: String = "" {
        didSet {
            titleNode.text = text
            titleNode.layoutIfNeeded()
            setNeedsLayout()
            updateLayout()
        }
    }

    weak var prism: Prism?

    weak var prismInteractor: PrismInteracting? {
        didSet {
            actionButton.onActivate = { [weak self] _ in
                if let relatedPrism = self?.prism {
                    self?.prismInteractor?.toggleInteractions(for: relatedPrism)
                }
            }
        }
    }

    private(set) var linearLayout: UiLinearLayoutNode!
    private(set) var titleNode: UiTextNode!
    private(set) var actionButton: UiButtonNode!

    override func setupNode() {
        super.setupNode()

        linearLayout = UiLinearLayoutNode()
        linearLayout.alignment = .centerCenter
        linearLayout.layoutOrientation = .vertical
        linearLayout.defaultItemAlignment = .centerCenter
        linearLayout.defaultItemPadding = UIEdgeInsets(top: 0.005, left: 0.0, bottom: 0.005, right: 0.0)

        titleNode = UiTextNode()
        titleNode.textSize = 0.055

        actionButton = UiButtonNode()
        actionButton.height = 0.11
        actionButton.buttonType = .icon
        actionButton.iconType = "genericThreeDimensional"

        linearLayout.addChild(titleNode)
        linearLayout.addChild(actionButton)

        contentNode.addChildNode(linearLayout)
        contentNode.resetClippingPlanesShaderModifiers(recursive: true)

        alignment = .centerCenter

        let billboardConstraint = SCNBillboardConstraint()
        billboardConstraint.freeAxes = [.Y]
        constraints = [billboardConstraint]
    }

    @objc override func _calculateSize() -> CGSize {
        return linearLayout.getSize()
    }

    @objc override func updateLayout() {
        linearLayout.layoutIfNeeded()
        super.updateLayout()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        linearLayout.setNeedsLayout()
        titleNode.setNeedsLayout()
        actionButton.setNeedsLayout()
    }

    override func hitTest(ray: Ray) -> HitTestResult? {
        transform = presentation.transform
        return linearLayout.hitTest(ray: ray)
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        linearLayout.setDebugMode(debug)
        titleNode.setDebugMode(debug)
        actionButton.setDebugMode(debug)
    }
}
