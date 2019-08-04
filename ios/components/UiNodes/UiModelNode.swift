//
//  UIModelNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/06/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

import SceneKit

@objc class UiModelNode: TransformNode {
    fileprivate var downloadTask: URLSessionDownloadTask?
    fileprivate var dataTask: URLSessionDataTask?
    deinit {
        downloadTask?.cancel()
        dataTask?.cancel()
    }

    @objc var url: URL? {
        didSet {
            guard let url = url else { cleanNode(); return }
            downloadModel(modelURL: url) { [weak self] (localURL) -> (Void) in
                self?.loadModel(localURL)
            }
        }
    }

    @objc override func setupNode() {
        super.setupNode()
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
                let sceneSource = GLTFSceneSource(url: modelURL, options: nil)
                let scene = try sceneSource.scene()

                contentNode.addChildNode(scene.rootNode)
            } catch {
                print("\(error.localizedDescription)")
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

extension UiModelNode {
    fileprivate func downloadModel(modelURL: URL, completion: @escaping (URL?) -> (Void)) {
        downloadTask?.cancel()

        if modelURL.isFileURL {
            completion(modelURL)
            return
        }

        downloadTask = URLSession.shared.downloadTask(with: modelURL) { [weak self] (tmpURL, response, error) in
            self?.downloadTask = nil
            guard let tmpURL = tmpURL else {
                DispatchQueue.main.async() { completion(nil) }
                return
            }

            do {
                let documentsURL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first!
                let localURL: URL = documentsURL.appendingPathComponent(modelURL.lastPathComponent)
                try? FileManager.default.removeItem(at: localURL)
                try FileManager.default.copyItem(at: tmpURL, to: localURL)
                DispatchQueue.main.async() { completion(localURL) }
            } catch (let writeError) {
                print("Error writing model file: \(writeError)")
                DispatchQueue.main.async() { completion(nil) }
            }
        }
        downloadTask?.resume()
    }
}
