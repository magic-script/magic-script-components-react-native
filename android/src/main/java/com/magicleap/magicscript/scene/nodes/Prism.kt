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

package com.magicleap.magicscript.scene.nodes

import android.os.Bundle
import android.view.MotionEvent
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.CubeRenderableBuilder
import com.magicleap.magicscript.ar.RenderableResult
import com.magicleap.magicscript.scene.CameraObserver
import com.magicleap.magicscript.scene.ReactScene
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.logMessage
import com.magicleap.magicscript.utils.read
import kotlin.math.min
import kotlin.math.pow

class Prism(initProps: ReadableMap, private val cubeBuilder: CubeRenderableBuilder) :
    AnchorNode(),
    ReactNode,
    CameraObserver {

    companion object {
        const val PROP_SIZE = "size"
        const val PROP_POSITION = "position"
        const val PROP_VISIBLE = "visible"
    }

    var initialized = false
        private set

    var beingTouched: Boolean = false
        private set(value) {
            field = value
            if (value) {
                anchor = null // removing the anchor, so we can change local position
            } else {
                tryToAnchorAtPosition(localPosition)
            }
        }

    var visible: Boolean = false
        set(value) {
            field = value
            container?.renderable = if (value) {
                renderableCopy
            } else {
                null
            }

            container?.rotationController?.isEnabled = value
        }

    private var properties = Arguments.toBundle(initProps) ?: Bundle()
    private var container: RotatableNode? = null
    private var session: Session? = null
    private var content: TransformNode? = null
    private var lastCameraPosition: Vector3? = null
    private var lastCameraState: TrackingState? = null
    private var requestedAnchorPosition: Vector3? = null
    private var lastCreatedAnchor: Anchor? = null
    private var size: Vector3 = Vector3(2f, 2f, 0.3f)
    private var renderableCopy: Renderable? = null
    private var reactScene: ReactScene? = null

    override val reactParent: ReactNode? = reactScene

    override val reactChildren: List<ReactNode>
        get() = container?.children?.filterIsInstance<TransformNode>() ?: listOf()

    fun setScene(scene: ReactScene) {
        this.reactScene = scene
    }

    override fun update(props: ReadableMap) {
        val propsToUpdate = Arguments.toBundle(props) ?: Bundle()
        this.properties.putAll(propsToUpdate) // save new props
        applyProperties(propsToUpdate)
    }

    override fun addContent(child: ReactNode) {
        if (child !is TransformNode) {
            return
        }
        if (content != null) {
            logMessage("Prism content already added", true)
            return
        }
        content = child
        container?.let {
            it.setParent(this)
            it.addChild(child)
        }
        clipContent()
    }

    override fun removeContent(child: ReactNode) {
        if (child is TransformNode) {
            container?.removeChild(child)
        }
    }

    override fun build() {
        applyProperties(properties)
    }

    fun setArDependencies(
        transformationSystem: TransformationSystem,
        session: Session
    ) {
        if (initialized) {
            return
        }

        this.session = session

        container = RotatableNode(transformationSystem).also {
            it.setParent(this)
            if (content != null) {
                it.addChild(content)
            }
        }
        applyProperties(properties)
        buildCube(size)

        initialized = true
    }

    override fun onCameraUpdated(cameraPosition: Vector3, state: TrackingState) {
        lastCameraState = state

        if (state != TrackingState.TRACKING) {
            return
        }

        requestedAnchorPosition?.let {
            tryToAnchorAtPosition(it)
        }

        if (lastCameraPosition == null) {
            lastCameraPosition = cameraPosition
            return
        }

        if (beingTouched) {
            val camToPrismDiff = Vector3.subtract(localPosition, cameraPosition)
            val camToPrismDistance =
                camToPrismDiff.x.pow(2) + camToPrismDiff.y.pow(2) + camToPrismDiff.z.pow(2)

            // Limiting the movement speed since sometimes the [dist] is near
            // to infinity when moving device (ar core bug?)
            val speed = min(1 + camToPrismDistance, 2f).pow(2)

            val diff = Vector3.subtract(cameraPosition, lastCameraPosition).scaled(speed)
            localPosition = Vector3.add(localPosition, diff)
        }
        lastCameraPosition = cameraPosition
    }

    override fun onPause() {
        // no-op
    }

    override fun onResume() {
        // no-op
    }

    override fun onDestroy() {
        session = null
    }

    override fun setAnchor(anchor: Anchor?) {
        super.setAnchor(anchor)
        requestedAnchorPosition = null
    }

    private fun buildCube(cubeSize: Vector3) {
        val color = Color(1f, 0f, 0f, 0.5f)
        cubeBuilder.buildRenderable(cubeSize, Vector3.zero(), color, resultCallback = {
            if (it is RenderableResult.Success) {
                renderableCopy = it.renderable
                if (visible) {
                    container?.renderable = it.renderable
                }
            }
        })
    }

    private fun applyProperties(props: Bundle) {
        setSize(props)
        setPosition(props)
        setVisible(props)
    }

    private fun setSize(props: Bundle) {
        props.read<Vector3>(PROP_SIZE)?.let { size ->
            if (size != this.size) {
                this.size = size
                buildCube(size)
                clipContent()
            }
        }
    }

    private fun setPosition(props: Bundle) {
        props.read<Vector3>(PROP_POSITION)?.let { position ->
            tryToAnchorAtPosition(position)
        }
    }

    private fun setVisible(props: Bundle) {
        props.read<Boolean>(PROP_VISIBLE)?.let { visible ->
            this.visible = visible
        }
    }

    private fun tryToAnchorAtPosition(position: Vector3) {
        requestedAnchorPosition = position

        if (lastCameraState != TrackingState.TRACKING) {
            return
        }

        session?.let {
            lastCreatedAnchor?.detach()

            val positionArray = floatArrayOf(position.x, position.y, position.z)
            val rotationArray =
                floatArrayOf(localRotation.x, localRotation.y, localRotation.z, localRotation.w)

            val pose = Pose(positionArray, rotationArray)
            anchor = it.createAnchor(pose)
            lastCreatedAnchor = anchor
            requestedAnchorPosition = null
        }
    }

    private fun clipContent() {
        val min = Vector3(-size.x / 2, -size.y / 2, -size.z / 2)
        val max = Vector3(size.x / 2, size.y / 2, size.z / 2)

        content?.clipBounds = AABB(min, max)
    }

    inner class RotatableNode(transformationSystem: TransformationSystem) :
        TransformableNode(transformationSystem) {

        init {
            // disabling default translationController, because it only allows moving on planes
            translationController.isEnabled = false
            scaleController.isEnabled = false
        }

        override fun onTouchEvent(
            hitTestResult: HitTestResult,
            motionEvent: MotionEvent
        ): Boolean {
            // action up is for some reason not returned for AnchorNode, so we attach renderable and
            // process click events of the container node
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> beingTouched = true
                MotionEvent.ACTION_UP -> beingTouched = false
            }
            return super.onTouchEvent(hitTestResult, motionEvent)
        }

    }

}