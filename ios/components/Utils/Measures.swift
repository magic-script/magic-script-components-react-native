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
    static public func pixels(in meters: CGFloat) -> CGFloat {
        let ppi: CGFloat = 326.0
        let inchesInMeters: CGFloat = 39.3700787
        let ppm: CGFloat = ppi * inchesInMeters
        return ppm * meters
    }

    static public var outlineWidthInPixels: CGFloat { return 5 }
}
