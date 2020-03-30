/*
 * Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.AnchorCreator
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.magicleap.magicscript.scene.ReactScene
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.*
import kotlin.math.min
import kotlin.math.pow

class Prism(
    initProps: ReadableMap,
    private val cubeBuilder: CubeRenderableBuilder,
    private val anchorCreator: AnchorCreator,
    private val arResourcesProvider: ArResourcesProvider,
    private val context: Context,
    private val appInfoProvider: AppInfoProvider
) : AnchorNode(), ReactNode, ArResourcesProvider.CameraUpdatedListener,
    ArResourcesProvider.TransformationSystemListener {

    companion object {
        const val PROP_SIZE = "size"
        const val PROP_SCALE = "scale"
        const val PROP_POSITION = "position"
        const val PROP_ROTATION = "rotation"
        const val PROP_MODE = "mode"
        const val PROP_ANCHOR_UUID = "anchorUuid"

        const val MODE_NORMAL = "normal"
        const val MODE_EDIT = "edit"

        private const val MENU_MARGIN_BOTTOM = 0.05f
    }

    private var properties = Arguments.toBundle(initProps) ?: Bundle()
    private var reactScene: ReactScene? = null
    private var container: PrismContentNode? = null
    private var childNode: TransformNode? = null
    private val menuNode: PrismMenu
    private var lastCameraPosition: Vector3? = null
    private var requestedAnchorPose: Pose? = null
    private var requestedAnchorUuid: String? = null
    private var lastCreatedAnchor: Anchor? = null
    private var requestedEditMode: Boolean = false
    private var size: Vector3 = Vector3(2f, 2f, 0.3f)
    private var requestedScale: Vector3? = null
    private var screenSizePx: Vector2

    private var beingTouched: Boolean = false
        private set(value) {
            field = value
            if (value) {
                anchor = null // remove anchor, so we can change prism local position while moving
            } else {
                // user has stopped moving the prism, so we anchor it to the final position
                val pose = createPose(localPosition, localRotation)
                tryToAnchorAtPose(pose)
            }
        }

    init {
        val screenWidthPx = context.resources.displayMetrics.widthPixels
        val screenHeightPx = context.resources.displayMetrics.heightPixels
        screenSizePx = Vector2(screenWidthPx.toFloat(), screenHeightPx.toFloat())

        arResourcesProvider.addCameraUpdatedListener(this)
        arResourcesProvider.addTransformationSystemListener(this)

        val title = appInfoProvider.getAppName()
        menuNode = PrismMenu(context, arResourcesProvider, title)
        menuNode.isVisible = false
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
        if (childNode != null) {
            logMessage("Prism child already added", true)
            return
        }
        childNode = child
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
        val size = properties.read(PROP_SIZE) ?: size
        arResourcesProvider.getTransformationSystem()?.let {
            buildContainer(it, size)
        }
        buildMenu()
        applyProperties(properties)
    }

    private fun applyProperties(props: Bundle) {
        setSize(props)
        setPose(props)
        setScale(props)
        setAnchorUuid(props)
        setMode(props)
    }

    private fun buildContainer(transformationSystem: TransformationSystem, size: Vector3) {
        // detach old container if exists
        detachContainer()
        container = PrismContentNode(transformationSystem, cubeBuilder, size).also { container ->
            container.setParent(this)

            requestedScale?.let {
                container.extendedScaleController.setScale(it)
                requestedScale = null
            }

            if (requestedEditMode) {
                container.editModeActive = requestedEditMode
                requestedEditMode = false
            }

            if (childNode != null) {
                container.addChild(childNode)
            }
            container.setOnTouchListener { _, motionEvent ->
                // action up is for some reason not returned for AnchorNode, so we attach renderable
                // and process click events of the container node
                when (motionEvent.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        beingTouched = true
                    }
                    MotionEvent.ACTION_UP -> {
                        beingTouched = false
                    }
                }
                false
            }

            container.scaleChangedListener = { _ ->
                adjustMenuPosition()
            }
        }
        adjustMenuPosition()
    }

    override fun onTransformationSystemChanged(transformationSystem: TransformationSystem) {
        buildContainer(transformationSystem, size)
    }

    override fun onCameraUpdated(position: Vector3, state: TrackingState) {
        if (state != TrackingState.TRACKING) {
            return
        }

        requestedAnchorPose?.let {
            tryToAnchorAtPose(it)
        }

        requestedAnchorUuid?.let {
            tryToAnchorAtUuid(it)
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

        adjustMenuVisibility()
        adjustMenuRotation(position)

        lastCameraPosition = position
    }

    override fun onPause() {
        // no-op
    }

    override fun onResume() {
        // no-op
    }

    override fun onDestroy() {
        container?.onDestroy()
        // Because Prism's container is a Transformable node, we should detach it explicitly.
        // See https://github.com/magic-script/magic-script-components-react-native/issues/494
        detachContainer()
        arResourcesProvider.removeCameraUpdatedListener(this)
        arResourcesProvider.removeTransformationSystemListener(this)
    }

    // Prism can also be anchored through this function when initial placement is active
    override fun setAnchor(anchor: Anchor?) {
        super.setAnchor(anchor)
        requestedAnchorPose = null
        requestedAnchorUuid = null
    }

    private fun buildMenu() {
        menuNode.onButtonClickListener = {
            container?.let {
                it.editModeActive = !it.editModeActive
            }
        }

        menuNode.build()
        addChild(menuNode)
        adjustMenuPosition()
    }

    private fun adjustMenuPosition() {
        val scaleY = container?.localScale?.y ?: 1f
        val posY = (size.y * scaleY) / 2 + MENU_MARGIN_BOTTOM
        menuNode.localPosition = Vector3(0f, posY, 0f)
    }

    private fun adjustMenuRotation(cameraPosition: Vector3) {
        val prismFlatPosition = Vector3(worldPosition.x, 0f, worldPosition.z)
        val cameraFlatPosition = Vector3(cameraPosition.x, 0f, cameraPosition.z)
        val direction = prismFlatPosition - cameraFlatPosition
        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
        menuNode.worldRotation = lookRotation
    }

    private fun adjustMenuVisibility() {
        arResourcesProvider.getCamera()?.let { camera ->
            val viewPos = camera.worldToScreenPoint(worldPosition)
            val showMenu = viewPos.x in 0f..screenSizePx.x && viewPos.y in 0f..screenSizePx.y
            if (showMenu) {
                if (!menuNode.isVisible) {
                    menuNode.showAnimated()
                }
            } else {
                menuNode.isVisible = false
            }
        }
    }

    private fun setSize(props: Bundle) {
        props.read<Vector3>(PROP_SIZE)?.let { size ->
            if (size != this.size) {
                this.size = size
                container?.setSize(size)
                adjustMenuPosition()
                clipContent()
            }
        }
    }

    private fun setPose(props: Bundle) {
        if (props.containsAny(PROP_POSITION, PROP_ROTATION)) {
            val position = properties.read(PROP_POSITION) ?: localPosition
            val rotation = properties.read(PROP_ROTATION) ?: localRotation
            val pose = createPose(position, rotation)
            tryToAnchorAtPose(pose)
        }
    }

    private fun setScale(props: Bundle) {
        props.read<Vector3>(PROP_SCALE)?.let { scale ->
            val scaleController = container?.extendedScaleController
            if (scaleController != null) {
                scaleController.setScale(scale)
            } else {
                requestedScale = scale
            }
        }
    }

    private fun setAnchorUuid(props: Bundle) {
        props.read<String>(PROP_ANCHOR_UUID)?.let { anchorUuid ->
            tryToAnchorAtUuid(anchorUuid)
        }
    }

    private fun setMode(props: Bundle) {
        val mode = props.read<String>(PROP_MODE) ?: return
        val container = this.container
        if (container != null) {
            container.editModeActive = mode == MODE_EDIT
        } else if (mode == MODE_EDIT) {
            requestedEditMode = true
        }
    }

    private fun tryToAnchorAtPose(pose: Pose) {
        requestedAnchorPose = pose

        anchorCreator.createAnchor(pose, result = {
            // it's important to release unused anchors
            lastCreatedAnchor?.detach()
            anchor = it
            lastCreatedAnchor = it
            requestedAnchorPose = null
        })
    }

    private fun tryToAnchorAtUuid(anchorUuid: String) {
        requestedAnchorUuid = anchorUuid

        val anchorNode = scene?.findByName(anchorUuid) as AnchorNode?
        anchorNode?.anchor?.let {
            anchor = it
            requestedAnchorUuid = null
        }
    }

    private fun clipContent() {
        val min = Vector3(-size.x / 2, -size.y / 2, -size.z / 2)
        val max = Vector3(size.x / 2, size.y / 2, size.z / 2)

        childNode?.clipBounds = AABB(min, max)
    }

    private fun detachContainer() {
        container?.let {
            if (children.contains(it)) {
                removeChild(it)
            }
        }
    }

    private fun createPose(position: Vector3, rotation: Quaternion): Pose {
        val positionArray = floatArrayOf(position.x, position.y, position.z)
        val rotationArray = floatArrayOf(rotation.x, rotation.y, rotation.z, rotation.w)
        return Pose(positionArray, rotationArray)
    }
}