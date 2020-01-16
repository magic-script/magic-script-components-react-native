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

@objc open class UiScrollBarNode: UiNode {

    static let defaultLength: CGFloat = 0.2
    static let defaultThickness: CGFloat = 0.02

    @objc override var alignment: Alignment {
        get { return .centerCenter }
        set { }
    }

    // The length of the scrollbar. This is a little bit longer than the scroll range
    // (which is the range the thumb will run).
    @objc var length: CGFloat = 0.0 {
        didSet { setNeedsLayout() }
    }

    // This is how wide the scrollbar will be. A user should normally not care about this
    // unless they intend to change the skin of an app.
    @objc var thickness: CGFloat = 0.0 {
        didSet { setNeedsLayout() }
    }

    // Gets an indication between 0 and 1 for the size of the thumb in respect to the track.
    // It returns 1 to mean the thumb is as long as its track.
    // It returns a value of .5f to mean the thumb is half as long as its track.
    @objc var thumbSize: CGFloat {
        get { return _thumbSize }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, 0.1, 1.0)
            if (_thumbSize != clampedValue) { _thumbSize = clampedValue; setNeedsLayout() }
        }
    }

    // Gets an indication between 0 and 1 for the position of the thumb along the track.
    // It returns 0 to mean the thumb is at the left/upper most possible position along the track.
    // It returns 1 to mean the thumb is at the right/bottom most possible position along the track.
    @objc var thumbPosition: CGFloat {
        get { return _thumbPosition }
        set {
            let clampedValue: CGFloat = Math.clamp(newValue, 0.0, 1.0)
            if (_thumbPosition != clampedValue) { _thumbPosition = clampedValue; setNeedsLayout() }
        }
    }

    @objc var scrollOrientation: Orientation = .vertical {
        didSet { setNeedsLayout() }
    }

    fileprivate var _thumbSize: CGFloat = 0.1
    fileprivate var _thumbPosition: CGFloat = 0
    fileprivate var _localScrollBarSize: CGSize = CGSize.zero
    fileprivate var backgroundNode: SCNNode!
    fileprivate var thumbNode: SCNNode!

    @objc override func setupNode() {
        super.setupNode()

        assert(backgroundNode == nil, "Node must not be initialized!")

        let backgroundGeometry = SCNPlane(width: length, height: thickness)
        backgroundGeometry.firstMaterial?.lightingModel = .constant
        backgroundGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        backgroundGeometry.firstMaterial?.diffuse.contents = UIColor.lightGray
        backgroundNode = SCNNode(geometry: backgroundGeometry)
        contentNode.addChildNode(backgroundNode)

        let thumbGeometry = SCNPlane(width: thumbSize * length, height: thickness)
        thumbGeometry.firstMaterial?.lightingModel = .constant
        thumbGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        thumbGeometry.firstMaterial?.diffuse.contents = UIColor.white
        thumbGeometry.firstMaterial?.readsFromDepthBuffer = false
        thumbNode = SCNNode(geometry: thumbGeometry)
        thumbNode.renderingOrder = 1
        backgroundNode.addChildNode(thumbNode)
        setNeedsLayout()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let length = Convert.toCGFloat(props["length"]) {
            self.length = length
        }

        if let thickness = Convert.toCGFloat(props["thickness"]) {
            self.thickness = thickness
        }

        if let thumbSize = Convert.toCGFloat(props["thumbSize"]) {
            self.thumbSize = thumbSize
        }

        if let thumbPosition = Convert.toCGFloat(props["thumbPosition"]) {
            self.thumbPosition = thumbPosition
        }

        if let scrollOrientation = Convert.toOrientation(props["orientation"]) {
            self.scrollOrientation = scrollOrientation
        }
    }

    @objc override func _calculateSize() -> CGSize {
        let barLength: CGFloat = (length > 0.0001) ? length : UiScrollBarNode.defaultLength
        let barThickness: CGFloat = (thickness > 0.0001) ? thickness : UiScrollBarNode.defaultThickness
        _localScrollBarSize = CGSize(width: barLength, height: barThickness)
        if scrollOrientation == .horizontal {
            return _localScrollBarSize
        }

        return CGSize(width: barThickness, height: barLength)
    }

    @objc override func updateLayout() {
        _ = getSize() // this forces _calculateSize() function to be called
        let size = _localScrollBarSize

        if let backgroundGeometry = backgroundNode.geometry as? SCNPlane {
            backgroundGeometry.width = size.width
            backgroundGeometry.height = size.height
            backgroundGeometry.cornerRadius = 0.5 * size.height
        }

        let barWidth = Swift.max(size.height, size.width * thumbSize)
        if let thumbGeometry = thumbNode.geometry as? SCNPlane {
            thumbGeometry.width = barWidth
            thumbGeometry.height = size.height
            thumbGeometry.cornerRadius = 0.5 * size.height
        }

        thumbNode.pivot = SCNMatrix4MakeTranslation(-0.5 * Float(barWidth), 0.0, 0.0)
        let thumbX = -0.5 * size.width + thumbPosition * (size.width - barWidth)
        thumbNode.position = SCNVector3(thumbX, 0.0, 0.0)

        let angle: Float = (scrollOrientation == Orientation.vertical) ? -0.5 * Float.pi : 0
        backgroundNode.transform = SCNMatrix4MakeRotation(angle, 0, 0, 1)
    }

    func setVisible(_ visible: Bool, animated: Bool = false, delay: Double = 0, onComplete: (() -> Void)? = nil) {
        var actions: [SCNAction] = []
        if (delay > 0) {
            actions.append(SCNAction.wait(duration: delay))
        }
        let duration: TimeInterval = animated ? 0.3 : 0
        let opacityAction = visible ? SCNAction.fadeIn(duration: duration) : SCNAction.fadeOut(duration: duration)
        let visibleAction = visible ? SCNAction.unhide() : SCNAction.hide()
        actions.append(contentsOf: [SCNAction.unhide(), opacityAction, visibleAction])
        let mainAction = SCNAction.sequence(actions)

        DispatchQueue.main.async { [weak self] in
            self?.removeAllActions()
            self?.runAction(mainAction, completionHandler: onComplete)
        }
    }
}
