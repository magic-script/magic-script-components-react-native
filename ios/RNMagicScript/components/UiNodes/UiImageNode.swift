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

@objc open class UiImageNode: UiNode {
    static let defaultSize: CGFloat = 0.2

    @objc var url: URL? {
        didSet {
            guard let url = url else { image = nil; return }
            downloadImage(imageURL: url) { [weak self] (image) -> (Void) in
                self?.image = image
            }
        }
    }
    @objc var icon: SystemIcon? {
        didSet { image = icon?.getImage(forceDefaultImage: useDefaultIcon) }
    }
    @objc var image: UIImage? {
        didSet { updateDisplay(); updateLayout() }
    }
    @objc var width: CGFloat = 0.0 {
        didSet { setNeedsLayout() }
    }
    @objc var height: CGFloat = 0.0 {
        didSet { setNeedsLayout() }
    }
    @objc var useFrame: Bool = false {
        didSet { setFrame(visible: useFrame); setNeedsLayout() }
    }
    @objc var color: UIColor? {
        didSet { updateDisplay(); setNeedsLayout() }
    }
    @objc var useDefaultIcon: Bool = false {
        didSet { updateDisplay(); setNeedsLayout() }
    }

    fileprivate var planeGeometry: SCNPlane!
    fileprivate var imageNode: SCNNode!
    fileprivate var frameNode: SCNNode?
    fileprivate var dataTask: URLSessionDataTask?
    deinit {
        dataTask?.cancel()
    }

    @objc override func setupNode() {
        super.setupNode()

        alignment = Alignment.centerCenter
        assert(planeGeometry == nil, "Node must not be initialized!")
        planeGeometry = SCNPlane(width: 1.0, height: 1.0)
        planeGeometry.firstMaterial?.lightingModel = .constant
        planeGeometry.firstMaterial?.diffuse.contents = UIColor.init(white: 1, alpha: 0)
        planeGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        imageNode = SCNNode(geometry: planeGeometry)
        contentNode.addChildNode(imageNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let useDefaultIcon = Convert.toBool(props["useDefaultIcon"]) {
            self.useDefaultIcon = useDefaultIcon
        }

        if let url = Convert.toFileURL(props["filePath"]) {
            self.url = url
        } else if let icon = Convert.toSystemIcon(props["icon"]) {
            self.icon = icon
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
        let imageSize: CGSize
        if let image = image {
            imageSize = image.size
        } else {
            let w: CGFloat = (width > 0) ? width : UiImageNode.defaultSize
            let h: CGFloat = (height > 0) ? height : UiImageNode.defaultSize
            imageSize = CGSize(width: w, height: h)
        }

        let horizontalFactor: CGFloat = width / imageSize.width
        let verticalFactor: CGFloat = height / imageSize.height
        let factor: CGFloat
        if horizontalFactor > 0 && verticalFactor > 0 {
            factor = min(horizontalFactor, verticalFactor)
        } else {
            factor = max(horizontalFactor, verticalFactor)
        }
        return CGSize(width:factor * imageSize.width, height: factor * imageSize.height)
    }

    @objc override func updateLayout() {
        let size = getSize()
        let scale = SCNVector3(Float(size.width), Float(size.height), 1.0)
        imageNode.scale = scale
        frameNode?.scale = scale
    }

    fileprivate func updateDisplay() {
        planeGeometry.firstMaterial?.diffuse.contents = image ?? color
        planeGeometry.firstMaterial?.multiply.contents = (image != nil) ? color : nil
        planeGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
    }

    fileprivate func setFrame(visible: Bool) {
        if visible {
            if frameNode == nil {
                frameNode = SCNNode(geometry: generateFrameGeometry())
                frameNode?.renderingOrder = 1
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
        let indices: [Int16] = [0, 1, 1, 2, 2, 3, 3, 0]
        let source = SCNGeometrySource(vertices: vertices)
        let element = SCNGeometryElement(indices: indices, primitiveType: .line)
        let frameGeometry = SCNGeometry(sources: [source], elements: [element])
        frameGeometry.firstMaterial?.lightingModel = .constant
        frameGeometry.firstMaterial?.diffuse.contents = UIColor.white
        frameGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        frameGeometry.firstMaterial?.readsFromDepthBuffer = false
        return frameGeometry
    }
}

extension UiImageNode {
    fileprivate func downloadImage(imageURL: URL, completion: @escaping (UIImage?) -> (Void)) {
        dataTask?.cancel()
        dataTask = URLSession.shared.dataTask(with: imageURL) { [weak self] data, response, error in
            self?.dataTask = nil
            guard let data = data, error == nil else { DispatchQueue.main.async() { completion(nil) }; return }
            let downloadedImage = UIImage(data: data)
            DispatchQueue.main.async() { completion(downloadedImage) }
        }
        dataTask?.resume()
    }
}
