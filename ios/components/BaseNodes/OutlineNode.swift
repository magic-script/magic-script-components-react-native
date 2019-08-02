//
//  OutlineNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 04/07/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit

@objc class OutlineNode: SCNNode {

    let width: CGFloat
    let height: CGFloat
    let cornerRadius: CGFloat
    let lineWidth: CGFloat
    let color: UIColor

    @objc init(width: CGFloat, height: CGFloat, cornerRadius: CGFloat, lineWidth: CGFloat = 0.005, color: UIColor = UIColor.white) {
        self.width = width
        self.height = height
        self.cornerRadius = cornerRadius
        self.lineWidth = lineWidth
        self.color = color
        super.init()
        setupNode()
    }

    @objc init(contentSize: CGSize, cornerRadius: CGFloat, lineWidth: CGFloat = 0.005, color: UIColor = UIColor.white) {
        self.width = contentSize.width
        self.height = contentSize.height
        self.cornerRadius = cornerRadius
        self.lineWidth = lineWidth
        self.color = color
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        self.width = 0
        self.height = 0
        self.cornerRadius = 0
        self.lineWidth = 0
        self.color = UIColor.white
        super.init(coder: aDecoder)
        setupNode()
    }

    @objc func setupNode() {
        guard width > 0 && height > 0 && lineWidth > 0 else { return }

        // Create SpriteKit scene
        let maxSceneSize: CGFloat = 2048
        let widthInPixels = Measures.pixels(from: width)
        let heightInPixels = Measures.pixels(from: height)
        let outlineWidth = Measures.pixels(from: lineWidth)
        let scaleFactor: CGFloat = min(min(maxSceneSize / widthInPixels, maxSceneSize / heightInPixels), 1)
        let sizeInPixels = CGSize(width: ceil(scaleFactor * widthInPixels), height: ceil(scaleFactor * heightInPixels))

        // Add outline node
        let outlineRect = CGRect(origin: CGPoint.zero, size: CGSize(width: sizeInPixels.width, height: sizeInPixels.height)).insetBy(dx: 2 * outlineWidth, dy: 2 * outlineWidth)
        let outlinePath = UIBezierPath(roundedRect: outlineRect, cornerRadius: Measures.pixels(from: cornerRadius))
        UIGraphicsBeginImageContextWithOptions(sizeInPixels, false, 1);
        let context = UIGraphicsGetCurrentContext()
        context?.setFillColor(UIColor.clear.cgColor);
        context?.fill(outlineRect)
        context?.setStrokeColor(color.cgColor);
        context?.addPath(outlinePath.cgPath);
        context?.strokePath();
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        let planeGeometry = SCNPlane(width: width, height: height)
        planeGeometry.firstMaterial?.lightingModel = .constant
        planeGeometry.firstMaterial?.diffuse.contents = image
        planeGeometry.firstMaterial?.isDoubleSided = false
        let node = SCNNode(geometry: planeGeometry)
        addChildNode(node)
    }
}
