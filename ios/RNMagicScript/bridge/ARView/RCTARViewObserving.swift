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

import ARKit
import SceneKit

//sourcery: AutoMockable
//sourcery: ObjcProtocol
@objc protocol RCTARViewObserving: NSObjectProtocol {

    //MARK: ARSCNViewDelegate
    @objc optional func renderer(_ renderer: SCNSceneRenderer, didAdd node: SCNNode, for anchor: ARAnchor)
    @objc optional func renderer(_ renderer: SCNSceneRenderer, willUpdate node: SCNNode, for anchor: ARAnchor)
    @objc optional func renderer(_ renderer: SCNSceneRenderer, didUpdate node: SCNNode, for anchor: ARAnchor)
    @objc optional func renderer(_ renderer: SCNSceneRenderer, didRemove node: SCNNode, for anchor: ARAnchor)

    //MARK: ARSessionObserver
    @objc optional func session(_ session: ARSession, didFailWithError error: Error)
    @objc optional func session(_ session: ARSession, cameraDidChangeTrackingState camera: ARCamera)
    @objc optional func sessionWasInterrupted(_ session: ARSession)
    @objc optional func sessionInterruptionEnded(_ session: ARSession)
    @objc optional func session(_ session: ARSession, didOutputAudioSampleBuffer audioSampleBuffer: CMSampleBuffer)

//    @objc @available(iOS 13.0, *)
//    optional func session(_ session: ARSession, didOutputCollaborationData data: ARSession.CollaborationData)

    //MARK: SCNSceneRendererDelegate
    @objc optional func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval)
    @objc optional func renderer(_ renderer: SCNSceneRenderer, didApplyAnimationsAtTime time: TimeInterval)
    @objc optional func renderer(_ renderer: SCNSceneRenderer, didSimulatePhysicsAtTime time: TimeInterval)

//    @objc @available(iOS 11.0, *)
//    optional func renderer(_ renderer: SCNSceneRenderer, didApplyConstraintsAtTime time: TimeInterval)
    @objc optional func renderer(_ renderer: SCNSceneRenderer, willRenderScene scene: SCNScene, atTime time: TimeInterval)
    @objc optional func renderer(_ renderer: SCNSceneRenderer, didRenderScene scene: SCNScene, atTime time: TimeInterval)
}
