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

import Foundation

//sourcery: AutoMockable
protocol DateTimeConverting {
    static func from(string: String, format: String) -> Date
    func toString(format: String) -> String
}

extension Date: DateTimeConverting {
    static func from(string: String, format: String) -> Date {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = TimeZone.current
        dateFormatter.locale = Locale.current
        return dateFormatter.date(from: string) ?? Date()
    }

    func toString(format: String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = TimeZone.current
        dateFormatter.locale = Locale.current
        return dateFormatter.string(from: self)
    }
}
