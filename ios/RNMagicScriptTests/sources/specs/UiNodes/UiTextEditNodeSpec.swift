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
            let shortReferenceText: String = "Info text"
            let veryLongReferenceText: String = "Very very very very very very very very very very very very long Info text to be set in UiTextNode."

            beforeEach {
                node = UiTextEditNode(props: [:])
                node.layoutIfNeeded()
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
                    expect(node.hintColor).to(beCloseTo(UIColor(white: 0.75, alpha: 0.5)))
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

            context("update properties") {
                it("should update 'text' prop") {
                    node.update(["text" : shortReferenceText])
                    expect(node.text).to(equal(shortReferenceText))

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.text).to(equal(shortReferenceText))
                }

                it("should update 'textColor' prop") {
                    let referenceTextColor = UIColor(white: 0.5, alpha: 0.5)
                    node.update(["textColor" : referenceTextColor.toArrayOfFloat])
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.isLayoutNeeded).to(beFalse())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textColor).to(beCloseTo(referenceTextColor))
                }

                it("should update 'textSize' prop") {
                    let referenceTextSize = 11.0
                    node.update(["textSize" : referenceTextSize])
                    expect(node.textSize).to(beCloseTo(referenceTextSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
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

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.text).to(equal(referenceLimitedText))
                }

                it("should update 'charSpacing' prop") {
                    let refrerenceCharSpacing: CGFloat = 2.3
                    node.update(["charSpacing" : refrerenceCharSpacing])
                    expect(node.charSpacing).to(beCloseTo(refrerenceCharSpacing))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.charSpacing).to(beCloseTo(refrerenceCharSpacing))
                }

                it("should update 'lineSpacing' prop") {
                    let refrerenceLineSpacing: CGFloat = 1.7
                    node.update(["lineSpacing" : refrerenceLineSpacing])
                    expect(node.lineSpacing).to(beCloseTo(refrerenceLineSpacing))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.lineSpacing).to(beCloseTo(refrerenceLineSpacing))
                }

                it("should update 'textAlignment' prop") {
                    let referenceTextAlignment = HorizontalTextAlignment.center
                    node.update(["textAlignment" : "center"])
                    expect(node.textAlignment).to(equal(referenceTextAlignment))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textAlignment).to(equal(referenceTextAlignment))
                }

                it("should update 'textPadding' prop") {
                    let referenceTextPadding = UIEdgeInsets(top: 0.01, left: 0.02, bottom: 0.03, right: 0.04)
                    node.update(["textPadding" : referenceTextPadding.toArrayOfFloat])
                    expect(node.textPadding).to(beCloseTo(referenceTextPadding))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textPadding).to(beCloseTo(referenceTextPadding))
                }

