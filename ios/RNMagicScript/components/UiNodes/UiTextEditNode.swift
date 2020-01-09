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

@objc open class UiTextEditNode: UiNode {
    fileprivate var _text: String?
    @objc var text: String? {
        get { return _text }
        set { updateText(newValue) }
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
        didSet { updateText(labelNode.text) }
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
        set { hintNode.textColor = newValue }
    }
    @objc var multiline: Bool {
        get { return labelNode.multiline }
        set { labelNode.multiline = newValue; hintNode.multiline = newValue; setNeedsLayout() }
    }
    @objc var password: Bool = false {
        didSet { updateText(_text); setNeedsLayout() }
    }
    @objc var scrolling: Bool = false {
        didSet { setNeedsLayout() }
    }
    @objc var textEntry: TextEntryMode = .normal
    @objc var scrollBarVisibility: ScrollBarVisibility = .auto
    fileprivate var _scrollSpeed: CGFloat = 0.5
    @objc var scrollSpeed: CGFloat {
        get { return _scrollSpeed }
        set { _scrollSpeed = max(0.0, newValue) }
    }
    fileprivate var _scrollValue: CGFloat = 0.0
    @objc var scrollValue: CGFloat {
        get { return _scrollValue }
        set { _scrollValue = max(0.0, min(newValue, 1.0)) }
    }
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
        set {
            (backgroundNode.geometry as? SCNPlane)?.width = newValue
            let size = CGSize(width: newValue, height: height)
            labelNode.boundsSize = size
            hintNode.boundsSize = size
            reloadOutline = true
            updateLine = true
            setNeedsLayout()
        }
    }
    @objc var height: CGFloat {
        get { return labelNode.boundsSize.height }
        set {
            (backgroundNode.geometry as? SCNPlane)?.height = newValue
            let size = CGSize(width: width, height: newValue)
            labelNode.boundsSize = size
            hintNode.boundsSize = size
            reloadOutline = true
            updateLine = true
            setNeedsLayout()
        }
    }

    @objc public var onTextChanged: ((_ sender: UiNode, _ text: String) -> (Void))?

    fileprivate var labelNode: LabelNode!
    fileprivate var hintNode: LabelNode!
    fileprivate var backgroundNode: SCNNode!
    fileprivate var lineNode: SCNNode!
    fileprivate var outlineNode: SCNNode!
    fileprivate var reloadOutline: Bool = false
    fileprivate var updateLine: Bool = true

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

    @discardableResult
    @objc override func leaveFocus(onBehalfOf node: UiNode? = nil) -> Bool {
        let result = super.leaveFocus(onBehalfOf: node)
        outlineNode?.isHidden = true
        return result
    }

    @objc override func setupNode() {
        super.setupNode()

        backgroundNode = NodesFactory.createPlaneNode(width: 0, height: 0, image: ImageAsset.textEditBackground.image)
        contentNode.addChildNode(backgroundNode)

        lineNode = NodesFactory.createLinesNode(vertices: [SCNVector3(x: 0, y: 0, z: 0), SCNVector3(x: 1, y: 0, z: 0)], color: .white)
        lineNode.renderingOrder = 1
        lineNode.geometry?.firstMaterial?.readsFromDepthBuffer = false
        lineNode.scale = SCNVector3(x: 0, y: 1, z: 1)
        contentNode.addChildNode(lineNode)

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.renderingOrder = 1
        let defaultCharSpacing: CGFloat = 0.005
        let defaultTextSize: CGFloat = 0.02
        let defaultTextPadding = UIEdgeInsets(top: 0.003, left: 0.003, bottom: 0.003, right: 0.003)
        labelNode.charSpacing = defaultCharSpacing
        labelNode.textPadding = defaultTextPadding
        labelNode.textSize = defaultTextSize
        contentNode.addChildNode(labelNode)

        hintNode = LabelNode()
        hintNode.renderingOrder = 1
        hintNode.charSpacing = defaultCharSpacing
        hintNode.textColor = UIColor(white: 0.75, alpha: 0.75)
        hintNode.textPadding = defaultTextPadding
        hintNode.textSize = defaultTextSize
        contentNode.addChildNode(hintNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        // 'charLimit' prop must be updated prior to 'text' prop
        if let charLimit = Convert.toInt(props["charLimit"]) {
            self.charLimit = charLimit
        }

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

        if let fontParams = props["fontParameters"] as? [String: Any] {
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
        return CGSize(width: width, height: height)
    }

    @objc override func updateLayout() {
        // Set opacity in order to show or hide background image. Setting .isHidden to true
        // causes that the area of text edit is not fully clickable.
        backgroundNode.opacity = multiline ? 1.0 : 0.01

        if updateLine {
            updateLine = false
            lineNode.scale = SCNVector3(x: Float(width), y: 1, z: 1)
            lineNode.position = SCNVector3(x: -0.5 * Float(width), y: -0.5 * Float(height), z: 0)
        }

        let hasText: Bool = (text?.count ?? 0 > 0)
        if hasText {
            labelNode.isHidden = false
            hintNode.isHidden = true
            labelNode.reload()
            labelNode.readsFromDepthBuffer = false
        } else {
            labelNode.isHidden = true
            hintNode.isHidden = false
            hintNode.reload()
            hintNode.readsFromDepthBuffer = false
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

    fileprivate func updateText(_ text: String?) {
        if let string = text, string.count > charLimit, charLimit > 0 {
            _text = String(string.prefix(charLimit))
        } else {
            _text = text
        }

        if let value = _text, password {
            labelNode.text = String(Array<Character>(repeating: "•", count: value.count))
        } else {
            labelNode.text = _text
        }
        setNeedsLayout()
    }

    fileprivate func reloadOutlineNode() {
        let size = getSize()

        outlineNode?.removeFromParentNode()

        let radius: CGFloat = 0.02
        let thickness: CGFloat = 0.004
        guard size.width > 0 && size.height > 0 && thickness > 0 else { return }
        outlineNode = NodesFactory.createOutlineNode(width: size.width, height: size.height, cornerRadius: radius, thickness: thickness)
        contentNode.addChildNode(outlineNode)
    }
}

extension UiTextEditNode: InputDataProviding {
    var value: Any? {
        get { return text }
        set {
            if let newText = newValue as? String, newText != text {
                text = newText
                layoutIfNeeded()
                onTextChanged?(self, newText)
            }
        }
    }
    var placeholder: String? { return hint }
    // var charLimit: Int { get }
    // var multiline: Bool { get }
    // var password: Bool { get }
    var autocapitalizationType: UITextAutocapitalizationType? {
        if allCaps {
            return UITextAutocapitalizationType.allCharacters
        }

        if textEntry == .email {
            return UITextAutocapitalizationType.none
        }

        return nil
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
