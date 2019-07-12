//
//  OutlineNode.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 04/07/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit
import SpriteKit

@objc class OutlineNode: SCNNode {

    let width: CGFloat
    let height: CGFloat
    let cornerRadius: CGFloat
    let lineWidth: CGFloat

    var color: UIColor = UIColor.white {
        didSet { outlineNode?.strokeColor = color }
    }

    fileprivate var outlineNode: SKShapeNode!

    @objc init(width: CGFloat, height: CGFloat, cornerRadius: CGFloat, lineWidth: CGFloat = 0.005) {
        let radius: CGFloat = min(0.5 * min(width, height), 5)
        self.width = width
        self.height = height
        self.cornerRadius = radius
        self.lineWidth = lineWidth
        super.init()
        setupNode()
    }

    @objc init(contentSize: CGSize, cornerRadius: CGFloat, lineWidth: CGFloat = 5) {
        self.width = contentSize.width
        self.height = contentSize.height
        self.cornerRadius = cornerRadius
        self.lineWidth = lineWidth
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        self.width = 0
        self.height = 0
        self.cornerRadius = 0
        self.lineWidth = 0
        super.init(coder: aDecoder)
        setupNode()
    }

    @objc func setupNode() {
        guard width > 0 && height > 0 && lineWidth > 0 else { return }

        // Create SpriteKit scene
        let maxSceneSize: CGFloat = 2048
        let widthInPixels = Measures.pixels(in: width)
        let heightInPixels = Measures.pixels(in: height)
        let outlineWidth = Measures.pixels(in: lineWidth)
        let scaleFactor: CGFloat = min(min(maxSceneSize / widthInPixels, maxSceneSize / heightInPixels), 1)
        let sizeInPixels = CGSize(width: ceil(scaleFactor * widthInPixels), height: ceil(scaleFactor * heightInPixels))
        let scene = SKScene(size: CGSize(width: sizeInPixels.width, height: sizeInPixels.height))
        scene.backgroundColor = UIColor.clear
        scene.setScale(1.0 / scaleFactor)

        // Add outline node
        let outlineRect = CGRect(origin: CGPoint.zero, size: scene.size).insetBy(dx: 2 * outlineWidth, dy: 2 * outlineWidth)
        let outlinePath = UIBezierPath(roundedRect: outlineRect, cornerRadius: Measures.pixels(in: cornerRadius))
        let outlineNode = SKShapeNode(path: outlinePath.cgPath)
        outlineNode.strokeColor = color
        outlineNode.lineWidth = outlineWidth
        outlineNode.glowWidth = outlineWidth
        scene.addChild(outlineNode)

        let planeGeometry = SCNPlane(width: width, height: height)
        planeGeometry.firstMaterial?.lightingModel = .constant
        planeGeometry.firstMaterial?.diffuse.contents = scene
        planeGeometry.firstMaterial?.isDoubleSided = false
        let node = SCNNode(geometry: planeGeometry)
        addChildNode(node)
    }
}
