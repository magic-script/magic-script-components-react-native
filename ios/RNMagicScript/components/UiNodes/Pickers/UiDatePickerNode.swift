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

@objc open class UiDatePickerNode: UiNode {
    static fileprivate let defaultTextSize: CGFloat = 0.0167
    static let defaultInputDateFormat = "MM/dd/yyyy"
    static let defaultInputDateRegex = "^(((0)[0-9])|((1)[0-2]))(\\/)([0-2][0-9]|(3)[0-1])(\\/)\\d{4}"

    @objc var label: String? {
        didSet {
            labelNode.text = label
            setNeedsLayout()
        }
    }

    @objc var labelSide: Side = .top

    fileprivate let dateFormats: [String] = ["MM/dd/yyyy",
                                             "dd/MM/yyyy",
                                             "DD/yyyy",
                                             "MM/yyyy"]
    fileprivate var _dateFormat: String = UiDatePickerNode.defaultInputDateFormat
    @objc var dateFormat: String {
        set {
            let normalizedDateFormats = dateFormats.map { $0.lowercased() }
            if normalizedDateFormats.contains(newValue.lowercased()) {
                _dateFormat = dateFormats[normalizedDateFormats.firstIndex(of: newValue.lowercased())!]; setNeedsLayout()
            }
        }
        get { return _dateFormat}
    }

    fileprivate var _defaultDate: Date = Date()
    fileprivate var _defaultDateString: String = Date().toString(format: UiDatePickerNode.defaultInputDateFormat)
    // accept string with format MM/DD/YYYY
    @objc var defaultDate: String {
        set {
            if newValue ~= UiDatePickerNode.defaultInputDateRegex {
                _defaultDate = Date.from(string: newValue, format: UiDatePickerNode.defaultInputDateFormat)
                _defaultDateString = _defaultDate.toString(format: dateFormat)
                valueNode.text = _defaultDateString
            }
        }
        get { return _defaultDateString }
    }

    @objc var yearMin: Int = -1

    @objc var yearMax: Int = -1

    fileprivate var labelNode: LabelNode!
    fileprivate var valueNode: LabelNode!

    fileprivate var underlineGeometry: SCNPlane!
    fileprivate var underlineNode: SCNNode!

    @objc override func setupNode() {
        super.setupNode()

        assert(labelNode == nil, "Node must not be initialized!")
        assert(valueNode == nil, "Node must not be initialized!")
        labelNode = LabelNode()
        labelNode.defaultTextSize = UiDatePickerNode.defaultTextSize
        labelNode.textColor = UIColor(white: 1.0, alpha: 0.75)
        labelNode.text = label
        labelNode.position = SCNVector3(0.0, labelNode.getSize().height + 0.025, 0.0)
        contentNode.addChildNode(labelNode)

        valueNode = LabelNode()
        valueNode.textAlignment = .center
        valueNode.defaultTextSize = UiDatePickerNode.defaultTextSize
        valueNode.textColor = UIColor(white: 1.0, alpha: 0.75)
        valueNode.text = defaultDate
        valueNode.position = SCNVector3((valueNode.getSize().width * 0.5 - labelNode.getSize().width * 0.5), 0.0, 0.0)
        contentNode.addChildNode(valueNode)

        labelNode.reload()
        valueNode.reload()

        underlineGeometry = SCNPlane(width: valueNode.getSize().width, height: 0.0010)
        underlineGeometry.firstMaterial?.lightingModel = .constant
        underlineGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        underlineGeometry.firstMaterial?.diffuse.contents = UIColor(white: 1.0, alpha: 0.45)
        underlineNode = SCNNode(geometry: underlineGeometry)
        underlineNode.position = SCNVector3(x: valueNode.position.x, y: Float(-valueNode.getSize().height), z: 0.0)

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

        if let dateFormat = Convert.toString(props["dateFormat"]) {
            self.dateFormat = dateFormat
        }

        if let defaultDate = Convert.toString(props["defaultDate"]) {
            self.defaultDate = defaultDate
        }

        if let yearMin = Convert.toInt(props["yearMin"]) {
            self.yearMin = yearMin
        }

        if let yearMax = Convert.toInt(props["yearMax"]) {
            self.yearMax = yearMax
        }
    }

    @objc override func updateLayout() {
        valueNode.position = SCNVector3((valueNode.getSize().width * 0.5 - labelNode.getSize().width * 0.5), 0.0, 0.0)

        underlineGeometry = SCNPlane(width: valueNode.getSize().width, height: 0.0010)
        underlineGeometry.firstMaterial?.lightingModel = .constant
        underlineGeometry.firstMaterial?.isDoubleSided = NodeConfiguration.isDoubleSided
        underlineGeometry.firstMaterial?.diffuse.contents = UIColor(white: 1.0, alpha: 0.45)
        underlineNode.geometry = underlineGeometry
        underlineNode.position = SCNVector3(x: valueNode.position.x, y: Float(-valueNode.getSize().height), z: 0.0)

        labelNode.reload()
        valueNode.reload()
    }
}
