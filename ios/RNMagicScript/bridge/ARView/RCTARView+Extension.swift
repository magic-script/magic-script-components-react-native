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

import UIKit
import ARKit
import SceneKit

extension RCTARView: ARSCNViewDelegate {
    //MARK: ARSCNViewDelegate - this is dataSource rather than delegate
    public func renderer(_ renderer: SCNSceneRenderer, nodeFor anchor: ARAnchor) -> SCNNode? {
        return SCNNode()
    }

    #warning("Should be updated according to expected behaviour")
    @available(iOS 11.3, *)
    public func sessionShouldAttemptRelocalization(_ session: ARSession) -> Bool {
        return true
    }

    //MARK: ARSCNViewDelegate
    public func renderer(_ renderer: SCNSceneRenderer, didAdd node: SCNNode, for anchor: ARAnchor) {
        for observer in observers {
            observer.value?.renderer?(renderer, didAdd: node, for: anchor)
        }
    }

    public func renderer(_ renderer: SCNSceneRenderer, willUpdate node: SCNNode, for anchor: ARAnchor) {
        for observer in observers {
            observer.value?.renderer?(renderer, willUpdate: node, for: anchor)
        }
    }

    public func renderer(_ renderer: SCNSceneRenderer, didUpdate node: SCNNode, for anchor: ARAnchor) {
        for observer in observers {
            observer.value?.renderer?(renderer, didUpdate: node, for: anchor)
        }
    }

    public func renderer(_ renderer: SCNSceneRenderer, didRemove node: SCNNode, for anchor: ARAnchor) {
        for observer in observers {
            observer.value?.renderer?(renderer, didRemove: node, for: anchor)
        }
    }

    //MARK: ARSessionObserver
    public func session(_ session: ARSession, didFailWithError error: Error) {
        for observer in observers {
            observer.value?.session?(session, didFailWithError: error)
        }
    }

    public func session(_ session: ARSession, cameraDidChangeTrackingState camera: ARCamera) {
        for observer in observers {
            observer.value?.session?(session, cameraDidChangeTrackingState: camera)
        }
    }

    public func sessionWasInterrupted(_ session: ARSession) {
        for observer in observers {
            observer.value?.sessionWasInterrupted?(session)
        }
    }

    public func sessionInterruptionEnded(_ session: ARSession) {
        for observer in observers {
            observer.value?.sessionInterruptionEnded?(session)
        }
    }


    public func session(_ session: ARSession, didOutputAudioSampleBuffer audioSampleBuffer: CMSampleBuffer) {
        for observer in observers {
            observer.value?.session?(session, didOutputAudioSampleBuffer: audioSampleBuffer)
        }
    }

    #warning("Should be updated according to expected behaviour")
    @available(iOS 13.0, *)
    public func session(_ session: ARSession, didOutputCollaborationData data: ARSession.CollaborationData) {
//        for observer in observers {
//            observer.value?.session?(session, didOutputCollaborationData: data)
//        }
    }

    //MARK: SCNSceneRendererDelegate
    public  func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        for observer in observers {
            observer.value?.renderer?(renderer, updateAtTime: time)
        }
    }

    public  func renderer(_ renderer: SCNSceneRenderer, didApplyAnimationsAtTime time: TimeInterval) {
        for observer in observers {
            observer.value?.renderer?(renderer, didApplyAnimationsAtTime: time)
        }
    }

    public  func renderer(_ renderer: SCNSceneRenderer, didSimulatePhysicsAtTime time: TimeInterval) {
        for observer in observers {
            observer.value?.renderer?(renderer, didSimulatePhysicsAtTime: time)
        }
    }

    #warning("Should be updated according to expected behaviour")
    @available(iOS 11.0, *)
    public func renderer(_ renderer: SCNSceneRenderer, didApplyConstraintsAtTime time: TimeInterval) {
//        for observer in observers {
//            observer.value?.renderer?(renderer, didApplyConstraintsAtTime: time)
//        }
    }

    public func renderer(_ renderer: SCNSceneRenderer, willRenderScene scene: SCNScene, atTime time: TimeInterval) {
        for observer in observers {
            observer.value?.renderer?(renderer, willRenderScene: scene, atTime: time)
        }
    }

    public func renderer(_ renderer: SCNSceneRenderer, didRenderScene scene: SCNScene, atTime time: TimeInterval) {
        for observer in observers {
            observer.value?.renderer?(renderer, didRenderScene: scene, atTime: time)
        }
    }
}
