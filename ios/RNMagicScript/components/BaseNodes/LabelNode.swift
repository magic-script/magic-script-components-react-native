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
import UIKit

class LabelNode: SCNNode {
    static fileprivate let defaultTextSizeInMeters: CGFloat = 0.015
    static fileprivate let geometryFixedTextSizeInMeters: CGFloat = 20.0

    @objc var allCaps: Bool = false {
        didSet { reloadNeeded = true }
    }
    @objc var boundsSize: CGSize = CGSize.zero {
        didSet { reloadNeeded = true }
    }
    // Sets the additional character spacing that is applied between characters.
    // Note a spacing of 0 is the default spacing. Any other value is the additional distance between
    // characters as a multiplier of the glyph height.
    @objc var charSpacing: CGFloat = 0.0 {
        didSet { reloadNeeded = true }
    }
    @objc var defaultTextSize: CGFloat = LabelNode.defaultTextSizeInMeters {
        didSet { reloadNeeded = true }
    }
    @objc var fontStyle: FontStyle = .normal {
        didSet { reloadNeeded = true }
    }
    @objc var fontWeight: FontWeight = .regular {
       didSet { reloadNeeded = true }
    }
    // Sets the line spacing to adjust the distance between lines of text;
    // e.g., use 1 for single-spaced text, 2 for double-spaced text. Default is 1.0.
    @objc var lineSpacing: CGFloat = 1.0 {
        didSet { reloadNeeded = true }
    }
    @objc var multiline: Bool = false {
        didSet { reloadNeeded = true }
    }
    @objc var text: String? {
        didSet { reloadNeeded = true }
    }
    @objc var textAlignment: HorizontalTextAlignment = .left {
        didSet { reloadNeeded = true }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { reloadNeeded = true }
    }
    @objc var textPadding: UIEdgeInsets = UIEdgeInsets.zero {
        didSet { reloadNeeded = true }
    }
    @objc var textSize: CGFloat = 0 {
        didSet { reloadNeeded = true }
    }
    @objc var tracking: Int = 50 // not supported by Lumin yet

    @objc var readsFromDepthBuffer: Bool {
        get { return labelGeometry.firstMaterial?.readsFromDepthBuffer ?? false }
        set { labelGeometry.firstMaterial?.readsFromDepthBuffer = newValue }
    }

    fileprivate var labelGeometry: SCNText!
    fileprivate var labelNode: SCNNode!
    fileprivate var reloadNeeded: Bool = false
    fileprivate var cachedAttributes: [NSAttributedString.Key : Any] = [:]
#if targetEnvironment(simulator)
    fileprivate var originNode: SCNNode?
    fileprivate var borderNode: SCNNode?
#endif

    @objc override init() {
        super.init()
        setupNode()
    }

