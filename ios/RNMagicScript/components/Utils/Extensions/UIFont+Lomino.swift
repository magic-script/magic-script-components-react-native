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
        let name: String = UIFont.fontName(from: style, weight: weight)
        if let font = UIFont(name: name, size: size) {
            return font
        }
        
        let systemFontName: String = UIFont.systemFontName(from: style, weight: weight)
        print("systemFontName: \(systemFontName)")
        let systemFont: UIFont = UIFont(name: systemFontName, size: size) ?? UIFont.systemFont(ofSize: 20.0, weight: UIFont.UIFontWeight(from: weight))
        
        if style == .italic {
            return systemFont.with(traits: .traitItalic)
        }
        
        return systemFont
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
    
    fileprivate static func systemFontName(from style: FontStyle, weight: FontWeight) -> String {
        var name: String
        if #available(iOS 14.0, *) {
            name = "HelveticaNeue"//.SFUIText"
        } else {
            name = "HelveticaNeue"
        }
        
        let fontWeight = UIFontWeight(from: weight)
        if let weightName = UIFont.name(of: fontWeight) {
            name += "-" + weightName
        }
        
        return name
    }
    
    fileprivate static func name(of weight: UIFont.Weight) -> String? {
        switch weight {
            case .ultraLight: return "UltraLight"
            case .thin: return "Thin"
            case .light: return "Light"
            case .regular: return nil
            case .medium: return "Medium"
            case .semibold: return "Semibold"
            case .bold: return "Bold"
            case .heavy: return "Heavy"
            case .black: return "Black"
            default: return nil
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
