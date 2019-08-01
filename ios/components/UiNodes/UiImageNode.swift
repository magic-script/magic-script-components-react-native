//
//  UiImageNode.swift
//  RNMagicScript
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
        set { planeGeometry.firstMaterial?.diffuse.contents = newValue; updateLayout() }
    }

    @objc var width: CGFloat = 0.5 {
        didSet { setNeedsLayout() }
    }

    @objc var height: CGFloat = 0.5 {
        didSet { setNeedsLayout() }
    }

    @objc var color: UIColor? {
        didSet {
            image = (color != nil) ? UIImage.image(from: [color!], size: 1) : nil
            setNeedsLayout()
        }
    }

    fileprivate var planeGeometry: SCNPlane!
    fileprivate var colorImage: UIImage?

    @objc override func setupNode() {
        super.setupNode()

        assert(planeGeometry == nil, "Node must not be initialized!")
        planeGeometry = SCNPlane(width: width, height: height)
        planeGeometry.firstMaterial?.lightingModel = .constant
        planeGeometry.firstMaterial?.diffuse.contents = UIColor.init(white: 1, alpha: 0)
        planeGeometry.firstMaterial?.isDoubleSided = true
        addChildNode(SCNNode(geometry: planeGeometry))
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let url = Convert.toFileURL(props["filePath"]) {
            self.url = url
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        if let color = Convert.toColor(props["color"]) {
            self.color = color
        }
    }

    @objc override func getSize() -> CGSize {
        return CGSize(width: width, height: height)
    }

    @objc override func updateLayout() {
        guard let image = self.image else {
            planeGeometry.width = width
            planeGeometry.height = height
            return
        }
        if let _ = self.url {
            let horizontalFactor: CGFloat = width / image.size.width
            let verticalFactor: CGFloat = height / image.size.height
            let factor = min(horizontalFactor, verticalFactor)
            planeGeometry.width = factor * image.size.width
            planeGeometry.height = factor * image.size.height
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
