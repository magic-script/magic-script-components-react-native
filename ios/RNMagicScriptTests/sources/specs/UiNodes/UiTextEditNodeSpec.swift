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

import Quick
import Nimble
import SceneKit
@testable import RNMagicScriptHostApplication

class UiTextEditNodeSpec: QuickSpec {
    override func spec() {
        describe("UiTextEditNode") {
            var node: UiTextEditNode!

            beforeEach {
                node = UiTextEditNode(props: [:])
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.topLeft))
                    expect(node.text).to(beNil())
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.textSize).to(beCloseTo(0.02))
                    expect(node.charSpacing).to(beCloseTo(0.005))
                    expect(node.lineSpacing).to(beCloseTo(1))
                    expect(node.textAlignment).to(equal(HorizontalTextAlignment.left))
                    expect(node.textPadding).to(beCloseTo(UIEdgeInsets(top: 0.003, left: 0.003, bottom: 0.003, right: 0.003)))
                    expect(node.hint).to(beNil())
                    expect(node.hintColor).to(beCloseTo(UIColor(white: 0.75, alpha: 0.75)))
                    expect(node.multiline).to(beFalse())
                    expect(node.password).to(beFalse())
                    expect(node.scrolling).to(beFalse())
                    expect(node.textEntry).to(equal(TextEntryMode.normal))
                    expect(node.scrollBarVisibility).to(equal(ScrollBarVisibility.auto))
                    expect(node.scrollSpeed).to(beCloseTo(0.5))
                    expect(node.scrollValue).to(beCloseTo(0.0))
                    expect(node.style).to(equal(FontStyle.normal))
                    expect(node.weight).to(equal(FontWeight.regular))
                    expect(node.tracking).to(equal(50))
                    expect(node.allCaps).to(beFalse())
                    expect(node.width).to(beCloseTo(0))
                    expect(node.height).to(beCloseTo(0))
                }
            }

            context("initialization") {
                it("should throw exception if 'setupNode' has been called more than once") {
                    expect(node.setupNode()).to(throwAssertion())
                }
            }

            context("update properties") {
                it("should update 'text' prop") {
                    let referenceText: String = "Info text"
                    node.update(["text" : referenceText])
                    expect(node.text).to(equal(referenceText))

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.text).to(equal(referenceText))
                }

                it("should update 'textColor' prop") {
                    let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["textColor" : referenceTextColor.toArrayOfFloat])
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.isLayoutNeeded).to(beFalse())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.textColor).to(beCloseTo(referenceTextColor))
                }

                it("should update 'textSize' prop") {
                    let referenceTextSize = 11.0
                    node.update(["textSize" : referenceTextSize])
                    expect(node.textSize).to(beCloseTo(referenceTextSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.textSize).to(beCloseTo(referenceTextSize))
                }

                it("should update 'charLimit' prop") {
                    let referenceText = "abcdefghijklmnopqrstuwxyz"
                    let referenceLimitedText = "abcdefghijklmnopqrst"
                    let refrerenceCharLimit: Int = 20
                    node.update(["text": referenceText, "charLimit" : refrerenceCharLimit])
                    expect(node.charLimit).to(equal(refrerenceCharLimit))
                    expect(node.text).to(equal(referenceLimitedText))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.text).to(equal(referenceLimitedText))

                    node.update(["text": referenceText, "charLimit" : 0])
                    expect(node.text).to(equal(referenceText))
                    expect(labelNode.text).to(equal(referenceText))
                }

                it("should update 'charSpacing' prop") {
                    let refrerenceCharSpacing: CGFloat = 2.3
                    node.update(["charSpacing" : refrerenceCharSpacing])
                    expect(node.charSpacing).to(beCloseTo(refrerenceCharSpacing))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.charSpacing).to(beCloseTo(refrerenceCharSpacing))
                }

                it("should update 'lineSpacing' prop") {
                    let refrerenceLineSpacing: CGFloat = 1.7
                    node.update(["lineSpacing" : refrerenceLineSpacing])
                    expect(node.lineSpacing).to(beCloseTo(refrerenceLineSpacing))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.lineSpacing).to(beCloseTo(refrerenceLineSpacing))
                }

                it("should update 'textAlignment' prop") {
                    let referenceTextAlignment = HorizontalTextAlignment.center
                    node.update(["textAlignment" : "center"])
                    expect(node.textAlignment).to(equal(referenceTextAlignment))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.textAlignment).to(equal(referenceTextAlignment))
                }

                it("should update 'textPadding' prop") {
                    let referenceTextPadding = UIEdgeInsets(top: 0.01, left: 0.02, bottom: 0.03, right: 0.04)
                    node.update(["textPadding" : referenceTextPadding.toArrayOfFloat])
                    expect(node.textPadding).to(beCloseTo(referenceTextPadding))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.textPadding).to(beCloseTo(referenceTextPadding))
                }

                it("should update 'hint' prop") {
                    let referenceHint = "Placeholder"
                    node.update(["hint" : referenceHint])
                    expect(node.hint).to(equal(referenceHint))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let hintNode = self.getHintNode(node)!
                    expect(hintNode.text).to(equal(referenceHint))
                }

                it("should update 'hintColor' prop") {
                    let referenceHintColor = UIColor.green
                    node.update(["hintColor" : referenceHintColor.toArrayOfFloat])
                    expect(node.hintColor).to(beCloseTo(referenceHintColor))
                    expect(node.isLayoutNeeded).to(beFalse())

                    let hintNode = self.getHintNode(node)!
                    expect(hintNode.textColor).to(beCloseTo(referenceHintColor))
                }

                it("should update 'multiline' prop") {
                    let referenceMultiline = true
                    node.update(["multiline" : referenceMultiline])
                    expect(node.multiline).to(equal(referenceMultiline))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.multiline).to(equal(referenceMultiline))
                }

                it("should update 'password' prop") {
                    let referencePasswordValue = "THZ3U+BHk#=8p=9B"
                    let referencePasswordValueHidden = String(Array<Character>(repeating: "•", count: referencePasswordValue.count))
                    node.text = referencePasswordValue
                    node.update(["password" : true])
                    expect(node.isLayoutNeeded).to(beTrue())
                    expect(node.password).to(beTrue())
                    expect(node.text).to(equal(referencePasswordValue))
                    expect(self.getLabelNode(node)!.text).to(equal(referencePasswordValueHidden))
                }

                it("should update 'scrolling' prop") {
                    node.update(["scrolling" : true])
                    expect(node.scrolling).to(beTrue())
                }

                it("should update 'textEntry' prop") {
                    let referenceTextEntry = TextEntryMode.numeric
                    node.update(["textEntry" : referenceTextEntry.rawValue])
                    expect(node.textEntry).to(equal(referenceTextEntry))
                }

                it("should update 'scrollBarVisibility' prop") {
                    let referenceScrollBarVisibility = ScrollBarVisibility.off
                    node.update(["scrollBarVisibility" : referenceScrollBarVisibility.rawValue])
                    expect(node.scrollBarVisibility).to(equal(referenceScrollBarVisibility))
                }

                it("should update 'scrollSpeed' prop") {
                    let referenceScrollSpeed: CGFloat = 0.21
                    node.update(["scrollSpeed" : referenceScrollSpeed])
                    expect(node.scrollSpeed).to(beCloseTo(referenceScrollSpeed))

                    node.scrollSpeed = -0.4
                    expect(node.scrollSpeed).to(beCloseTo(0.0))
                }

                it("should update 'scrollValue' prop") {
                    let referenceScrollValueInRange: CGFloat = 0.21
                    node.update(["scrollValue" : referenceScrollValueInRange])
                    expect(node.scrollValue).to(beCloseTo(referenceScrollValueInRange))

                    node.scrollValue = 1.18
                    expect(node.scrollValue).to(beCloseTo(1.0))

                    node.scrollValue = -0.3
                    expect(node.scrollValue).to(beCloseTo(0.0))
                }

                it("should update 'style' prop") {
                    let referenceFontStyle = FontStyle.italic
                    node.update(["fontParameters": ["style" : referenceFontStyle.rawValue]])
                    expect(node.style).to(equal(referenceFontStyle))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.fontStyle).to(equal(referenceFontStyle))
                }

                it("should update 'weight' prop") {
                    let referenceFontWeight = FontWeight.bold
                    node.update(["fontParameters": ["weight" : referenceFontWeight.rawValue]])
                    expect(node.weight).to(equal(referenceFontWeight))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.fontWeight).to(equal(referenceFontWeight))
                }

                it("should update 'fontSize' prop") {
                    let refrerenceFontSize: CGFloat = 0.27
                    node.update(["fontParameters": ["fontSize" : refrerenceFontSize]])
                    expect(node.textSize).to(beCloseTo(refrerenceFontSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.textSize).to(beCloseTo(refrerenceFontSize))
                }

                it("should update 'tracking' prop") {
                    let refrerenceTracking: Int = 90
                    node.update(["fontParameters": ["tracking" : refrerenceTracking]])
                    expect(node.tracking).to(equal(refrerenceTracking))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.tracking).to(equal(refrerenceTracking))
                }

                it("should update 'allCaps' prop") {
                    let refrerenceAllCaps = true
                    node.update(["fontParameters": ["allCaps" : refrerenceAllCaps]])
                    expect(node.allCaps).to(equal(refrerenceAllCaps))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = self.getLabelNode(node)!
                    expect(labelNode.allCaps).to(equal(refrerenceAllCaps))
                }

                it("should update 'width' prop") {
                    let refrerenceWidth = 0.18
                    node.update(["width" : refrerenceWidth])
                    expect(node.width).to(beCloseTo(refrerenceWidth))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.layoutIfNeeded()
                    expect(node.getSize().width).to(beCloseTo(refrerenceWidth))
                }

                it("should update 'height' prop") {
                    let refrerenceHeight = 0.81
                    node.update(["height" : refrerenceHeight])
                    expect(node.height).to(beCloseTo(refrerenceHeight))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.layoutIfNeeded()
                    expect(node.getSize().height).to(beCloseTo(refrerenceHeight))
                }
            }

            context("text-placeholder") {
                it("should display either text or placeholder") {
                    let emptyText = ""
                    let labelNode = self.getLabelNode(node)!
                    let hintNode = self.getHintNode(node)!

                    node.update(["text" : emptyText])
                    node.layoutIfNeeded()
                    expect(labelNode.isHidden).to(beTrue())
                    expect(hintNode.isHidden).to(beFalse())

                    let referenceText = "Some text"
                    node.update(["text" : referenceText])
                    node.layoutIfNeeded()
                    expect(labelNode.isHidden).to(beFalse())
                    expect(hintNode.isHidden).to(beTrue())
                }
            }

            context("when initialized") {
                it("should contain three child nodes") {
                    let childNodes = node.contentNode.childNodes
                    expect(childNodes.count).to(equal(4))
                    expect(childNodes[0]).notTo(beNil()) // background node
                    expect(childNodes[0].geometry as? SCNPlane).notTo(beNil())
                    expect(childNodes[1]).notTo(beNil()) // line node
                    expect(childNodes[2] is LabelNode).to(beTrue()) // text node
                    expect(childNodes[3] is LabelNode).to(beTrue()) // hint node
                }

                it("should contain four zero-sized nodes") {
                    let childNodes = node.contentNode.childNodes
                    let backgroundNode = childNodes[0]
                    let lineNode = childNodes[1]
                    let textNode = childNodes[2] as! LabelNode
                    let hintNode = childNodes[3] as! LabelNode
                    let plane = backgroundNode.geometry as! SCNPlane
                    expect(CGSize(width: plane.width, height: plane.height)).to(beCloseTo(CGSize.zero))
                    expect(lineNode.scale.x).to(beCloseTo(0))
                    expect(textNode.boundsSize).to(beCloseTo(CGSize.zero))
                    expect(hintNode.boundsSize).to(beCloseTo(CGSize.zero))
                }
            }

            context("focus") {
                it("should be focusable") {
                    expect(node.canHaveFocus).to(beTrue())
                    node.enabled = false
                    expect(node.canHaveFocus).to(beFalse())
                }

                it("should has focus") {
                    node.enterFocus()
                    expect(node.hasFocus).to(beTrue())
                }

                it("should not display outline if focused and has no size") {
                    expect(node.contentNode.childNodes.count).to(equal(4))
                    node.enterFocus()
                    expect(node.contentNode.childNodes.count).to(equal(4))
                }

                it("should have outline when focused") {
                    node.update(["width": 0.8, "height": 0.2])
                    let childNodesBefore = node.contentNode.childNodes
                    expect(childNodesBefore.count).to(equal(4))

                    node.enterFocus()
                    node.layoutIfNeeded()
                    let childNodesAfter = node.contentNode.childNodes
                    expect(childNodesAfter.count).to(equal(5))
                    let outlineNode = childNodesAfter[4]
                    expect(outlineNode.isHidden).to(beFalse())
                }

                it("should hide outline when unfocused") {
                    node.update(["width": 0.8, "height": 0.2])
                    node.enterFocus()
                    node.leaveFocus()
                    let childNodes = node.contentNode.childNodes
                    expect(childNodes.count).to(equal(5))
                    let outlineNode = childNodes[4]
                    expect(outlineNode.isHidden).to(beTrue())
                }
            }

            context("InputDataProviding") {
                it("should get/set value") {
                    let referenceValue = "Edited value"
                    node.value = referenceValue
                    expect(node.value as? String).to(equal(referenceValue))
                    expect(node.text).to(equal(referenceValue))

                    let referenceBoolTypeValue = true
                    node.value = referenceBoolTypeValue
                    expect(node.text).to(equal(referenceValue))
                }

                it("should get/set placeholder") {
                    let referencePlaceholder = "Password"
                    node.hint = referencePlaceholder
                    expect(node.placeholder).to(equal(referencePlaceholder))
                }

                it("autocapitalizationType") {
                    expect(node.autocapitalizationType).to(beNil())

                    node.allCaps = true
                    expect(node.autocapitalizationType).to(equal(UITextAutocapitalizationType.allCharacters))

                    node.allCaps = false
                    node.textEntry = .email
                    expect(node.autocapitalizationType).to(equal(UITextAutocapitalizationType.none))
                }

                it("keyboardType") {
                    let keyboardTypeByTextEntry: [TextEntryMode : UIKeyboardType?] = [
                        TextEntryMode.email: UIKeyboardType.emailAddress,
                        TextEntryMode.none: nil,
                        TextEntryMode.normal: UIKeyboardType.default,
                        TextEntryMode.numeric: UIKeyboardType.numberPad,
                        TextEntryMode.url: UIKeyboardType.URL
                    ]

                    keyboardTypeByTextEntry.forEach { (arg0) in
                        let (key, value) = arg0
                        node.textEntry = key
                        if key == .none {
                            expect(node.keyboardType).to(beNil())
                        } else {
                            expect(node.keyboardType).to(equal(value))
                        }
                    }
                }

                it("textContentType") {
                    expect(node.textContentType).to(beNil())

                    node.password = true
                    expect(node.textContentType).to(equal(UITextContentType.password))

                    node.password = false
                    node.textEntry = TextEntryMode.email
                    expect(node.textContentType).to(equal(UITextContentType.emailAddress))

                    node.textEntry = TextEntryMode.url
                    expect(node.textContentType).to(equal(UITextContentType.URL))

                    node.textEntry = TextEntryMode.none
                    expect(node.textContentType).to(beNil())

                    node.textEntry = TextEntryMode.normal
                    expect(node.textContentType).to(beNil())

                    node.textEntry = TextEntryMode.numeric
                    expect(node.textContentType).to(beNil())
                }
            }
        }
    }

    fileprivate func getBackgroundNode(_ parent: UiTextEditNode) -> SCNNode? {
        return parent.contentNode.childNodes.first
    }

    fileprivate func getBottomLineNode(_ parent: UiTextEditNode) -> SCNNode? {
        return parent.contentNode.childNodes[1]
    }

    fileprivate func getLabelNode(_ parent: UiTextEditNode) -> LabelNode? {
        return parent.contentNode.childNodes[2] as? LabelNode
    }

    fileprivate func getHintNode(_ parent: UiTextEditNode) -> LabelNode? {
        return parent.contentNode.childNodes[3] as? LabelNode
    }
}
