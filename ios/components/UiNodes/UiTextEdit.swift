//
//  UiTextEdit.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 02/07/2019.
//  Copyright © 2019 Nomtek. All rights reserved.
//

import SceneKit
import SpriteKit

@objc class UiTextEditNode: UiNode {

    static fileprivate let defaultSize: CGSize = CGSize(width: 0.05, height: 0.02)
    fileprivate func pixels(in meters: CGFloat) -> CGFloat {
        let ppi: CGFloat = 326.0
        let inchesInMeters: CGFloat = 39.3700787
        let ppm: CGFloat = ppi * inchesInMeters
        return ppm * meters
    }

    @objc var text: String = "" {
        didSet { labelNode.text = text }
    }
    @objc var textColor: UIColor = UIColor.white {
        didSet { labelNode.fontColor = textColor }
    }
    @objc var textSize: CGFloat = 0.02 {
        didSet { labelNode.fontSize = min(0.5 * pixels(in: textSize), 0.9 * sprite.size.height) }
    }
    @objc var width: CGFloat = 0 {
        didSet { reloadNeeded = true }
    }
    @objc var height: CGFloat = 0 {
        didSet { reloadNeeded = true }
    }

    fileprivate var sprite: SKScene!
    fileprivate var labelNode: SKLabelNode!
    fileprivate var underlineNode: SKShapeNode!
    fileprivate var contentNode: SCNNode!
    fileprivate var reloadNeeded: Bool = true

    deinit {
    }

    @objc override func setupNode() {
        super.setupNode()
        reload()
    }

    fileprivate func createSpriteScene(size: CGSize) -> SKScene {
        // Create SpriteKit scene
        let scene = SKScene(size: size)
        scene.backgroundColor = UIColor.init(white: 0, alpha: 0.1)

        // Add underline node
        let linePath = UIBezierPath()
        let lineWidth: CGFloat = 5
        let underlineY: CGFloat = scene.size.height - lineWidth
        linePath.move(to: CGPoint(x: 0, y: 0))
        linePath.addLine(to: CGPoint(x: scene.size.width, y: 0))
        underlineNode = SKShapeNode(path: linePath.cgPath)
        underlineNode.position = CGPoint(x: 0, y: underlineY)
        underlineNode.strokeColor = UIColor.white
        underlineNode.lineWidth = lineWidth
        scene.addChild(underlineNode)

        // Add label node
        let topMargin: CGFloat = 2 * lineWidth
        let bottomMargin: CGFloat = topMargin + 3 * lineWidth
        let labelBottom: CGFloat = underlineY - bottomMargin
        labelNode = SKLabelNode(text: text)
        labelNode.fontColor = textColor
        labelNode.fontSize = min(0.5 * pixels(in: textSize), scene.size.height - (topMargin + bottomMargin))
        labelNode.horizontalAlignmentMode = .left
        labelNode.position = CGPoint(x: 0, y: labelBottom)
        labelNode.preferredMaxLayoutWidth = scene.size.width
        labelNode.yScale = -1
        scene.addChild(labelNode)

        return scene
    }

    @objc func reload() {
        guard reloadNeeded else { return }
        reloadNeeded = false

        let width: CGFloat = (self.width > 0) ? self.width : UiTextEditNode.defaultSize.width
        let height: CGFloat = (self.height > 0) ? self.height : UiTextEditNode.defaultSize.height
        let sizeInPixels = CGSize(width: pixels(in: width), height: pixels(in: height))

        sprite = createSpriteScene(size: sizeInPixels)

        if let plane = contentNode?.geometry as? SCNPlane {
            plane.width = width
            plane.height = height
            plane.firstMaterial?.diffuse.contents = sprite
        } else {
            contentNode?.removeFromParentNode()
            let planeGeometry = SCNPlane(width: width, height: height)
            planeGeometry.firstMaterial?.lightingModel = .constant
            planeGeometry.firstMaterial?.diffuse.contents = sprite
            planeGeometry.firstMaterial?.isDoubleSided = true
            contentNode = SCNNode(geometry: planeGeometry)
            addChildNode(contentNode)
        }
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let text = Convert.toString(props["text"]) {
            self.text = text
        }

        if let textColor = Convert.toColor(props["textColor"]) {
            self.textColor = textColor
        }

        if let textSize = Convert.toCGFloat(props["textSize"]) {
            self.textSize = textSize
        }

        if let width = Convert.toCGFloat(props["width"]) {
            self.width = width
        }

        if let height = Convert.toCGFloat(props["height"]) {
            self.height = height
        }

        reload()
    }
}

