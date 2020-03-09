/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.prism

import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.core.Session
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.SessionReadyListener
import com.magicleap.magicscript.utils.logMessage

class SimpleAnchorCreator() : AnchorCreator, SessionReadyListener {

    private var session: Session? = null

    override fun onSessionLoaded(session: Session) {
        this.session = session
    }

    override fun createAnchor(
        position: Vector3,
        rotation: Quaternion,
        result: (anchor: Anchor) -> Unit
    ) {
        val positionArray = floatArrayOf(position.x, position.y, position.z)
        val rotationArray = floatArrayOf(rotation.x, rotation.y, rotation.z, rotation.w)
        val pose = Pose(positionArray, rotationArray)

        val session = this.session
        if (session == null) {
            logMessage("Cannot create anchor, session is not set up", warn = true)
        } else {
            val anchor = session.createAnchor(pose)
            result(anchor)
        }
    }

}