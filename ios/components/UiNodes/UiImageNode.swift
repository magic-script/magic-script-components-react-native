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
        didSet { updateDisplay(); updateLayout() }
    }

    @objc var width: CGFloat = 0.5 {
        didSet { setNeedsLayout() }
    }

    @objc var height: CGFloat = 0.5 {
        didSet { setNeedsLayout() }
    }

    @objc var useFrame: Bool = false {
        didSet { setFrame(visible: useFrame); setNeedsLayout() }
    }

    @objc var color: UIColor? {
        didSet { updateDisplay(); setNeedsLayout() }
    }

    fileprivate var planeGeometry: SCNPlane!
    fileprivate var frameNode: SCNNode?
    fileprivate var colorImage: UIImage?

    @objc override func setupNode() {
        super.setupNode()

        assert(planeGeometry == nil, "Node must not be initialized!")
        planeGeometry = SCNPlane(width: width, height: height)
        planeGeometry.firstMaterial?.lightingModel = .constant
        planeGeometry.firstMaterial?.diffuse.contents = UIColor.init(white: 1, alpha: 0)
        planeGeometry.firstMaterial?.isDoubleSided = true
        contentNode.addChildNode(SCNNode(geometry: planeGeometry))
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

        if let useFrame = Convert.toBool(props["useFrame"]) {
            self.useFrame = useFrame
        }

        if let color = Convert.toColor(props["color"]) {
            self.color = color
        }
    }

    @objc override func _calculateSize() -> CGSize {
        return CGSize(width: width, height: height)
    }

    @objc override func updateLayout() {
        let size: CGSize = image?.size ?? CGSize(width: width, height: height)
        let horizontalFactor: CGFloat = width / size.width
        let verticalFactor: CGFloat = height / size.height
        let factor = min(horizontalFactor, verticalFactor)
        planeGeometry.width = factor * size.width
        planeGeometry.height = factor * size.height

        frameNode?.scale = SCNVector3(Float(planeGeometry.width), Float(planeGeometry.height), 1.0)
    }

    fileprivate func updateDisplay() {
        planeGeometry.firstMaterial?.diffuse.contents = image ?? color
        planeGeometry.firstMaterial?.multiply.contents = (image != nil) ? color : nil
    }

    fileprivate func setFrame(visible: Bool) {
        if visible {
            if frameNode == nil {
                frameNode = SCNNode(geometry: generateFrameGeometry())
                contentNode.addChildNode(frameNode!)
            }
        } else {
            frameNode?.removeFromParentNode()
            frameNode = nil
        }
    }

    fileprivate func generateFrameGeometry() -> SCNGeometry? {
        guard useFrame else { return nil }
        let vertices: [SCNVector3] = [
            SCNVector3(-0.5, -0.5, 0),
            SCNVector3( 0.5, -0.5, 0),
            SCNVector3( 0.5,  0.5, 0),
            SCNVector3(-0.5,  0.5, 0)
        ]

        let data = NSData(bytes: vertices, length: MemoryLayout<SCNVector3>.size * vertices.count) as Data
        let vertexSource = SCNGeometrySource(data: data,
                                             semantic: .vertex,
                                             vectorCount: vertices.count,
                                             usesFloatComponents: true,
                                             componentsPerVector: 3,
                                             bytesPerComponent: MemoryLayout<Float>.size,
                                             dataOffset: 0,
                                             dataStride: MemoryLayout<SCNVector3>.stride)

        let indices: [Int16] = [0, 1, 1, 2, 2, 3, 3, 0]
        let indexData = NSData(bytes: indices, length: MemoryLayout<Int16>.size * indices.count) as Data
        let element = SCNGeometryElement(data: indexData, primitiveType: .line, primitiveCount: indices.count/2, bytesPerIndex: MemoryLayout<Int16>.size)
        let frameGeometry = SCNGeometry(sources: [vertexSource], elements: [element])
        frameGeometry.firstMaterial?.lightingModel = .constant
        frameGeometry.firstMaterial?.diffuse.contents = UIColor.white
        return frameGeometry
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
