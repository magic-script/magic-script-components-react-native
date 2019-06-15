//
//  UiImageNode.swift
//  SceneKitDemo
//
//  Created by Pawel Leszkiewicz on 23/05/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class UiImageNode: UiNode {
    fileprivate var dataTask: URLSessionDataTask?
    deinit {
        dataTask?.cancel()
    }

    @objc var url: URL? {
        didSet {
            guard let url = url else { image = nil; return }
            downloadImage(imageURL: url) { [weak self] (image) -> (Void) in
                self?.image = image
            }
        }
    }

    @objc var image: UIImage? {
        get { return planeGeometry.firstMaterial?.diffuse.contents as? UIImage }
        set { planeGeometry.firstMaterial?.diffuse.contents = newValue }
    }

    @objc var width: CGFloat {
        get { return planeGeometry.width }
        set { planeGeometry.width = newValue }
    }

    @objc var height: CGFloat {
        get { return planeGeometry.height }
        set { planeGeometry.height = newValue }
    }

    fileprivate var planeGeometry: SCNPlane!

    @objc override func setupNode() {
        super.setupNode()
        
        planeGeometry = SCNPlane(width: 1, height: 1)
        planeGeometry.firstMaterial?.lightingModel = .constant
        addChildNode(SCNNode(geometry: planeGeometry))
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let filePath = Convert.toString(props["filePath"]) {
            #if targetEnvironment(simulator)
            let localURL: URL = URL(string: "http://localhost:8081/assets/")!
            #else
            // bundle.url: file:///var/containers/Bundle/Application/3E4ECDCA-D4AD-4C3A-93A1-A8B91DAB263F/ARDemo%20Release.app/
            // target.url: file:///var/containers/Bundle/Application/62FE4E48-5D00-4E13-8F46-78CB10D95CB6/ARDemo%20Release.app/assets/
            let localURL: URL = Bundle.main.bundleURL.appendingPathComponent("assets")
            #endif
            print("image.url = \(localURL.appendingPathComponent(filePath))")
            url = localURL.appendingPathComponent(filePath)
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }
    }
}

extension UiImageNode {
    fileprivate func downloadImage(imageURL: URL, completion: @escaping (UIImage?) -> (Void)) {
        dataTask?.cancel()
        dataTask = URLSession.shared.dataTask(with: imageURL) { [weak self] data, response, error in
            self?.dataTask = nil
            guard let data = data, error == nil else { DispatchQueue.main.async() { completion(nil) }; return }
            if let downloadedImage = UIImage(data: data) {
                DispatchQueue.main.async() { completion(downloadedImage) }
            } else {
                DispatchQueue.main.async() { completion(nil) }
            }
        }
        dataTask?.resume()
    }
}
