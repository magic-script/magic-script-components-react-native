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

import UIKit

extension UIFont {

    public static func font(with style: FontStyle, weight: FontWeight, size: CGFloat) -> UIFont {
        let name: String = UIFont.fontName(from: style, weight: weight)
        guard let font = UIFont(name: name, size: size) else {
            // return system font in case Lomino font not installed
            let systemFont = UIFont.systemFont(ofSize: size, weight: UIFont.UIFontWeight(from: weight))
            if style == .italic {
                return systemFont.with(traits: .traitItalic)
            }
            
            return systemFont
        }
        
        return font
    }

    public func with(traits: UIFontDescriptor.SymbolicTraits) -> UIFont {
        guard let descriptor = fontDescriptor.withSymbolicTraits(traits) else {
            return self
        }

        return UIFont(descriptor: descriptor, size: 0)
    }

    fileprivate static func fontName(from style: FontStyle, weight: FontWeight) -> String {
        switch weight {
        case .extraLight:
            return (style == .normal) ? "LominoUIApp-Light" : "LominoUIApp-LightItalic"
        case .light:
            return (style == .normal) ? "LominoUIApp-Light" : "LominoUIApp-LightItalic"
        case .regular:
            return (style == .normal) ? "LominoUIApp-Regular" : "LominoUIApp-Italic"
        case .medium:
            return (style == .normal) ? "LominoUIApp-Medium" : "LominoUIApp-MediumItalic"
        case .bold:
            return (style == .normal) ? "LominoUIApp-Bold" : "LominoUIApp-BoldItalic"
        case .extraBold:
            return (style == .normal) ? "LominoUIApp-ExtraBold" : "LominoUIApp-ExtraBoldItalic"
        }
    }

    public static func UIFontWeight(from weight: FontWeight) -> UIFont.Weight {
        switch weight {
        case .extraLight:
            return UIFont.Weight.ultraLight
        case .light:
            return UIFont.Weight.light
        case .regular:
            return UIFont.Weight.regular
        case .medium:
            return UIFont.Weight.medium
        case .bold:
            return UIFont.Weight.bold
        case .extraBold:
            return UIFont.Weight.heavy
        }
    }
}
