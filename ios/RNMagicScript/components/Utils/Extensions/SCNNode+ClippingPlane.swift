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

import SceneKit

extension SCNNode {
    static fileprivate var clippingPlanesShaderModifiers: [SCNShaderModifierEntryPoint : String]? = {
        guard let bundle = Bundle.resourcesBundle else { return nil }
        
        let geometryURL = bundle.url(forResource: "ClippingPlane.geometry", withExtension: "txt")!
        let geometryModifier: String = try! String(contentsOf: geometryURL)

        let fragmentURL = bundle.url(forResource: "ClippingPlane.fragment", withExtension: "txt")!
        let fragmentModifier: String = try! String(contentsOf: fragmentURL)

        let modifiers = [
            SCNShaderModifierEntryPoint.geometry: geometryModifier,
            SCNShaderModifierEntryPoint.fragment: fragmentModifier
        ]
        return modifiers
    }()
    
    func applyClippingPlanesShaderModifiers(recursive: Bool = false) {
        guard let modifiers = SCNNode.clippingPlanesShaderModifiers else { return }
            
        if recursive {
            enumerateHierarchy { (node, result) in
                guard let nodeGeometry = node.geometry else { return }
                if nodeGeometry.shaderModifiers == nil {
                    nodeGeometry.shaderModifiers = modifiers
                }
            }
        } else {
            guard let nodeGeometry = geometry else { return }
            if nodeGeometry.shaderModifiers == nil {
                nodeGeometry.shaderModifiers = modifiers
            }
        }
    }
    
    func forceUpdateClipping(recursive: Bool = false) {
        if let boundsClipper = getBoundsClipper() {
            boundsClipper.updateClipping(for: self, recursive: recursive)
        }
    }
    
    func invalidateBoundsClipper() {
        if let boundsClipper = getBoundsClipper() {
            boundsClipper.invalidateClipping()
        }
    }
    
    fileprivate func getBoundsClipper() -> BoundsClipping? {
        var node: SCNNode? = parent
        while (node != nil) {
            if let boundsClippingNode = node as? BoundsClipping {
                return boundsClippingNode
            }
            node = node?.parent
        }
        
        return nil
    }
    
    func setClippingPlanes(_ planes: [SCNVector4], recursive: Bool = false) {
        guard let _ = SCNNode.clippingPlanesShaderModifiers else { return }
        if recursive {
            enumerateHierarchy { (node, result) in applyClippingPlanes(planes, for: node) }
        } else {
            applyClippingPlanes(planes, for: self)
        }
    }

    fileprivate func applyClippingPlanes(_ planes: [SCNVector4], for node: SCNNode) {
        guard let nodeGeometry = node.geometry else { return }
        let maxPlanesSupported: Int = 6
        let validPlanesCount: Int = min(planes.count, maxPlanesSupported)
        for i in 0..<validPlanesCount {
            let value = NSValue(scnVector4: planes[i])
            nodeGeometry.setValue(value, forKey: "clippingPlane\(i + 1)")
        }
        
        for i in validPlanesCount..<maxPlanesSupported {
            let value = NSValue(scnVector4: SCNVector4.zero)
            nodeGeometry.setValue(value, forKey: "clippingPlane\(i + 1)")
        }
    }
}
