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
        case "light":
            self = .light
        case "extra-light":
            self = .extraLight
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
