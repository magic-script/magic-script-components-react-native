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
import UIKit
@testable import RNMagicScriptHostApplication

class UIFontLominoSpec: QuickSpec {
    override func spec() {
        describe("UIFont+Lomino") {
            context("fontWithStyle:weight:size") {
                it("should return font") {
                    let styles: [FontStyle] = [.normal, .italic]
                    let weights: [FontWeight] = [.extraLight, .light, .regular, .medium, .bold, .extraBold]
                    let sizes: [CGFloat] = [0.3, 5.0, 12.0]
                    for style in styles {
                        for weight in weights {
                            for size in sizes {
                                let font = UIFont.font(with: style, weight: weight, size: size)
                                expect(font).notTo(beNil())
                                expect(font.pointSize).to(beCloseTo(size))
                            }
                        }
                    }
                }
            }

            context("withTraits:") {
                it("should return the same font if given traits not supported") {
                    let font1: UIFont! = UIFont(name: "AvenirNext-UltraLightItalic", size: 12.0)!
                    expect(font1).notTo(beNil())
                    let font2: UIFont! = font1.with(traits: .traitExpanded)
                    expect(font1).to(beIdenticalTo(font2))
                }
            }

            context("UIFontWeight") {
                it("should return proper UIFont.Weight") {
                    let weightsMap: [FontWeight: UIFont.Weight] = [
                        FontWeight.extraLight: UIFont.Weight.ultraLight,
                        FontWeight.light: UIFont.Weight.light,
                        FontWeight.regular: UIFont.Weight.regular,
                        FontWeight.medium: UIFont.Weight.medium,
                        FontWeight.bold: UIFont.Weight.bold,
                        FontWeight.extraBold: UIFont.Weight.heavy
                    ]
                    for (key, value) in weightsMap {
                        expect(UIFont.UIFontWeight(from: key)).to(equal(value))
                    }
                }
            }
        }
    }
}
