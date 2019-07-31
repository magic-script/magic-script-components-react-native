//
//  MLXrClientAnchorData.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation
import SceneKit
import mlxr_ios_client_internal

class XrClientAnchorData: NSObject {
    fileprivate let anchorData: MLXRAnchor!

    public init(_ anchorData: MLXRAnchor) {
        self.anchorData = anchorData
    }

    public func getState() -> String {
        if let state = anchorData.getState(), state.tracked {
            return "tracked"
        } else {
            return "notTracked"
        }
    }

    public func getConfidence() -> [String: Any] {
        guard let confidence = anchorData.getConfidence() else {
            return [:]
        }
        return [
            "confidence": confidence.confidence,
            "validRadiusM": confidence.validRadiusM,
            "rotationErrDeg": confidence.rotationErrDeg,
            "translationErrM": confidence.translationErrM
        ]
    }

    public func getPose() -> [Float] {
        let pose = getMagicPose()
        return [
            pose[0][0], pose[1][0], pose[2][0], pose[3][0],
            pose[0][1], pose[1][1], pose[2][1], pose[3][1],
            pose[0][2], pose[1][2], pose[2][2], pose[3][2],
            pose[0][3], pose[1][3], pose[2][3], pose[3][3]
        ]
    }

    public func getMagicPose() -> simd_float4x4 {
        return anchorData.getPose()!.pose * XrClientAnchorData.magic_rotation
    }

    public func getAnchorId() -> String {
        return anchorData.getId()?.uuidString ?? "DEFAULT"
    }

    @objc public func getJsonRepresentation() -> [String: Any] {
        return [
            "state": getState(),
            "confidence": getConfidence(),
            "pose": getPose(),
            "anchorId": getAnchorId()
        ]
    }
}

// Magic rotation
extension XrClientAnchorData {
    static fileprivate func makeRotate(radians: Float, _ x: Float, _ y: Float, _ z: Float) -> float4x4 {
        return unsafeBitCast(GLKMatrix4MakeRotation(radians, x, y, z), to: float4x4.self)
    }

    static fileprivate func rotate(radians: Float, _ x: Float, _ y: Float, _ z: Float) -> float4x4 {
        return makeRotate(radians: radians, x, y, z)
    }

    static public let magic_rotation: simd_float4x4 = rotate(radians: 3.14, 1.0, 0, 0)
}
