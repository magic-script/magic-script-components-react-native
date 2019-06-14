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
    
    @objc var size: CGSize {
        get { return CGSize(width: planeGeometry.width, height: planeGeometry.height) }
        set { planeGeometry.width = newValue.width; planeGeometry.height = newValue.height; updateImageSize() }
    }

    @objc var width: CGFloat {
        get { return planeGeometry.width }
        set { planeGeometry.width = newValue; updateImageSize() }
    }

    @objc var height: CGFloat {
        get { return planeGeometry.height }
        set { planeGeometry.height = newValue; updateImageSize() }
    }

    fileprivate var planeGeometry: SCNPlane!

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    fileprivate func setupNode() {
        planeGeometry = SCNPlane(width: 1, height: 1)
        planeGeometry.firstMaterial?.lightingModel = .constant
        addChildNode(SCNNode(geometry: planeGeometry))
        updateImageSize()
    }

    fileprivate func updateImageSize() {
//        setBBox(visible: true, forceUpdate: true)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let filePath = props["filePath"] as? String {
            let localURL: URL = URL(string: "http://localhost:8081/assets/")!
            url = localURL.appendingPathComponent(filePath);
        } else if let source = props["source"] {
            print("source: \(source)")
            url = RCTConvert.rctImageSource(source).request?.url
        } else {
            url = nil
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
