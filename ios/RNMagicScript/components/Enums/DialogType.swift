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

@objc public enum DialogType: Int {
    case singleAction, dualAction, custom, timed, noAction
    
    public typealias RawValue = String
    
    public var rawValue: RawValue {
        switch self {
        case .singleAction:
            return "singleAction"
        case .dualAction:
            return "dualAction"
        case .custom:
            return "custom"
        case .timed:
            return "timed"
        case .noAction:
            return "noAction"
        }
    }
    
    public init?(rawValue: RawValue) {
        switch rawValue {
        case "single-action":
            self = .singleAction
        case "dual-action":
            self = .dualAction
        case "custom":
            self = .custom
        case "timed":
            self = .timed
        case "no-action":
            self = .noAction
        default:
            return nil
        }
    }
}
