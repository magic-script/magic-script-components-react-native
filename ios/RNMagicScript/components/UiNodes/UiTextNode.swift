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

import SceneKit

@objc open class UiTextNode: UiNode {
    @objc override var anchorPosition: SCNVector3 {
        get { return self.pivot.position }
        set { }
    }
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
    @objc var textAlignment: HorizontalTextAlignment {
        get { return labelNode.textAlignment }
        set { labelNode.textAlignment = newValue; setNeedsLayout() }
    }
    @objc var charSpacing: CGFloat {
        get { return labelNode.charSpacing }
        set { labelNode.charSpacing = newValue; setNeedsLayout() }
    }
    @objc var lineSpacing: CGFloat {
        get { return labelNode.lineSpacing }
        set { labelNode.lineSpacing = newValue; setNeedsLayout() }
    }
    @objc var boundsSize: CGSize {
        get { return labelNode.boundsSize }
        set { labelNode.boundsSize = newValue; setNeedsLayout() }
    }
    @objc var wrap: Bool {
        get { return labelNode.multiline }
        set { labelNode.multiline = newValue; setNeedsLayout() }
    }
    @objc var style: FontStyle {
        get { return labelNode.fontStyle }
        set { labelNode.fontStyle = newValue; setNeedsLayout() }
    }
    @objc var weight: FontWeight {
        get { return labelNode.fontWeight }
        set { labelNode.fontWeight = newValue; setNeedsLayout() }
    }
    @objc var tracking: Int {
        get { return labelNode.tracking }
        set { labelNode.tracking = newValue; setNeedsLayout() }
    }
    @objc var allCaps: Bool {
        get { return labelNode.allCaps }
        set { labelNode.allCaps = newValue; setNeedsLayout() }
    }

    fileprivate var labelNode: LabelNode!

    @objc override func setupNode() {
        super.setupNode()
        alignment = Alignment.bottomLeft // default alignment of UiText

        assert(labelNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
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

        if let textAlignment = Convert.toHorizontalTextAlignment(props["textAlignment"]) {
            self.textAlignment = textAlignment
        }

        if let charSpacing = Convert.toCGFloat(props["charSpacing"]) {
            self.charSpacing = charSpacing
        }

        if let lineSpacing = Convert.toCGFloat(props["lineSpacing"]) {
            self.lineSpacing = lineSpacing
        }

        if let boundsSizeProps = props["boundsSize"] as? [String: Any] {
            if let boundsSize = Convert.toCGSize(boundsSizeProps["boundsSize"]) {
                self.boundsSize = boundsSize
            }

            if let wrap = Convert.toBool(boundsSizeProps["wrap"]) {
                self.wrap = wrap
            }
        }
        
        if let style = Convert.toFontStyle(props["style"]) {
            self.style = style
        }

        if let weight = Convert.toFontWeight(props["weight"]) {
            self.weight = weight
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
    }

    @objc override func _calculateSize() -> CGSize {
        return labelNode.getSize()
    }

    @objc override func updateLayout() {
        labelNode.reload()
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
    }
}
