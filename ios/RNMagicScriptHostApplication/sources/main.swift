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
import UIKit

/// Checks if the app is being opened by the test target
///
/// - Returns: True if the app is opened by the test target
private func isRunningTests() -> Bool {
    return NSClassFromString("XCTestCase") != nil
}

/// Gets the right AppDelegate class for the current environment.
/// The real AppDelegate should not be used in testing since it may have side effects.
/// Side effects include making api calls, registering for notifications, setting core data, setting UI etc.
///
/// - Returns: TestsAppDelegate if the app was opened by the test target. Normal AppDelegate otherwise.
private func getDelegateClassName() -> String {
    return isRunningTests() ? NSStringFromClass(TestsAppDelegate.self) : NSStringFromClass(AppDelegate.self)
}

/// Load the actual app with the right app delegate depending on environment
/// Based on https://marcosantadev.com/fake-appdelegate-unit-testing-swift/
let args = UnsafeMutableRawPointer(CommandLine.unsafeArgv).bindMemory(to: UnsafeMutablePointer<Int8>.self,
                                                                      capacity: Int(CommandLine.argc))
_ = UIApplicationMain(CommandLine.argc, CommandLine.unsafeArgv, nil, getDelegateClassName())

