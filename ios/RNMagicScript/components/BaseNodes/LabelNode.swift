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

    @objc var text: String? {
        didSet { reloadNeeded = true }
    }
    @objc var textAlignment: HorizontalTextAlignment = .left {
        didSet { reloadNeeded = true }
    }
    @objc var textColor: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { reloadNeeded = true }
    }
    @objc var textSize: CGFloat = 0 {
        didSet { reloadNeeded = true }
    }
    @objc var defaultTextSize: CGFloat = LabelNode.defaultTextSizeInMeters {
        didSet { reloadNeeded = true }
    }
    @objc var boundsSize: CGSize = CGSize.zero {
        didSet { reloadNeeded = true }
    }
    @objc var wrap: Bool = false {
        didSet { reloadNeeded = true }
    }

    fileprivate var labelGeometry: SCNText!
    fileprivate var labelNode: SCNNode!
    fileprivate var reloadNeeded: Bool = false
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

    deinit {
        labelNode.geometry?.firstMaterial?.diffuse.contents = nil
    }

    fileprivate func setupNode() {
        labelGeometry = SCNText(string: "", extrusionDepth: 0)
        labelGeometry.font = UIFont.font(with: .normal, weight: .regular, size: LabelNode.geometryFixedTextSizeInMeters)
        labelGeometry.alignmentMode = CATextLayerAlignmentMode.left.rawValue
        labelGeometry.flatness = 0.5
        labelGeometry.firstMaterial?.lightingModel = .constant
        labelGeometry.firstMaterial?.diffuse.contents = UIColor.white
        labelGeometry.firstMaterial?.isDoubleSided = true
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

    fileprivate func updateLabelContents() {
        let scale = getTextSize() / LabelNode.geometryFixedTextSizeInMeters
        labelGeometry.string = text
        let size = getSize()
        let rect = CGRect(origin: CGPoint.zero, size: CGSize(width: size.width / scale, height: size.height / scale))
        labelGeometry.containerFrame = rect
        labelGeometry.firstMaterial?.diffuse.contents = textColor
        labelGeometry.isWrapped = wrap
        labelGeometry.alignmentMode = textAlignment.textLayerAlignmentMode.rawValue
        labelGeometry.truncationMode = CATextLayerTruncationMode.end.rawValue
        labelNode?.removeFromParentNode()
        labelNode = SCNNode(geometry: labelGeometry)
        labelNode.scale = SCNVector3(scale, scale, scale)
        addChildNode(labelNode)
        updateTextNodePosition()
    }

    fileprivate func getTextSize() -> CGFloat {
        let platformScaleFactor: CGFloat = 0.8
        return platformScaleFactor * ((textSize > 0) ? textSize : defaultTextSize)
    }

    fileprivate func getFont() -> UIFont {
        return labelGeometry.font
    }

    func getSize() -> CGSize {
        guard let text = text, !text.isEmpty else { return boundsSize }

        if boundsSize.width > 0 && boundsSize.height > 0 {
            return boundsSize
        }

        let scale: CGFloat = (getTextSize() / LabelNode.geometryFixedTextSizeInMeters)
        let preferredSizeInPixels = getPreferredSizeInPixels(text, attributes: [NSAttributedString.Key.font : getFont()])
        let width: CGFloat = (boundsSize.width > 0) ? boundsSize.width : (ceil(preferredSizeInPixels.width) * scale)
        let height: CGFloat = (boundsSize.height > 0) ? boundsSize.height : (ceil(preferredSizeInPixels.height) * scale)
        return CGSize(width: width, height: height)
    }

    fileprivate func getPreferredSizeInPixels(_ text: String, attributes: [NSAttributedString.Key : Any]? = nil) -> CGSize {
        if boundsSize.width > 0 && wrap {
            let constraintSize = CGSize(width: boundsSize.width, height: .greatestFiniteMagnitude)
            let boundingBox: CGRect = text.boundingRect(with: constraintSize, options: [.usesLineFragmentOrigin, .usesFontLeading], attributes: attributes, context: nil)
            return boundingBox.size
        } else {
            return text.size(withAttributes: attributes)
        }
    }

    fileprivate func updateTextNodePosition() {
        let size = getSize()
        labelNode.position = SCNVector3(-0.5 * size.width, -0.5 * size.height, 0)
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
