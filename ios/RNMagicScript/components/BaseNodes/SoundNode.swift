//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
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

@objc class SoundNode: SCNNode {
    @objc var url: URL? {
        didSet {
            guard let inputURL = url else { unloadAudio(); return }
            downloader.download(remoteURL: inputURL) { [weak self] (localURL) -> (Void) in
                self?.loadAudio(localURL: localURL)
            }
        }
    }
    @objc var stream: Bool = false {
        didSet { audioSource?.shouldStream = stream }
    }
    @objc var volume: CGFloat = 1 {
        didSet { audioSource?.volume = Float(volume) }
    }
    @objc var pitch: CGFloat = 1 {
        didSet { audioSource?.rate = Float(pitch) }
    }
    @objc var mute: Bool = false {
           didSet { audioSource?.volume = mute ? 0.0 : Float(volume) }
    }
    @objc var loop: Bool = false {
        didSet { audioSource?.loops = loop }
    }
    @objc var spatial: Bool = false {
        didSet { audioSource?.isPositional = spatial }
    }
    @objc var direction: SCNQuaternion {
        get { return orientation }
        set { orientation = newValue }
    }

    var downloader: Downloading = FileDownloader()
    var soundLoaded: (() -> Void)?

    fileprivate var audioPlayer: SCNAudioPlayer?
    fileprivate var audioSource: SCNAudioSource?

    deinit {
        unloadAudio()
    }

    @objc var isLoaded: Bool { return audioSource != nil }
    @objc var isPlaying: Bool { return audioPlayer != nil && !isHidden }

    func start() {
        // audio must be loaded
        guard let source = audioSource else { return }
        // and must be stopped
        guard audioPlayer == nil else { return }

        audioPlayer = SCNAudioPlayer(source: source)
        isHidden = false
        addAudioPlayer(audioPlayer!)
    }

    func pause() {
        isHidden = true
    }

    func resume() {
        isHidden = false
    }

    func stop() {
        if let player = audioPlayer {
            removeAudioPlayer(player)
        }
        audioPlayer = nil
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
        soundLoaded?()
    }

    fileprivate func unloadAudio() {
        removeAllAudioPlayers()
        audioPlayer = nil
        audioSource = nil
    }
}
