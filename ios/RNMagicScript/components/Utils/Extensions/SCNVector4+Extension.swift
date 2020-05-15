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

import Foundation
import SceneKit

extension SCNVector4 {
    init(_ v: SCNVector3) {
        self.init(x: v.x, y: v.y, z: v.z, w: 1)
    }
}

extension SCNVector4 {
    var toArrayOfFloat: [Float] {
        return [x, y, z, w]
    }
    var toArrayOfCGFloat: [CGFloat] {
        return [CGFloat(x), CGFloat(y), CGFloat(z), CGFloat(w)]
    }
    var toArrayOfDouble: [Double] {
        return [Double(x), Double(y), Double(z), Double(w)]
    }
    var toArrayOfInt: [Int] {
        return [Int(x), Int(y), Int(z), Int(w)]
    }
}

extension SCNVector4 {
    static var zero: SCNVector4 = SCNVector4Zero
}

public func SCNVector4NOTEqualToVector4(_ a: SCNVector4, _ b: SCNVector4) -> Bool {
    return !SCNVector4EqualToVector4(a, b)
}
