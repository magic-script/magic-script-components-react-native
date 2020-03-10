//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
// 

import Foundation
import SceneKit

// Spatial audio setup:
// https://stackoverflow.com/questions/58044242/arkit-spatial-audio-barely-changes-the-volume-over-distance
@objc class SoundNode: SCNNode {
    @objc var url: URL? {
        didSet {
            guard let inputURL = url else { unloadAudio(); return }
            downloader.download(remoteURL: inputURL) { [weak self] (localURL) -> (Void) in
                self?.loadAudio(localURL: localURL)
                NotificationCenter.default.post(name: .didLoadResource, object: self)
            }
        }
    }
    @objc var action: AudioAction = .stop {
        didSet { performAction() }
    }
    @objc var stream: Bool = false {
        didSet { audioSource?.shouldStream = stream; needsReloadPlayer = true }
    }
    @objc var volume: CGFloat = 1 {
        didSet { audioSource?.volume = Float(volume); needsReloadPlayer = true }
    }
    @objc var pitch: CGFloat = 1 {
        didSet { audioSource?.rate = Float(pitch); needsReloadPlayer = true }
    }
    @objc var mute: Bool = false {
        didSet { audioSource?.volume = mute ? 0.0 : Float(volume); needsReloadPlayer = true }
    }
    @objc var loop: Bool = false {
        didSet { audioSource?.loops = loop; needsReloadPlayer = true }
    }
    @objc var spatial: Bool = false {
        didSet { audioSource?.isPositional = spatial; needsReloadPlayer = true }
    }
    @objc var spatialMinDistance: CGFloat = 0
    @objc var spatialMaxDistance: CGFloat = 1.0
    @objc var direction: SCNQuaternion {
        get { return orientation }
        set { orientation = newValue }
    }

    var downloader: Downloading = FileDownloader()

    fileprivate var audioPlayer: SCNAudioPlayer?
    fileprivate var audioSource: SCNAudioSource?
    fileprivate var needsReloadPlayer: Bool = false

    @objc var isLoaded: Bool { return audioSource != nil }
    @objc var isPlaying: Bool { return audioPlayer != nil && !isHidden }

    @objc func start() { action = .start }
    @objc func pause() { action = .pause }
    @objc func resume() { action = .resume }
    @objc func stop() { action = .stop }

    fileprivate func internalStart() {
        // audio must be loaded
        guard let source = audioSource else { return }
        // and must be stopped
        guard audioPlayer == nil else { return }

        audioPlayer = SCNAudioPlayer(source: source)
        isHidden = false
        addAudioPlayer(audioPlayer!)
    }

    fileprivate func internalPause() {
        isHidden = true
    }

    fileprivate func internalResume() {
        isHidden = false
    }

    fileprivate func internalStop() {
        if let player = audioPlayer {
            removeAudioPlayer(player)
        }
        audioPlayer = nil
    }

    func reloadPlayerIfNeeded() {
        guard needsReloadPlayer else { return }
        needsReloadPlayer = false
        guard audioPlayer != nil else { return }

        let wasPaused = isHidden
        stop()
        start()
        isHidden = wasPaused
    }

    fileprivate func loadAudio(localURL: URL?) {
        unloadAudio()
        guard let url = localURL else { return }

        audioSource = SCNAudioSource(url: url)
        audioSource?.shouldStream = stream
        audioSource?.volume = mute ? 0.0 : Float(volume)
        audioSource?.rate = Float(pitch)
        audioSource?.loops = loop
        audioSource?.isPositional = spatial
        audioSource?.load()
        performAction()

        debugNode?.isHidden = !spatial
    }

    fileprivate func unloadAudio() {
        removeAllAudioPlayers()
        audioPlayer = nil
        audioSource = nil
    }

    fileprivate func performAction() {
        guard isLoaded else { return }
        switch action {
        case .start: internalStart()
        case .pause: internalPause()
        case .resume: internalResume()
        case .stop: internalStop()
        }
    }

    fileprivate var debugNode: SCNNode? { return childNodes.first }

    @objc func setDebugMode(_ debug: Bool) {
        let hasDebugNode = (debugNode != nil)
        guard debug != hasDebugNode else { return }

        childNodes.first?.removeFromParentNode()
        if debug {
            let vertices: [SCNVector3] = [
                SCNVector3(0, 0, spatialMinDistance),
                SCNVector3(0, 0, spatialMaxDistance),
            ]
            let line = NodesFactory.createLinesNode(vertices: vertices, color: UIColor.green)
            line.isHidden = !spatial
            addChildNode(line)
        }
    }
}
