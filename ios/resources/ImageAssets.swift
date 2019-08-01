//
//  ImageAssets.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 02/07/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#if os(iOS) || os(tvOS) || os(watchOS)
import UIKit.UIImage
//typealias Image = UIImage
#elseif os(OSX)
import AppKit.NSImage
//typealias Image = NSImage
#endif

public enum ImageAsset: String {
    case progressBar = "progress_bar"
    case spinner = "spinner"
    case toggleOn = "toggle_on"
    case toggleOff = "toggle_off"

    var image: Image {
        let bundle = Bundle.resourcesBundle()!
        #if os(iOS) || os(tvOS)
        let image = Image(named: rawValue, in: bundle, compatibleWith: UIScreen.main.traitCollection)
        #elseif os(OSX)
        let image = bundle.image(forResource: rawValue)
        #elseif os(watchOS)
        let image = Image(named: rawValue)
        #endif
        guard let result = image else { fatalError("Unable to load image \(rawValue).") }
        return result
    }
}

extension Image {
    convenience init!(asset: ImageAsset) {
        #if os(iOS) || os(tvOS)
        let bundle = Bundle.resourcesBundle()
        self.init(named: asset.rawValue, in: bundle, compatibleWith: nil)
        #elseif os(OSX) || os(watchOS)
        self.init(named: asset.rawValue)
        #endif
    }
}

extension Bundle {
    static func resourcesBundle() -> Bundle? {
        guard let resourcesBundleURL = Bundle.main.url(forResource: "RNMagicScriptResources", withExtension: "bundle") else { return nil }
        return Bundle(url: resourcesBundleURL)
    }
}

private final class BundleToken {}
