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
import QuartzCore
import UIKit

@objc public enum HorizontalTextAlignment: Int {
    case center
    case justify
    case left
    case right

    public typealias RawValue = String

    public var rawValue: RawValue {
        switch self {
        case .center:
            return "center"
        case .justify:
            return "justify"
        case .left:
            return "left"
        case .right:
            return "right"
        }
    }

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "center":
            self = .center
        case "justify":
            self = .justify
        case "left":
            self = .left
        case "right":
            self = .right
        default:
            return nil
        }
    }

    public var nsTextAlignment: NSTextAlignment {
        switch self {
        case .center:
            return NSTextAlignment.center
        case .justify:
            return NSTextAlignment.justified
        case .left:
            return NSTextAlignment.left
        case .right:
            return NSTextAlignment.right
        }
    }
}
