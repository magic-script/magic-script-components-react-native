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

@objc open class UiDropdownListItemNode: UiNode {
    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }
    fileprivate var _label: String?
    @objc open var label: String? {
        get { return _label }
        set {
            _label = newValue
            labelNode.text = alignTextLength(newValue, maxCharacterLimit)
            setNeedsLayout()
        }
    }
    @objc open var id: Int = 0
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { labelNode.textColor = textColor; setNeedsLayout() }
    }
    @objc var textSize: CGFloat = 0 {
        didSet { labelNode.textSize = textSize; setNeedsLayout() }
    }
    @objc var maxCharacterLimit: Int = 0 {
        didSet { labelNode.text = alignTextLength(label, maxCharacterLimit); setNeedsLayout() }
    }
    @objc var selected: Bool = false {
        didSet {
            labelNode.fontWeight = selected ? .bold : .regular
            labelNode.textColor = UIColor(white: selected ? 1.0 : 0.75, alpha: 1.0)
            setNeedsLayout()
        }
    }

    var tapHandler: DropdownListItemTapHandling?

    fileprivate var labelNode: LabelNode!

    @objc override func activate() {
        super.activate()
        tapHandler?.handleTap(self)
    }

    @objc override func setupNode() {
        super.setupNode()
        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        contentNode.addChildNode(labelNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let label = Convert.toString(props["label"]) {
            self.label = label
        }

        if let id = Convert.toInt(props["id"]) {
            self.id = id
        }

        if let selected = Convert.toBool(props["selected"]) {
            self.selected = selected
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let buttonToTextHeightMultiplier: CGFloat = 1.4
        let labelSize = labelNode.getSize()
        let contentWidth: CGFloat = labelSize.width + buttonToTextHeightMultiplier * labelSize.height
        let contentHeight: CGFloat = buttonToTextHeightMultiplier * labelSize.height
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override func updateLayout() {
        labelNode.reload()
    }

    fileprivate func alignTextLength(_ text: String?, _ maxCharacterLimit: Int) -> String? {
        guard let text = text else { return nil }
        if text.count > maxCharacterLimit && maxCharacterLimit > 0 {
            let trailingCharacters = "..."
            return text.prefix(maxCharacterLimit) + trailingCharacters
        }
        return text
    }

    func toggleSelection() {
        selected = !selected
        layoutIfNeeded()
    }
}