//                it("should update 'hint' prop") {
//                    expect
//                }
//
//                it("should update 'hintColor' prop") {
//                    expect
//                }

                it("should update 'multiline' prop") {
                    let referenceMultiline = true
                    node.update(["multiline" : referenceMultiline])
                    expect(node.multiline).to(equal(referenceMultiline))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.multiline).to(equal(referenceMultiline))
                }

                it("should update 'password' prop") {
                    let referencePasswordValue = "THZ3U+BHk#=8p=9B"
                    let referencePasswordValueHidden = String(Array<Character>(repeating: "•", count: referencePasswordValue.count))
                    node.text = referencePasswordValue
                    node.update(["password" : true])
                    expect(node.isLayoutNeeded).to(beTrue())
                    expect(node.password).to(beTrue())
                    expect(node.text).to(equal(referencePasswordValueHidden))
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
                    node.update(["fontParams": ["style" : referenceFontStyle.rawValue]])
                    expect(node.style).to(equal(referenceFontStyle))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.fontStyle).to(equal(referenceFontStyle))
                }

                it("should update 'weight' prop") {
                    let referenceFontWeight = FontWeight.bold
                    node.update(["fontParams": ["weight" : referenceFontWeight.rawValue]])
                    expect(node.weight).to(equal(referenceFontWeight))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.fontWeight).to(equal(referenceFontWeight))
                }

                it("should update 'fontSize' prop") {
                    let refrerenceFontSize: CGFloat = 0.27
                    node.update(["fontParams": ["fontSize" : refrerenceFontSize]])
                    expect(node.textSize).to(beCloseTo(refrerenceFontSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textSize).to(beCloseTo(refrerenceFontSize))
                }

                it("should update 'tracking' prop") {
                    let refrerenceTracking: Int = 90
                    node.update(["fontParams": ["tracking" : refrerenceTracking]])
                    expect(node.tracking).to(equal(refrerenceTracking))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.tracking).to(equal(refrerenceTracking))
                }

                it("should update 'allCaps' prop") {
                    let refrerenceAllCaps = true
                    node.update(["fontParams": ["allCaps" : refrerenceAllCaps]])
                    expect(node.allCaps).to(equal(refrerenceAllCaps))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
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

//            context("when initialized") {
//                it("should contain child label node") {
//                    let childNodes = node.contentNode.childNodes
//                    expect(childNodes.count).to(equal(1)) // LabelNode
//                    let labelNode = childNodes.first as! LabelNode
//                    expect(labelNode.boundsSize).to(beCloseTo(CGSize.zero))
//                }
//            }
//
//            context("when wrap enabled") {
//                let referenceBoundsSize = CGSize(width: 0.005, height: 0.0025)
//                let shortTextRefereneceSizeForBounds = CGSize(width: 0.005, height: 0.0025)
//                let veryLongTextRefereneceSizeForBounds = CGSize(width: 0.005, height: 0.0025)
//
//                it("shouldn't change bounds when text length increases") {
//                    node.update(["boundsSize" : ["boundsSize": referenceBoundsSize.toArrayOfFloat, "wrap": true]])
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                    expect(node.isLayoutNeeded).to(beTrue())
//
//                    node.textSize = 0.015
//                    node.text = shortReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(shortTextRefereneceSizeForBounds))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//
//                    node.text = veryLongReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(veryLongTextRefereneceSizeForBounds))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                }
//
//                it("shouldn't change bounds when text length decreases") {
//                    node.update(["boundsSize" : ["boundsSize": referenceBoundsSize.toArrayOfFloat, "wrap": true]])
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                    expect(node.isLayoutNeeded).to(beTrue())
//
//                    node.textSize = 0.015
//                    node.text = veryLongReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(veryLongTextRefereneceSizeForBounds))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//
//                    node.text = shortReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(shortTextRefereneceSizeForBounds))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                }
//            }
//
//            context("when wrap disabled") {
//                let referenceBoundsSize = CGSize.zero
//                let shortTextRefereneceSizeForBounds = CGSize(width: 0.042, height: 0.0144)
//                let veryLongTextRefereneceSizeForBounds = CGSize(width: 0.4932, height: 0.0144)
//
//                it("should change bounds when text length increases") {
//                    node.update(["boundsSize" : ["wrap": false]])
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                    expect(node.isLayoutNeeded).to(beTrue())
//
//                    node.textSize = 0.015
//                    node.text = shortReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(shortTextRefereneceSizeForBounds))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//
//                    node.text = veryLongReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(veryLongTextRefereneceSizeForBounds))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                }
//
//                it("should change bounds when text length decrease") {
//                    node.update(["boundsSize" : ["wrap": false]])
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                    expect(node.isLayoutNeeded).to(beTrue())
//
//                    node.textSize = 0.015
//                    node.text = veryLongReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(veryLongTextRefereneceSizeForBounds))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//
//                    node.text = shortReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(shortTextRefereneceSizeForBounds))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                }
//            }
//
//            context("when boundsSize.height not set") {
//                let referenceBoundsSize = CGSize(width: 0.1, height: 0)
//                let refereneceBoundsSizeWhenWrapDisabled = CGSize(width: 0.1, height: 0.0144)
//                let refereneceBoundsSizeWhenWrapEnabled = CGSize(width: 0.1, height: 0.072)
//
//                it("should change bounds when wrap changes") {
//                    node.update(["boundsSize" : ["boundsSize": referenceBoundsSize.toArrayOfFloat, "wrap": false]])
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                    expect(node.isLayoutNeeded).to(beTrue())
//
//                    node.textSize = 0.015
//                    node.text = veryLongReferenceText
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(refereneceBoundsSizeWhenWrapDisabled))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//
//                    node.wrap = true
//                    node.updateLayout()
//
//                    expect(node.getSize()).to(beCloseTo(refereneceBoundsSizeWhenWrapEnabled))
//                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
//                }
//            }
        }
    }
}
