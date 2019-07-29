//
//  MLXrClientSession.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation
import ARKit
import CoreLocation
import mlxr_ios_client

@objc(XrClientSession)
class XrClientSession: NSObject {

    static fileprivate weak var arSession: ARSession?
    static fileprivate let locationManager = CLLocationManager()
    fileprivate var xrClientSession: MLXrClientSession?
    fileprivate var updateInterval: TimeInterval = 2.0
    fileprivate var timer: Timer?
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
        setupLocationManager()
    }

    deinit {
        timer?.invalidate()

        // NOTE: Due to the following warning:
        // "Failure to deallocate CLLocationManager on the same runloop as its creation
        // may result in a crash"
        // locationManager is a static member and we only stop updating location in deinit.
        XrClientSession.locationManager.stopUpdatingLocation()
        XrClientSession.locationManager.delegate = nil
    }

    fileprivate func setupLocationManager() {
        XrClientSession.locationManager.delegate = self
        XrClientSession.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        XrClientSession.locationManager.requestWhenInUseAuthorization()
        XrClientSession.locationManager.startUpdatingLocation()
    }

    @objc
    static public func registerARSession(_ arSession: ARSession) {
        XrClientSession.arSession = arSession
    }

    @objc
    public func connect(_ address: String, deviceId: String, token: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        guard let arSession = XrClientSession.arSession else {
            reject("code", "ARSession does not exist.", nil)
            return
        }

        xrClientSession = MLXrClientSession(authToken: OpaquePointer(bitPattern: 0), session: arSession)
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

        guard let frame = XrClientSession.arSession?.currentFrame else {
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
        let allAnchors: [MLXrClientAnchorData] = xrSession.getAllAnchors()
        let uniqueAnchors: [XrClientAnchorData] = allAnchors.map { XrClientAnchorData($0) }

        // Remove current local anchors
        if let currentAnchors = XrClientSession.arSession?.currentFrame?.anchors {
            for anchor in currentAnchors {
                XrClientSession.arSession?.remove(anchor: anchor)
            }
        }

        // Only add unique anchors to the list, for existing ones just update the pose.
        for anchor in uniqueAnchors {
            let testAnchor = ARAnchor(name: anchor.getAnchorId(), transform: anchor.getMagicPose())
            XrClientSession.arSession?.add(anchor: testAnchor)
        }

        let results: [[String: Any]] = uniqueAnchors.map { $0.getJsonRepresentation() }
        resolve(results)
    }

    @objc
    public func getAnchorByPcfId(pcfId: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        guard let uuid = UUID(uuidString: pcfId) else {
            reject("code", "Incorrect PCF id", nil)
            return
        }

        guard let xrSession = xrClientSession else {
            reject("code", "XrClientSession has not been initialized!", nil)
            return
        }

        guard let anchorData = xrSession.getAnchorByPcfId(id: uuid) else {
            // Achor data does not exist for given PCF id
            resolve(nil)
            return
        }

        let result: [String : Any] = XrClientAnchorData(anchorData).getJsonRepresentation()
        resolve(result)
    }

    @objc
    public func getLocalizationStatus(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        guard let xrSession = xrClientSession else {
            reject("code", "XrClientSession has not been initialized!", nil)
            return
        }

        let status: XrClientLocalization = XrClientLocalization(localizationStatus: xrSession.getLocalizationStatus())
        resolve(status.rawValue)
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
}

// CLLocationManagerDelegate
extension XrClientSession: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        lastLocation = locations.last
    }
}
