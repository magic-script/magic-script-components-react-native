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

import UIKit

extension UIFont {

    public static func font(with style: FontStyle, weight: FontWeight, size: CGFloat) -> UIFont {
        // Check Lomino font first
        let name: String = UIFont.lominoFontName(from: style, weight: weight)
        if let font = UIFont(name: name, size: size) {
            return font
        }
        
        // Perform extra check for iOS 14 as there is an issue with system font
        // (more info: https://stackoverflow.com/questions/64448087/missing-characters-in-scntext-ios-14)
        if #available(iOS 14.0, *) {
            let systemFontName: String = UIFont.avenirNextFontName(from: style, weight: weight)
            return UIFont(name: systemFontName, size: size)!
        }
        
        // Then use system font
        let systemFont: UIFont = UIFont.systemFont(ofSize: size, weight: UIFont.UIFontWeight(from: weight))
        if style == .italic {
            return systemFont.with(traits: .traitItalic)
        }
        
        return systemFont
    }

    fileprivate static func lominoFontName(from style: FontStyle, weight: FontWeight) -> String {
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
    
    fileprivate static func avenirNextFontName(from style: FontStyle, weight: FontWeight) -> String {
        switch weight {
        case .extraLight:
            return (style == .normal) ? "AvenirNext-UltraLight" : "AvenirNext-UltraLightItalic"
        case .light:
            return (style == .normal) ? "AvenirNext-UltraLight" : "AvenirNext-UltraLightItalic"
        case .regular:
            return (style == .normal) ? "AvenirNext-Regular" : "AvenirNext-Italic"
        case .medium:
            return (style == .normal) ? "AvenirNext-Medium" : "AvenirNext-MediumItalic"
        case .bold:
            return (style == .normal) ? "AvenirNext-Bold" : "AvenirNext-BoldItalic"
        case .extraBold:
            return (style == .normal) ? "AvenirNext-Heavy" : "AvenirNext-HeavyItalic"
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

// MARK: - Traits
extension UIFont {
    public func with(traits: UIFontDescriptor.SymbolicTraits) -> UIFont {
        guard let descriptor = fontDescriptor.withSymbolicTraits(traits) else {
            return self
        }

        return UIFont(descriptor: descriptor, size: 0)
    }
}
