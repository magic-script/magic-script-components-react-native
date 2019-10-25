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
    static let defaultInputDateFormat = "MM/dd/YYYY"
    static let defaultInputDateRegex = "^(((0)[0-9])|((1)[0-2]))(\\/)([0-2][0-9]|(3)[0-1])(\\/)\\d{4}"

    @objc var label: String?

    @objc var labelSide: Side = .top

    fileprivate let dateFormats: [String] = ["MM/dd/YYYY",
                                             "dd/MM/YYYY",
                                             "DD/YYYY",
                                             "MM/YYYY"]
    fileprivate var _dateFormat: String = UiDatePickerNode.defaultInputDateFormat
    @objc var dateFormat: String {
        set {
            let normalizedDateFormats = dateFormats.map { $0.lowercased() }
            if normalizedDateFormats.contains(newValue.lowercased()) {
                _dateFormat = newValue; setNeedsLayout()
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
            _defaultDateString = newValue
            }
        }
        get { return _defaultDateString }
    }

    @objc var yearMin: Int = -1

    @objc var yearMax: Int = -1

    fileprivate var labelNode: LabelNode!
    fileprivate var valueNode: LabelNode!

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
            self.yearMin = yearMax
        }
    }

}
