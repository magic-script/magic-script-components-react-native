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

import Quick
import Nimble
import SceneKit
@testable import RNMagicScriptHostApplication

class SCNNinePatchSpec: QuickSpec {
    override func spec() {
        describe("SCNNinePatch") {
            context("init") {
                it("should init geometry") {
                    let size = CGSize(width: 0.2, height: 0.1)
                    let geometryCaps = UIEdgeInsets(top: 0.1, left: 0.1, bottom: 0.1, right: 0.1)
                    let texCoordsCaps = UIEdgeInsets(top: 0.2, left: 0.2, bottom: 0.2, right: 0.2)
                    let geom = SCNNinePatch(size: size, geometryCaps: geometryCaps, texCoordsCaps: texCoordsCaps)

                    expect(geom.width).to(beCloseTo(size.width))
                    expect(geom.height).to(beCloseTo(size.height))

                    expect(geom.sources.count).to(equal(2))
                    expect(geom.sources[0].vectorCount).to(equal(16))
                    expect(geom.sources[0].semantic).to(equal(SCNGeometrySource.Semantic.vertex))
                    expect(geom.sources[1].semantic).to(equal(SCNGeometrySource.Semantic.texcoord))

                    expect(geom.elements.count).to(equal(1))
                    expect(geom.elements[0].primitiveType).to(equal(.triangles))
                    expect(geom.elements[0].primitiveCount).to(equal(18))
                }
            }
        }
    }
}
