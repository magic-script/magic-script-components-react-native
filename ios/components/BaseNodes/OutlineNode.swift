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
    fileprivate let cornerRadius: CGFloat

    @objc init(width: CGFloat, height: CGFloat) {
        let radius: CGFloat = min(0.5 * min(width, height), 5)
        self.width = width + 2 * radius
        self.height = height + 2 * radius
        self.cornerRadius = radius
        super.init()
        setupNode()
    }

    @objc init(contentSize: CGSize) {
        let margin: CGFloat = 0.003
        let radius: CGFloat = 0.5 * min(contentSize.width, contentSize.height) + margin
        self.width = contentSize.width + ((contentSize.width > contentSize.height) ? 2 * radius : 2 * margin)
        self.height = contentSize.height + ((contentSize.height > contentSize.width) ? 2 * radius : 2 * margin)
        self.cornerRadius = radius
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        self.width = 0
        self.height = 0
        self.cornerRadius = 0
        super.init(coder: aDecoder)
        setupNode()
    }

    @objc func setupNode() {
        guard width > 0 && height > 0 else { return }

        // Create SpriteKit scene
        let sizeInPixels = CGSize(width: ceil(Measures.pixels(in: width)), height: ceil(Measures.pixels(in: height)))
        let scene = SKScene(size: CGSize(width: sizeInPixels.width, height: sizeInPixels.height))
        scene.backgroundColor = UIColor.clear

        // Add outline node
        let outlineWidth: CGFloat = 5
        let outlineRect = CGRect(origin: CGPoint.zero, size: scene.size).insetBy(dx: 2 * outlineWidth, dy: 2 * outlineWidth)
        let outlinePath = UIBezierPath(roundedRect: outlineRect, cornerRadius: Measures.pixels(in: cornerRadius))
        let outlineNode = SKShapeNode(path: outlinePath.cgPath)
        outlineNode.strokeColor = UIColor.blue
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
