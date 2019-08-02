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

    @objc var text: String? {
        didSet { labelNode.text = text }
    }
    @objc var textColor: UIColor = UIColor.white {
        didSet { labelNode.fontColor = textColor }
    }
    @objc var textSize: CGFloat = 0.02 {
        didSet { reloadNeeded = true }
    }
//    @objc var textAlignment: HorizontalTextAlignment {
//        get { return labelNode.textAlignment }
//        set { labelNode.textAlignment = newValue }
//    }
    @objc var width: CGFloat = 0 {
        didSet { reloadNeeded = true }
    }
    @objc var height: CGFloat = 0 {
        didSet { reloadNeeded = true }
    }

    fileprivate var sprite: SKScene!
    fileprivate var labelNode: SKLabelNode!
    fileprivate var underlineNode: SKShapeNode!
    fileprivate var outlineNode: OutlineNode?
    fileprivate var reloadNeeded: Bool = true

    deinit {
    }

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }

        if outlineNode == nil {
            let sizeInMeters = getPrefferedSize()
            let margin: CGFloat = 0.003
            let radius: CGFloat = 0.5 * min(sizeInMeters.width, sizeInMeters.height) + margin
            let outlineWidth = sizeInMeters.width + ((sizeInMeters.width > sizeInMeters.height) ? 2 * radius : 2 * margin)
            let outlineHeight = sizeInMeters.height + ((sizeInMeters.height > sizeInMeters.width) ? 2 * radius : 2 * margin)
            outlineNode = OutlineNode(width: outlineWidth, height: outlineHeight, cornerRadius: radius)
            contentNode.insertChildNode(outlineNode!, at: 0)
        }

        outlineNode?.isHidden = false
    }

    @objc override func leaveFocus() {
        super.leaveFocus()
        outlineNode?.isHidden = true
    }

    @objc override func setupNode() {
        super.setupNode()
        reload()
    }

    fileprivate func createSpriteScene(size: CGSize, scale: CGFloat) -> SKScene {
        // Create SpriteKit scene
        let scene = SKScene(size: size)
        scene.backgroundColor = UIColor.clear
        scene.setScale(scale)

        // Add underline node
        let lineWidth: CGFloat = 5
        let underlineY: CGFloat = scene.size.height - lineWidth
        let linePath = UIBezierPath()
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
        labelNode.fontSize = min(0.5 * Measures.pixels(from: textSize), scene.size.height - (topMargin + bottomMargin))
        labelNode.horizontalAlignmentMode = .left
        labelNode.position = CGPoint(x: 0, y: labelBottom)
        labelNode.preferredMaxLayoutWidth = scene.size.width
        labelNode.yScale = -1
        scene.addChild(labelNode)

        return scene
    }

    fileprivate func getPrefferedSize() -> CGSize {
        let width: CGFloat = (self.width > 0) ? self.width : UiTextEditNode.defaultSize.width
        let height: CGFloat = (self.height > 0) ? self.height : UiTextEditNode.defaultSize.height
        return CGSize(width: width, height: height)
    }

    @objc func reload() {
        guard reloadNeeded else { return }
        reloadNeeded = false

        let sizeInMeters = getPrefferedSize()

        let maxSceneSize: CGFloat = 2048
        let widthInPixels = Measures.pixels(from: sizeInMeters.width)
        let heightInPixels = Measures.pixels(from: sizeInMeters.height)
        let scaleFactor: CGFloat = min(min(maxSceneSize / widthInPixels, maxSceneSize / heightInPixels), 1)
        let sizeInPixels = CGSize(width: ceil(scaleFactor * widthInPixels), height: ceil(scaleFactor * heightInPixels))
        sprite = createSpriteScene(size: sizeInPixels, scale: 1.0 / scaleFactor)

        if let plane = contentNode?.geometry as? SCNPlane {
            plane.width = width
            plane.height = height
            plane.firstMaterial?.diffuse.contents = sprite
        } else {
//            contentNode?.removeFromParentNode()
            let planeGeometry = SCNPlane(width: width, height: height)
            planeGeometry.firstMaterial?.lightingModel = .constant
            planeGeometry.firstMaterial?.diffuse.contents = sprite
            planeGeometry.firstMaterial?.isDoubleSided = true
//            contentNode = SCNNode(geometry: planeGeometry)
//            addChildNode(contentNode)
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

