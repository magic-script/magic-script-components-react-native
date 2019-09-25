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

    static let defaultSize: CGSize = CGSize(width: 0.05, height: 0.02)

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
        set { labelNode.textSize = newValue; hintNode.textSize = newValue; setNeedsLayout() }
    }
    @objc var charLimit: Int = 0 {
        didSet {
            guard charLimit > 0 else { return }
            if let string = text, string.count > charLimit {
                text = String(string.prefix(charLimit))
            }
        }
    }
    @objc var charSpacing: CGFloat {
        get { return labelNode.charSpacing }
        set { labelNode.charSpacing = newValue; hintNode.charSpacing = newValue; setNeedsLayout() }
    }
    @objc var lineSpacing: CGFloat {
        get { return labelNode.lineSpacing }
        set { labelNode.lineSpacing = newValue; hintNode.lineSpacing = newValue; setNeedsLayout() }
    }
    @objc var textAlignment: HorizontalTextAlignment {
        get { return labelNode.textAlignment }
        set { labelNode.textAlignment = newValue; hintNode.textAlignment = newValue; setNeedsLayout() }
    }
    // @objc var cursorEdgeScrollMode: CursorEdgeScrollMode
    @objc var textPadding: UIEdgeInsets {
        // Defaults to half the text size unless explicitly set.
        get { return labelNode.textPadding }
        set { labelNode.textPadding = newValue; hintNode.textPadding = newValue; setNeedsLayout() }
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
        get { return labelNode.multiline }
        set { labelNode.multiline = newValue; hintNode.multiline = newValue; setNeedsLayout() }
    }
    @objc var password: Bool = false {
        didSet { setNeedsLayout() }
    }
    @objc var scrolling: Bool = false {
        didSet { setNeedsLayout() }
    }
    @objc var textEntry: TextEntryMode = .normal
    @objc var scrollBarVisibility: ScrollBarVisibility = .auto
    @objc var scrollSpeed: CGFloat = 0.5
    @objc var scrollValue: CGFloat = 0.0
    @objc var style: FontStyle {
        get { return labelNode.fontStyle }
        set { labelNode.fontStyle = newValue; hintNode.fontStyle = newValue; setNeedsLayout() }
    }
    @objc var weight: FontWeight {
       get { return labelNode.fontWeight }
        set { labelNode.fontWeight = newValue; hintNode.fontWeight = newValue; setNeedsLayout() }
    }
    @objc var tracking: Int {
        get { return labelNode.tracking }
        set { labelNode.tracking = newValue; setNeedsLayout() }
    }
    @objc var allCaps: Bool {
        get { return labelNode.allCaps }
        set { labelNode.allCaps = newValue; hintNode.allCaps = newValue; setNeedsLayout() }
    }
    @objc var width: CGFloat {
        get { return labelNode.boundsSize.width }
        set { labelNode.boundsSize = CGSize(width: newValue, height: height); setNeedsLayout() }
    }
    @objc var height: CGFloat {
        get { return labelNode.boundsSize.height }
        set { labelNode.boundsSize = CGSize(width: width, height: newValue); setNeedsLayout() }
    }

    @objc public var onTap: ((_ sender: UiNode) -> (Void))?
    @objc public var onTextChanged: ((_ sender: UiNode, _ text: String) -> (Void))?

    fileprivate var labelNode: LabelNode!
    fileprivate var hintNode: LabelNode!
    fileprivate var outlineNode: SCNNode!
    fileprivate var reloadOutline: Bool = false

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

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()

        let defaultCharSpacing: CGFloat = 0.005
        let defaultTextSize: CGFloat = 0.02
        let defaultTextPadding = UIEdgeInsets(top: 0.03, left: 0.03, bottom: 0.03, right: 0.03)
        labelNode.charSpacing = defaultCharSpacing
        labelNode.textPadding = defaultTextPadding
        labelNode.textSize = defaultTextSize
        contentNode.addChildNode(labelNode)

        hintNode = LabelNode()
        hintNode.charSpacing = defaultCharSpacing
        hintNode.textColor = UIColor(white: 0.75, alpha: 0.5)
        hintNode.textPadding = defaultTextPadding
        hintNode.textSize = defaultTextSize
        contentNode.addChildNode(hintNode)
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
                // fontSize is the same as textSize
                self.textSize = fontSize
            }

            if let tracking = Convert.toInt(fontParams["tracking"]) {
                self.tracking = tracking
            }

            if let allCaps = Convert.toBool(fontParams["allCaps"]) {
                self.allCaps = allCaps
            }
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let localWidth: CGFloat = (width > 0) ? width : UiTextEditNode.defaultSize.width
        let localHeight: CGFloat = (height > 0) ? height : UiTextEditNode.defaultSize.height
        return CGSize(width: localWidth, height: localHeight)
    }

    @objc override func updateLayout() {
        let hasText: Bool = (text?.count ?? 0 > 0)
        if hasText {
            labelNode.isHidden = false
            hintNode.isHidden = true
            labelNode.reload()
        } else {
            labelNode.isHidden = true
            hintNode.isHidden = false
            hintNode.reload()
        }

        if hasFocus && reloadOutline {
            reloadOutline = false
            reloadOutlineNode()
        }
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
        hintNode.setDebugMode(debug)
    }

    fileprivate func reloadOutlineNode() {
        let size = getSize()

        outlineNode?.removeFromParentNode()

        let roundness: CGFloat = 0.5
        let minSize: CGFloat = min(size.width, size.height)
        let outlineOffset: CGFloat = 0.3 * minSize
        let radius: CGFloat = 0.5 * minSize * roundness
        let thickness: CGFloat = 0.05 * minSize
        guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
        outlineNode = NodesFactory.createOutlineNode(width: size.width + outlineOffset, height: size.height + outlineOffset, cornerRadius: radius, thickness: thickness)
        contentNode.addChildNode(outlineNode)
    }
}

extension UiTextEditNode: InputDataProviding {
    var value: Any? {
        get { return text }
        set {
            if let newText = newValue as? String {
                text = newText
                layoutIfNeeded()
            }
        }
    }
    var keyboardType: UIKeyboardType? {
        switch textEntry {
        case .email:
            return UIKeyboardType.emailAddress
        case .none:
            return nil
        case .normal:
            return UIKeyboardType.default
        case .numeric:
            return UIKeyboardType.numberPad
        case .url:
            return UIKeyboardType.URL
        }
    }
    var textContentType: UITextContentType? {
        if password {
            return UITextContentType.password
        }

        switch textEntry {
        case .email:
            return UITextContentType.emailAddress
        case .none:
            return nil
        case .normal:
            return nil
        case .numeric:
            return nil
        case .url:
            return UITextContentType.URL
        }
    }
}
