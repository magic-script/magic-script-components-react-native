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

    public var offset: CGPoint {
        switch self {
        case .topLeft:
            return CGPoint(x: -0.5, y: 0.5)
        case .topCenter:
            return CGPoint(x: 0.0, y: 0.5)
        case .topRight:
            return CGPoint(x: 0.5, y: 0.5)
        case .centerLeft:
            return CGPoint(x: -0.5, y: 0.0)
        case .centerCenter:
            return CGPoint(x: 0.0, y: 0.0)
        case .centerRight:
            return CGPoint(x: 0.5, y: 0.0)
        case .bottomLeft:
            return CGPoint(x: -0.5, y: -0.5)
        case .bottomCenter:
            return CGPoint(x: 0.0, y: -0.5)
        case .bottomRight:
            return CGPoint(x: 0.5, y: -0.5)
        }
    }
}
