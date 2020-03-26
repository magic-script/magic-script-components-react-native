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
}
