//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import UIKit

/// The TestsAppDelegate used when the app is run in the test environment
/// This AppDelegate usage is determined in the main.swift file in the main app target
class TestsAppDelegate: NSObject {

    var window: UIWindow?
    /// This constructor is called at the start of the tests and is a good point for test wide customization
    override init() {
        super.init()
        // Customize any TestsAppDelegate logic here

        window = UIWindow()
        window?.makeKeyAndVisible()
        window?.rootViewController = UIViewController()
    }
}
