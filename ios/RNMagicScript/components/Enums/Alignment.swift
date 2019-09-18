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

import Foundation
import CoreGraphics

@objc public enum Alignment: Int {
    case topLeft
    case topCenter
    case topRight
    case centerLeft
    case centerCenter
    case centerRight
    case bottomLeft
    case bottomCenter
    case bottomRight

    public typealias RawValue = String

    public var rawValue: RawValue {
        switch self {
        case .topLeft:
            return "top-left"
        case .topCenter:
            return "top-center"
        case .topRight:
            return "top-right"
        case .centerLeft:
            return "center-left"
        case .centerCenter:
            return "center-center"
        case .centerRight:
            return "center-right"
        case .bottomLeft:
            return "bottom-left"
        case .bottomCenter:
            return "bottom-center"
        case .bottomRight:
            return "bottom-right"
        }
    }

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "top-left":
            self = .topLeft
        case "top-center":
            self = .topCenter
        case "top-right":
            self = .topRight
        case "center-left":
            self = .centerLeft
        case "center-center":
            self = .centerCenter
        case "center-right":
            self = .centerRight
        case "bottom-left":
            self = .bottomLeft
        case "bottom-center":
            self = .bottomCenter
        case "bottom-right":
            self = .bottomRight
        default:
            return nil
        }
    }

    public var shiftDirection: CGPoint {
        switch self {
        case .topLeft:
            return CGPoint(x: 0.5, y: -0.5)
        case .topCenter:
            return CGPoint(x: 0.0, y: -0.5)
        case .topRight:
            return CGPoint(x: -0.5, y: -0.5)
        case .centerLeft:
            return CGPoint(x: 0.5, y: 0.0)
        case .centerCenter:
            return CGPoint(x: 0.0, y: 0.0)
        case .centerRight:
            return CGPoint(x: -0.5, y: 0.0)
        case .bottomLeft:
            return CGPoint(x: 0.5, y: 0.5)
        case .bottomCenter:
            return CGPoint(x: 0.0, y: 0.5)
        case .bottomRight:
            return CGPoint(x: -0.5, y: 0.5)
        }
    }

    public var boundsOffset: CGPoint {
        switch self {
        case .topLeft:
            return CGPoint(x: 0.0, y: -1.0)
        case .topCenter:
            return CGPoint(x: -0.5, y: -1.0)
        case .topRight:
            return CGPoint(x: -1.0, y: -1.0)
        case .centerLeft:
            return CGPoint(x: 0.0, y: -0.5)
        case .centerCenter:
            return CGPoint(x: -0.5, y: -0.5)
        case .centerRight:
            return CGPoint(x: -1.0, y: -0.5)
        case .bottomLeft:
            return CGPoint(x: 0.0, y: 0.0)
        case .bottomCenter:
            return CGPoint(x: -0.5, y: 0.0)
        case .bottomRight:
            return CGPoint(x: -1.0, y: 0.0)
        }
    }
}
