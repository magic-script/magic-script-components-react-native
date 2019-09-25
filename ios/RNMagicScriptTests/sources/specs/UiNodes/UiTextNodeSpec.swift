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

class UiTextNodeSpec: QuickSpec {
    override func spec() {
        describe("UiTextNode") {
            var node: UiTextNode!
            let shortReferenceText: String = "Info text"
            let veryLongReferenceText: String = "Very very very very very very very very very very very very long Info text to be set in UiTextNode."

            beforeEach {
                node = UiTextNode(props: [:])
                node.layoutIfNeeded()
            }
            
            context("initial properties") {
                it("should have set default values") {
                    expect(node.alignment).to(equal(Alignment.bottomLeft))
                    expect(node.text).to(beNil())
                    let referenceTextColor = UIColor(white: 0.75, alpha: 1.0)
                    expect(node.textColor).to(beCloseTo(referenceTextColor))
                    expect(node.textSize).to(beCloseTo(0.0))
                    expect(node.charSpacing).to(beCloseTo(0))
                    expect(node.lineSpacing).to(beCloseTo(1))
                    expect(node.style).to(equal(FontStyle.normal))
                    expect(node.weight).to(equal(FontWeight.regular))
                    expect(node.tracking).to(equal(50))
                    expect(node.allCaps).to(beFalse())
                    expect(node.textAlignment).to(equal(HorizontalTextAlignment.left))
                    expect(node.boundsSize).to(beCloseTo(CGSize.zero))
                    expect(node.wrap).to(beFalse())
                }
            }

            context("update properties") {
                it("should not update 'anchorPosition' prop") {
                    let referenceAnchorPosition = SCNVector3(0.1, 0.2, 0.3)
                    node.update(["anchorPosition" : referenceAnchorPosition.toArrayOfFloat])
                    expect(node.anchorPosition).notTo(beCloseTo(referenceAnchorPosition))
                    expect(node.anchorPosition).to(beCloseTo(SCNVector3Zero))
                }

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

                it("should update 'textAlignment' prop") {
                    let referenceTextAlignment = HorizontalTextAlignment.center
                    node.update(["textAlignment" : "center"])
                    expect(node.textAlignment).to(equal(referenceTextAlignment))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.textAlignment).to(equal(referenceTextAlignment))
                }

                it("should update 'boundsSize' prop") {
                    let referenceBoundsSize = CGSize(width: 2.5, height: 2.5)
                    node.update(["boundsSize" : ["boundsSize": [2.5, 2.5]]])
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.boundsSize).to(beCloseTo(referenceBoundsSize))
                }

                it("should update 'boundsSize' prop") {
                    node.update(["boundsSize" : ["wrap": true]])
                    expect(node.isLayoutNeeded).to(beTrue())

                    let labelNode = node.contentNode.childNodes.first as! LabelNode
                    expect(labelNode.multiline).to(beTrue())
                }
            }

            context("when initialized") {
                it("should contain child label node") {
                    let childNodes = node.contentNode.childNodes
                    expect(childNodes.count).to(equal(1)) // LabelNode
                    let labelNode = childNodes.first as! LabelNode
                    expect(labelNode.boundsSize).to(beCloseTo(CGSize.zero))
                }
            }

            context("when wrap enabled") {
                let referenceBoundsSize = CGSize(width: 0.005, height: 0.0025)
                let shortTextRefereneceSizeForBounds = CGSize(width: 0.005, height: 0.0025)
                let veryLongTextRefereneceSizeForBounds = CGSize(width: 0.005, height: 0.0025)

                it("shouldn't change bounds when text length increases") {
                    node.update(["boundsSize" : ["boundsSize": referenceBoundsSize.toArrayOfFloat, "wrap": true]])
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.textSize = 0.015
                    node.text = shortReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(shortTextRefereneceSizeForBounds))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))

                    node.text = veryLongReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(veryLongTextRefereneceSizeForBounds))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                }

                it("shouldn't change bounds when text length decreases") {
                    node.update(["boundsSize" : ["boundsSize": referenceBoundsSize.toArrayOfFloat, "wrap": true]])
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.textSize = 0.015
                    node.text = veryLongReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(veryLongTextRefereneceSizeForBounds))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))

                    node.text = shortReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(shortTextRefereneceSizeForBounds))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                }
            }

            context("when wrap disabled") {
                let referenceBoundsSize = CGSize.zero
                let shortTextRefereneceSizeForBounds = CGSize(width: 0.042, height: 0.0144)
                let veryLongTextRefereneceSizeForBounds = CGSize(width: 0.4932, height: 0.0144)

                it("should change bounds when text length increases") {
                    node.update(["boundsSize" : ["wrap": false]])
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.textSize = 0.015
                    node.text = shortReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(shortTextRefereneceSizeForBounds))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))

                    node.text = veryLongReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(veryLongTextRefereneceSizeForBounds))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                }

                it("should change bounds when text length decrease") {
                    node.update(["boundsSize" : ["wrap": false]])
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.textSize = 0.015
                    node.text = veryLongReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(veryLongTextRefereneceSizeForBounds))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))

                    node.text = shortReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(shortTextRefereneceSizeForBounds))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                }
            }

            context("when boundsSize.height not set") {
                let referenceBoundsSize = CGSize(width: 0.1, height: 0)
                let refereneceBoundsSizeWhenWrapDisabled = CGSize(width: 0.1, height: 0.0144)
                let refereneceBoundsSizeWhenWrapEnabled = CGSize(width: 0.1, height: 0.072)

                it("should change bounds when wrap changes") {
                    node.update(["boundsSize" : ["boundsSize": referenceBoundsSize.toArrayOfFloat, "wrap": false]])
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                    expect(node.isLayoutNeeded).to(beTrue())

                    node.textSize = 0.015
                    node.text = veryLongReferenceText
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(refereneceBoundsSizeWhenWrapDisabled))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))

                    node.wrap = true
                    node.updateLayout()

                    expect(node.getSize()).to(beCloseTo(refereneceBoundsSizeWhenWrapEnabled))
                    expect(node.boundsSize).to(beCloseTo(referenceBoundsSize))
                }
            }
        }
    }
}
