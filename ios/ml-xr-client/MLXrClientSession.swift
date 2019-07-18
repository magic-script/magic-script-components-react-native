//
//  MLXrClientSession.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation
//import mlxr_ios_client
import ARKit

@objc(MLXrClientSession)
class MLXrClientSession: NSObject {

    static fileprivate var arSession: ARSession?
//    fileprivate var xrClientSession: mlxr_ios_client.MLXrClientSession?
    fileprivate var updateInterval: TimeInterval = 2.0

    @objc static public func registerARSession(_ arSession: ARSession) {
        MLXrClientSession.arSession = arSession
    }

    @objc
    public func connect(address: String, deviceId: String, token: String, callback: RCTResponseSenderBlock) {
        guard let arSession = MLXrClientSession.arSession else {
            callback(["ARSession does not exist.", false])
            return
        }

//        xrClientSession = mlxs_ios_client.MLXrClientSession(nil, arSession)
//        if let xrSession = xrClientSession {
//            let result: Bool = xrSession.connect(address, deviceId, token)
//            callback([NSNull(), result])
//        } else {
//            callback(["XrClientSession has not been initialized!", NSNull()])
//        }

        // Mocked response
        callback([NSNull(), true])
    }

    func setUpdateInterval(_ interval: TimeInterval) {
        updateInterval = interval
    }

    @objc
    public func update(_ callback: RCTResponseSenderBlock) {
//        guard let xrSession = xrClientSession else {
//            callback(["XrClientSession has not been initialized!", false])
//            return
//        }
//
//        guard let arSession = MLXrClientSession.arSession,
//            let frame = arSession.currentFrame else {
//            callback(["ARFrame does not exist!", false])
//            return
//        }
//
////        let location: CLLocation =
//        let result: Bool = xrSession.update(frame, location)
//        callback([NSNull(), result])

        // Mocked response
        guard let arSession = MLXrClientSession.arSession,
            let frame = arSession.currentFrame else {
                callback(["ARFrame does not exist!", false])
                return
        }

        print("ARFrame[\(frame.timestamp)]: \(frame)")
        callback([NSNull(), false])
    }

    @objc
    public func getAllAnchors(_ callback: RCTResponseSenderBlock) {
//        guard let xrSession = xrClientSession else {
//            callback(["XrClientSession has not been initialized!", NSNull()])
//            return
//        }
//        let anchors: [mlxr_ios_client.MLXrClientAnchorData] = xrSession.getAllAnchors()
//        let results: [[String : Any]] = anchors.map({ MLXrClientAnchorData($0).getJSONRepresenation() })
//        callback([NSNull(), results])

        // Mocked response
        let results: [[String : Any]] = [MLXrClientAnchorData().getJsonRepresentation()]
        callback([NSNull(), results])
    }

    @objc
    public func getAnchorByPcfId(id: String, callback: RCTResponseSenderBlock) {
//        guard let uuid = UUID(uuidString: id) else {
//            callback(["Incorrect PCF id", NSNull()])
//            return
//        }
//
//        guard let xrSession = xrClientSession else {
//            callback(["XrClientSession has not been initialized!", NSNull()])
//            return
//        }
//
//        guard let anchorData = xrSession.getAnchorByPcfId(uuid) else {
//            // Achor data does not exist for given PCF id
//            callback([NSNull(), NSNull()])
//            return
//        }
//
//        let result: [String : Any] = MLXrClientAnchorData(anchorData: anchorData).getJsonRepresentation()
//        callback([NSNull(), result])

        // Mocked response
        let result: [String : Any] = MLXrClientAnchorData().getJsonRepresentation()
        callback([NSNull(), result])
    }

    @objc
    public func getLocalizationStatus(_ callback: RCTResponseSenderBlock) {
//        guard let xrSession = xrClientSession else {
//            callback(["XrClientSession has not been initialized!", NSNull()])
//            return
//        }
//
//        let status: MLXrClientLocalization = MLXrClientLocalization(localizationStatus: xrSession.getLocalizationStatus())
//        callback([NSNull(), status.rawValue])

        // Mocked response
        let status: MLXrClientLocalization = MLXrClientLocalization.localized
        callback([NSNull(), status.rawValue])
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
}
