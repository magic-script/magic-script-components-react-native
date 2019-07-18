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

@objc(MLXrClientAnchorData)
class MLXrClientAnchorData: NSObject {
//    fileprivate let anchorData: mlxr_ios_client.MLXrClientAnchorData
//
//    public init(_ anchorData: mlxr_ios_client.MLXrClientAnchorData) {
//        self.anchorData = anchorData
//    }

    @objc
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

     @objc
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

    @objc
    public func getPose() -> [Float] {
//        let pose: simd_float4x4 = anchorData.getPose()
        let pose: simd_float4x4 = simd_float4x4(SCNMatrix4Identity)
        return [
            pose[0][0], pose[1][0], pose[2][0], pose[3][0],
            pose[0][1], pose[1][1], pose[2][1], pose[3][1],
            pose[0][2], pose[1][2], pose[2][2], pose[3][2],
            pose[0][3], pose[1][3], pose[2][3], pose[3][3],
        ]
    }

    @objc
    public func getAnchorId() -> String {
//        let uuid: UUID = MLXrClientAnchorData.theAnchorData.getAnchorId()
        let uuid: UUID = UUID(uuidString: "E621E1F8-C36C-495A-93FC-0C247A3E6E5F")!
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
