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
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.AnchorCreator
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.magicleap.magicscript.ar.renderable.RenderableResult
import com.magicleap.magicscript.scene.ReactScene
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.logMessage
import com.magicleap.magicscript.utils.read
import kotlin.math.min
import kotlin.math.pow

class Prism(
    initProps: ReadableMap,
    private val cubeBuilder: CubeRenderableBuilder,
    private val anchorCreator: AnchorCreator,
    private val arResourcesProvider: ArResourcesProvider
) : AnchorNode(), ReactNode, ArResourcesProvider.CameraUpdatedListener,
    ArResourcesProvider.TransformationSystemListener {

    companion object {
        const val PROP_SIZE = "size"
        const val PROP_POSITION = "position"
        const val PROP_VISIBLE = "visible"
    }

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
    private var content: TransformNode? = null
    private var lastCameraPosition: Vector3? = null
    private var requestedAnchorPosition: Vector3? = null
    private var lastCreatedAnchor: Anchor? = null
    private var size: Vector3 = Vector3(2f, 2f, 0.3f)
    private var renderableCopy: Renderable? = null
    private var reactScene: ReactScene? = null

    private var renderableLoadRequest: CubeRenderableBuilder.LoadRequest? = null

    init {
        arResourcesProvider.addCameraUpdatedListener(this)
        arResourcesProvider.addTransformationSystemListener(this)
    }

    override val reactParent: ReactNode? get() = reactScene

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
        arResourcesProvider.getTransformationSystem()?.let {
            buildContentNode(it)
        }
        applyProperties(properties)
    }

    private fun buildContentNode(transformationSystem: TransformationSystem) {
        // detach old container if exists
        detachContainer()
        container = RotatableNode(transformationSystem).also {
            it.setParent(this)
            if (content != null) {
                it.addChild(content)
            }
        }

        buildCube()
    }

    override fun onTransformationSystemChanged(transformationSystem: TransformationSystem) {
        buildContentNode(transformationSystem)
    }

    override fun onCameraUpdated(position: Vector3, state: TrackingState) {
        if (state != TrackingState.TRACKING) {
            return
        }

        requestedAnchorPosition?.let {
            tryToAnchorAtPosition(it)
        }

        if (lastCameraPosition == null) {
            lastCameraPosition = position
            return
        }

        if (beingTouched) {
            val camToPrismDiff = Vector3.subtract(localPosition, position)
            val camToPrismDistance =
                camToPrismDiff.x.pow(2) + camToPrismDiff.y.pow(2) + camToPrismDiff.z.pow(2)

            // Limiting the movement speed since sometimes the [dist] is near
            // to infinity when moving device (ar core bug?)
            val speed = min(1 + camToPrismDistance, 2f).pow(2)

            val diff = Vector3.subtract(position, lastCameraPosition).scaled(speed)
            localPosition = Vector3.add(localPosition, diff)
        }
        lastCameraPosition = position
    }

    override fun onPause() {
        // no-op
    }

    override fun onResume() {
        // no-op
    }

    override fun onDestroy() {
        // Because Prism's container is a Transformable node, we should detach it explicitly.
        // See https://github.com/magic-script/magic-script-components-react-native/issues/494
        detachContainer()
        arResourcesProvider.removeCameraUpdatedListener(this)
        arResourcesProvider.removeTransformationSystemListener(this)

        renderableLoadRequest?.let {
            cubeBuilder.cancel(it)
        }
    }

    override fun setAnchor(anchor: Anchor?) {
        super.setAnchor(anchor)
        requestedAnchorPosition = null
    }

    private fun buildCube() {
        // cancel previous load task if exists
        renderableLoadRequest?.let {
            cubeBuilder.cancel(it)
        }

        val color = Color(1f, 0f, 0f, 0.5f)
        renderableLoadRequest = CubeRenderableBuilder.LoadRequest(size, Vector3.zero(), color) {
            if (it is RenderableResult.Success) {
                renderableCopy = it.renderable
                if (visible) {
                    container?.renderable = it.renderable
                }
            }
        }.also {
            cubeBuilder.buildRenderable(it)
        }
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
                buildCube()
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

        anchorCreator.createAnchor(position, localRotation, result = {
            // it's important to release unused anchors
            lastCreatedAnchor?.detach()
            anchor = it
            lastCreatedAnchor = it
            requestedAnchorPosition = null
        })
    }

    private fun clipContent() {
        val min = Vector3(-size.x / 2, -size.y / 2, -size.z / 2)
        val max = Vector3(size.x / 2, size.y / 2, size.z / 2)

        content?.clipBounds = AABB(min, max)
    }

    private fun detachContainer() {
        container?.let {
            if (children.contains(it)) {
                removeChild(it)
            }
        }
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