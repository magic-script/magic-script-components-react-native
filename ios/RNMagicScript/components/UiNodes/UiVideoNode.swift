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
    static let defaultWidth: CGFloat = 512.0
    static let defaultHeight: CGFloat = 512.0

    @objc var alignment: Alignment = .centerCenter {
        didSet { setNeedsLayout() }
    }

    @objc var looping: Bool = true

    @objc var width: CGFloat = UiVideoNode.defaultWidth {
        didSet {
            videoScene?.size.width = width
            setNeedsLayout()
        }
    }

    @objc var height: CGFloat = UiVideoNode.defaultHeight {
        didSet {
            videoScene?.size.height = height
            setNeedsLayout()
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
            setupVideoPlayer()
            setupVideoNode()
            setupVideoScene()
            setNeedsLayout()
        }
    }

    @objc var viewMode: VideoViewMode = .fullArea {
        didSet { setNeedsLayout() }
    }

    @objc var volume: Float = 0.5 {
        didSet {
            videoPlayer?.volume = volume
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
    var videoPlayer: AVPlayerProtocol?
    var videoNode: SKVideoNode?

    @objc override func setupNode() {
        super.setupNode()
        setupInitialState()
    }

    @objc func setupInitialState() {
        plane = SCNPlane(width: size.width, height: size.height)
        plane.firstMaterial?.diffuse.wrapS = SCNWrapMode.clamp
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

    func setupVideoPlayer() {
        guard let videoURL = self.videoPath else { return }
        videoItem = AVPlayerItem(url: videoURL)
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
            self.width = Math.clamp(width, 1.0, 2048)
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = Math.clamp(height, 1.0, 2048)
        }

        if let action = Convert.toVideoAction(props["action"]) {
            self.action = action
        }

        if let volume = Convert.toFloat(props["volume"]) {
            self.volume = Math.clamp(volume, 0.0, 1.0)
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
        return CGSize(width: width, height: height)
    }
}
