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
    func applyClippingPlanesShaderModifiers(recursive: Bool = false) {
        guard let modifiers = SCNNode.clippingPlanesShaderModifiers else { return }
        invokeClosure(recursive: recursive, input: modifiers) { (node, input) -> ([SCNShaderModifierEntryPoint : String]) in
            guard let nodeGeometry = node.geometry else { return modifiers }
            if nodeGeometry.shaderModifiers == nil {
                nodeGeometry.shaderModifiers = input
            } else {
                nodeGeometry.shaderModifiers![.fragment] = input[.fragment]
                nodeGeometry.shaderModifiers![.geometry] = input[.geometry]
            }
            return modifiers
        }
    }
    
    func resetClippingPlanesShaderModifiers(recursive: Bool = false) {
        guard let modifiers = SCNNode.clippingPlanesShaderModifiers else { return }
        invokeClosure(recursive: recursive, input: modifiers) { (node, input) -> ([SCNShaderModifierEntryPoint : String]) in
            guard let nodeGeometry = node.geometry else { return modifiers }
            if nodeGeometry.shaderModifiers != nil {
                nodeGeometry.shaderModifiers!.removeValue(forKey: .fragment)
                nodeGeometry.shaderModifiers!.removeValue(forKey: .geometry)
                if nodeGeometry.shaderModifiers!.isEmpty {
                   nodeGeometry.shaderModifiers = nil
                }
            }
            return modifiers
        }
    }

    func setClippingPlanes(_ planes: [SCNVector4], recursive: Bool = false) {
        guard let _ = SCNNode.clippingPlanesShaderModifiers else { return }
        invokeClosure(recursive: recursive, input: planes) { node, input -> ([SCNVector4]) in
            let clippingPlanes: [SCNVector4]
            if let boundsClipper = node as? BoundsClipping {
                let morePlanes = boundsClipper.getClippingPlanesAsVector4()
                clippingPlanes = morePlanes.count > 0 ? SCNNode.mergeClippingPlanes(input, morePlanes) : input
            } else {
                clippingPlanes = input
            }
            node.applyClippingPlanes(clippingPlanes)
            return clippingPlanes
        }
    }
    
    func invalidateBoundsClippingManager() {
        if let boundsClippingManager = getBoundsClippingManager() {
            boundsClippingManager.invalidateClipping()
        }
    }
    
    func forceUpdateClipping(recursive: Bool = false) {
        if let boundsClipper = getBoundsClippingManager() {
            boundsClipper.updateClipping(for: self, recursive: recursive)
        }
    }
}

// MARK: - Clipping (private)
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
    
    static fileprivate func mergeClippingPlanes(_ planes1: [SCNVector4], _ planes2: [SCNVector4]) -> [SCNVector4] {
        assert(planes1.count == planes2.count, "Both planes containers must have the same number of elements!")
        var result: [Plane] = []
        let tmpPlanes1 = planes1.map { Plane(vector: $0) }
        let tmpPlanes2 = planes2.map { Plane(vector: $0) }
        for i in 0..<tmpPlanes1.count {
            let pl1 = tmpPlanes1[i]
            let pl2 = tmpPlanes2[i]
            assert(pl1.normal.dot(pl2.normal) > 0.95, "Normal vectors must be the same.")
            if pl1.distanceToPoint(SCNVector3.zero) > pl2.distanceToPoint(SCNVector3.zero) {
                result.append(pl2)
            } else {
                result.append(pl1)
            }
        }
        return result.map { $0.toVector4() }
    }
    
    fileprivate func getBoundsClippingManager() -> BoundsClippingManaging? {
        var node: SCNNode? = parent
        while (node != nil) {
            if let boundsClippingManager = node as? BoundsClippingManaging {
                return boundsClippingManager
            }
            node = node?.parent
        }
        
        return nil
    }
    
    fileprivate func applyClippingPlanes(_ planes: [SCNVector4]) {
        guard let nodeGeometry = geometry else { return }
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
    
    fileprivate func invokeClosure<T>(recursive: Bool, input: T, closure: @escaping (SCNNode, T) -> (T)) {
    #if targetEnvironment(simulator)
        // Do not clip model nodes on simulator. There is a limitation that causes that models
        // are not rendered when are clipped.
        // CompilerError: "only 14 constant buffers binding are supported in the simulator but 20 were used"
        if self is UiModelNode { return }
    #endif
        if recursive {
            let output = closure(self, input)
            childNodes.forEach { $0.invokeClosure(recursive: recursive, input: output, closure: closure) }
        } else {
            let _ = closure(self, input)
        }
    }
}
