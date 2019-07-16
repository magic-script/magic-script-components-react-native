//
//  Measures.swift
//  SceneKitComponents
//
//  Created by Pawel Leszkiewicz on 04/07/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import Foundation
import CoreGraphics

public class Measures {
    static fileprivate let ppi: CGFloat = 0.5 * 326.0
    static fileprivate let inchesInMeters: CGFloat = 39.3700787
    static fileprivate let ppm: CGFloat = Measures.ppi * Measures.inchesInMeters
    static public func pixels(from meters: CGFloat) -> CGFloat {
        return Measures.ppm * meters
    }

    static public func pixels(from meters: CGSize) -> CGSize {
        return CGSize(width: Measures.pixels(from: meters.width), height: Measures.pixels(from: meters.height))
    }

    static public func meters(from pixels: CGFloat) -> CGFloat {
        return pixels / Measures.ppm
    }

    static public func meters(from pixels: CGSize) -> CGSize {
        return CGSize(width: Measures.meters(from: pixels.width), height: Measures.meters(from: pixels.height))
    }

    static public var outlineWidthInPixels: CGFloat { return 5 }
}
