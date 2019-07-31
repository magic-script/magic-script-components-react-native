//
//  MLXrClientLocalization.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation
import mlxr_ios_client_internal

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

    public init(localizationStatus: MLXRLocalizationStatus) {
        switch localizationStatus {
        case MLXRLocalizationStatus_AwaitingLocation:
            self = .awaitingLocation
        case MLXRLocalizationStatus_ScanningLocation:
            self = .scanningLocation
        case MLXRLocalizationStatus_Localized:
            self = .localized
        case MLXRLocalizationStatus_LocalizationFailed:
            self = .localizationFailed
        default:
            self = .localizationFailed
        }
    }
}
