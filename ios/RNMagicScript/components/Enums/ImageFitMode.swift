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

@objc public enum ImageFitMode: Int {
    case aspectFill
    case aspectFit
    case stretch

    public typealias RawValue = String

    public var rawValue: RawValue {
        switch self {
        case .aspectFill:
            return "aspect-fill"
        case .aspectFit:
            return "aspect-fit"
        case .stretch:
            return "stretch"
        }
    }

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "aspect-fill":
            self = .aspectFill
        case "aspect-fit":
            self = .aspectFit
        case "stretch":
            self = .stretch
        default:
            return nil
        }
    }
}