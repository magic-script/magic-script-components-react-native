//
//  MLXrClientSession.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation


@objc(MLXrClientSession)
class MLXrClientSession: NSObject {

    @objc
    public func connect(address: String, deviceId: String, token: String, callback: RCTResponseSenderBlock) {
        let result: Bool = true // MLXrClientSession.theSession.connect(address, deviceId, token)
        callback([NSNull(), result])
    }

    @objc
    public func update(_ callback: RCTResponseSenderBlock) {
//        let frame: ARFrame =
//        let location: CLLocation =
        let result: Bool = true // MLXrClientSession.theSession.update(frame, location)
        callback([NSNull(), result])
    }

    @objc
    public func getAllAnchors(_ callback: RCTResponseSenderBlock) {
//        let anchors: [mlxr_ios_client.MLXrClientAnchorData] = MLXrClientSession.theSession.getAllAnchors()
//        let results: [[String : Any]] = anchors.map({ MLXrClientAnchorData($0).getJSONRepresenation() })
        let results: [[String : Any]] = [MLXrClientAnchorData().getJsonRepresentation()]
        callback([NSNull(), results])
    }

    @objc
    public func getAnchorByPcfId(id: String, callback: RCTResponseSenderBlock) {
        guard let uuid = UUID(uuidString: id) else {
            callback(["Incorrect id", NSNull()])
            return
        }

//        guard let anchorData: mlxr_ios_client.MLXrClientAnchorData? = MLXrClientSession.theSession.getAnchorByPcfId(uuid) else {
//            callback([NSNull(), NSNull()])
//            return
//        }

//        let result: MLXrClientAnchorData = MLXrClientAnchorData(anchorData: anchorData)
        let result: [String : Any] = MLXrClientAnchorData().getJsonRepresentation()
        callback([NSNull(), result])
    }

    @objc
    public func getLocalizationStatus(_ callback: RCTResponseSenderBlock) {
//        let status: MLXrClientLocalization = MLXrClientLocalization(localizationStatus: MLXrClientSession.theSession.getLocalizationStatus())
        let status: MLXrClientLocalization = MLXrClientLocalization.localized
        callback([NSNull(), status.rawValue])
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
}
