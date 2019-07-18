//
//  MLXrClientAnchorData.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation
import SceneKit
//import mlxr_ios_client

class MLXrClientAnchorData: NSObject {
//    fileprivate let anchorData: mlxr_ios_client.MLXrClientAnchorData
//
//    public init(_ anchorData: mlxr_ios_client.MLXrClientAnchorData) {
//        self.anchorData = anchorData
//    }

    static public let uuidString1 = "A621E1F8-C36C-495A-93FC-0C247A3E6E5F"
    static public let uuidString2 = "B721E1F8-C36C-495A-93FC-0C247A3E6E5F"
    fileprivate let uuidString: String

    public init(_ uuidString: String) {
        self.uuidString = uuidString
    }

    public func getState() -> String {
//        let state: anchorData.getState()
//        switch state {
//        case .Tracked:
//            return "tracked"
//        case .NotTracked:
//            return "notTracked"
//        }
        return "tracked"
    }

    public func getConfidence() -> [String: Any] {
//        let confidence = anchorData.getConfidence()
//        return [
//            "confidence" : confidence.confidence,
//            "validRadiusM" : confidence.validRadiusM,
//            "rotationErrDeg" : confidence.rotationErrDeg,
//            "translationErrM" : confidence.translationErrM
//        ]
        return [
            "confidence" : 0.9,
            "validRadiusM" : 0.7,
            "rotationErrDeg" : 0.5,
            "translationErrM" : 0.3
        ]

     }

    public func getPose() -> [Float] {
//        let anchorPose: simd_float4x4 = anchorData.getPose()
//        let magic_rotation = simd_float4x4(simd_quatf(angle: Float.pi, axis: float3(1.0, 0, 0)))
//        let pose: simd_float4x4 = anchorPose * magic_rotation

        let matrix: SCNMatrix4
        if uuidString == MLXrClientAnchorData.uuidString1 {
            matrix = SCNMatrix4MakeTranslation(-0.5, 0.3, 1)
        } else if uuidString == MLXrClientAnchorData.uuidString2 {
            matrix = SCNMatrix4MakeTranslation(-1.5, -0.1, -1)
        } else {
            matrix = SCNMatrix4Identity
        }
        let pose = simd_float4x4(matrix)

        return [
            pose[0][0], pose[1][0], pose[2][0], pose[3][0],
            pose[0][1], pose[1][1], pose[2][1], pose[3][1],
            pose[0][2], pose[1][2], pose[2][2], pose[3][2],
            pose[0][3], pose[1][3], pose[2][3], pose[3][3],
        ]
    }

    public func getAnchorId() -> String {
//        let uuid: UUID = MLXrClientAnchorData.theAnchorData.getAnchorId()
        let uuid: UUID = UUID(uuidString: uuidString)!
        return uuid.uuidString
    }

    @objc public func getJsonRepresentation() -> [String : Any] {
        return [
            "state" : getState(),
            "confidence" : getConfidence(),
            "pose" : getPose(),
            "anchorId" : getAnchorId()
        ]
    }
}
