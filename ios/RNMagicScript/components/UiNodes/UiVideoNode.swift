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

import SceneKit
import ARKit
import AVKit

@objc open class UiVideoNode: RenderNode {
    static let defaultDimension: CGFloat = 512.0
    static let minimumVolume: CGFloat = 0.0
    static let maximumVolume: CGFloat = 1.0
    static let minimumDimension: CGFloat = 1.0
    static let maximumDimension: CGFloat = 2048.0

    @objc var alignment: Alignment = .centerCenter {
        didSet { setNeedsLayout() }
    }

    @objc var looping: Bool = true

    fileprivate var _width: CGFloat = UiVideoNode.defaultDimension
    @objc var width: CGFloat {
        get { return _width }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, UiVideoNode.minimumDimension, UiVideoNode.maximumDimension)
            if clampedValue != _width {
                _width = clampedValue
                videoScene?.size.width = clampedValue
                setNeedsLayout()
            }
        }
    }

    fileprivate var _height: CGFloat = UiVideoNode.defaultDimension
    @objc var height: CGFloat {
        get { return _height }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, UiVideoNode.minimumDimension, UiVideoNode.maximumDimension)
            if clampedValue != _height {
                _height = clampedValue
                videoScene?.size.height = clampedValue
                setNeedsLayout()
            }
        }
    }

    @objc var size: CGSize = CGSize(width: 1.0, height: 1.0) {
        didSet {
            plane?.width = size.width
            plane?.height = size.height
            setNeedsLayout()
        }
    }

    @objc override var anchorPosition: SCNVector3 {
        didSet {
            planeNode?.position = anchorPosition
        }
    }

    @objc var videoPath: URL? = nil {
        didSet {
            cleanupVideoPlayer()
            setupVideoPlayer()
            setupVideoNode()
            setupVideoScene()
            setNeedsLayout()
        }
    }

    @objc var viewMode: VideoViewMode = .fullArea {
        didSet { setNeedsLayout() }
    }

    fileprivate var _volume: CGFloat = 0.5
    @objc var volume: CGFloat {
        get { return _volume }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, UiVideoNode.minimumVolume, UiVideoNode.maximumVolume)
            if clampedValue != _volume {
                _volume = clampedValue
                videoPlayer?.volume = Float(_volume)
            }
        }
    }

    @objc var action: VideoAction = .stop {
        didSet {
            switch action {
            case .start:
                videoPlayer?.play()
            case .stop:
                videoPlayer?.pause()
                videoPlayer?.seek(to: CMTime.zero)
            case .pause:
                videoPlayer?.pause()
            }
        }
    }

    var plane: SCNPlane!
    var planeNode: SCNNode!
    var videoScene: SKScene?
    var videoItem: AVPlayerItem?
    fileprivate var videoItemStatusObserver: NSKeyValueObservation?
    var videoPlayer: AVPlayerProtocol?
    var videoNode: SKVideoNode?

    @objc override func setupNode() {
        super.setupNode()
        setupInitialState()
    }

    @objc func setupInitialState() {
        plane = SCNPlane(width: size.width, height: size.height)
        plane.firstMaterial?.lightingModel = .constant
        plane.firstMaterial?.isDoubleSided = true
        planeNode = SCNNode(geometry: plane)
        planeNode.position = anchorPosition
        contentNode.addChildNode(planeNode)
    }

    func setupVideoScene() {
        guard let videoNode = self.videoNode else { return }
        videoScene = SKScene(size: CGSize(width: width, height: height))
        videoScene?.scaleMode = .fill
        videoScene?.addChild(videoNode)
        plane?.firstMaterial?.diffuse.contents = videoScene
    }

    func setupVideoNode() {
        guard let videoPlayer = self.videoPlayer else { return }
        videoNode = SKVideoNode(avPlayer: videoPlayer as! AVPlayer)
        videoNode?.size = CGSize(width: width, height: height)
        videoNode?.position = CGPoint(x: width/2, y: height/2)
        videoNode?.yScale = -1
    }

    func cleanupVideoPlayer() {
        guard let videoPlayer = videoPlayer else { return }
        videoPlayer.pause()
        NotificationCenter.default.removeObserver(self, name: .AVPlayerItemDidPlayToEndTime, object: videoPlayer.currentItem)
    }

    func setupVideoPlayer() {
        guard let videoURL = self.videoPath else { return }
        videoItem = AVPlayerItem(url: videoURL)
        self.videoItemStatusObserver = videoItem?.observe(\.status, options:  [.new, .old], changeHandler: { [weak self] (playerItem, change) in
            if playerItem.status == .readyToPlay {
                self?.videoPlayer?.volume = Float(self?._volume ?? 0.0)
            }
        })
        videoPlayer = AVPlayer(playerItem: videoItem)

        NotificationCenter.default.addObserver(self, selector: #selector(playerDidFinishPlaying),
                                               name: .AVPlayerItemDidPlayToEndTime, object: videoPlayer?.currentItem)
    }

    @objc func playerDidFinishPlaying(note: NSNotification) {
        if looping {
            videoPlayer?.seek(to: CMTime.zero)
            videoPlayer?.play()
        }
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let alignment = Convert.toAlignment(props["alignment"]) {
            self.alignment = alignment
        }

        if let size = Convert.toCGSize(props["size"]) {
            self.size = size
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let action = Convert.toVideoAction(props["action"]) {
            self.action = action
        }

        if let volume = Convert.toCGFloat(props["volume"]) {
            self.volume = volume
        }

        if let looping = Convert.toBool(props["looping"]) {
            self.looping = looping
        }

        if let videoPath = Convert.toFileURL(props["videoPath"]) {
            self.videoPath = videoPath
        }

        if let viewMode = Convert.toVideoViewMode(props["viewMode"]) {
            self.viewMode = viewMode
        }
    }

    @objc override func _calculateSize() -> CGSize {
        return size
    }
}
