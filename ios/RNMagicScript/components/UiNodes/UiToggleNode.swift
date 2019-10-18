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

import SceneKit

@objc open class UiToggleNode: UiNode {

    static let defaultSize: CGSize = CGSize(width: 0.07337, height: 0.03359)

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; setNeedsLayout() }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { labelNode.textColor = textColor }
    }
    @objc var textSize: CGFloat {
        get { return labelNode.textSize }
        set { labelNode.textSize = newValue; setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var on: Bool = false {
        didSet { toggleGeometry.firstMaterial?.diffuse.contents = on ? ImageAsset.toggleOn.image : ImageAsset.toggleOff.image }
    }

    @objc public var onChanged: ((_ sender: UiNode, _ on: Bool) -> (Void))?

    fileprivate var labelNode: LabelNode!

    fileprivate var toggleNode: SCNNode!
    fileprivate var toggleGeometry: SCNPlane!

    @objc override var canHaveFocus: Bool {
        return enabled
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        on = !on
        leaveFocus()
        onChanged?(self, on)
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.textAlignment = .left
        contentNode.addChildNode(labelNode)

        let toggleSize = getToggleSize()
        toggleGeometry = SCNPlane(width: toggleSize.width, height: toggleSize.height)
        toggleGeometry.firstMaterial?.lightingModel = .constant
        toggleGeometry.firstMaterial?.diffuse.contents = ImageAsset.toggleOff.image
        toggleGeometry.firstMaterial?.isDoubleSided = false
        toggleNode = SCNNode(geometry: toggleGeometry)
        contentNode.addChildNode(toggleNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let text = Convert.toString(props["text"]) {
            self.text = text
        }

        if let textColor = Convert.toColor(props["textColor"]) {
            self.textColor = textColor
        }

        if let textSize = Convert.toCGFloat(props["textSize"]) {
            self.textSize = textSize
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let on = Convert.toBool(props["on"]) {
            self.on = on
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let labelSize = labelNode.getSize()
        let toggleSize = getToggleSize()
        let textToggleGap: CGFloat = 0.75 * toggleSize.width
        let textMargin: CGFloat = (labelSize.width > 0 && labelSize.height > 0) ? textToggleGap : 0

        let heightContent: CGFloat = max(toggleSize.height, labelSize.height)
        let widthContent: CGFloat = toggleSize.width + labelSize.width + textMargin
        return CGSize(width: widthContent, height: heightContent)
    }

    @objc override func updateLayout() {
        labelNode.reload()

        let size = getSize()
        let labelSize = labelNode.getSize()
        let toggleSize = getToggleSize()
        toggleGeometry.width = toggleSize.width
        toggleGeometry.height = toggleSize.height

        let x: Float = -Float(size.width - 0.5 * (labelSize.width + toggleSize.width))
        labelNode.position = SCNVector3(x, 0, 0)
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }

    fileprivate func getToggleSize() -> CGSize {
        let toggleHeight: CGFloat = (height > 0) ? height : UiToggleNode.defaultSize.height
        let toggleWidth: CGFloat = (UiToggleNode.defaultSize.width / UiToggleNode.defaultSize.height) * toggleHeight
        return CGSize(width: toggleWidth, height: toggleHeight)
    }
}
