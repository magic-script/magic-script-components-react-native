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
import SpriteKit

@objc open class UiTextEditNode: UiNode {

    static fileprivate let defaultSize: CGSize = CGSize(width: 0.05, height: 0.02)

    @objc var text: String? {
        get { return labelNode.text }
        set { labelNode.text = newValue; setNeedsLayout() }
    }
    @objc var textColor: UIColor {
        get { return labelNode.textColor }
        set { labelNode.textColor = newValue }
    }
    @objc var textSize: CGFloat {
        get { return labelNode.textSize }
        set { labelNode.textSize = newValue; setNeedsLayout() }
    }
    @objc var style: FontStyle {
        get { return labelNode.style }
        set { labelNode.style = newStyle; setNeedsLayout() }
    }
    @objc var weight: FontWeight {
       get { return labelNode.weight }
       set { labelNode.weight = newStyle; setNeedsLayout() }
    }
    @objc var charLimit: Int {
        get { return labelNode.charLimit }
        set { labelNode.charLimit = newValue; setNeedsLayout() }
    }
    @objc var charSpacing: CGFloat {
        get { return labelNode.charSpacing }
        set { labelNode.charSpacing = newValue; setNeedsLayout() }
    }
    @objc var lineSpacing: CGFloat {
        get { return labelNode.lineSpacing }
        set { labelNode.lineSpacing = newValue; setNeedsLayout() }
    }
    @objc var allCaps: Bool {
        get { return labelNode.allCaps }
        set { labelNode.allCaps = newValue; setNeedsLayout() }
    }
    @objc var textAlignment: HorizontalTextAlignment {
        get { return labelNode.textAlignment }
        set { labelNode.textAlignment = newValue; setNeedsLayout() }
    }
    // @objc var cursorEdgeScrollMode: CursorEdgeScrollMode
    @objc var textPadding: UIEdgeInsets {
        // Defaults to half the text size unless explicitly set.
        didSet { setNeedsLayout() }
    }
    @objc var hint: String? {
        get { return hintNode.text }
        set { hintNode.text = newValue; setNeedsLayout() }
    }
    @objc var hintColor: UIColor {
        get { return hintNode.textColor }
        set { hintNode.textColor = newValue; setNeedsLayout() }
    }
    @objc var multiline: Bool {
        get { return labelNode.wrap }
        set { labelNode.wrap = newValue; setNeedsLayout() }
    }
    @objc var password: Bool {
        get { return labelNode.password }
        set { labelNode.password = newValue; setNeedsLayout() }
    }
    @objc var scrolling: Bool = true {
        didSet { setNeedsLayout() }
    }
    @objc var textEntry: TextEntryMode = .normal
    @objc var scrollBarVisibility: ScrollBarVisibility = .auto
    @objc var scrollSpeed: CGFloat = 1.0
    @objc var scrollValue: CGFloat = 0.0
    @objc var fontSize: CGFloat = 0.02
    @objc var tracking: Int = 50 // not supported by Lumin yet

    @objc public var onTap: ((_ sender: UiNode) -> (Void))?
    @objc public var onTextChanged: ((_ sender: UiNode, _ text: String) -> (Void))?

    fileprivate var labelNode: LabelNode!
    fileprivate var hintNode: LabelNode!
    fileprivate var outlineNode: SCNNode!
    fileprivate var reloadOutline: Bool

    @objc override var canHaveFocus: Bool {
        return enabled
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        if outlineNode == nil {
            reloadOutlineNode()
        }

        outlineNode?.isHidden = false
    }

    @objc override func leaveFocus() {
        super.leaveFocus()
        outlineNode?.isHidden = true
    }

    @objc override func setupNode() {
        super.setupNode()
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

        if let textAlignment = Convert.toHorizontalTextAlignment(props["textAlignment"]) {
            self.textAlignment = textAlignment
        }

        if let charLimit = Convert.toInt(props["charLimit"]) {
            self.charLimit = charLimit
        }

        if let charSpacing = Convert.toCGFloat(props["charSpacing"]) {
            self.charSpacing = charSpacing
        }

        if let lineSpacing = Convert.toCGFloat(props["lineSpacing"]) {
            self.lineSpacing = lineSpacing
        }

        if let textPadding = Convert.toPadding(props["textPadding"]) {
            self.textPadding = textPadding
        }

        if let hint = Convert.toString(props["hint"]) {
            self.hint = hint
        }

        if let hintColor = Convert.toColor(props["hintColor"]) {
            self.hintColor = hintColor
        }

        if let multiline = Convert.toBool(props["multiline"]) {
            self.multiline = multiline
        }

        if let password = Convert.toBool(props["password"]) {
            self.password = password
        }

        if let scrolling = Convert.toBool(props["scrolling"]) {
            self.scrolling = scrolling
        }

        if let textEntry = Convert.toTextEntryMode(props["textEntry"]) {
            self.textEntry = textEntry
        }

        if let scrollBarVisibility = Convert.toScrollBarVisibility(props["scrollBarVisibility"]) {
            self.scrollBarVisibility = scrollBarVisibility
        }

        if let scrollSpeed = Convert.toCGFloat(props["scrollSpeed"]) {
            self.scrollSpeed = scrollSpeed
        }

        if let scrollValue = Convert.toCGFloat(props["scrollValue"]) {
            self.scrollValue = scrollValue
        }

        if let fontParams = props["fontParams"] as? [String: Any] {
            if let style = Convert.toFontStyle(fontParams["style"]) {
                self.style = style
            }

            if let weight = Convert.toFontWeight(fontParams["weight"]) {
                self.weight = weight
            }

            if let fontSize = Convert.toCGFloat(fontParams["fontSize"]) {
                self.fontSize = fontSize
            }

            if let tracking = Convert.toInt(fontParams["tracking"]) {
                self.tracking = tracking
            }

            if let allCaps = Convert.toBool(fontParams["allCaps"]) {
                self.allCaps = allCaps
            }
        }
    }

    @objc override func updateLayout() {
        labelNode.reload()
        if hasFocus && reloadOutline {
            reloadOutline = false
            reloadOutlineNode()
        }
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }

    fileprivate func reloadOutlineNode() {
        let size = getSize()

        outlineNode?.removeFromParentNode()

        let radius: CGFloat = 0.5
        let thickness: CGFloat = 0.05 * min(size.width, size.height)
        guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
        outlineNode = NodesFactory.createOutlineNode(width: size.width, height: size.height, cornerRadius: radius, thickness: thickness)
        contentNode.addChildNode(outlineNode)
    }
}

