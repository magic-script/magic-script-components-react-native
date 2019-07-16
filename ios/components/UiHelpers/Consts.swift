//
//  Consts.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 12/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation

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

    public var textLayerAlignmentMode: CATextLayerAlignmentMode {
        switch self {
        case .center:
            return CATextLayerAlignmentMode.center
        case .justify:
            return CATextLayerAlignmentMode.justified
        case .left:
            return CATextLayerAlignmentMode.left
        case .right:
            return CATextLayerAlignmentMode.right
        }
    }
}
