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

@objc enum VideoViewMode: Int {
    case fullArea, leftRight

    public typealias RawValue = String

    public var rawValue: RawValue {
        switch self {
        case .fullArea:
            return "full-area"
        case .leftRight:
            return "left-right"
        }
    }

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "full-area":
            self = .fullArea
        case "left-right":
            self = .leftRight
        default:
            return nil
        }
    }
}
