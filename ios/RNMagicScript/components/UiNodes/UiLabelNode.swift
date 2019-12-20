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

class UiLabelNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.0167

    @objc override var alignment: Alignment {
        get { return .centerRight }
        set { }
    }
    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; setNeedsLayout() }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { labelNode.textColor = textColor; setNeedsLayout() }
    }
    @objc var textSize: CGFloat = 0 {
        didSet { updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }
    @objc var width: CGFloat = 0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0 {
        didSet { updateLabelTextSizeBasedOnHeight(); setNeedsLayout() }
    }

    fileprivate var labelNode: LabelNode!

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.textAlignment = .center
        labelNode.defaultTextSize = UiLabelNode.defaultTextSize
        contentNode.addChildNode(labelNode)
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

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let labelSize = labelNode.getSize()
        let buttonToTextHeightMultiplier: CGFloat = 1.1
        let contentWidth: CGFloat = (width > 0) ? width : labelSize.width + buttonToTextHeightMultiplier * labelSize.height
        let contentHeight: CGFloat = (height > 0) ? height : buttonToTextHeightMultiplier * labelSize.height
        print("BUKA UiLabelNode \(CGSize(width: contentWidth, height: contentHeight))")
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        let _ = labelNode.getSize()
        labelNode.reload()
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }

    fileprivate func updateLabelTextSizeBasedOnHeight() {
        guard textSize == 0 && height > 0 else  {
            labelNode.textSize = textSize
            return
        }

        labelNode.textSize = max(0, 0.333 * height)
    }
}