    @objc required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupNode()
    }

    fileprivate func setupNode() {
        labelGeometry = SCNText(string: "", extrusionDepth: 0)
        labelGeometry.flatness = 0.5
        labelGeometry.firstMaterial?.lightingModel = .constant
        labelGeometry.firstMaterial?.diffuse.contents = UIColor.white
        labelGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        labelNode = SCNNode(geometry: labelGeometry)
        addChildNode(labelNode)
        reload()
    }

    func reload() {
        guard reloadNeeded else { return }
        reloadNeeded = false

        updateLabelContents()
#if targetEnvironment(simulator)
        updateDebugLayout()
#endif
    }

    fileprivate func getTextScale() -> CGFloat {
        return getTextSize() / LabelNode.geometryFixedTextSizeInMeters
    }

    fileprivate func getString(_ attributes: [NSAttributedString.Key: Any]) -> Any? {
        guard let text = text else { return nil }
        let string: String = allCaps ? text.uppercased() : text
        return NSAttributedString(string: string, attributes: attributes)
    }

    fileprivate func createStringAttributes(font: UIFont, scale: CGFloat) -> [NSAttributedString.Key: Any] {
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = textAlignment.nsTextAlignment
        paragraphStyle.lineBreakMode = multiline ? .byWordWrapping : .byClipping
        paragraphStyle.lineHeightMultiple = lineSpacing

        let attributes: [NSAttributedString.Key: Any] = [
            NSAttributedString.Key.kern: charSpacing * textSize / scale,
            NSAttributedString.Key.font: font,
            NSAttributedString.Key.paragraphStyle: paragraphStyle,
        ]

        return attributes
    }

    fileprivate func updateLabelContents() {
        let scale = getTextScale()
        let font = UIFont.font(with: fontStyle, weight: fontWeight, size: LabelNode.geometryFixedTextSizeInMeters)
        cachedAttributes = createStringAttributes(font: font, scale: scale)
        let size = (getSize() - getPaddingSize()) / scale
        labelGeometry.containerFrame = CGRect(origin: CGPoint.zero, size: size)
        labelGeometry.string = getString(cachedAttributes)
        labelGeometry.firstMaterial?.diffuse.contents = textColor
        labelNode.scale = SCNVector3(scale, scale, scale)

        updateTextNodePosition()
    }

    fileprivate func getTextSize() -> CGFloat {
        let platformScaleFactor: CGFloat = 0.8
        return platformScaleFactor * ((textSize > 0) ? textSize : defaultTextSize)
    }

    func getSize() -> CGSize {
        guard let text = text, !text.isEmpty else { return boundsSize }

        if boundsSize.width > 0 && boundsSize.height > 0 {
            return boundsSize
        }

        let preferredSizeInPixels = getPreferredSizeInPixels(text, attributes: cachedAttributes)
        let width: CGFloat = (boundsSize.width > 0) ? boundsSize.width : preferredSizeInPixels.width
        let height: CGFloat = (boundsSize.height > 0) ? boundsSize.height : preferredSizeInPixels.height
        return CGSize(width: width, height: height)
    }

    fileprivate func getPaddingSize() -> CGSize {
        return CGSize(width: textPadding.left + textPadding.right, height: textPadding.top + textPadding.bottom)
    }

    fileprivate func getPreferredSizeInPixels(_ text: String, attributes: [NSAttributedString.Key : Any]? = nil) -> CGSize {
        let scale = getTextScale()
        let padding: CGSize = getPaddingSize()
        let size: CGSize
        if boundsSize.width > 0 && multiline {
            let constraintSize = CGSize(width: (boundsSize.width - padding.width) / scale, height: .infinity)
            let boundingBox: CGRect = text.boundingRect(with: constraintSize, options: [.usesFontLeading, .usesLineFragmentOrigin], attributes: attributes, context: nil)
            size = boundingBox.size
        } else {
            size = text.size(withAttributes: attributes)
        }

        return CGSize(width: ceil(size.width) * scale, height: ceil(size.height) * scale) + padding
    }

    fileprivate func updateTextNodePosition() {
        let offset = -0.5 * getSize() + CGSize(width: textPadding.left, height: textPadding.top)
        labelNode.position = SCNVector3(offset.width, offset.height, 0)
    }
}

// MARK: - Debug mode
extension LabelNode {
    @objc func setDebugMode(_ debug: Bool) {
#if targetEnvironment(simulator)
        guard debug else {
            originNode?.removeFromParentNode()
            borderNode?.removeFromParentNode()
            return
        }

        // origin
        if originNode == nil {
            let sphere = SCNSphere(radius: 0.008)
            sphere.segmentCount = 4
            sphere.firstMaterial?.lightingModel = .constant
            sphere.firstMaterial?.diffuse.contents = UIColor.white
            originNode = SCNNode(geometry: sphere)
        }
        addChildNode(originNode!)

        updateDebugLayout()
#endif
    }

#if targetEnvironment(simulator)
    @objc fileprivate var isDebugMode: Bool { return originNode?.parent != nil }
    @objc fileprivate func updateDebugLayout() {
        guard isDebugMode else { return }

        // border
        let size = getSize()
        borderNode?.removeFromParentNode()
        borderNode = NodesFactory.createOutlineNode(size: size, cornerRadius: 0, thickness: 0, color: UIColor.white)
        insertChildNode(borderNode!, at: 0)
    }
#endif
}
