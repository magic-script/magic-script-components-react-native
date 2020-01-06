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
import GLTFSceneKit

@objc open class UiModelNode: RenderNode {

    var downloader: Downloading = FileDownloader()
    var sceneBuilder: GLTFSceneSourceBuilding = GLTFSceneSourceBuilder()

    @objc var url: URL? {
        didSet {
            guard let url = url else { cleanNode(); setNeedsLayout(); return }
            downloader.download(remoteURL: url) { [weak self] (localURL) -> (Void) in
                self?.loadModel(localURL)
                self?.setNeedsLayout()
            }
        }
    }

    @objc var isLoaded: Bool {
        return !contentNode.childNodes.isEmpty
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let url = Convert.toFileURL(props["modelPath"]) {
            self.url = url
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let bbox = self.boundingBox
        return CGSize(width: CGFloat(bbox.max.x - bbox.min.x), height: CGFloat(bbox.max.y - bbox.min.y))
    }

    fileprivate func cleanNode() {
        while !contentNode.childNodes.isEmpty {
            contentNode.childNodes.last?.removeFromParentNode()
        }
    }

    fileprivate func loadModel(_ modelURL: URL?) {
        cleanNode()
        guard let modelURL = modelURL else { return }

        if ["glb", "gltf"].contains(modelURL.pathExtension.lowercased()) {
            do {
                let sceneSource = try sceneBuilder.build(path: modelURL.path, options: nil, extensions: nil)
                let scene = try sceneSource.scene(options: nil)
                contentNode.addChildNode(scene.rootNode)
            } catch {
                print("[UiModelNode] \(error.localizedDescription) Path: \(modelURL.path)")
                return
            }
        } else {
            if let refNode = SCNReferenceNode(url: modelURL) {
                refNode.load()
                if refNode.isLoaded {
                    contentNode.addChildNode(refNode)
                }
            }
        }
    }
}
