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

@objc open class UiTimePickerNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.065
    static fileprivate let defaultLabelGap: CGFloat = 0.015
    static let defaultInputTimeFormat = "HH:mm:ss"
    static let defaultInputTimeRegex = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)"

    @objc var label: String? {
        didSet {
            labelNode.text = label
            setNeedsLayout()
        }
    }

    @objc var labelSide: Side = .top {
        didSet { setNeedsLayout() }
    }

    fileprivate var _time: String? = nil
    @objc var time: String {
        get {
            guard let time = _time else { return timeFormat.uppercased() }
            return Date.from(string: time, format: UiTimePickerNode.defaultInputTimeFormat).toTimeString(format: timeFormat)
        }
        set {
            _time = (newValue ~= UiTimePickerNode.defaultInputTimeRegex) ? newValue : nil
            valueNode.text = Date.fromTime(string: newValue, format: UiTimePickerNode.defaultInputTimeFormat).toTimeString(format: timeFormat)
            setNeedsLayout()
        }
    }

    @objc var color: UIColor = UIColor(white: 0.75, alpha: 1.0) {
        didSet { valueNode.textColor = color }
    }

    fileprivate let timeFormats: [String] = ["HH:mm:ss p",
                                             "HH:mm p",
                                             "HH:ss p",
                                             "HH:mm:ss",
                                             "HH:mm",
                                             "HH:ss",
                                             "mm:ss"]
    fileprivate var _timeFormat: String = UiTimePickerNode.defaultInputTimeFormat
    @objc var timeFormat: String {
        get { return _timeFormat }
        set {
            let normalizedDateFormats = timeFormats.map { $0.lowercased() }
            if normalizedDateFormats.contains(newValue.lowercased()) {
                _timeFormat = timeFormats[normalizedDateFormats.firstIndex(of: newValue.lowercased())!]
                valueNode.text = time // update displayed value
                setNeedsLayout()
            }
        }
    }

    fileprivate var _defaultTime: Date = Date()
    fileprivate var _defaultTimeString: String = Date().toTimeString(format: UiTimePickerNode.defaultInputTimeFormat)
    // accept string with format hh:mm:ss
    @objc var defaultTime: String {
        get { return _defaultTimeString }
        set {
            if newValue ~= UiTimePickerNode.defaultInputTimeRegex {
                _defaultTime = Date.fromTime(string: newValue, format: UiTimePickerNode.defaultInputTimeFormat)
                _defaultTimeString = _defaultTime.toTimeString(format: timeFormat)
                setNeedsLayout()
            }
        }
    }

    @objc public var onTimeChanged: ((_ sender: UiTimePickerNode, _ selected: String) -> (Void))?
    @objc public var onTimeConfirmed: ((_ sender: UiTimePickerNode, _ confirmed: String) -> (Void))?

    fileprivate var labelNode: LabelNode!
    fileprivate(set) var valueNode: LabelNode!
    fileprivate var underlineNode: SCNNode!

    fileprivate var isActive: Bool = false {
        didSet {
            valueNode.textColor = isActive ? UIColor(white: 1.0, alpha: 1.0) : UIColor(white: 1.0, alpha: 0.75)
        }
    }

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "Node must not be initialized!")
        assert(valueNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.defaultTextSize = UiTimePickerNode.defaultTextSize
        labelNode.textColor = UIColor(white: 1.0, alpha: 0.75)
        labelNode.text = label

        contentNode.addChildNode(labelNode)

        valueNode = LabelNode()
        valueNode.textAlignment = .center
        valueNode.defaultTextSize = UiTimePickerNode.defaultTextSize
        valueNode.textColor = isActive ? UIColor(white: 1.0, alpha: 1.0) : UIColor(white: 1.0, alpha: 0.75)
        valueNode.text = time

        contentNode.addChildNode(valueNode)

        labelNode.reload()
        valueNode.reload()

        let underlineGeometry = SCNPlane(width: 0, height: 0.001)
        underlineGeometry.firstMaterial?.lightingModel = .constant
        underlineGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        underlineGeometry.firstMaterial?.diffuse.contents = UIColor(white: 1.0, alpha: 0.45)

        underlineNode = SCNNode(geometry: underlineGeometry)

        contentNode.addChildNode(underlineNode)

        setNeedsLayout()
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let label = Convert.toString(props["label"]) {
            self.label = label
        }

        if let labelSide = Convert.toSide(props["labelSide"]) {
            self.labelSide = labelSide
        }

        if let time = Convert.toString(props["time"]) {
            self.time = time
        }

        if let color = Convert.toColor(props["color"]) {
            self.color = color
        }

        if let timeFormat = Convert.toString(props["timeFormat"]) {
            self.timeFormat = timeFormat
        }

        if let defaultTime = Convert.toString(props["defaultTime"]) {
            self.defaultTime = defaultTime
        }
    }

    @objc override func updateLayout() {
        let labelNodeSize = labelNode.getSize()
        let valueNodeSize = valueNode.getSize()
        let size = getSize()
        let gap = UiTimePickerNode.defaultLabelGap

        if labelSide == Side.top {
            labelNode.position = SCNVector3(-0.5 * (size.width - labelNodeSize.width), 0.5 * (size.height - labelNodeSize.height), 0.0)
            valueNode.position = SCNVector3(-0.5 * (size.width - valueNodeSize.width), -0.5 * size.height + 0.5 * valueNodeSize.height + gap, 0.0)
        } else {
            labelNode.position = SCNVector3(-0.5 * (size.width - labelNodeSize.width), 0.0, 0.0)
            valueNode.position = SCNVector3(0.5 * (size.width - valueNodeSize.width), 0.0, 0.0)
        }

        if let underlineGeometry = underlineNode.geometry as? SCNPlane {
            underlineGeometry.width = valueNode.getSize().width
        }
        underlineNode.position = SCNVector3(CGFloat(valueNode.position.x), -0.5 * size.height, 0.0)

        labelNode.reload()
        valueNode.reload()
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
        labelNode.setDebugMode(debug)
        valueNode.setDebugMode(debug)
    }

    @objc override func _calculateSize() -> CGSize {
        let labelNodeSize = labelNode.getSize()
        let valueNodeSize = valueNode.getSize()
        let gap = UiTimePickerNode.defaultLabelGap
        var contentWidth: CGFloat = 0.0
        var contentHeight: CGFloat = 0.0
        if labelSide == Side.top {
            contentWidth = max(valueNodeSize.width, labelNodeSize.width)
            contentHeight = labelNodeSize.height + valueNodeSize.height + 2 * gap
        } else {
            contentWidth = valueNodeSize.width + labelNodeSize.width + gap
            contentHeight = max(labelNodeSize.height, valueNodeSize.height) + gap
        }
        return CGSize(width: contentWidth, height: contentHeight)
    }

    @objc override var canHaveFocus: Bool {
        return true
    }

    @objc override func enterFocus() {
        super.enterFocus()
        guard hasFocus else { return }
        isActive = hasFocus
    }

    @discardableResult
    @objc override func leaveFocus(onBehalfOf node: UiNode? = nil) -> Bool {
        let result = super.leaveFocus(onBehalfOf: node)
        isActive = hasFocus
        return result
    }
}

extension UiTimePickerNode: TimePickerDataProviding {
    var amPmFormat: Bool {
        get {
            return timeFormat.contains("p")
        }
    }

    var timePickerValue: Date {
        get {
            return _time != nil ? Date.fromTime(string: _time!, format: UiTimePickerNode.defaultInputTimeFormat) : Date()
        }
        set {
            // callback
            if timePickerValue != newValue {
                time = newValue.toTimeString(format: UiTimePickerNode.defaultInputTimeFormat)
                layoutIfNeeded()
            }
        }
    }

    func timeChanged() {
        onTimeChanged?(self, time)
    }

    func timeConfirmed() {
        onTimeConfirmed?(self, time)
    }
}
