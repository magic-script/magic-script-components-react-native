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

    static fileprivate weak var arSession: ARSession?
//    fileprivate var xrClientSession: mlxr_ios_client.MLXrClientSession?
    fileprivate var updateInterval: TimeInterval = 2.0
    fileprivate var timer: Timer?

    public override init() {
        super.init()
        print("MLXrClientSession initialized by React Native.")
    }

    deinit {
        timer?.invalidate()
        print("MLXrClientSession deinitialized by React Native.")
    }

    @objc
    static public func registerARSession(_ arSession: ARSession) {
        MLXrClientSession.arSession = arSession
    }

    @objc
    public func connect(_ address: String, deviceId: String, token: String, callback: RCTResponseSenderBlock) {
        guard let arSession = MLXrClientSession.arSession else {
            callback(["ARSession does not exist.", false])
            return
        }

//        xrClientSession = mlxs_ios_client.MLXrClientSession(nil, arSession)
//        if let xrSession = xrClientSession {
//            let result: Bool = xrSession.connect(address, deviceId, token)
//            resetTimer()
//            callback([NSNull(), result])
//        } else {
//            callback(["XrClientSession has not been initialized!", NSNull()])
//        }

        // Mocked response
        resetTimer()
        callback([NSNull(), true])
    }

    @objc
    public func setUpdateInterval(_ interval: TimeInterval, callback: RCTResponseSenderBlock) {
        updateInterval = max(0.5, interval)
        resetTimer()
        callback([NSNull(), true])
    }

    fileprivate func resetTimer() {
        guard Thread.isMainThread else {
            DispatchQueue.main.async { [weak self] in
                self?.resetTimer()
            }
            return
        }
        print("MLXrClientSession resetTimer")
        timer?.invalidate()
        timer = Timer.scheduledTimer(withTimeInterval: updateInterval, repeats: true, block: { [weak self] _ in
            self?.update()
        })
    }

    fileprivate func update() {
        print("MLXrClientSession update")
//        guard let xrSession = xrClientSession,
//            let frame = MLXrClientSession.arSession?.currentFrame else {
//            return
//        }
//
////        let location: CLLocation =
//        xrSession.update(frame, location)

        // Mocked response
        guard let frame = MLXrClientSession.arSession?.currentFrame else {
            return
        }

        print("ARFrame[\(frame.timestamp)]: \(frame)")
    }

    @objc
    public func getAllAnchors(_ callback: RCTResponseSenderBlock) {
//        guard let xrSession = xrClientSession else {
//            callback(["XrClientSession has not been initialized!", NSNull()])
//            return
//        }
//        let anchors: [mlxr_ios_client.MLXrClientAnchorData] = xrSession.getAllAnchors()
//        let results: [[String : Any]] = anchors.map({ MLXrClientAnchorData($0).getJsonRepresenation() })
//        callback([NSNull(), results])

        // Mocked response
        let anchors: [String] = [MLXrClientAnchorData.uuidString1, MLXrClientAnchorData.uuidString2, "ABCDE1F8-C36C-495A-93FC-8C247A3E6E5F"]
        let results: [[String : Any]] = anchors.map({ MLXrClientAnchorData($0).getJsonRepresentation() })
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
        let result: [String : Any] = MLXrClientAnchorData("A621E1F8-C36C-495A-93FC-0C247A3E6E5F").getJsonRepresentation()
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
