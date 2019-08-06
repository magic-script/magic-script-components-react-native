//
//  Font.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 06/08/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import UIKit

@objc public enum FontStyle: Int {
    case normal
    case italic

    public typealias RawValue = String

    public var rawValue: RawValue {
        switch self {
        case .normal:
            return "normal"
        case .italic:
            return "italic"
        }
    }

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "normal":
            self = .normal
        case "italic":
            self = .italic
        default:
            return nil
        }
    }
}

@objc public enum FontWeight: Int {
    case extraLight
    case light
    case regular
    case medium
    case bold
    case extraBold

    public typealias RawValue = String

    public var rawValue: RawValue {
        switch self {
        case .extraLight:
            return "extra-light"
        case .light:
            return "light"
        case .regular:
            return "regular"
        case .medium:
            return "medium"
        case .bold:
            return "bold"
        case .extraBold:
            return "extra-bold"
        }
    }

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "extra-light":
            self = .extraLight
        case "light":
            self = .light
        case "regular":
            self = .regular
        case "medium":
            self = .medium
        case "bold":
            self = .bold
        case "extra-bold":
            self = .extraBold
        default:
            return nil
        }
    }
}

extension UIFont {

    public static func font(with style: FontStyle, weight: FontWeight, size: CGFloat) -> UIFont {
        let name: String = UIFont.fontName(from: style, weight: weight)
        return UIFont(name: name, size: size)!
    }

    static fileprivate func fontName(from style: FontStyle, weight: FontWeight) -> String {
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
}

// MARK: - Font registration
extension UIFont {

    static func loadAllCustomFonts() {
        guard let bundle = Bundle.resourcesBundle() else { return }
        UIFont.registerFont(with: "LominoUIApp-Bold.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-BoldItalic.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-ExtraBold.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-ExtraBoldItalic.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-Italic.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-Light.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-LightItalic.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-Medium.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-MediumItalic.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-Regular.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-WideBoldItalic.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-Wide.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-WideItalic.ttf", bundle: bundle)
        UIFont.registerFont(with: "LominoUIApp-WideBold.ttf", bundle: bundle)
    }

    static fileprivate func registerFont(with filenameString: String, bundle: Bundle) {
        guard let pathForResourceString = bundle.path(forResource: filenameString, ofType: nil) else {
            print("UIFont:  Failed to register font - path for resource not found.")
            return
        }

        guard let fontData = NSData(contentsOfFile: pathForResourceString) else {
            print("UIFont:  Failed to register font - font data could not be loaded.")
            return
        }

        guard let dataProvider = CGDataProvider(data: fontData) else {
            print("UIFont:  Failed to register font - data provider could not be loaded.")
            return
        }

        guard let font = CGFont(dataProvider) else {
            print("UIFont:  Failed to register font - font could not be loaded.")
            return
        }

        var errorRef: Unmanaged<CFError>? = nil
        if (CTFontManagerRegisterGraphicsFont(font, &errorRef) == false) {
            print("UIFont:  Failed to register font - register graphics font failed - this font may have already been registered in the main bundle.")
        }
    }
}
