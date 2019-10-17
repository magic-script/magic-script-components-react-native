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

extension SCNNode {
    func setClippingPlanes(_ planes: [SCNVector4]) {
        guard !planes.isEmpty else {
            resetClippingPlanes()
            return
        }

        let geometryURL = Bundle.main.url(forResource: "ClippingPlane.geometry", withExtension: "txt")!
        let geometryModifier: String = try! String(contentsOf: geometryURL)

        let fragmentURL = Bundle.main.url(forResource: "ClippingPlane.fragment", withExtension: "txt")!
        let fragmentModifier: String = try! String(contentsOf: fragmentURL)

        let modifiers = [
            SCNShaderModifierEntryPoint.geometry: geometryModifier,
            SCNShaderModifierEntryPoint.fragment: fragmentModifier
        ]

        applyShanderModifiers(modifiers, planes: planes)
    }

    func resetClippingPlanes() {
        applyShanderModifiers(nil, planes: [])
    }

    fileprivate func applyShanderModifiers(_ modifiers: [SCNShaderModifierEntryPoint: String]?, planes: [SCNVector4]) {
        enumerateHierarchy { (node, result) in
            node.geometry?.shaderModifiers = modifiers
            for (index, plane) in planes.enumerated() {
                let value = NSValue(scnVector4: plane)
                node.geometry?.setValue(value, forKey: "clippingPlane\(index + 1)")
            }

        }
    }
}
