//
//  MLXrClientLocalization.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation
import mlxr_ios_client

@objc enum XrClientLocalization : Int {
    case awaitingLocation
    case scanningLocation
    case localized
    case localizationFailed

    public typealias RawValue = String

    public var rawValue: RawValue {
        switch self {
        case .awaitingLocation:
            return "awaitingLocation"
        case .scanningLocation:
            return "scanningLocation"
        case .localized:
            return "localized"
        case .localizationFailed:
            return "localizationFailed"
        }
    }

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "awaitingLocation":
            self = .awaitingLocation
        case "scanningLocation":
            self = .scanningLocation
        case "localized":
            self = .localized
        case "localizationFailed":
            self = .localizationFailed
        default:
            return nil
        }
    }

    public init(localizationStatus: MLXrClientLocalization.LocalizationStatus) {
        switch localizationStatus {
        case MLXrClientLocalization.LocalizationStatus.AwaitingLocation:
            self = .awaitingLocation
        case MLXrClientLocalization.LocalizationStatus.ScanningLocation:
            self = .scanningLocation
        case MLXrClientLocalization.LocalizationStatus.Localized:
            self = .localized
        case MLXrClientLocalization.LocalizationStatus.LocalizationFailed:
            self = .localizationFailed
        }
    }
}
