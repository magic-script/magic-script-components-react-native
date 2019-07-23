//
//  MLXrClientSession.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation
import mlxr_ios_client
import ARKit
import CoreLocation

@objc(MLXrClientSession)
class MLXrClientSession: NSObject {

    static fileprivate weak var arSession: ARSession?
    fileprivate var xrClientSession: mlxr_ios_client.MLXrClientSession?
    fileprivate var updateInterval: TimeInterval = 2.0
    fileprivate var timer: Timer?
    fileprivate let locationManager = CLLocationManager()
    fileprivate var internalLocation: CLLocation!
    fileprivate let internalLocationQueue: DispatchQueue = DispatchQueue(label: "internalLocationQueue")
    fileprivate var lastLocation: CLLocation? {
        get {
            return internalLocationQueue.sync { internalLocation }
        }
        set (newLocation) {
            internalLocationQueue.sync { internalLocation = newLocation }
        }
    }

    public override init() {
        super.init()
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestWhenInUseAuthorization()
        self.locationManager.startUpdatingLocation()
    }

    deinit {
        timer?.invalidate()
    }

    @objc
    static public func registerARSession(_ arSession: ARSession) {
        MLXrClientSession.arSession = arSession
    }

    @objc
    public func connect(_ address: String, deviceId: String, token: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        guard let arSession = MLXrClientSession.arSession else {
            reject("code", "ARSession does not exist.", nil)
            return
        }

        xrClientSession = mlxr_ios_client.MLXrClientSession(authToken: OpaquePointer(bitPattern: 0), session: arSession)
        if let xrSession = xrClientSession {
            let result: Bool = xrSession.connect(address: address, deviceId: deviceId, token: token)
            resetTimer()
            resolve(result)
        } else {
            reject("code", "XrClientSession has not been initialized!", nil)
        }
    }

    @objc
    public func setUpdateInterval(_ interval: TimeInterval, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        updateInterval = max(0.5, interval)
        resetTimer()
        resolve(true)
    }

    fileprivate func resetTimer() {
        guard Thread.isMainThread else {
            DispatchQueue.main.async { [weak self] in
                self?.resetTimer()
            }
            return
        }
        timer?.invalidate()
        timer = Timer.scheduledTimer(withTimeInterval: updateInterval, repeats: true, block: { [weak self] _ in
            self?.update()
        })
    }

    fileprivate func update() {
        guard let xrSession = xrClientSession else {
            print("no mlxr session avaiable")
            return
        }

        guard let currentLocation = lastLocation else {
            print("current location is not available")
            return
        }

        guard let frame = MLXrClientSession.arSession?.currentFrame else {
            print("no ar frame available")
            return
        }
        _ = xrSession.update(frame: frame, location: currentLocation)
    }

    @objc
    public func getAllAnchors(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        guard let xrSession = xrClientSession else {
            reject("code", "XrClientSession has not been initialized!", nil)
            return
        }
        let allAnchors: [mlxr_ios_client.MLXrClientAnchorData] = xrSession.getAllAnchors()
        let uniqueAnchors: [MLXrClientAnchorData] = allAnchors.map { MLXrClientAnchorData($0) }

        // Remove current local anchors
        if let currentAnchors = MLXrClientSession.arSession?.currentFrame?.anchors {
            for anchor in currentAnchors {
                MLXrClientSession.arSession?.remove(anchor: anchor)
            }
        }

        // Only add unique anchors to the list, for existing ones just update the pose.
        for anchor in uniqueAnchors {
            let testAnchor = ARAnchor(name: anchor.getAnchorId(), transform: anchor.getMagicPose())
            MLXrClientSession.arSession?.add(anchor: testAnchor)
        }

        let results: [[String: Any]] = uniqueAnchors.map { $0.getJsonRepresentation() }
        resolve(results)
    }

    @objc
    public func getAnchorByPcfId(pcfId: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
//        guard let uuid = UUID(uuidString: pcfId) else {
//            reject("code", "Incorrect PCF id", nil)
//            return
//        }
//
//        guard let xrSession = xrClientSession else {
//            reject("code", "XrClientSession has not been initialized!", nil)
//            return
//        }
//
//        guard let anchorData = xrSession.getAnchorByPcfId(uuid) else {
//            // Achor data does not exist for given PCF id
//            resolve(nil)
//            return
//        }
//
//        let result: [String : Any] = MLXrClientAnchorData(anchorData: anchorData).getJsonRepresentation()
//        resolve(result])
    }

    @objc
    public func getLocalizationStatus(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        guard let xrSession = xrClientSession else {
            reject("code", "XrClientSession has not been initialized!", nil)
            return
        }

        let status: MLXrClientLocalization = MLXrClientLocalization(localizationStatus: xrSession.getLocalizationStatus())
        resolve(status.rawValue)
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
}

// CLLocationManagerDelegate
extension MLXrClientSession: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        lastLocation = locations.last
    }
}
